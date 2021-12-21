package com.katouyi.tools.netty._二刷.nio;

import com.katouyi.tools.netty.nioDemo.ByteBufferUtil;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * 阻塞模式下，相关方法都会导致线程暂停
 *   🌟   ServerSocketChannel.accept 会在没有连接建立时让线程暂停  🌟
 *   🌟   SocketChannel.read 会在通道中没有数据可读时让线程暂停     🌟
 * 阻塞的表现其实就是线程暂停了，暂停期间不会占用 cpu，但线程相当于闲置
 * 单线程下，阻塞方法之间相互影响，几乎不能正常工作，需要多线程支持
 * 但多线程下，有新的问题，体现在以下方面
 *      32 位 jvm 一个线程 320k，64 位 jvm 一个线程 1024k，如果连接数过多，必然导致 OOM，
 *      并且线程太多，反而会因为频繁上下文切换导致性能降低
 *      可以采用线程池技术来减少线程数和线程上下文切换，但治标不治本，如果有很多连接建立，但长时间 inactive，会阻塞线程池中所有线程，因此不适合长连接，只适合短连接
 */
public class _007网络编程阻塞Server {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(9999));
            ArrayList<SocketChannel> channels = new ArrayList<>();
            while (true) {
                System.out.println("before connecting...");
                // 没有链接，线程阻塞
                SocketChannel socketChannel = server.accept();
                System.out.println("success connecting...");
                channels.add(socketChannel);
                for (SocketChannel channel : channels) {
                    System.out.println("start read...");
                    channel.read(buffer);
                    buffer.flip();
                    ByteBufferUtil.debugRead(buffer);
                    buffer.clear();
                    System.out.println("end read...");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}

class _007网络编程Client {
    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open()){
            socketChannel.connect(new InetSocketAddress("localhost", 9999));
            System.out.println("start connect.");
            /*
             * 利用断点write，防止被关闭
             */
            socketChannel.write(ByteBuffer.wrap("hello".getBytes()));
        } catch (Exception e) {e.printStackTrace();}
    }
}