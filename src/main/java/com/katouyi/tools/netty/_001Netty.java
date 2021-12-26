package com.katouyi.tools.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class _001Netty {

    /**
     * server
     */
    public static void main(String[] args) {
        DefaultEventLoopGroup taskGroup = new DefaultEventLoopGroup();
        new ServerBootstrap()
                // Boss  Worker,如果定义的handler里面有很耗时的操作，会影响别的channel，此时可以提交到独立的EventLoopGroup中 (taskGroup)
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(10))
                .channel(NioServerSocketChannel.class)
                // head <-> handlers <-> tail
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new StringDecoder());
                        // handler1，使用默认的NioEventLoopGroup处理
                        channel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println("收到的消息是：" + msg.toString());
                                ctx.fireChannelRead(msg);   // 🌟🌟🌟 将消息传给下一个handler，不使用的话下面的handler就接收不到了
                            }
                        });
                        // 🌟🌟🌟 使用一个第三方的taskGroup,去处理耗时的handler
                        channel.pipeline().addLast(taskGroup, "customHandler ", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println("收到的消息是：" + msg.toString());
                            }
                        });
                    }
                })
                .bind(8080);
    }
}

class _001NettyClient {
    public static void main(String[] args) throws InterruptedException {
        new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel()
                .writeAndFlush("zengGF ");
    }

}
