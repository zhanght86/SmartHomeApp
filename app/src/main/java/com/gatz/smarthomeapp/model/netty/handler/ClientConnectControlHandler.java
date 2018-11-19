package com.gatz.smarthomeapp.model.netty.handler;

import com.citic.zktd.saber.server.entity.json.ConnectResponse;
import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.citic.zktd.saber.server.entity.protocol.response.ReturnCode;
import com.gatz.smarthomeapp.base.MyAppliCation;
import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;
import com.gatz.smarthomeapp.model.netty.session.AppStandardSessionManager;
import com.gatz.smarthomeapp.model.netty.task.SendHeartTask;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.model.netty.utils.NettyUtils;
import com.gatz.smarthomeapp.model.netty.utils.Utils;
import com.gatz.smarthomeapp.utils.DbUttil;

import io.netty.channel.ChannelHandlerContext;

/** 
 * Description: 接入<br/>
 * Copyright (c) 2015, 中信国安 
 *
 * @author david 
 * @date 2015年8月25日 下午3:22:16 
 */
public class ClientConnectControlHandler extends ClientBaseHandler {
	private static final String TAG = "ClientConnectControlHandler";
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, JsonMessage msg) throws Exception {
		if (!(msg instanceof ConnectResponse)) {
			ctx.fireChannelRead(msg);
			return;
		}
		ConnectResponse response = (ConnectResponse)msg;
		Utils.showErrorLog(TAG,"{}接收到的响应为={}"+ this.getClass().getSimpleName()+response);
		ReturnCode returnCode = response.getReturnCode();
		NettyUtils.connectTag = true;
		AppStandardSessionManager appStandardSessionManager = AppStandardSessionManager.getInstance();
		appStandardSessionManager.createSession(AppGlobal.getInstance().
				getUserInfo().getSessionId(), response.getTimeout() * 1000);
		switch (returnCode) {
		case SUCCESS_GATEWAY_INVALID:
			updataPingTime(response);
			NettyUtils.sendHeart(MyAppliCation.getInstance().getApplicationContext());
			break;
		case SUCCESS:
			MsgUtils.dispatchEvent(ConnectResultEvent.CONNECT_SUCCESS, MsgUtils.PROTOCOL.NULL);
			updataPingTime(response);
			NettyUtils.sendHeart(MyAppliCation.getInstance().getApplicationContext());
			break;
		default:
			break;
		}
	}

	/**
	 * 更新ping的时间参数
	 * @param response
     */
	private void updataPingTime(ConnectResponse response){
		DbUttil.updataPingTime(MyAppliCation.getInstance().getApplicationContext(),
				response.getIntervalTime(),
				response.getTimeout());
	}
}
