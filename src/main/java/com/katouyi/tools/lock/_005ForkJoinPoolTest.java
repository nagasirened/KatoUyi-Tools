package com.katouyi.tools.lock;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RestController
public class _005ForkJoinPoolTest {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/testForkJoin")
    public void testForkJoin() throws ExecutionException, InterruptedException {

        /**
         * ForkJoinPool forkJoinPool = Executors.newWorkStealingPool();
         * Runtime.getRuntime().availableProcessors()  获取核心数
         * 3.handler 异常处理器
         * 4. true FIFO 先进先出   false LIFO 先进后出
         *
         * forkJoinPool 内核还是一个线程池，可以提交Runnable和Callable
         * 但是我们使用提交ForkJoinTask的方式
         *   ---  常用子类1：RecursiveTask<T>
         *
         *
         *  🌟🌟🌟建议使用在内存运算、结果汇总、统计等，尽量不要用于数据库查询等阻塞的网络操作，
         *      全局使用，线程就那么点，所以...🌟🌟🌟
         *
         */
        ForkJoinPool forkJoinPool = new ForkJoinPool
                (Runtime.getRuntime().availableProcessors(),
                        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                        null, true);

        ArrayList<String> urls = new ArrayList<>();
        urls.add("http://tool.bitefu.net/jiari/?d=20210430");
        urls.add("http://tool.bitefu.net/jiari/?d=20210501");
        HttpJsonRequest requestTask = new HttpJsonRequest(restTemplate, urls, 0, urls.size() - 1);
        ForkJoinTask<JSONObject> task = forkJoinPool.submit(requestTask);
        JSONObject baseResult = task.get();
    }
}

@Slf4j
class HttpJsonRequest extends RecursiveTask<JSONObject> {

    RestTemplate restTemplate;
    private List<String> urls;
    private int start;      // 开始的任务index
    private int end;        // 结束的任务index

    HttpJsonRequest(RestTemplate restTemplate, List<String> urls, int start, int end) {
        this.restTemplate = restTemplate;
        this.urls = urls;
        this.start = start;
        this.end = end;
    }

    // 这个方式就是ForkJoinPool实际调用的方法
    @Override
    protected JSONObject compute() {
        /** start == end ,代表只有一条处理则执行   否则再次拆分 */
        int count = end - start;
        if (count == 0) {
            StopWatch sw = new StopWatch();
            sw.start();
            String response = restTemplate.getForObject(urls.get(0), String.class);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("res", response);
            sw.stop();
            log.info("{} : 接口调用完毕，耗时：{}, 接口为：{}", Thread.currentThread(), sw.getTime(), urls.get(0));
        } else {
            System.out.println("任务拆分");
            /** 如果是有多个接口调用，就需要拆分了 */
            // HttpJsonRequest httpJsonRequest = new HttpJsonRequest(urls, restTemplate);
            int mid = (start + end) / 2;
            HttpJsonRequest requestTask1 = new HttpJsonRequest(restTemplate, urls, start, mid);
            requestTask1.fork();  // 把当前任务再次push到线程池的处理队列中
            HttpJsonRequest requestTask2 = new HttpJsonRequest(restTemplate, urls, (mid + 1), end);
            requestTask2.fork();

            // join处理结果
            JSONObject result = new JSONObject();
            result.putAll(requestTask1.join());
            result.putAll(requestTask2.join());
            return result;
        }
        return null;
    }
}
