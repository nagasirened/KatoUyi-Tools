package com.katouyi.tools.netty.nettyDemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.Charset;

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

    }

}
