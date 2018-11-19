package com.gatz.smarthomeapp.model.http;

import java.io.IOException;

/**
 * Created by Debby on 2017/1/12.
 */
public interface ObserverCallBack {
    void onSuccessHttp(String responseInfo, int resultCode);

    void onFailureHttp(IOException e, int resultCode);

    void setData(Object obj);
}
