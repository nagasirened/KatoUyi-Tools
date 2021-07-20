package com.katouyi.tools.netty.nettyDemo;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class _004Netty_Promise {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1.准备好一个EventLoop对象
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        // 2.Promise不像前两个Future，任务提交后别人创建的，Promise可以自己创建
        /** 它是一个结果容器，可以让别的线程设置结果进去 */
        DefaultPromise<Integer> promise = new DefaultPromise<>(group.next());

        new Thread(() -> {
            // 3.任意一个线程计算完成向promise中填充结果
            log.info("开始计算结果");
            try {
                // int i = 1/0;
                TimeUnit.SECONDS.sleep(1);
                promise.setSuccess(99);
            } catch (Exception e) {
                promise.setFailure(e);
            }
        }).start();

        //4.接受结果
        log.info("等待获取结果");
        if (!promise.isDone()) {
            promise.await();
            if (promise.isSuccess()) {
                log.info("结果是：" + promise.getNow());
            } else {
                log.error(promise.cause().getMessage());
            }
        }
        group.shutdownGracefully();
    }

}
