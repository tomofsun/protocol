//package com.wakzz.client;
//
//import com.wakzz.common.model.ProtoBody;
//import com.wakzz.common.utils.ProtoBodyUtils;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class HelloClient {
//
//    public static void main(String[] args) throws Exception {
//        ProtoTemplate protoTemplate = new ProtoTemplate("127.0.0.1", 8000);
//        for (int i = 0; i < 1; i++) {
////            protoTemplate.sendAsyncRequest(ProtoBodyUtils.valueOf("hello world" + i), new Callback() {
////                @Override
////                public void onFailure(ProtoBody request, Exception e) {
////                    log.error(e.getMessage(), e);
////                }
////
////                @Override
////                public void onSuccess(ProtoBody request, ProtoBody response) {
////                    log.info("response2: {}", new String(response.getBody()));
////                }
////            });
//            StringBuilder builder = new StringBuilder();
//            for (int j = 0; j < 100; j++) {
//                builder.append("hello world");
//            }
//            ProtoBody response = protoTemplate.sendSyncRequest(ProtoBodyUtils.valueOf(builder.toString() + i));
//            log.info("response2: {}", new String(response.getBody()));
//        }
//        Thread.sleep(10_000);
//        protoTemplate.close();
//    }
//}