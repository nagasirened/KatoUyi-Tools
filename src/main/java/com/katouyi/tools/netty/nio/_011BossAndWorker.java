package com.katouyi.tools.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class _011BossAndWorker {

    public static void main(String[] args) {
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(8080));

            Selector boss = Selector.open();
            server.register(boss, SelectionKey.OP_ACCEPT);

            Worker[] workers = new Worker[4];
            for (int i = 0; i < 4; i++) {
                workers[i] = new Worker("worker-" + i);
            }
            AtomicInteger robin = new AtomicInteger(0);
            while (true) {
                boss.select();
                Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        SocketChannel sc = server.accept();
                        sc.configureBlocking(false);
                        workers[robin.getAndIncrement() % workers.length].register(sc);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Worker implements Runnable{
        private Thread thread;
        private String name;
        private volatile Selector selector;
        private volatile boolean started = false;
        private ConcurrentLinkedQueue<Runnable> queue;

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel sc) throws IOException {
            if (!started) {
                synchronized (this) {
                    if (!started) {
                        this.thread = new Thread(this, name);
                        selector = Selector.open();
                        queue = new ConcurrentLinkedQueue<>();
                        thread.start();
                        started = true;
                    }
                }
            }

            // 如果不加到队列里面，它会是boss线程执行的内容，如果selector卡在了select()方法，就还是无法执行
            queue.add(() -> {
                try {
                    sc.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(16));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            /**
             * 唤醒是因为，如果新增连接需要注册进当前的selector, 如果selector正卡在select()方法，
             * 是不回允许注册的，因此手动唤醒一下先。唤醒了之后执行Queue中的Runnable才能注册
             */
            selector.wakeup();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Runnable task = queue.poll();
                    while (task != null) {
                        task.run();
                        task = queue.poll();
                    }
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            try {
                                SocketChannel sc = (SocketChannel) key.channel();
                                ByteBuffer buffer = (ByteBuffer) key.attachment();
                                int read = sc.read(buffer);
                                if (read == -1) {
                                    key.cancel();
                                    continue;
                                }

                                deal(buffer);
                                // 如果容量不够，扩容buffer再次读取
                                if (buffer.position() == buffer.limit()) {
                                    ByteBuffer newBuffer = ByteBuffer.allocate(buffer.limit() << 1);
                                    buffer.flip(); // 这个切换回读模式
                                    newBuffer.put(buffer);
                                    key.attach(newBuffer);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                key.cancel();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void deal(ByteBuffer buffer) {
            buffer.flip();
            for (int i = 0; i < buffer.limit(); i++) {
                byte b = buffer.get(i);
                if (b == '\n') {
                    int length = i + 1 - buffer.position();
                    ByteBuffer subBuffer = ByteBuffer.allocate(length);
                    int q = 0;
                    while (q < length) {
                        subBuffer.put(buffer.get());
                        q++;
                    }
                    ByteBufferUtil.debugAll(subBuffer);
                }
            }
            buffer.compact();
        }
    }
}


