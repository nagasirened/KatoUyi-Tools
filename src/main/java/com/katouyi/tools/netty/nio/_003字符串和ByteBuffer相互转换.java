package com.katouyi.tools.netty.nio;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class _003字符串和ByteBuffer相互转换 {

    public static void main(String[] args) {
        func1();
    }

    public static void func1() {
        // String放进buffer
        String str1 = "hello";
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(str1.getBytes());
        ByteBufferUtil.debugAll(buffer);

        buffer.flip();

        // buffer转String
        String str2 = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println(str2);
        ByteBufferUtil.debugAll(buffer);
    }

    public static void func2() {
        // String 放进 buffer  (使用encode),不需要初始化 Buffer的大小
        String str = "hello";
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(str);
        ByteBufferUtil.debugAll(buffer);
    }

    public static void func3() {
        // String 放进 buffer （使用 .wrap 方法 ）
        String str = "hello";
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        ByteBufferUtil.debugAll(buffer);
    }
}
