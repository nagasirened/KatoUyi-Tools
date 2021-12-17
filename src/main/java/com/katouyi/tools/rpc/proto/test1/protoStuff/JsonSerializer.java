package com.katouyi.tools.rpc.proto.test1.protoStuff;

import com.alibaba.fastjson.JSON;

import java.nio.charset.StandardCharsets;

public class JsonSerializer implements Serializer{

    @Override
    public <T> byte[] serialize(T object) {
        String obj = JSON.toJSONString(object);
        return obj.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        String json = new String(bytes, StandardCharsets.UTF_8);
        return JSON.parseObject(json, clazz);
    }
}
