package com.katouyi.tools.netty.nettyDemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Random;

public class _008FrameDecoder {

    /**
     * 粘包的原因：接收方 ByteBuf 设置太大（Netty 默认 1024）    1.TCP连接的滑动窗口机制  2.Nagle算法
     * 半包的原因：接收方 ByteBuf 小于实际发送数据量             1.TCP连接的滑动窗口机制  2.MSS限制，发送数据超过了MSS限制，会出现半包
     */

    /**
     * 方式1：短链接 -- shame算了吧
     * 方式2：定长解码器 「FixedLengthFrameDecoder」  客户端于服务器约定一个最大长度，保证客户端每次发送的数据长度都不会大于该长度。若发送数据长度不足则需要补齐至该长度
     * 方式3：行解码器「LineBasedFrameDecoder(int maxLength)」  拆分以换行符(\n)为分隔符的数据
     *       也可以通过DelimiterBasedFrameDecoder(int maxFrameLength, ByteBuf... delimiters)来指定通过什么分隔符来拆分数据
     */
    public static void main行解码器(String[] args) {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new DelimiterBasedFrameDecoder(64, ByteBufAllocator.DEFAULT.buffer().writeBytes("Q".getBytes())),
                // new LineBasedFrameDecoder(64),
                new LoggingHandler(LogLevel.DEBUG)
        );
        char a = 'a';
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int length = random.nextInt(20 + 1);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < length; j++) {
                sb.append(a);
            }
            a++;
            sb.append("Q");// sb.append("\n");
            byteBuf.writeBytes(sb.toString().getBytes());
            embeddedChannel.writeInbound(byteBuf);
            byteBuf.clear();
        }
    }

    /**
     * 长度字段解码器
     * 在传送数据时可以在数据中添加一个用于表示有用数据长度的字段，在解码时读取出这个用于表明长度的字段，同时读取其他相关参数，即可知道最终需要的数据是什么样子的
     * LengthFieldBasedFrameDecoder解码器可以提供更为丰富的拆分方法
     */
    public static void main(String[] args) {
                        // int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip
        /* 参数1：最大长度
        lengthFieldLength：提供长度的字节是从第几位开始
        lengthFieldLength：标示长度的内容占几个字节，int4个，根据实际情况来
        lengthAdjustment：如果length后面不是直接接触内容，那么这个字段就是length之后还有几个字节之后才是正式的内容
        initialBytesToStrip：从左往右开始数，去掉几个字节的前缀，开始读取正式内容   如果想要读取length或者header之类的信息，可以设置为0

        info1  length  info2  content   要直接读取内容         1024，1，4，3，8
          1      4       3      16      要读取length之后的内容  2014，1，4，3，5

        */
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 1, 4, 3, 8),
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        embeddedChannel.writeInbound(packageInfo(byteBuf, "hello, world"));
        System.out.println(byteBuf.readerIndex() + "    " + byteBuf.writerIndex());
        byteBuf.clear();
        embeddedChannel.writeInbound(packageInfo(byteBuf, "hi!"));
    }

    public static ByteBuf packageInfo(ByteBuf byteBuf, String msg) {
        byteBuf.writeBytes("a".getBytes());
        byteBuf.writeInt(msg.length());
        byteBuf.writeBytes("bcd".getBytes());
        byteBuf.writeBytes(msg.getBytes());
        return byteBuf;
    }
}
