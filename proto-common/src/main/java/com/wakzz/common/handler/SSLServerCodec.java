package com.wakzz.common.handler;

import com.wakzz.common.context.ProtoType;
import com.wakzz.common.model.ProtoBody;
import com.wakzz.common.utils.DESUtils;
import com.wakzz.common.utils.ProtoBodyUtils;
import com.wakzz.common.utils.RSASignUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Slf4j
@ChannelHandler.Sharable
public class SSLServerCodec extends MessageToMessageCodec<ProtoBody, ProtoBody> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        // 给channel生成rsa非对称key
        Pair<RSAPublicKey, RSAPrivateKey> pair = RSASignUtil.initKey();
        String publicKeyValue = Base64.encodeBase64String(pair.getKey().getEncoded());
        String privateKeyValue = Base64.encodeBase64String(pair.getValue().getEncoded());

        Attribute<String> publicKeyAttr = ctx.channel().attr(AttributeKey.valueOf("_publicKeyValue"));
        publicKeyAttr.set(publicKeyValue);
        Attribute<String> privateKeyAttr = ctx.channel().attr(AttributeKey.valueOf("_privateKeyValue"));
        privateKeyAttr.set(privateKeyValue);
        super.channelRegistered(ctx);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtoBody protoBody, List<Object> out) throws Exception {
        Attribute<String> encryptKeyAttr = ctx.channel().attr(AttributeKey.valueOf("_encryptKeyValue"));
        String encryptKey = encryptKeyAttr.get();
        protoBody.setBody(DESUtils.encrypt(encryptKey, protoBody.getBody()));
        out.add(protoBody);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ProtoBody protoBody, List<Object> out) throws Exception {
        Attribute<String> encryptKeyAttr = ctx.channel().attr(AttributeKey.valueOf("_encryptKeyValue"));
        String encryptKey = encryptKeyAttr.get();

        // 已完成非对称加密认证,通过对称key解密
        if (ProtoType.SSL_Exchange.getValue() != protoBody.getType() && StringUtils.isNotBlank(encryptKey)) {
            System.out.println("server des: " + encryptKey);
            protoBody.setBody(DESUtils.decrypt(encryptKey, protoBody.getBody()));
            out.add(protoBody);
            return;
        }

        // 未完成非对称加密认证
        if (ProtoType.SSL_Exchange.getValue() == protoBody.getType()) {
            // 客户端上送对称key
            Attribute<String> privateKeyAttr = ctx.channel().attr(AttributeKey.valueOf("_privateKeyValue"));
            String privateKeyValue = privateKeyAttr.get();
            encryptKey = new String(RSASignUtil.decryptByPvk(protoBody.getBody(), privateKeyValue));
            encryptKeyAttr.set(encryptKey);
        } else {
            // 服务端返回非对称公钥给客户端
            Attribute<String> publicKeyAttr = ctx.channel().attr(AttributeKey.valueOf("_publicKeyValue"));
            String publicKeyValue = publicKeyAttr.get();
            ctx.writeAndFlush(ProtoBodyUtils.valueOf(ProtoType.SSL_Hello, publicKeyValue.getBytes()));
        }
    }
}