package com.gatz.smarthomeapp.activity.voice;

public interface HandlerCode {

	/**语音识别*/
	 int RECOGNIZE = 100010;
	/**智能问答提示*/
	 int SHOW_ANSWER = 100011;
	/**开始听写*/
	 int START_LISTENING = 100016;
	/**停止听写*/
	 int STOP_LISTENINING = 100017;
	/**显示录音动画*/
	 int SHOW_RECORD = 100019;
	/**隐藏录音动画*/
	 int HIDE_RECORD = 100020;
	/**文本类*/
	public int TEXT = 100000;
	/**链接类*/
	public int URL = 200000;
	/**新闻类*/
	public int NEWS = 302000;
	/**菜谱类*/
	public int COOKBOOK = 308000;

}
