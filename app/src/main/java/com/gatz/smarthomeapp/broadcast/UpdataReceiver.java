package com.gatz.smarthomeapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gatz.smarthomeapp.bean.UserInfoBean;
import com.gatz.smarthomeapp.service.UpDataAppService;
import com.gatz.smarthomeapp.utils.DbUttil;

/**
 * Created by zhouh on 2017/6/16.
 */
public class UpdataReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("UPDATA_ALARM_ACTION")) {
            UserInfoBean bean = DbUttil.getUser(context);
            //TODO 每隔15个小时监测一次
            if (bean != null) {
                Intent it = new Intent();
                it.putExtra("cId", bean.getCommunityId());
                it.putExtra("sId", bean.getSessionId());
                it.setClass(context, UpDataAppService.class);
                context.startService(it);
            }
        }
    }
}
