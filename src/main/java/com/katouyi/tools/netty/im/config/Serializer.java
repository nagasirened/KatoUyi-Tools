package com.katouyi.tools.netty.im.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 自定义序列化和反序列化对象
 */
public interface Serializer {

    /**
     * 序列化
     */
    <T> byte[] serialize(T object);

    /**
     * 反序列化
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

    @Slf4j
    enum Algorithm implements Serializer{

        JAVA{
            @Override
            public <T> byte[] serialize(T object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return bos.toByteArray();
                } catch (Exception e) {
                    throw new RuntimeException("JDK方式序列化失败", e);
                }
            }

            @Override
            public <T> T deserialize(byte[] bytes, Class<T> clazz) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return (T) ois.readObject();
                } catch (Exception e) {
                    throw new RuntimeException("JDK方式反序列化失败", e);
                }
            }
        },

        JSON {
            @Override
            public <T> byte[] serialize(T object) {
                String jsonStr = com.alibaba.fastjson.JSON.toJSONString(object);
                return jsonStr.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public <T> T deserialize(byte[] bytes, Class<T> clazz) {
                String info = new String(bytes, StandardCharsets.UTF_8);
                return com.alibaba.fastjson.JSON.parseObject(info, clazz);
            }
        }

        ;
    }
}
