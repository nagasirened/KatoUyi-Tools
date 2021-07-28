package com.katouyi.tools.netty.nettyDemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;


@Slf4j
public class _009HttpDecoder {

    public static void main(String[] args) {
        new ServerBootstrap().group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                        pipeline.addLast(new HttpServerCodec());   // 包含了 HttpRequestDecoder 和 HttpResponseEncoder
                        // Http解码器将http请求解析为了两个部分，只关注HttpRequest的话，就选择下面这个类
                        pipeline.addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
                                log.debug(request.uri());

                                // 包装响应，设置版本号和状态码
                                DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
                                byte[] contentBytes = "<h1>test success</h1>".getBytes(StandardCharsets.UTF_8);
                                response.content().writeBytes(contentBytes);        // content本身是一个ByteBuf
                                // http请求要设置长度，不设置的话会导致网页一直等待接收更多的数据
                                response.headers().add(HttpHeaders.CONTENT_LENGTH, contentBytes.length);
                                response.headers().add(HttpHeaders.ACCEPT_CHARSET, "utf-8");
                                ctx.writeAndFlush(response);
                            }
                        });
                    }
                }).bind(8080);
    }
}
