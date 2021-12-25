package com.katouyi.tools.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class _010注册Write事件 {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8089));

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey selectionKey = iter.next();
                iter.remove();
                if (selectionKey.isAcceptable()) {
                    // 其实它就是上面的ssc,对象是同一个  ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ);

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = StandardCharsets.UTF_8.encode(sb.toString());
                    // ✨✨✨ sc写事件，内容过大不能一次性写完。但是下面的遍历的话，有很多时候是写不进去的，只能等待写完，因此改为注册写事件  ✨✨✨
                    /*if (buffer.hasRemaining()) {
                        int writeNumber = sc.write(buffer);
                        System.out.println(writeNumber);
                    }*/
                    // 先写一次，如果还有剩的，就注册写事件等下一次循环过来到这里
                    int writeNumber = sc.write(buffer);
                    System.out.println(writeNumber);
                    if (buffer.hasRemaining()) {
                        // 这样写直接覆盖了写事件,下面则保留了写，并关注读   sc.register(selector, SelectionKey.OP_WRITE, buffer);
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        // 把没有写完的数据挂仅scKey中
                        scKey.attach(buffer);
                    }
                } else if (selectionKey.isWritable()) {
                    SocketChannel sc = (SocketChannel) selectionKey.channel();
                    ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();

                    // 还是没有读完的话，回自动再次进入写事件，因为我们没有删除这个selectionKey
                    int writeNumber = sc.write(buffer);
                    System.out.println(writeNumber);
                    // ✨✨✨ 读取完成后，清理buffer，关闭可写事件 ✨✨✨
                    if (!buffer.hasRemaining()) {
                        selectionKey.attach(null);
                        selectionKey.interestOps(selectionKey.interestOps() - SelectionKey.OP_WRITE);
                    }
                }
            }

        }

    }
}
