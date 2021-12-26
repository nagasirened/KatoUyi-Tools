package com.katouyi.tools.netty.nio;

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
                /***
                 * SocketChannel 正常异常断开连接都会触发读事件，都需要cancel掉  68  79
                 */
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    // 🌟 selectionKey处理完了之后，不会主动删除，我们主动删除即可
                    iterator.remove();
                    // 连接事件
                    if (selectionKey.isAcceptable()) {
                        // channel下面的事件代表处理了selectionKey，如果不处理，则会永远死循环它，也可以cancel取消事件
                        // selectionKey.cancel();
                        ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel sc = channel.accept();
                        // sc关注可读事件
                        sc.configureBlocking(false);

                        // 🌟 给每个SocketChannel绑定一个附件📎 ByteBuffer,这样不会被干扰
                        sc.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(16));

                        //处理后移除
                        iterator.remove();
                    }
                    // 可读事件
                    if (selectionKey.isReadable()) {
                        // 客户端断开连接，会发生IO异常，导致server端挂了，因此捕获异常
                        try {
                            SocketChannel sc = (SocketChannel)selectionKey.channel();
                            ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                            // 如果是正常断开，read方法返回 -1
                            int read = sc.read(buffer);
                            if (read == -1) {
                                selectionKey.cancel();
                                continue;
                            }
                            // 如果字符串短， 自动根据分隔符 \n 分割字符串
                            deal(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.limit() << 1);
                                buffer.flip(); // 这个切换回读模式
                                newBuffer.put(buffer);
                                selectionKey.attach(newBuffer);
                            }
                        } catch (Exception e) {
                            // 🌟 异常断开连接 必须处理，否则会触发一个read事件导致循环一直有报错信息
                             e.printStackTrace();
                            selectionKey.cancel(); // 客户端断开了，没必要处理它了，直接取消即可  它是真正能够selectionKeys里面删除
                        }
                    }
                }
            }

        } catch (Exception e) {e.printStackTrace();}
    }


    /**
     * 如果字符串太长了，第一次没有找到，会自动触发第二次的读取事件（会把剩下没有读的内容读取出来）
     * 这样的话，就不能使用byteBuffer局部变量，两次要使用同一个局部变量
     */
    private static void deal(ByteBuffer buffer) {
        // 切换至读模式
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            // get(index) 不回改变position  get()才会
            byte b = buffer.get(i);
            if (b == '\n') {
                int length = i + 1 - buffer.position();   //截取字符串  包含\n
                ByteBuffer subBuffer = ByteBuffer.allocate(length);
                int q = 0;
                while (q < length) {
                    subBuffer.put(buffer.get());        // get() 会移动position
                    q++;
                }
                ByteBufferUtil.debugAll(subBuffer);  //当前这条处理完了，处理下一条
            }
        }
        buffer.compact();
    }
}
