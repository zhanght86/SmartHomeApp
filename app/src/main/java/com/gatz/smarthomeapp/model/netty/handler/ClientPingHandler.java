package com.gatz.smarthomeapp.model.netty.handler;

import com.citic.zktd.saber.server.entity.json.PingResponse;
import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.citic.zktd.saber.server.entity.protocol.response.ReturnCode;
import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.session.AppSession;
import com.gatz.smarthomeapp.model.netty.session.AppStandardSessionManager;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.model.netty.utils.NettyUtils;
import com.gatz.smarthomeapp.model.netty.utils.Utils;

import io.netty.channel.ChannelHandlerContext;

/**
 * Description: 接入<br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月25日 下午3:22:16
 */
public class ClientPingHandler extends ClientBaseHandler {
    public static final String TAG = "ClientPingHandler";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonMessage msg)
            throws Exception {
        if (!(msg instanceof PingResponse)) {
            ctx.fireChannelRead(msg);
            return;
        }

        AppSession session = null;
        if(null != msg.getSessionId()) {
            session = AppStandardSessionManager.getInstance().
                    getSession(AppGlobal.getInstance().getUserInfo().getSessionId());
        }
        PingResponse pingResponse = (PingResponse) msg;
        ReturnCode returnCode = pingResponse.getReturnCode();
        Utils.showErrorLog(TAG, "心跳响应...." + pingResponse.toString());
        switch (returnCode) {
            case SESSION_INVALID:
                if (null != NettyUtils.sendHeartTask) {
                    NettyUtils.sendHeartTask.stopTask();
                }
                NettyUtils.connectTag = false;
                AppSession session1 = AppStandardSessionManager.getInstance().
                        getSession(AppGlobal.getInstance().getUserInfo().getSessionId());
                if (null != session1) {
                    session1.invalidate();
                }
                MsgUtils.dispatchEvent(ConnectResultEvent.SESSION_INVALID, MsgUtils.PROTOCOL.NULL);
                break;
            case SUCCESS:
                if (session != null) {
                    session.refresh();
                } else {
                    AppStandardSessionManager appStandardSessionManager = AppStandardSessionManager.getInstance();
                    appStandardSessionManager.createSession(AppGlobal.getInstance().
                            getUserInfo().getSessionId(), 80 * 1000);
                }
                NettyUtils.connectTag = true;
                MsgUtils.dispatchEvent(ConnectResultEvent.PING_SUCCESS, MsgUtils.PROTOCOL.NULL);
                break;
            case GATEWAY_NOT_EXIST:
                if (session != null) {
                    session.refresh();
                } else {
                    AppStandardSessionManager appStandardSessionManager = AppStandardSessionManager.getInstance();
                    appStandardSessionManager.createSession(AppGlobal.getInstance().
                            getUserInfo().getSessionId(), 80 * 1000);
                }
                NettyUtils.connectTag = true;
                MsgUtils.dispatchEvent(ConnectResultEvent.GATEWAY_UNEXIST, MsgUtils.PROTOCOL.NULL);
                break;
            default:
                break;
        }
    }
}
