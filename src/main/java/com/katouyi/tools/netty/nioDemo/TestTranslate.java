package com.katouyi.tools.netty.nioDemo;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestTranslate {

    /**
     * 相互转换1
     */
    public static void main(String[] args) throws FileNotFoundException {
        String s1 = "hello";

        // 方法1、2
        // ByteBuffer buffer1 = ByteBuffer.allocate(16);
        // 直接getBytes()
        // buffer1.put(s1.getBytes());
        // 使用StandardCharsets转换
        // buffer1.put(StandardCharsets.UTF_8.encode(s1));
        // buffer1.flip();

        // 方法3 直接就是读模式，不需要切换
        ByteBuffer buffer1 = ByteBuffer.wrap(s1.getBytes());



        // 通过StandardCharsets解码，获得CharBuffer，再通过toString获得字符串
        String r1 = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(r1);


        File f = new File("/Users/kaion/20191230书签.html");
        RandomAccessFile file = new RandomAccessFile(f, "r");
        FileChannel channel = file.getChannel();  // RandomAccessFile 可以输入输出

        Path path = Paths.get("/Users/kaion", "20191230书签.html");
        System.out.println(path);
        System.out.println(Files.exists(path));
        System.out.println(Files.isDirectory(path));
    }
}
