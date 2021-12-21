package com.katouyi.tools.netty._二刷.nio;

import com.katouyi.tools.netty.nioDemo.ByteBufferUtil;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * 可以通过ServerSocketChannel的configureBlocking(false) 方法将获得连接设置为非阻塞的。
 * 此时若没有连接，accept会返回null
 *
 * 可以通过SocketChannel的configureBlocking(false)方法将从通道中读取数据设置为非阻塞的。
 * 若此时通道中没有数据可读，read会返回-1
 *
 *
 * 缺点：线程一直处于循环处理的状态，CPU占用
 */
public class _008非阻塞网络编程Server {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 获得服务器通道
        try(ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(9999));
            ArrayList<SocketChannel> channels = new ArrayList<>();
            /** ServerSocketChannel 非阻塞模式 */
            server.configureBlocking(false);
            while (true) {
                // System.out.println("before connect...");
                SocketChannel acceptChannel = server.accept();
                if (acceptChannel != null) {
                    System.out.println("success connect...");
                    channels.add(acceptChannel);
                }

                for (SocketChannel channel : channels) {
                    /** SocketChannel 非阻塞模式 */
                    channel.configureBlocking(false);
                    if (channel.read(buffer) > 0) {
                        buffer.flip();
                        ByteBufferUtil.debugRead(buffer);
                        buffer.clear();
                        System.out.println("after read...");
                    }

                }
            }

        } catch (Exception e) {e.printStackTrace();}
    }
}
