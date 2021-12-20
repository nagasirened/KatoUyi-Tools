package com.katouyi.tools.netty._二刷.nio;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class _005文件拷贝transferTo零拷贝 {

    /**
     * 这样复制文件也太棒了吧，这样的零拷贝上限是2G
     */
    public static void main(String[] args) {
        try (
                FileInputStream fis = new FileInputStream("/Users/katouyi/project/mine/testNetty/a.txt");
                FileOutputStream fos = new FileOutputStream("/Users/katouyi/project/mine/testNetty/b.txt");
                FileChannel inputChannel = fis.getChannel();        // 读取channel
                FileChannel outputChannel = fos.getChannel()
        ){
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果文件大小超过2G，就分多次传输
     */
    public static void main2(String[] args){
        try (FileInputStream fis = new FileInputStream("stu.txt");
             FileOutputStream fos = new FileOutputStream("student.txt");
             FileChannel inputChannel = fis.getChannel();
             FileChannel outputChannel = fos.getChannel()) {
            long size = inputChannel.size();
            long capacity = inputChannel.size();
            // 分多次传输
            while (capacity > 0) {
                // transferTo返回值为传输了的字节数，capacity -= 传输了的字节数，就是剩下没有传的字节数
                capacity -= inputChannel.transferTo(size - capacity, capacity, outputChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
