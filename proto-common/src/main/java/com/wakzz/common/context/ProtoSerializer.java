package com.wakzz.common.context;

public enum ProtoSerializer {
    /**
     * body为二进制的数据包
     */
    Binary((byte) 0x00),
    /**
     * body为string的数据包
     */
    String((byte) 0x01),
    ;

    private byte value;

    ProtoSerializer(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static ProtoSerializer valueOf(byte value) {
        ProtoSerializer[] arrays = values();
        for (ProtoSerializer serializer : arrays) {
            if (serializer.getValue() == value) {
                return serializer;
            }
        }
        return null;
    }
}
