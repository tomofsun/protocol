package com.wakzz.common.serializer;

import com.wakzz.common.context.ProtoSerializer;

public class StringSerializer implements Serializer {

    private static final ProtoSerializer serializerType = ProtoSerializer.String;

    @Override
    public ProtoSerializer getProtoType() {
        return serializerType;
    }

    @Override
    public byte[] serialize(Object object) {
        if (object == null)
            return new byte[0];
        return object.toString().getBytes();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null) {
            return null;
        }
        return (T) new String(bytes);
    }
}
