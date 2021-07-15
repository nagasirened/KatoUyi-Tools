package com.katouyi.tools.netty.nioDemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class WriteableTest {

    public static void main(String[] args) throws IOException {

        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(8080));

            Selector selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        // ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel(); // 它就是ssc
                        SocketChannel sc = ssc.accept();
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < 5000000; i++) {
                            builder.append("a");
                        }
                        ByteBuffer buffer = StandardCharsets.UTF_8.encode(builder.toString());  // 读模式
                        // write是读取的长度，如果没有读完，重新注册写事件进去
                        int write = sc.write(buffer);
                        System.out.println(write);
                        if (buffer.hasRemaining()) {
                            sc.configureBlocking(false);
                            // 因为可能这个sc有别的事件，因此用加法，或者位运算中的或 "|" 来处理
                            sc.register(selector, key.interestOps() | SelectionKey.OP_WRITE, buffer);
                        }
                    } else if (key.isWritable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        // 获取buffer再次读取
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int write = sc.write(buffer);
                        System.out.println(write);
                        // 如果读取完了
                        if (!buffer.hasRemaining()) {
                            key.attach(null);
                            key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);   // 减法  或者 异或(相同为0，相异为1)
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
