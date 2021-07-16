package com.katouyi.tools.netty.nioDemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadServer {

    /**
     * boss仅仅负责接受链接，worker负责处理读写
     * @param args
     */
    public static void main(String[] args) {
        try (ServerSocketChannel ssc = ServerSocketChannel.open();) {
            Thread.currentThread().setName("boss");
            Selector boss = Selector.open();
            ssc.configureBlocking(false);
            ssc.register(boss, SelectionKey.OP_ACCEPT, null);
            ssc.bind(new InetSocketAddress(8080));
            // 创建固定数量的worker，且初始化一下  docker不要用 Runtime.getRuntime().availableProcessors() 获取，它拿的物理机的核心数
            Worker[] workers = new Worker[2];
            for (int i = 0; i < workers.length; i++) {
                workers[i] = new Worker("worker-" + i);
            }
            AtomicInteger index = new AtomicInteger(0);
            while (true) {
                boss.select();
                Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
                if (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    System.out.println(key);
                    iter.remove();
                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                        SocketChannel sc = channel.accept();
                        sc.configureBlocking(false);
                        // sc.register 注册到worker的selector要比 selector.select()才能正常运行
                        // 其次，就算正常注册了第一个客户端，第二个register的时候，selector.select()还是在执行，它不能正常注册进去
                        workers[index.getAndIncrement() % workers.length].register(sc);                                     /** 运行在worker中 */
                        // sc.register(worker0.selector, SelectionKey.OP_READ);    /** 运行在boss中 */
                        /**
                         * worker0.register();
                         * worker0.selector.wakeup();
                         * sc.register(worker0.selector, SelectionKey.OP_READ)
                         */
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private boolean isUseful = false;
        private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.thread = new Thread(this, name);
        }

        public void register(SocketChannel sc) throws IOException {
            // 实现了Runnable接口的关系,设置线程名称
            /** 这些都是主线程执行的内容，为了让sc能成功注册到selector中，保险就是让worker执行注册 */
            if (!isUseful) {
                synchronized (this) {
                    if (!isUseful) {
                        this.selector = Selector.open();
                        this.thread.start();
                        this.isUseful = true;
                    }
                }
            }

            // 加入到队列之后，主动唤醒一下；
            // 让新的sc注册进去，目的是上worker的线程去执行注册；
            queue.add(() -> {
               try {
                   sc.register(selector, SelectionKey.OP_READ, null);
               } catch (Exception e) {
                   e.printStackTrace();
               }
            });

            selector.wakeup();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Runnable task = queue.poll();
                    if (task != null) {
                        task.run();
                    }
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    if (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.read(buffer);
                            buffer.flip();
                            System.out.println(StandardCharsets.UTF_8.decode(buffer).toString());
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
