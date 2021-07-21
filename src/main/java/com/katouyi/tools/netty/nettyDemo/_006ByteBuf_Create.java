package com.katouyi.tools.netty.nettyDemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * ByteBuf的创建
 */
public class _006ByteBuf_Create {

    public static void main(String[] args) {
        /**
         * 每种类型的ByteBuf都实现了ReferenceCounted接口，
         * 它是用来对ByteBuf引用计数的：
         * retain() 方法加1， release() 方法减1
         *
         * 非池化的，release() 减1到0后，会释放内存
         *   池化的，release() 减1到0后，会还给池子
         *
         *   所有的handler中，最后一个处理ByteBuf的才释放ByteBuf
         *   其次，首尾的 head 和 tail 会检查ByteBuf有没有被释放，么有的话就主动去释放
         */
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();  // 默认是初始化256，可以动态扩容的ByteBuf

        // class io.netty.buffer.PooledUnsafeDirectByteBuf  默认【池化】的【直接内存】ByteBuf
        System.out.println(buffer.getClass());
        System.out.println(buffer);      // 256
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            StringBuilder a = sb.append("a");
        }

        buffer.writeBytes(sb.toString().getBytes(Charset.defaultCharset()));

        System.out.println(buffer);      // 512 动态扩容了2倍

        /**
         * ReferenceCounted  retain()   release()
         */
        ByteBuf buffer1 = ByteBufAllocator.DEFAULT.buffer();
        buffer1.release();
        System.out.println(buffer1);  // PooledUnsafeDirectByteBuf(freed)

        /**
         * 数据填充方法
         */
        ByteBuf buffer2 = ByteBufAllocator.DEFAULT.buffer();
        buffer2.writeBytes(new byte[] {'a', 'b'});
        buffer2.writeInt(2);                            // 0000 0010
        buffer2.writeIntLE(2);   // 高位写入，奇奇怪怪     //  0010 0000
        buffer2.writeLong(23);
        System.out.println(buffer2);

        /**
         * 切片slice  和 复制duplicate  它们都不是 复制数据到一个新的ByteBuf，而是在原来的内存基础上，新建两个指针
         * 注意：原来的数据或者切片的数据，如果修改或者release掉了之后，会相互影响，因此一般会先retain()一下，再自己release一下
         */
        ByteBuf buffer3 = ByteBufAllocator.DEFAULT.buffer();
        buffer3.writeBytes("1234567890".getBytes());

        ByteBuf s1 = buffer3.slice(0, 5);
        s1.retain();
        ByteBuf s2 = buffer3.slice(5, 5);
        s2.retain();

        buffer3.release();
        System.out.println(s1);
        System.out.println(s2);
        s1.release();
        s2.release();


        /**
         * 合并两个ByteBuf使用，原始的Write方法会复制
         * 但是我们使用CompositeByteBuf的话，就不会复制，而是将N个ByteBuf聚合到一起。使用的还是原来的物理内存
         * 因此也会受到修改或者release的影响
         */
        ByteBuf buffer4 = ByteBufAllocator.DEFAULT.buffer(5);
        buffer4.writeBytes(new byte[]{'a', 'b', 'c'});
        ByteBuf buffer5 = ByteBufAllocator.DEFAULT.buffer(5);
        buffer5.writeBytes(new byte[]{'a', 'b', 'c'});

        CompositeByteBuf compositeByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        //compositeByteBuf.addComponents(buffer4, buffer5);       // 直接这样用，不会修改compositeByteBuf 的读写指针
        compositeByteBuf.addComponents(true, buffer4, buffer5);

        /**
         * 工具方法Unpooled，可以用来包装多个ByteBuf或者字节数组
         * wrappedBuffer 方法底层也使用了CompositeByteBuf
         */
        ByteBuf buffer6 = Unpooled.wrappedBuffer(buffer4, buffer5);
        // ByteBuf buffer7 = Unpooled.copiedBuffer(buffer4, buffer5);  应该是复制
        System.out.println(buffer6);

    }

}
