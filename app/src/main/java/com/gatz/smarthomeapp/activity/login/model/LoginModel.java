package com.gatz.smarthomeapp.activity.login.model;

import com.gatz.smarthomeapp.bean.UserInfoBean;
import com.gatz.smarthomeapp.model.http.ObserverCallBack;
import com.gatz.smarthomeapp.model.netty.UserInfo;
import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.utils.HttpUtil;
import com.gatz.smarthomeapp.utils.UrlUtils;
import com.gatz.smarthomeapp.utils.Utils;

/**
 * Created by zhouh on 2017/2/22.
 */
public class LoginModel {
    private ObserverCallBack callBack;
    private UserInfoBean bean;

    public LoginModel(ObserverCallBack callBack) {
        this.callBack = callBack;
    }

    public void setBean(UserInfoBean bean) {
        this.bean = bean;
    }

    public void login(String roomId, String pwd) {
        HttpUtil.doLogin(Utils.getMacAddr(), roomId, pwd,
                UrlUtils.TERMINAL, callBack, UrlUtils.REQUEST_LOGIN_CODE);
    }

    public void getRooms() {
        HttpUtil.getRooms(bean.getSessionId(), bean.getRoomId(),
                callBack, UrlUtils.FINDBEDROOM_CODE);
    }

    public void getDevices(String bedRoomId) {
        HttpUtil.getDevices(bean.getSessionId(), bean.getRoomId(), bedRoomId,
                callBack, UrlUtils.FINDEPBID_CODE);
    }

    public void getConnectIp() {
        HttpUtil.getConnectIp(bean.getSessionId(), bean.getRoomId(),
                callBack, UrlUtils.GET_CONNECTIP_CODE);
    }

    public static void setNettyInfo(UserInfoBean bean) {
        //配置Netty所用的参数
        UserInfo userInfo = new UserInfo();
        userInfo.setSessionId(bean.getSessionId());
        userInfo.setUserName(bean.getUserName());
        userInfo.setRoomid(bean.getRoomId());
        userInfo.setUnitId(bean.getUnitId());
        AppGlobal.getInstance().setUserInfo(userInfo);
    }
}
