package com.katouyi.tools.netty.nioDemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
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
            // *******  selectKeys 集合只会添加selectKey,不会主动去删除，因此需要我们手动处理一下
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                /** 拿到了key之后一定要删除掉 */
                iter.remove();
                if (key.isAcceptable()) {
                    // 因为目前仅仅只有ssc注册了，所以这个肯定是ssc
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer attachment = ByteBuffer.allocate(16);
                    SelectionKey scKey = sc.register(selector, 0, attachment);  // 附件
                    scKey.interestOps(SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        // --- 局部变量会导致一次消息没读完，第二次读取的时候第一次的丢了，弄成附件
                        // ByteBuffer buffer = ByteBuffer.allocate(16);
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        int read = channel.read(buffer);           // 如果在这儿client突然挂断了，异常断开，会注册一个新的read事件到selectKeys中，因此后面需要一个key.cancel()反注册
                                                                   // 如果返回-1，代表客户端正常断开，也需要取消事件
                        if (read == -1) {
                            key.cancel();
                        } else {
                            /* 1.可能出现粘包现象
                                 buffer.flip();  System.out.println(Charset.defaultCharset().decode(buffer).toString());    */
                            /* 2.如果消息长度超过了allocate的长度限制，会默认出发两次读取，第一次读取因为没有完整消息，会跳过。 第二次如果完整了则会打印第二次
                                 比如说1234567890abcdefghijk\n， 就只会打印出 ghijk 来，不会打印前面的
                                 split(buffer);  */
                            /* 3.动态扩容 */
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() << 1);
                                buffer.flip();  // compact() 将buffer转换为了写模式，改为读取模式拷贝到新的newBuffer中去
                                newBuffer.put(buffer);
                                // 替换旧的附件buffer
                                key.attach(newBuffer);
                            }
                        }
                    } catch (Exception e) {
                        // 有可能主机挂断等异常出现
                        e.printStackTrace();
                        key.cancel();
                    }
                }
            }
        }
    }

    public static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 获取到换行符，认为是一个完整的内容，解析出它
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                target.flip();
                System.out.println(StandardCharsets.UTF_8.decode(target).toString());
            }
        }
        source.compact();
    }

}
