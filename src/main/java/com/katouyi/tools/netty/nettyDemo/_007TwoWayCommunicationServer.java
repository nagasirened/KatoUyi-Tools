package com.katouyi.tools.netty.nettyDemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.charset.Charset;

public class _007TwoWayCommunicationServer {

    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                // get方法不会会改变readIndex指针，read方法才会改变指针
                                CharSequence charSequence = buf.getCharSequence(0, buf.readableBytes(), Charset.defaultCharset());
                                System.out.println("我是in, 我得到了消息：" + charSequence);
                                // 释放release
                                buf.release();
                                super.channelRead(ctx, charSequence);
                            }
                        });
                        pipeline.addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                System.out.println("我是out, 不处理，就看看");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                String str = (String) msg;
                                System.out.println("我也是in：我要发送这个消息回去：" + str);
                                // 包装成一个ByteBuf再发
                                ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(str.getBytes(Charset.defaultCharset())));
                            }
                        });
                        pipeline.addLast(new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                System.out.println("我是out, 我实时ctx.writeAndFlush(), 我能不能被看到");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                })
                .bind(8080);
    }

}
