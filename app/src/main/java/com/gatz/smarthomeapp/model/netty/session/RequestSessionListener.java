package com.gatz.smarthomeapp.model.netty.session;

import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.model.netty.utils.NettyUtils;
import com.gatz.smarthomeapp.utils.Utils;

import io.netty.channel.Channel;

/**
 * Created by Debby on 2016/12/22.
 */
public class RequestSessionListener implements AppSessionListener {

    private static RequestSessionListener instance = null;

    private RequestSessionListener() {
    }

    public static RequestSessionListener getInstance() {
        if (instance == null) {
            instance = new RequestSessionListener();
        }
        return instance;
    }

    //TODO 局域网不需要处理这个监听
    @Override
    public void sessionIde(AppSession session) {
        Utils.showLogE("RequestSessionListener", "request会话超时" + session.getId());
        Channel channel = SendManager.getInstance().getChannel();
        if(null != channel){
            SendManager.getInstance().closeChannel();
            channel = null;
            NettyUtils.sendHeartTask.stopTask();
            MsgUtils.dispatchEvent(ConnectResultEvent.CONNECT_FAILURE, MsgUtils.PROTOCOL.NULL);
        }
    }

    @Override
    public void attributeAdded(AppSession session, String name, Object value) {

    }

    @Override
    public void sessionRemoved(AppSession session) {

    }
}
