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
     * SSL认证-服务端返回客户端非对称公钥
     */
    SSL_Hello((byte) 0x02),
    /**
     * SSL认证-客户端上送对称key
     */
    SSL_Exchange((byte) 0x03),
    /**
     * body为二进制的数据包
     */
    Binary((byte) 0x04),
    /**
     * body为string的数据包
     */
    String((byte) 0x05),
    ;

    private byte value;

    ProtoType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
