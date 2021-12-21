package com.katouyi.tools.netty._二刷.nio;

import com.katouyi.tools.netty.nioDemo.ByteBufferUtil;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * accept  是ServerSocketChannel 才有的事件，有连接请求时触发
 * connect 是客户端连接建立后触发
 * read    可读事件
 * write   可写事件
 */
public class _009多路复用Selector之初体验 {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 获得服务器通道
        try(ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(8080));
            /** 创建Selector选择器 */
            Selector selector = Selector.open();
            // 通道必须设置为非阻塞模式
            server.configureBlocking(false);
            // 注册selector监听链接事件（将server注册到selector上面）
            /* SelectionKey sscKey = server.register(selector, 0, null);
             * sscKey.interestOps(SelectionKey.OP_ACCEPT); */
            server.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                // 若没有事件就绪，线程会被阻塞，反之不会被阻塞。从而避免了CPU空转   返回值为就绪的事件个数
                // 四种事件之一发生就触发
                int ready = selector.select();
                System.out.println("selector ready counts : " + ready);

                // 获取所有事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    // 连接事件
                    if (selectionKey.isAcceptable()) {
                        // channel下面的事件代表处理了selectionKey，如果不处理，则会永远死循环它，也可以cancel取消事件
                        // selectionKey.cancel();
                        ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel sc = channel.accept();
                        // sc关注可读事件
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);

                        //处理后移除
                        iterator.remove();
                    }
                    // 可读事件
                    if (selectionKey.isReadable()) {
                        SocketChannel sc = (SocketChannel)selectionKey.channel();
                        sc.read(buffer);
                        buffer.flip();
                        ByteBufferUtil.debugRead(buffer);
                    }
                }
            }

        } catch (Exception e) {e.printStackTrace();}
    }
}
