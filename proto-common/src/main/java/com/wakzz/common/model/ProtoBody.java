package com.wakzz.common.model;

import lombok.Data;

@Data
public class ProtoBody {

    /**
     * 魔法数(4字节)
     * 固定为0x55776688
     */
    private byte[] magic;
    /**
     * 版本号(1字节)
     */
    private byte version;
    /**
     * 报文类型(1字节)
     */
    private byte type;
    /**
     * 数据序列化算法(1字节)
     */
    private byte serializer;
    /**
     * 预留字段(1字节)
     */
    private byte todo;
    /**
     * 请求序列号(4字节)
     */
    private int requestId;
    /**
     * body报文长度(4字节)
     */
    private int length;
    /**
     * 报文内容
     */
    private byte[] body;
    /**
     * 校验和(4字节)
     */
    private byte[] checksum;

}
