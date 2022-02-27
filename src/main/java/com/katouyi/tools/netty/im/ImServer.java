package com.katouyi.tools.netty.im;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

public class ImServer {
    public static void main(String[] args) {

        new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(
                        Math.max(Runtime.getRuntime().availableProcessors() >> 1, 4)
                )).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LoggingHandler());
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0));
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            // 消息读事件
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                super.channelRead(ctx, msg);
                            }
                        });
                    }
                }).bind(8080);
    }
}
