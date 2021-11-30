package com.katouyi.tools.proto.test1.protoStuff.test;

import com.alibaba.fastjson.JSON;
import com.katouyi.tools.proto.test1.protoStuff.JdkSerializer;
import com.katouyi.tools.proto.test1.protoStuff.JsonSerializer;
import com.katouyi.tools.proto.test1.protoStuff.ProtoStuffSerializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestClass {

    public static void main(String[] args) {
        // 1.获取对象
        User user = User.testOne();

        // 2.jdk序列化  对象必须实现Serializable接口
        byte[] jdkSerialize = new JdkSerializer().serialize(user);
        log.info("jdkSerialize, length: {}, content: {}", jdkSerialize.length, jdkSerialize);

        // 3.fastJson序列化
        byte[] jsonSerialize = new JsonSerializer().serialize(user);
        log.info("jsonSerialize, length: {}, content: {}", jsonSerialize.length, jsonSerialize);

        // 4.protoStuff序列化
        byte[] protoStuffSerialize = new ProtoStuffSerializer().serialize(user);
        log.info("protoStuffSerialize, length: {}, content: {}", protoStuffSerialize.length, protoStuffSerialize);

        // 反序列化打印
        log.info("jdkDeserialize, object: {}", JSON.toJSONString(new JdkSerializer().deserialize(jdkSerialize, User.class)));
        log.info("jsonDeserialize, object: {}", JSON.toJSONString(new JsonSerializer().deserialize(jsonSerialize, User.class)));
        log.info("protoStuffDeserialize, object: {}", JSON.toJSONString(new ProtoStuffSerializer().deserialize(protoStuffSerialize, User.class)));
    }
}
