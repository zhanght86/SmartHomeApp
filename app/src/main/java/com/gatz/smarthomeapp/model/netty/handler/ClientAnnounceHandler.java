package com.gatz.smarthomeapp.model.netty.handler;

import com.citic.zktd.saber.server.entity.json.announce.GatewayAnnounceMessage;
import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.citic.zktd.saber.server.entity.protocol.enums.ConnectStatus;
import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;
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
public class ClientAnnounceHandler extends ClientBaseHandler {
    public static final String TAG = "ClientAnnounceHandler";
    private SendManager sendManager = SendManager.getInstance();
    private AppGlobal appGlobal = AppGlobal.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonMessage msg) throws Exception {

        if (!(msg instanceof GatewayAnnounceMessage)) {
            ctx.fireChannelRead(msg);
            return;
        }
        GatewayAnnounceMessage gateWayAnnounceMessage = (GatewayAnnounceMessage) msg;
        Utils.showErrorLog(TAG, gateWayAnnounceMessage.toString());
        String roomId = gateWayAnnounceMessage.getRoomId();
        ConnectStatus cs = gateWayAnnounceMessage.getConnectStatus();
        String str = "";
        switch (cs) {
            case CONNECTED:
                str = "智能网关:" + roomId + "连接";
//                if (null != NettyUtils.sendHeartTask) {
//                    NettyUtils.sendHeartTask.stopTask();
//                }
                MsgUtils.dispatchEvent(ConnectResultEvent.CONNECT_SUCCESS, MsgUtils.PROTOCOL.NULL);
//                appGlobal.setTempNettySaberIp(gateWayAnnounceMessage.getSaberIp());
//                sendManager.closeChannel();
//                appGlobal.setGateway_reconnect(true);
                Utils.showErrorLog(TAG, str);
                break;
            case DISCONNECT:
                str = "智能网关:" + roomId + "断开";
                Utils.showErrorLog(TAG, str);
                MsgUtils.dispatchEvent(ConnectResultEvent.GATEWAY_UNEXIST, MsgUtils.PROTOCOL.NULL);
                break;
            default:
                break;
        }
    }
}
