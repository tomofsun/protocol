package com.wakzz.client;

import com.wakzz.common.model.ProtoBody;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProtoTemplate {

    private ExecutorService executorService;
    private ProtoConnectionManager connectionManager;

    public ProtoTemplate(String host, int port) {
        this(new ProtoConnectionManager(host, port));
    }

    public ProtoTemplate(ProtoConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.executorService = Executors.newCachedThreadPool();
    }

    public ProtoBody sendSyncRequest(ProtoBody request) throws InterruptedException {
        ProtoConnection connection = null;
        try {
            connection = connectionManager.getConnection();
            Channel channel = connection.getChannel();
            ArrayBlockingQueue<ProtoBody> queue = new ArrayBlockingQueue<>(1);
            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) {
                    channel.pipeline().remove(this);
                    ProtoBody protoBody = (ProtoBody) msg;
                    queue.add(protoBody);
                    ReferenceCountUtil.release(msg);
                }
            });
            channel.writeAndFlush(request);
            ProtoBody response = queue.poll(5, TimeUnit.SECONDS);
            connection.release();
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (connection != null) {
                connection.close();
            }
            throw e;
        }
    }

    public void sendAsyncRequest(ProtoBody request, Callback callback) {
        executorService.execute(() -> {
            try {
                ProtoBody response = sendSyncRequest(request);
                callback.onSuccess(request, response);
            } catch (Exception e) {
                callback.onFailure(request, e);
            }
        });
    }
}
