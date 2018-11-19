package com.gatz.smarthomeapp.utils;

import com.gatz.smarthomeapp.model.netty.session.AppSession;
import com.gatz.smarthomeapp.model.netty.session.AppSessionListener;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;

public class AppSessionListenerImpl implements AppSessionListener {
	private static final String TAG = "VoiceSessionListenerImpl";
	private static AppSessionListenerImpl instance = null;
	private AppSessionListenerImpl() {
	}

	public static AppSessionListenerImpl getInstance() {
		if (instance == null) {
			instance = new AppSessionListenerImpl();
		}
		return instance;
	}

	@Override
	public void sessionIde(AppSession session) {
		Utils.showLogE(TAG,"{}会话超时"+session.getId());
		//语音识别会话超时停止识别引擎
		MsgUtils.dispatchEvent(VoiceUtils.VOICE_SESSION,MsgUtils.PROTOCOL.NULL);
	}

	@Override
	public void attributeAdded(AppSession session, String name, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionRemoved(AppSession session) {
		// TODO Auto-generated method stub
		
	}
}
