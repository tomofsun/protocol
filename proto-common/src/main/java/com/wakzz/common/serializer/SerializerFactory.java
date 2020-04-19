package com.wakzz.common.serializer;

import com.wakzz.common.context.ProtoSerializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerFactory {

    private static final Map<ProtoSerializer, StringSerializer> map = new ConcurrentHashMap<>();

    static {
        map.put(ProtoSerializer.String, new StringSerializer());
    }

    public static StringSerializer getSerializer(ProtoSerializer protoSerializer) {
        return map.get(protoSerializer);
    }

}
