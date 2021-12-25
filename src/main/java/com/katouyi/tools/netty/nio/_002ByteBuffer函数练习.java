package com.katouyi.tools.netty.nio;

import com.katouyi.tools.netty.nioDemo.ByteBufferUtil;

import java.nio.ByteBuffer;

public class _002ByteBuffer函数练习 {
    /**
     *
     * @param args
     */

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte)97);
        ByteBufferUtil.debugAll(buffer);   // a

        buffer.put(new byte[]{98, 99, 100, 101});
        ByteBufferUtil.debugAll(buffer);    // abcde

        // 获取数据
        buffer.flip();
        ByteBufferUtil.debugAll(buffer);    // abcde
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
        ByteBufferUtil.debugAll(buffer);    // abcde  虽然下标变了，但是不会清除

        // 压缩没有读取的
        buffer.compact();
        ByteBufferUtil.debugAll(buffer);    // cdede  cde没有读取，往左压缩，45位的de暂时还没有删除

        // 再次写入
        buffer.put((byte)102);
        buffer.put((byte)103);
        ByteBufferUtil.debugAll(buffer);    // cdefg
    }
}
