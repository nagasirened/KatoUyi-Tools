package com.katouyi.tools.netty.nioDemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SelectorDemo {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 设置为非阻塞模式
        ssc.configureBlocking(false);
        // 注册到selector中，且ssc关注accept事件
        SelectionKey sscKey = ssc.register(selector, 0);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            selector.select();
            // 获取有事件的各个selectionKey,因为后面有移除事件，一次使用迭代器
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                // 因为目前仅仅只有ssc注册了，所以这个肯定是ssc
                ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                SocketChannel sc = channel.accept();
            }

        }
    }
}
