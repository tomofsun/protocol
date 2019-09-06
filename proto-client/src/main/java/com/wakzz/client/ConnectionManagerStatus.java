package com.wakzz.client;

public enum  ConnectionManagerStatus {

    /**
     * 运行中
     * 允许创建新连接
     */
    Running(),
    /**
     * 已停止
     * 禁止创建新连接,并逐步关闭已创建的连接
     */
    Shutdown()
    ;

    ConnectionManagerStatus(){
    }
}
