package com.gatz.smarthomeapp.activity.login.view;

import java.io.IOException;

/**
 * Created by zhouh on 2017/2/22.
 */
public interface IinputView {
    String getUserPhone();

    String getUserPwd();

    void loginNameErr0();

    void loginNameErr1();

    void loginPwdErr0();

    void loginPwdErr1();

    void loginPwdErr2();

    void loginSucess(String infon, int code);

    void fail(IOException error, String msg);

    void loginIntentFailed();

    void loginAlready();

    void dialogShow();

    void dialogDisMiss(boolean isShowAc);
}
