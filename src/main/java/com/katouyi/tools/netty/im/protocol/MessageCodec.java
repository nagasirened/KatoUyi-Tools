package com.katouyi.tools.netty.im.protocol;

import com.katouyi.tools.netty.im.config.Serializer;
import com.katouyi.tools.netty.im.config.SerializerConfig;
import com.katouyi.tools.netty.im.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Slf4j
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    /**
     * 编码器
     * @param ctx     ctx
     * @param message   消息
     * @param list      结果list
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> list) throws Exception {
        Serializer.Algorithm serializeAlgorithm = SerializerConfig.getSerializeAlgorithm();

        ByteBuf out = ctx.alloc().buffer();
        // 1. 4字节魔数
        out.writeBytes(new byte[] {1, 2, 3, 4});
        // 2. 1字节版本
        out.writeByte(1);
        // 3. 1字节序列化方式  jdk0,  json1  枚举有下标的，从0开始
        out.writeByte(serializeAlgorithm.ordinal());
        // 4. 1字节的指令类型
        out.writeByte(msg.getMessageType());
        // 5. 4个字节的请求序号
        out.writeInt(msg.getSequenceId());
        // 无意义，对齐填充
        // 6. 获取内容的字节数组
        out.writeByte(0xff);
        byte[] bytes = serializeAlgorithm.serialize(msg);
        // 7. 4 个字节长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
        list.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        // 1. 魔数
        int magicNum = in.readInt();
        // 2. 版本
        byte version = in.readByte();
        // 3. 序列化方式
        byte serializeAlgorithm = in.readByte();
        // 4. 消息类型
        byte messageType = in.readByte();
        // 5. 请求序列号
        int sequenceId = in.readInt();
        // 跳过padding
        in.readByte();
        // 6. 消息长度
        int length = in.readInt();
        byte[] bytes = new byte[length];
        // 读入实际内容数组
        in.readBytes(bytes, 0, length);
        // 根据获取的序列化算法
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializeAlgorithm];
        // 确定具体消息的类型 根据messageType获取对应的消息类型
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = algorithm.deserialize(bytes, messageClass);
        // 反序列化
        log.debug("magic:{}, version:{}, serializeAlgorithm:{}, message:{}, sequenceId:{}, msgLength:{}", magicNum, version, serializeAlgorithm, messageType, sequenceId, length);
        log.debug("{}", message);
        // 解析一条消息之后要装入 out 这个 List 以便给下一个 Handler 使用
        list.add(message);
    }
}
