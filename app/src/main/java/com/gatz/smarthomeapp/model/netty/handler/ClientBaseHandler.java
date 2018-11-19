package com.gatz.smarthomeapp.model.netty.handler;

import android.os.Build;
import android.text.TextUtils;

import com.citic.zktd.saber.server.entity.json.ConnectRequest;
import com.citic.zktd.saber.server.entity.json.ConnectResponse;
import com.citic.zktd.saber.server.entity.json.JsonResponse;
import com.citic.zktd.saber.server.entity.json.PingRequest;
import com.citic.zktd.saber.server.entity.json.enums.DeviceType;
import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.citic.zktd.saber.server.entity.protocol.response.ReturnCode;
import com.gatz.smarthomeapp.model.netty.AppClient;
import com.gatz.smarthomeapp.model.netty.UserInfo;
import com.gatz.smarthomeapp.model.netty.common.AppConstants;
import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;
import com.gatz.smarthomeapp.model.netty.session.AppSession;
import com.gatz.smarthomeapp.model.netty.session.AppSessionManager;
import com.gatz.smarthomeapp.model.netty.session.AppStandardSessionManager;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.model.netty.utils.NettyUtils;
import com.gatz.smarthomeapp.model.netty.utils.Utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Description: 业务处理类的基类<br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月25日 下午3:55:41
 */
public class ClientBaseHandler extends SimpleChannelInboundHandler<JsonMessage> {
    AppClient appClient = AppClient.getInstance();
    private SendManager sendManager = SendManager.getInstance();
    private AppGlobal appGlobal = AppGlobal.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonMessage msg) throws Exception {
        if (!(msg instanceof JsonResponse)) {
            ctx.fireChannelRead(msg);
            return;
        }
        JsonResponse response = (JsonResponse) msg;
        ReturnCode returnCode = response.getReturnCode();
        switch (returnCode) {
            case SUCCESS:
                ctx.fireChannelRead(msg);
                break;
            case RECONNECT:
                if (null != NettyUtils.sendHeartTask) {
                    NettyUtils.sendHeartTask.stopTask();
                }
                NettyUtils.connectTag = false;
                appGlobal.setTempNettySaberIp(response.getSaberIp());
                appGlobal.setGateway_reconnect(true);
                sendManager.closeChannel();
                break;
            case SESSION_INVALID://无效的会话
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
            case GATEWAY_NOT_EXIST:
            case GATEWAY_DISCONNECT://智能网关已断开
                MsgUtils.dispatchEvent(ConnectResultEvent.GATEWAY_UNEXIST, MsgUtils.PROTOCOL.NULL);
                break;
            case SUCCESS_GATEWAY_INVALID:
                ctx.fireChannelRead(msg);
                MsgUtils.dispatchEvent(ConnectResultEvent.GATEWAY_UNEXIST, MsgUtils.PROTOCOL.NULL);
                break;
            case FAILURE://设备操作失败
                MsgUtils.dispatchEvent(ConnectResultEvent.FAILURE, MsgUtils.PROTOCOL.NULL);
                break;
            case SYSTEM_ERROR://系统错误
                break;
            case DEVICE_UNIQUE_ID_NOT_EMPTY:
                appClient.doConnect();
                break;
            default:
                break;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        try {
            UserInfo bean = appGlobal.getUserInfo();
            if (null != bean) {
                String sessionid = bean.getSessionId();
                if (appGlobal.isGateway_reconnect()) {
                    ConnectRequest connectRequest = new ConnectRequest();
                    if (!TextUtils.isEmpty(sessionid)) {
                        connectRequest.setSessionId(sessionid);
                    }
                    if (!TextUtils.isEmpty(bean.getUserName()))
                        connectRequest.setUserName(bean.getUserName());
                    if (!TextUtils.isEmpty(bean.getRoomid())) {
                        connectRequest.setRoomId(bean.getRoomid());
                    }
                    if (AppConstants.SYSTEM_V901.equals(Build.MODEL) || AppConstants.SYSTEM_V902.equals(Build.MODEL)) {
                        connectRequest.setDeviceType(DeviceType.ZEROIOT_PAD);
                    } else {
                        connectRequest.setDeviceType(DeviceType.ANDROID_PAD);
                    }
                    connectRequest.setSeq(AppConstants.SEQ.incrementAndGet());
                    sendManager.sendMessage(connectRequest);
                    appGlobal.setGateway_reconnect(false);
                } else {
                    PingRequest pingRequest = new PingRequest();
                    if (!TextUtils.isEmpty(sessionid)) {
                        pingRequest.setSessionId(sessionid);
                    }
                    if (!TextUtils.isEmpty(bean.getRoomid())) {
                        pingRequest.setRoomId(bean.getRoomid());
                    }
                    if (NettyUtils.sendHeartTask != null) {
                        pingRequest.setSeq(AppConstants.SEQ.incrementAndGet());
                        sendManager.sendMessage(pingRequest);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
