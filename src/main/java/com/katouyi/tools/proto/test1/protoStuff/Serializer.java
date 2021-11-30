package com.katouyi.tools.proto.test1.protoStuff;

public interface Serializer {

    <T> byte[] serialize(T object);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
