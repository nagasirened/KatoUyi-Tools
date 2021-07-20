package com.katouyi.tools.netty.nettyDemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class _002ChannelFuture {

    public static void main(String[] args) throws InterruptedException {
        // 1.启动累
        ChannelFuture channelFuture = new Bootstrap()
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
                .connect(new InetSocketAddress("localhost", 8080));

                /** 1.使用sync方法同步处理结果：主线程等待channelFuture建立完成，然后获取channel发送消息 */
                // channelFuture.sync()                     // 阻塞住当前线程，知道NIO线程连接建立完毕，那么后面的消息发出去就肯定能收到了
                //.channel()                  // 获取一个channel
                //.writeAndFlush("hello world!!!");   // 向服务器发送数据  write() + flush()

                /** 2.使用addListener(回调对象) 方法异步处理结果：等待建立完成和发送消息的都不是主线程，交代给别人去做 */
                channelFuture.addListener(new ChannelFutureListener() {
                    // 这个future就和上面那个一样
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        Channel channel = future.channel();
                        log.info("debug: channel:{}", channel);
                        channel.writeAndFlush("test2");
                    }
                });

    }
}
