package com.wakzz.client;

import com.wakzz.common.model.Callback;
import com.wakzz.common.model.DefaultFuture;
import com.wakzz.common.model.ProtoBody;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;

@Slf4j
public class ProtoTemplate implements Closeable {

    private ProtoConnectionManager connectionManager;

    public ProtoTemplate(String host, int port) {
        this(new ProtoConnectionManager(host, port));
    }

    public ProtoTemplate(ProtoConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public ProtoBody sendSyncRequest(ProtoBody request) {
        ProtoConnection connection = null;
        try {
            connection = connectionManager.getConnection();
            Channel channel = connection.getChannel();
            DefaultFuture future = DefaultFuture.newFuture(channel, request, 5_000_000);
            future.doSend();
            ProtoBody response = future.get();
            connection.release();
            return response;
        } catch (Exception e) {
            if (connection != null) {
                connection.close();
            }
            throw new RuntimeException(e);
        }
    }

    public void sendAsyncRequest(ProtoBody request, Callback callback) {

        ProtoConnection connection = null;
        try {
            connection = connectionManager.getConnection();
            Channel channel = connection.getChannel();
            DefaultFuture future = DefaultFuture.newFuture(channel, request, 5000);
            future.setCallback(callback);
            channel.writeAndFlush(request);
            connection.release();
        } catch (Exception e) {
            if (connection != null) {
                connection.close();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        this.connectionManager.close();
    }
}
