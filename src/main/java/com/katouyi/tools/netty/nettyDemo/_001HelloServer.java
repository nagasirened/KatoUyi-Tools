package com.katouyi.tools.netty.nettyDemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class _001HelloServer {

    public static void main(String[] args) {
        // 1. 服务器端的启动器，负责组装netty组建，协调工作
        new ServerBootstrap()
                // 2.EventLoop它包含了线程和selector选择器 组件：BossEventLoop(selector, thread)  WorkerEventLoop(selector, thread)
                .group(new NioEventLoopGroup())
                // 3.选择一个ServerChannel的实现，NIO、OIO、KQueue、epoll等
                .channel(NioServerSocketChannel.class)
                // 4.child就像是之前的worker(child)，决定了能做哪些操作  （boss负责连接，worker负责处理）
                .childHandler(
                    // 5.channel代表和客户端进行数据读写的通道 Initializer初始化器，负责添加别的handler
                    new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            // 6.添加具体的handler
                            ch.pipeline().addLast(new StringDecoder());     // 将ByteBuf转为字符串
                            // 自己的业务组件，自定义事件，打印上一步转换的结果
                            /** inBound入栈：处理输入数据 outBound出栈：处理输出数据 */
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println(msg);
                                }
                            });
                        }
                    })
                // 7.绑定监听端口
                .bind(8080);
    }
}
