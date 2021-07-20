package com.katouyi.tools.netty.nettyDemo;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class _004Netty_Future {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
        EventLoop eventLoop = eventLoopGroup.next();

        /** Netty中的future,继承了JDK的，还进行了拓展  */
        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                TimeUnit.SECONDS.sleep(1);
                log.info("执行计算");
                // throw new RuntimeException("错误信息test");
                return 80;
            }
        });
        /** 1.同步阻塞执行 */
        /*System.out.println("等待结果");
        System.out.println("结果是：" + future.get());
        eventLoopGroup.shutdownGracefully();*/

        /** 2.异步等有结果了再处理 */
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                // 根据是否成功打印结果  getNow非阻塞方式获取结果，没有则返回null   cause获取异常信息
                if (future.isSuccess()) {
                    System.out.println("结果是：" + future.getNow());
                } else {
                    System.out.println("错误信息：" + future.cause().getMessage());
                }
                eventLoopGroup.shutdownGracefully();
            }
        });

    }
}
