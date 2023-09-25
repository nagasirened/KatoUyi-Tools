package com.katouyi.tools.asyncTools京东.worker;

import com.katouyi.tools.asyncTools京东.callback.DefaultCallback;
import com.katouyi.tools.asyncTools京东.callback.ICallback;
import com.katouyi.tools.asyncTools京东.callback.IWorker;
import com.katouyi.tools.asyncTools京东.exception.SkippedException;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class WorkerWrapper<T, V> {
    Logger log = LoggerFactory.getLogger(WorkerWrapper.class);

    /* 唯一主键 */
    private String id;
    /* 参数 */
    private T param;
    /* 跟depend参数相关； 如果自己作为depends，must=true只有自己完成了下游才能开始 */
    @Setter
    private Boolean must;
    /* 处理器 */
    private IWorker<T, V> worker;
    /* 回掉器 */
    private ICallback<T, V> callback;
    /* 在自己后面的WorkerWrappers */
    private List<WorkerWrapper<?, ?>> nextWrappers;
    /* 自己依赖的WorkerWrappers */
    private List<WorkerWrapper<?, ?>> dependWrappers;
    /* 标记该事件是否已经被处理过了，譬如已经超时返回false了，后续rpc又收到返回值了，则不再二次回调
       状态 1-finish, 2-error, 3-working */
    private final AtomicInteger state = new AtomicInteger(INIT);
    /* 存储结果，预先生成对象引用 */
    private volatile WorkerResult<V> workerResult = WorkerResult.defaultResult();

    /*
     * 是否在执行自己前，去校验nextWrapper的执行结果<p>
     * 1   4
     * -------3
     * 2
     * 如这种在4执行前，可能3已经执行完毕了（被2执行完后触发的），那么4就没必要执行了。
     * 注意，该属性仅在nextWrapper数量<=1时有效，>1时的情况是不存在的
     */
    private volatile boolean needCheckNextWrapperResult = true;

    public static final int INIT = 0;
    public static final int FINISH = 1;
    public static final int ERROR = 2;
    public static final int WORKING = 3;

    private int getState() {
        return state.get();
    }


    /**
     * 该map存放所有wrapper的id和wrapper映射
     */
    private Map<String, WorkerWrapper<?, ?>> forParamUseWrappers;

    public WorkerWrapper(String id, T param, IWorker<T, V> worker, ICallback<T, V> callback) {
        if (worker == null) {
            throw new NullPointerException("async.worker is null");
        }
        this.id = id;
        this.param = param;
        this.worker = worker;
        this.callback = Objects.isNull(callback) ? new DefaultCallback<>() : callback;
    }

    public void work(ThreadPoolExecutor executor, long remainTime, Map<String, WorkerWrapper<?, ?>> forParamUseWrappers) {
        work(executor, null, remainTime, forParamUseWrappers);
    }

    private void work(ThreadPoolExecutor executor, WorkerWrapper<?, ?> fromWrapper, long remainTime, Map<String, WorkerWrapper<?, ?>> forParamUseWrappers) {
        this.forParamUseWrappers = forParamUseWrappers;
        forParamUseWrappers.put(id, this);
        long now = System.currentTimeMillis();
        // 总的已经超时了，就直接失败进行下一个
        try {
            if (remainTime <= 0) {
                fastFail(INIT, null);
                return;
            }
            // 如果自己已经执行完成了，就不再重复处理，而是处理后面的任务
            if (getState() == FINISH || getState() == ERROR) {

                return;
            }
            // 如果在执行前需要校验nextWrapper的状态
            if (needCheckNextWrapperResult) {
                // 代表下游已经执行了，自己就可以不执行了
                if (!checkNextWrapperResult()) {
                    fastFail(INIT, new SkippedException());
                    beginNext(executor, now, remainTime);
                    return;
                }
            }

            // 如果没有任何依赖的workerWrapper，自己就是第一批执行的
            if (dependWrappers == null || dependWrappers.isEmpty()) {
                fire();
                return;
            }

            // 如果自己有依赖，一个是只有一个依赖，那么
            if (dependWrappers.size() == 1) {
                WorkerWrapper<?, ?> dependWorker = dependWrappers.get(0);
                ResultState dependState = dependWorker.getWorkerResult().getResultState();
                if (ResultState.TIMEOUT.equals(dependState)) {
                    fastFail(INIT, null);
                } else if (ResultState.EXCEPTION.equals(dependState)) {
                    fastFail(INIT, dependWorker.getWorkerResult().getEx());
                } else if (ResultState.SUCCESS.equals(dependState)) {
                    fire();
                }
            }
        } finally {
            // 自己无论失败还是执行，都要执行后续的WorkerWrapper
            beginNext(executor, now, remainTime);
        }

        // 有非必要执行beginNext的执行方式
        doDependJobs(fromWrapper, executor, now, remainTime);
    }

    private void doDependJobs(WorkerWrapper<?, ?> fromWrapper, ThreadPoolExecutor executor, long now, long remainTime) {
        LinkedList<WorkerWrapper<?, ?>> mustWrappers = new LinkedList<>();
        boolean fromIsMust = false;
        for (WorkerWrapper<?, ?> dependWrapper : dependWrappers) {
            if (dependWrapper.isMust()) {
                mustWrappers.add(dependWrapper);
            }
            // 如果依赖的是自己的上游
            if (dependWrapper.equals(fromWrapper)) {
                fromIsMust = fromWrapper.isMust();
            }
        }

        // 如果上游都不是必须条件，到了自己就可以开始执行了
        if (mustWrappers.isEmpty()) {
            if (ResultState.TIMEOUT == fromWrapper.getWorkerResult().getResultState()) {
                fastFail(INIT, null);
            } else {
                fire();
            }
            return;
        }

        // 存在必须依赖的，但是不是from的这个，就先不干活
        if (!fromIsMust) {
            return;
        }

        boolean existNoFinish = false;
        boolean hasError = false;
        // 先判断前面必须要执行的依赖任务的执行结果，如果有任何一个失败，那就不用走action了，直接给自己设置为失败，进行下一步就是了
        for (WorkerWrapper<?, ?> mustDependWrapper : mustWrappers) {
            int mustDependWrapperState = mustDependWrapper.getState();
            if (mustDependWrapperState == INIT || mustDependWrapperState == WORKING) {
                existNoFinish = true;
                break;
            }
            WorkerResult<?> mustWorkerResult = mustDependWrapper.getWorkerResult();
            if (ResultState.TIMEOUT.equals(mustWorkerResult.getResultState())) {
                workerResult = defaultResult();
                hasError = true;
                break;
            }
            if (ResultState.EXCEPTION.equals(mustWorkerResult.getResultState())) {
                workerResult = defaultExResult(mustDependWrapper.getWorkerResult().getEx());
                hasError = true;
                break;
            }
        }

        if (hasError) {
            fastFail(INIT, null);
            beginNext(executor, now, remainTime);
            return;
        }

        // 上游都finish了，自己才开始跑
        if (!existNoFinish) {
            fire();
            beginNext(executor, now, remainTime);
            return;
        }
    }

    private void fire() {
        workerResult = workerDoJob();
    }

    private WorkerResult<V> workerDoJob() {
        // 避免重复执行
        if (!isDefaultResult()) {
            return workerResult;
        }

        // 如果已经不是init状态了，说明正在被执行或已执行完毕。这一步很重要，可以保证任务不被重复执行
        if (!compareAndSetState(INIT, WORKING)) {
            return workerResult;
        }

        try {
            // 钩子，再开始执行之前
            callback.begin();
            V resultValue = worker.action(param, forParamUseWrappers);
            // 如果状态不是在working,说明别的地方已经修改了
            if (!compareAndSetState(WORKING, FINISH)) {
                return workerResult;
            }
            workerResult.setResultState(ResultState.SUCCESS);
            workerResult.setResult(resultValue);

            // 回调
            callback.result(true, param, workerResult);
            return workerResult;
        } catch (Exception e) {
            if (!isDefaultResult()) {
                return workerResult;
            }
            fastFail(WORKING, e);
            return workerResult;
        }
    }

    /**
     * 判断自己下游链路上，是否存在已经出结果的或已经开始执行的
     * 如果没有则返回true, 如果有则返回false     (有些情况是下游都完成了，自己也没必要再执行了)
     */
    private boolean checkNextWrapperResult() {
        // 如果自己就是最后一个，或者后面有并行的多个，就返回true
        if (nextWrappers == null || nextWrappers.size() != 1) {
            return getState() == INIT;
        }
        WorkerWrapper<?, ?> workerWrapper = nextWrappers.get(0);
        boolean nextIdle = workerWrapper.getState() == INIT;
        return nextIdle && workerWrapper.checkNextWrapperResult();
    }

    /**
     * 执行关联的next Workers
     */
    private void beginNext(ThreadPoolExecutor executor, long now, long remainTime) {
        // 前一个任务花费的时间
        long cost = System.currentTimeMillis() - now;
        long remainNow = remainTime - cost;
        if (nextWrappers == null || nextWrappers.isEmpty()) {
            return;
        }
        if (nextWrappers.size() == 1) {
            nextWrappers.get(0).work(executor, this, remainNow, forParamUseWrappers);
            return;
        }
        CompletableFuture<?>[] futures = new CompletableFuture[nextWrappers.size()];
        for (int i = 0; i < nextWrappers.size(); i++) {
            final int finalI = i;
            futures[i] = CompletableFuture.runAsync(() -> nextWrappers.get(finalI).work(executor, this, remainNow, forParamUseWrappers), executor);
        }
        try {
            CompletableFuture.allOf(futures).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("beginNext", e);
        }
    }

    private void fastFail(int expect, Exception e) {
        if (!compareAndSetState(expect, ERROR)) {
            log.error("fastFail updateState fail, expect: {}", expect);
            return;
        }

        if (isDefaultResult()) {
            if (e == null) {
                workerResult = defaultResult();
            } else {
                workerResult = defaultExResult(e);
            }
        }

        callback.result(false, param, workerResult);
    }

    /**
     * 总控制台超时，停止所有任务
     */
    public void stopNow() {
        if (getState() == INIT || getState() == WORKING) {
            fastFail(getState(), null);
        }
    }

    /**
     * 默认超时快速失败，如果有异常则是异常失败
     */
    private WorkerResult<V> defaultResult() {
        workerResult.setResultState(ResultState.TIMEOUT);
        workerResult.setResult(worker.defaultValue());
        return workerResult;
    }

    private WorkerResult<V> defaultExResult(Exception e) {
        workerResult.setResultState(ResultState.EXCEPTION);
        workerResult.setResult(worker.defaultValue());
        workerResult.setEx(e);
        return workerResult;
    }

    /**
     * 判断当前结果是不是初始化的
     */
    private boolean isDefaultResult() {
        return ResultState.DEFAULT == workerResult.getResultState();
    }

    /**
     * 更改状态
     */
    private boolean compareAndSetState(int expect, int update) {
        return this.state.compareAndSet(expect, update);
    }

    public boolean isMust() {
        return must;
    }

    private void addDepend(WorkerWrapper<?, ?> workerWrapper) {
        addDepend(workerWrapper, false);
    }

    private void addDepend(WorkerWrapper<?, ?> workerWrapper, boolean must) {
        workerWrapper.setMust(must);
        if (dependWrappers == null) {
            dependWrappers = new LinkedList<>();
        }
        // 如果添加的有重复的，就不需要添加了
        for (WorkerWrapper<?, ?> dependWrapper : dependWrappers) {
            if (dependWrapper.equals(workerWrapper)) {
                return;
            }
        }
        dependWrappers.add(workerWrapper);
    }

    private void addNext(WorkerWrapper<?, ?> workerWrapper) {
        if (nextWrappers == null) {
            nextWrappers = new LinkedList<>();
        }
        // 如果添加的有重复的，就不需要添加了
        for (WorkerWrapper<?, ?> nextWrapper : nextWrappers) {
            if (nextWrapper.equals(workerWrapper)) {
                return;
            }
        }
        nextWrappers.add(workerWrapper);
    }

    private void addNextWrappers(List<WorkerWrapper<?, ?>> wrappers) {
        if (wrappers == null || wrappers.isEmpty()) {
            return;
        }
        for (WorkerWrapper<?, ?> wrapper : wrappers) {
            addNext(wrapper);
        }
    }

    public static class Builder<W, C> {
        // 唯一标识
        private String id = UUID.randomUUID().toString();
        private W param;
        private IWorker<W, C> worker;
        private ICallback<W, C> callback;
        private List<WorkerWrapper<?, ?>> nextWrappers;
        private List<WorkerWrapper<?, ?>> dependWrappers;
        // 存储强依赖于自己的wrapper集合
        private Set<WorkerWrapper<?, ?>> selfIsMustSet;

        private boolean needCheckNextWrapperResult = true;

        public Builder<W, C> worker(IWorker<W, C> worker) {
            this.worker = worker;
            return this;
        }

        public Builder<W, C> param(W w) {
            this.param = w;
            return this;
        }

        public Builder<W, C> id(String id) {
            this.id = id;
            return this;
        }

        public Builder<W, C> needCheckNextWrapperResult(boolean needCheckNextWrapperResult) {
            this.needCheckNextWrapperResult = needCheckNextWrapperResult;
            return this;
        }

        public Builder<W, C> callback(ICallback<W, C> callback) {
            this.callback = callback;
            return this;
        }

        public Builder<W, C> depend(WorkerWrapper<?, ?> ... wrappers) {
            if (wrappers == null) {
                return this;
            }
            for (WorkerWrapper<?, ?> wrapper : wrappers) {
                depend(wrapper);
            }
            return this;
        }

        public Builder<W, C> depend(WorkerWrapper<?, ?> wrapper) {
            return depend(wrapper, true);
        }

        public Builder<W, C> depend(WorkerWrapper<?, ?> wrapper, boolean isMust) {
            if (wrapper == null) {
                return this;
            }
            wrapper.setMust(isMust);
            if (dependWrappers == null) {
                dependWrappers = new ArrayList<>();
            }
            dependWrappers.add(wrapper);
            return this;
        }

        public Builder<W, C> next(WorkerWrapper<?, ?> wrapper) {
            return next(wrapper, true);
        }

        public Builder<W, C> next(WorkerWrapper<?, ?> wrapper, boolean selfIsMust) {
            if (nextWrappers == null) {
                nextWrappers = new ArrayList<>();
            }
            nextWrappers.add(wrapper);

            //强依赖自己
            if (selfIsMust) {
                if (selfIsMustSet == null) {
                    selfIsMustSet = new HashSet<>();
                }
                selfIsMustSet.add(wrapper);
            }
            return this;
        }

        public Builder<W, C> next(WorkerWrapper<?, ?>... wrappers) {
            if (wrappers == null) {
                return this;
            }
            for (WorkerWrapper<?, ?> wrapper : wrappers) {
                next(wrapper);
            }
            return this;
        }

        public WorkerWrapper<W, C> build() {
            WorkerWrapper<W, C> wrapper = new WorkerWrapper<>(id, param, worker, callback);
            wrapper.setNeedCheckNextWrapperResult(needCheckNextWrapperResult);
            if (dependWrappers != null) {
                for (WorkerWrapper<?, ?> workerWrapper : dependWrappers) {
                    workerWrapper.addNext(wrapper);
                    wrapper.addDepend(workerWrapper);
                }
            }
            if (nextWrappers != null) {
                for (WorkerWrapper<?, ?> workerWrapper : nextWrappers) {
                    boolean must = false;
                    if (selfIsMustSet != null && selfIsMustSet.contains(workerWrapper)) {
                        must = true;
                    }
                    workerWrapper.addDepend(wrapper, must);
                    wrapper.addNext(workerWrapper);
                }
            }

            return wrapper;
        }

    }

}
