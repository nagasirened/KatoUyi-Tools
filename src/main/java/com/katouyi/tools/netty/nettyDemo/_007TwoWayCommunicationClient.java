package com.katouyi.tools.netty.nettyDemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;
import java.util.Scanner;

public class _007TwoWayCommunicationClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 接收从server返回回来的消息
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg.getClass());
                                ByteBuf buf = (ByteBuf) msg;
                                System.out.println("====================");
                                System.out.println(buf.getCharSequence(0, buf.readableBytes(), Charset.defaultCharset()));
                                System.out.println("====================");
                                buf.release();
                            }
                        });
                        pipeline.addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                System.out.println(" ====== " + msg);
                                String info = (String) msg;
                                ByteBuf byteBuf = ctx.alloc().buffer().writeBytes(info.getBytes(Charset.defaultCharset()));
                                super.write(ctx, byteBuf, promise);
                            }
                        });
                    }
                })
                .connect("localhost", 8080);
        // 同步等待 channel 成功建立连接
        Channel channel = channelFuture.sync().channel();
        Scanner scanner = new Scanner(System.in);
        new Thread(() -> {
            while (true) {
                String line = scanner.nextLine();
                if ("exit".equals(line)) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(line);
            }
        }).start();

        ChannelFuture closeFuture = channel.closeFuture();
        closeFuture.addListener(future -> {
            System.out.println("关闭连接");
            group.shutdownGracefully();
        });
    }

}
