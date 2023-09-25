package com.katouyi.tools.asyncTools京东.worker;

public class WorkerResult<V> {

    /* 执行结果 */
    private V result;
    /* 状态 */
    private ResultState resultState;
    /* 异常 */
    private Exception ex;

    public WorkerResult(V result, ResultState resultState) {
        this.result = result;
        this.resultState = resultState;
    }

    public WorkerResult(V result, ResultState resultState, Exception ex) {
        this.result = result;
        this.resultState = resultState;
        this.ex = ex;
    }

    public static <V> WorkerResult<V> defaultResult() {
        return new WorkerResult<>(null, ResultState.DEFAULT);
    }

    @Override
    public String toString() {
        return "WorkerResult{" +
                "result=" + result +
                ", resultState=" + resultState +
                ", ex=" + ex +
                '}';
    }

    public V getResult() {
        return result;
    }

    public ResultState getResultState() {
        return resultState;
    }

    public Exception getEx() {
        return ex;
    }

    public void setResult(V result) {
        this.result = result;
    }

    public void setResultState(ResultState resultState) {
        this.resultState = resultState;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }
}
