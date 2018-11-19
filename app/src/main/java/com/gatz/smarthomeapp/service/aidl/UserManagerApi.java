package com.gatz.smarthomeapp.service.aidl;

import android.os.RemoteException;

import com.gatz.smarthomeapp.IUserManagerApi;
import com.gatz.smarthomeapp.utils.Utils;

/**
 * Created by zhouh on 2017/3/3.
 */
public class UserManagerApi extends IUserManagerApi.Stub{
    private static final String TAG = "UserManagerApi";

    @Override
    public String getSessionId() throws RemoteException {
        Utils.showLogE(TAG, "getSessionId.............");
        return "getSessionId";
    }
}
