package com.wakzz.common.context;

/**
 * 报文类型
 */
public enum ProtoType {

    /**
     * 心跳包请求ping
     */
    Ping((byte) 0x00),
    /**
     * 心跳包返回pong
     */
    Pong((byte) 0x01),
    /**
     * body为二进制的数据包
     */
    Binary((byte) 0x02),
    /**
     * body为string的数据包
     */
    String((byte) 0x03),
    ;

    private byte value;

    ProtoType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
