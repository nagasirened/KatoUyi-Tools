package com.katouyi.tools.netty.nettyDemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class _003ClosedFuture {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringEncoder());
                }
            })
            .connect("localhost", 8080);
        channelFuture.sync();
        Channel channel = channelFuture.channel();

        Scanner scanner = new Scanner(System.in);
        new Thread(() -> {
            while (true) {
                String line = scanner.nextLine();
                if ("q".equals(line)) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(line);
            }
        }).start();
        // 获得closeFuture对象
        ChannelFuture closeFuture = channel.closeFuture();
        // 同步等待NIO线程执行完close操作，close完后再执行一些关闭后的相关操作
        closeFuture.sync();
        System.out.println("我要关闭了呀");  /** TODO 关闭后执行的操作 */
        // ❤️最后因为EventLoopGroup还有别的线程，所以也需要关闭❤️
        group.shutdownGracefully();
        System.out.println("测试结束");

        /**
         * 方式二，异步处理，不需要sync同步等待
         */
        /* closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("channel关闭之后的操作，跟着关闭group");
                group.shutdownGracefully();
                System.out.println("测试结束");
            }
        }); */
        /* closeFuture.addListener(future -> {
            System.out.println("channel关闭之后的操作，跟着关闭group");
            group.shutdownGracefully();
            System.out.println("测试结束");
        });*/
    }
}
