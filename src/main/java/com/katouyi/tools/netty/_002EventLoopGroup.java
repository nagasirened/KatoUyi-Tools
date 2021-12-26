package com.katouyi.tools.netty;

import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.TimeUnit;

public class _002EventLoopGroup {

    public static void main(String[] args) throws InterruptedException {
        /**
         * NioEventLoopGroup, 继承自 ScheduledExecutorService， 可以执行 【IO任务】、【普通任务】和【定时任务】
         * DefaultEventLoopGroup, 不能执行IO任务，但是可以执行【普通任务】和【定时任务】
         */
        // 不传入核心数，默认是电脑核心数量的2倍
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
        // 默认轮训
        System.out.println(eventLoopGroup.next());      // 5faeada1
        System.out.println(eventLoopGroup.next());      // 528931cf
        System.out.println(eventLoopGroup.next());      // 5faeada1
        System.out.println(eventLoopGroup.next());      // 528931cf

        // 提交普通任务 相当于 eventLoopGroup.next().submit(...)
        eventLoopGroup.submit(() -> {
            System.out.println("test zeng");
        });

        // 延时任务
        eventLoopGroup.next().schedule(() -> {
            System.out.println("test scheduled");
        }, 2, TimeUnit.SECONDS);

        // 定时任务 l:delay  l1:period
        eventLoopGroup.next().scheduleAtFixedRate(() -> {
            System.out.println("fixed rate task");
        }, 0, 2, TimeUnit.SECONDS);


        TimeUnit.SECONDS.sleep(3);
        if (!eventLoopGroup.isShutdown()) {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
