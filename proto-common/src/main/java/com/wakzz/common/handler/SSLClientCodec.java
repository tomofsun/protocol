//package com.wakzz.common.handler;
//
//import com.wakzz.common.context.ProtoType;
//import com.wakzz.common.model.ProtoBody;
//import com.wakzz.common.utils.DESUtils;
//import com.wakzz.common.utils.ProtoBodyUtils;
//import com.wakzz.common.utils.RSASignUtil;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.MessageToMessageCodec;
//import io.netty.util.Attribute;
//import io.netty.util.AttributeKey;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.List;
//
//@Slf4j
//@ChannelHandler.Sharable
//public class SSLClientCodec extends MessageToMessageCodec<ProtoBody, ProtoBody> {
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush(ProtoBodyUtils.valueOf(ProtoType.Ping, null));
//        super.channelActive(ctx);
//    }
//
//    @Override
//    protected void encode(ChannelHandlerContext ctx, ProtoBody protoBody, List<Object> out) throws Exception {
//        Attribute<String> encryptKeyAttr = ctx.channel().attr(AttributeKey.valueOf("_encryptKeyValue"));
//
//        // 已SSL认证完成,通过对称key解密报文
//        String encryptKey = encryptKeyAttr.get();
//        if (StringUtils.isNotBlank(encryptKey)) {
//            protoBody.setBody(DESUtils.decrypt(encryptKey, protoBody.getBody()));
//        }
//        out.add(protoBody);
//    }
//
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ProtoBody protoBody, List<Object> out) throws Exception {
//        Attribute<String> encryptKeyAttr = ctx.channel().attr(AttributeKey.valueOf("_encryptKeyValue"));
//        String encryptKey = encryptKeyAttr.get();
//        if (ProtoType.SSL_Hello.getValue() == protoBody.getType()) {
//            // 生成对称加密key上送给服务端
//            String pukValue = new String(protoBody.getBody());
//            if (StringUtils.isBlank(encryptKey)) {
//                encryptKey = RandomStringUtils.randomAlphanumeric(16);
//            }
//            System.out.println("client des: " + encryptKey);
//            encryptKeyAttr.set(encryptKey);
//            byte[] data = RSASignUtil.encryptByPuk(encryptKey.getBytes(), pukValue);
//            ctx.writeAndFlush(ProtoBodyUtils.valueOf(ProtoType.SSL_Exchange, data));
//            return;
//        }
//
//        // 已SSL认证完成,通过对称key解密报文
//        if (StringUtils.isNotBlank(encryptKey)) {
//            protoBody.setBody(DESUtils.decrypt(encryptKey, protoBody.getBody()));
//        }
//        out.add(protoBody);
//    }
//}