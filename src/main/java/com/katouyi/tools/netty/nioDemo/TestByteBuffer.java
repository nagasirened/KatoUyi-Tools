package com.katouyi.tools.netty.nioDemo;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        // 1. 输入/出流获取FileChannel
        try (FileChannel fileChannel = new FileInputStream("data.txt").getChannel() ){
            // 2.准备缓存区存储内容
            ByteBuffer byteBuffer = ByteBuffer.allocate(3);
            // 3.从channel中读取数据并写入到buffer中, 返回值如果是-1则代表读取完成了。不是-1的话就继续读
            while (fileChannel.read(byteBuffer) != -1) {
                // 切换至读模式
                byteBuffer.flip();
                // 无参get就是只读一个数据
                while (byteBuffer.hasRemaining()) {
                    System.out.print((char) byteBuffer.get());
                }

                /** 文件没有读取完全，切换byteBuffer的写入模式   clear()或者compact()切换至写入模式
                // clear() 会清空(没读完也清空)并切换到写入状态   compact() 是把没有读完的数据往前压缩，position指向未读完的数据结尾处，并切换到写模式 */
                byteBuffer.clear();
            }
        } catch (Exception e) {
            log.error("xxx");
        }
    }

}
