package com.wakzz.common.model;

public interface Callback {

    void onFailure(ProtoBody request, Exception  e);

    void onSuccess(ProtoBody request, ProtoBody response);

}
