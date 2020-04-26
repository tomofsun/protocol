package com.wakzz.common.model;

import lombok.Data;

import java.util.Date;

@Data
public class ProtoParams {

    /**
     * SSL非对称加密公钥
     */
    private String sslPubKey;
    /**
     * SSL非对称加密私钥
     */
    private String sslPriKey;
    /**
     * SSL对称加密秘钥
     */
    private String sslCipher;

    private Date lastReadTime;
}
