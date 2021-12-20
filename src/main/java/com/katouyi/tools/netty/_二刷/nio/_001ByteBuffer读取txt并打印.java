package com.katouyi.tools.netty._二刷.nio;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class _001ByteBuffer读取txt并打印 {

    public static void main(String[] args) throws FileNotFoundException {
        File file = FileUtil.file("/Users/katouyi/project/mine/testNetty/a.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        try (
            FileChannel channel = fileInputStream.getChannel()
        ) {
            // 写数据
            ByteBuffer buffer = ByteBuffer.allocate(10);
            StringBuilder sb = new StringBuilder();
            int hasNext = 0;
            while ((hasNext = channel.read(buffer)) > 0) {
                // 切换至读模式
                buffer.flip();
                while (buffer.hasRemaining()) {
                    sb.append((char) buffer.get());
                }
                // 清空并切换模式  // compact 是压缩到左边且切换读模式
                buffer.clear();
            }
            System.out.println(sb.toString());
        } catch (Exception e) { }
    }

}
