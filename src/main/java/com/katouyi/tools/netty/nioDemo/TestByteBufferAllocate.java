package com.katouyi.tools.netty.nioDemo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestByteBufferAllocate {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});

        buffer.flip();

        buffer.get(new byte[4]);   // 一次性读取4个字节，position会移动
    }


    public static void main2(String[] args) throws IOException {
        FileChannel channel = new FileInputStream("data.txt").getChannel();
        /*
         * 1.分配buffer内存
         * 堆内存：读写效率较低，受到GC垃圾回收的影响，清除整理算法会移动它的位置
         * 堆外内存：读写效率高，因为少一次拷贝，不受到垃圾回收的影响；  调用操作系统函数分配内存，分配内存效率相对低下
         */
        ByteBuffer buffer = ByteBuffer.allocate(10);
        System.out.println(ByteBuffer.allocate(10).getClass());         // class java.nio.HeapByteBuffer       堆内存
        System.out.println(ByteBuffer.allocateDirect(10).getClass());   // class java.nio.DirectByteBuffer     堆外内存

        /*
         * 2.写入buffer数据
         * channel.read(buffer);
         * buffer.put(内容);
         */
        channel.read(buffer);
        buffer.flip();
        /*
         * 3.从buffer读取数据
         */

        buffer.rewind(); // 可以将position 重新置为0，重复读取
        // buffer.get(int i)   它不会移动读取的指针，按照下标读取

        byte b = buffer.get();
        FileChannel testFileChannel = new FileInputStream("test.txt").getChannel();
        int write = testFileChannel.write(buffer);          // 将buffer中的数据写入channel，必须要把writable 设为true才醒
        System.out.println(write);


    }


}
