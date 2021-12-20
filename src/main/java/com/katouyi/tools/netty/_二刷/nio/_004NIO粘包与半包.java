package com.katouyi.tools.netty._二刷.nio;

import com.katouyi.tools.netty.nioDemo.ByteBufferUtil;

import java.nio.ByteBuffer;

/**
 *
 * 现象
 * 网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
 * 但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
 *
 * Hello,world\n
 * I’m Nyima\n
 * How are you?\n
 * 变成了下面的两个 byteBuffer (粘包，半包)
 *
 * Hello,world\nI’m Nyima\nHo
 * w are you?\n
 *
 *
 *
 * 粘包：发送方在发送数据时，并不是一条一条地发送数据，而是将数据整合在一起，当数据达到一定的数量后再一起发送。这就会导致多条信息被放在一个缓冲区中被一起发送出去
 *
 * 半包：接收方的缓冲区的大小是有限的，当接收方的缓冲区满了以后，就需要将信息截断，等缓冲区空了以后再继续放入数据。这就会发生一段完整的数据最后被截断的现象
 *
 * 解决方法：通过get(index)方法遍历ByteBuffer，遇到分隔符时进行处理。注意：get(index)不会改变position的值
 *         get() 方法会改变position的值，可以rewind()回去
 *         // a.记录该段数据长度，以便于申请对应大小的缓冲区
 *         // b.将缓冲区的数据通过get()方法写入到target中
 */
public class _004NIO粘包与半包 {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        // 模拟粘包+半包
        buffer.put("Hello,world\nI'm Nyima\nHo".getBytes());
        deal(buffer);
        buffer.put("w are you?\n".getBytes());
        deal(buffer);
    }

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
