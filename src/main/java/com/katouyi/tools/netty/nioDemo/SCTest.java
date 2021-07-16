package com.katouyi.tools.netty.nioDemo;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class SCTest {

    public static void main(String[] args) throws Exception {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        sc.write(Charset.defaultCharset().encode("123456789"));
        System.in.read();
    }
}
