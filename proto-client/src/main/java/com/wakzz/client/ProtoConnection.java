//package com.wakzz.client;
//
//import io.netty.channel.Channel;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.Closeable;
//
//@Data
//@Slf4j
//public class ProtoConnection implements Closeable {
//
//    private Channel channel;
//    private ProtoConnectionManager connectionManager;
//
//    public void close() {
//        connectionManager.closeConnection(this);
//    }
//
//    public void release(){
//        try {
//            connectionManager.release(this);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}
