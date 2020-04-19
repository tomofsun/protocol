package com.wakzz.common.serializer;

import com.wakzz.common.context.ProtoSerializer;

public interface Serializer {

    ProtoSerializer getProtoType();

    byte[] serialize(Object object);

    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
