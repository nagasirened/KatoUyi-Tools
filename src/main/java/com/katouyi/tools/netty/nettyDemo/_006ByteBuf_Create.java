package com.katouyi.tools.netty.nettyDemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.Charset;

/**
 * ByteBuf的创建
 */
public class _006ByteBuf_Create {

    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();  // 默认是初始化256，可以动态扩容的ByteBuf

        System.out.println(buffer);      // 256
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            StringBuilder a = sb.append("a");
        }

        buffer.writeBytes(sb.toString().getBytes(Charset.defaultCharset()));

        System.out.println(buffer);      // 512 动态扩容了2倍
    }
}
