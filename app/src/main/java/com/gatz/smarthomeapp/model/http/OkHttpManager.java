package com.gatz.smarthomeapp.model.http;

import com.gatz.smarthomeapp.utils.Utils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Debby on 2017/1/17.
 */
public class OkHttpManager {
    private static final String TAG = "OkHttpManager";
    private static OkHttpManager okHttpManager;
    private OkHttpClient okHttpClient;

    public synchronized static OkHttpManager getInstance() {
        if (null == okHttpManager) {
            okHttpManager = new OkHttpManager();
        }
        return okHttpManager;
    }


    private OkHttpManager() {
        okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
    }

    public void GET(String url, ObserverCallBack callBack, int resultCode) {
        getRequest(url, callBack, resultCode);
    }

    public void POST(String url, HashMap<String, String> params, ObserverCallBack callBack, int resultCode) {
        postRequest(url, params, callBack, resultCode);
    }

    private void getRequest(String url, ObserverCallBack callBack, int resultCode) {
        Utils.showLogE(TAG, "get-----" + url);
        Request request = new Request.Builder().url(url).build();
        handleResponse(request, callBack, resultCode);
    }

    private void postRequest(String url, HashMap<String, String> params, ObserverCallBack callBack, int resultCode) {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        Utils.showLogE(TAG, "post-----" + url);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            formEncodingBuilder.add(key, value);
            Utils.showLogE(TAG, key + "-----" + value);
        }
        RequestBody requestBody = formEncodingBuilder.build();
        Request request = new Request.Builder().post(requestBody).url(url).build();
        handleResponse(request, callBack, resultCode);
    }


    private void handleResponse(Request request, final ObserverCallBack callBack, final int resultCode) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (null != callBack) {
                    callBack.onFailureHttp(e, resultCode);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (null != callBack) {
                    callBack.onSuccessHttp(response.body().string(), resultCode);
                }
            }
        });
    }


}
