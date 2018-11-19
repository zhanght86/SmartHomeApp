/*
 * Copyright (C) 2017 Gatz.
 * All rights, including trade secret rights, reserved.
 */
package com.gatz.smarthomeapp.activity.login.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.activity.home.HomeActivity;
import com.gatz.smarthomeapp.activity.login.presenter.LoginCompl;
import com.gatz.smarthomeapp.activity.setup.view.NetworkActivity;
import com.gatz.smarthomeapp.base.MyAppliCation;
import com.gatz.smarthomeapp.utils.ToastUtil;
import com.gatz.smarthomeapp.utils.Utils;
import com.gatz.smarthomeapp.view.WaitProgressDialog;

import java.io.IOException;

public class LoginActivity extends Activity implements View.OnClickListener, IinputView {
    private static final String TAG = "LoginActivity-";
    private EditText lgPhoneEdtText;
    private EditText lgPwdEdtText;
    private Button lgLoginBtn;
    private RelativeLayout loginLayout;

    private LoginCompl loginCompl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.showLogE(TAG, "==onCreate==");
        setContentView(R.layout.activity_main);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        MyAppliCation.getInstance().addActivity(this);
        initData();
        initView();
    }

    private void initData() {
        loginCompl = new LoginCompl(this, getApplicationContext());
        boolean isSetUser = getIntent().getBooleanExtra("user_set", false);
        loginCompl.startLoginPresent(isSetUser);
    }

    private void initView() {
        WaitProgressDialog.init(this, false);
        lgLoginBtn = (Button) findViewById(R.id.login_btn);
        lgPhoneEdtText = (EditText) findViewById(R.id.login_phone_edtText);
        lgPwdEdtText = (EditText) findViewById(R.id.login_pwd_edtText);
        loginLayout = (RelativeLayout) findViewById(R.id.rl_login);
        ImageView networkIv = (ImageView) findViewById(R.id.iv_network);
        lgLoginBtn.setOnClickListener(this);
        loginLayout.setOnClickListener(this);
        networkIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, NetworkActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public String getUserPhone() {
        return lgPhoneEdtText.getText().toString();
    }

    @Override
    public String getUserPwd() {
        return lgPwdEdtText.getText().toString();
    }

    @Override
    public void loginNameErr0() {
        ToastUtil.makeLongText(getApplicationContext(), getString(R.string.phone_err0));
    }

    @Override
    public void loginNameErr1() {
        ToastUtil.makeLongText(getApplicationContext(), getString(R.string.phone_err1));
    }

    @Override
    public void loginPwdErr0() {
        ToastUtil.makeLongText(getApplicationContext(), getString(R.string.phone_err1));
    }

    @Override
    public void loginPwdErr1() {
        ToastUtil.makeLongText(getApplicationContext(), getString(R.string.pwd_err1));
    }

    @Override
    public void loginPwdErr2() {
        ToastUtil.makeLongText(getApplicationContext(), getString(R.string.pwd_err2));
    }

    @Override
    public void loginSucess(String infon, int code) {
        //stratHomeAc();
    }

    @Override
    public void fail(IOException error, String msg) {
        Message ms = new Message();
        ms.what = 1;
        ms.obj = msg;
        handler.sendMessage(ms);
    }

    @Override
    public void loginIntentFailed() {
        handler.sendEmptyMessage(2);
    }

    @Override
    public void loginAlready() {
        stratHomeAc();
    }

    @Override
    public void dialogShow() {
        WaitProgressDialog.vshow();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 0.05f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void dialogDisMiss(boolean isShowAc) {
        WaitProgressDialog.dimiss();
        Message ms = new Message();
        ms.obj = isShowAc;
        ms.what = 0;
        handler.sendMessage(ms);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == 0) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness = 0.8f;
                getWindow().setAttributes(lp);
                Utils.hideBottomUIMenu(LoginActivity.this);
                boolean is = (boolean) msg.obj;
                if (is) {
                    stratHomeAc();
                }
            } else if (what == 1) {
                String ms = (String) msg.obj;
                ToastUtil.makeLongText(getApplicationContext(), ms);
            } else if (what == 2) {
                ToastUtil.makeLongText(getApplicationContext(), getString(R.string.net_err));
            }
        }
    };

    private void stratHomeAc() {
        Intent intent = new Intent();
        intent.setClass(this, HomeActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.login_btn:
                loginCompl.login(getApplicationContext());
                break;
            case R.id.rl_login:
                View v = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                break;
            default:
                break;
        }
    }
}
