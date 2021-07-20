package com.katouyi.tools.netty.nettyDemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class _001HelloClient {

    public static void main(String[] args) throws InterruptedException {
        // 1.启动累
        new Bootstrap()
                // 2.添加EventLoop
                .group(new NioEventLoopGroup())
                // 3.选择channel客户端实现
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    // 4.连接简历后被调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 5.编码发送出去的数据
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 6.连接到服务器   connect方法是异步非阻塞方法，不加入sync()的方法的话，最后的writeAndFlush消息可能就在真正连接成功之前发送，会发送不到消息。
                .connect(new InetSocketAddress("localhost", 8080))

                // 下面是ChannelFuture的方法
                .sync()
                .channel()
                // 向服务器发送数据
                .writeAndFlush("hello world!!!");
    }
}
