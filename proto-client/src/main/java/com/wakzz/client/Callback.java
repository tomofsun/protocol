package com.wakzz.client;

import com.wakzz.common.model.ProtoBody;

public interface Callback {

    void onFailure(ProtoBody request, Exception  e);

    void onSuccess(ProtoBody request, ProtoBody response);

}
