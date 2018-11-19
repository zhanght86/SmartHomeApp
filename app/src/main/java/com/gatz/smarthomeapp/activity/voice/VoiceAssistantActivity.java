package com.gatz.smarthomeapp.activity.voice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.adapter.VoiceAdapter;
import com.gatz.smarthomeapp.base.BaseActivity;
import com.gatz.smarthomeapp.bean.CookBook;
import com.gatz.smarthomeapp.bean.UserInfoBean;
import com.gatz.smarthomeapp.bean.VoiceMessage;
import com.gatz.smarthomeapp.model.http.ObserverCallBack;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.service.VoiceService;
import com.gatz.smarthomeapp.utils.DbUttil;
import com.gatz.smarthomeapp.utils.TulingRequest;
import com.gatz.smarthomeapp.utils.Utils;
import com.gatz.smarthomeapp.utils.VoiceUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VoiceAssistantActivity extends BaseActivity implements ObserverCallBack, MsgUtils.HandleEventListener {
    public static final String TAG = "VoiceAssistantActivity";
    // 语音合成
    private SpeechSynthesizer mTts;
    // 默认发音人
    private String voicer = "xiaoyan";
    private SharedPreferences mSharedPreferences;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    public static Intent intent = null;
    public static final String PREFER_NAME = "com.gatz.fragment.setting";
    private ListView listView;
    private VoiceAdapter voiceAdapter;
    private List<VoiceMessage> voiceMessageList;
    private static final String WELCOMTIP = "欢迎使用语音助手";
    private RelativeLayout rl_recording;
    private ScreenStatusReceiver mScreenStatusReceiver;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private static final int HIDE_RECORD = 1000;
    private static final int SHOW_RECORD = 10001;
    private static final int SHOW_ANSWER = 10002;
    private static final int ADD_MESSAGE = 10003;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_assistant);
        VoiceService.voiceActivityShowed = true;
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    private void init() {
        initView();
        rigesterListener();
        initData();
        keepWakeLock();
    }

    @Override
    public void onStart() {
        super.onStart();
        //去掉底部栏
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onStop() {
        super.onStop();
        //停止识别，开始唤醒
        MsgUtils.dispatchEvent(VoiceUtils.RECOGNIZE_WAKEUP, MsgUtils.PROTOCOL.NULL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        ImageView voiceBackIv = (ImageView) this.findViewById(R.id.voice_back);
        voiceBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTts.stopSpeaking();
                // 退出时释放连接
                mTts.destroy();
                mTts = null;
                VoiceService.voiceActivityShowed = false;
                intent = null;
                //停止识别，开始唤醒
                MsgUtils.dispatchEvent(VoiceUtils.RECOGNIZE_WAKEUP, MsgUtils.PROTOCOL.NULL);
                unregisterReceiver(mScreenStatusReceiver);
                VoiceAssistantActivity.this.finish();
            }
        });
        listView = (ListView) this.findViewById(R.id.lv_voice);
        rl_recording = (RelativeLayout) this.findViewById(R.id.rl_recording);
    }

    private void rigesterListener() {
        MsgUtils.addEventListener(VoiceUtils.SHOW_ANSWER, this);
        MsgUtils.addEventListener(VoiceUtils.SHOW_RECORD, this);
        MsgUtils.addEventListener(VoiceUtils.HIDE_RECORD, this);
    }

    /***
     * 保持屏幕长亮
     */
    private void keepWakeLock() {
        powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
    }


    private void initData() {
        mSharedPreferences = this.getSharedPreferences(PREFER_NAME, this.MODE_PRIVATE);
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        voiceMessageList = new ArrayList<>();
        voiceMessageList.add(new VoiceMessage(WELCOMTIP, VoiceMessage.Type.TEXT, VoiceMessage.IMsgViewType.IMVT_COM_MSG, new Date(System.currentTimeMillis())));
        voiceAdapter = new VoiceAdapter(VoiceAssistantActivity.this, voiceMessageList);
        listView.setAdapter(voiceAdapter);
        initReceiver();

        // 解析数据
//        setItsParam();
//        mTts.startSpeaking(WELCOMTIP, mTtsListener);
    }

    private void initReceiver() {
        mScreenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStatusReceiver, intentFilter);
    }

    private void showAnswer(String text, String time, String transferTuling) {
        UserInfoBean bean = DbUttil.getUser(this);
        if (VoiceUtils.NO_DEVICE.equals(transferTuling)) {
            add(new VoiceMessage(text, VoiceMessage.Type.TEXT, VoiceMessage.IMsgViewType.IMVT_TO_MSG, new Date(Long.parseLong(time))));
            TulingRequest.requestTuring(text, bean.getUserName(), this, TulingRequest.TULING_COED);
        } else if (VoiceUtils.FAILED_UP.equals(transferTuling)) {
            add(new VoiceMessage(text, VoiceMessage.Type.TEXT, VoiceMessage.IMsgViewType.IMVT_COM_MSG, new Date(Long.parseLong(time))));
            if (null != mTts) {
                setItsParam();
                mTts.startSpeaking(text, mTtsListener);
            }
        } else if (VoiceUtils.FIALED_UP_MSG.equals(transferTuling)) {
            add(new VoiceMessage(text, VoiceMessage.Type.TEXT, VoiceMessage.IMsgViewType.IMVT_COM_MSG, new Date(Long.parseLong(time))));
        } else if (VoiceUtils.WITH_DEVICE.equals(transferTuling)) {
            add(new VoiceMessage(text, VoiceMessage.Type.TEXT, VoiceMessage.IMsgViewType.IMVT_TO_MSG, new Date(Long.parseLong(time))));
            MsgUtils.dispatchEvent(VoiceUtils.WAKEUP_RECOGNIZE);
        }
    }

    public void add(VoiceMessage msg) {
        voiceAdapter.add(msg);
        listView.setSelection(voiceAdapter.getCount() - 1);
    }

    @Override
    public void onSuccessHttp(String responseInfo, int resultCode) {
        if (resultCode == TulingRequest.TULING_COED) {
            VoiceMessage voiceMessage = new VoiceMessage();
            voiceMessage.setiMsgViewType(VoiceMessage.IMsgViewType.IMVT_COM_MSG);
            String text = "";
            try {
                JSONObject jsonObject = new JSONObject(responseInfo);
                if (null != jsonObject && jsonObject.has(TulingRequest.CODE)) {
                    int code = jsonObject.getInt(TulingRequest.CODE);
                    if (jsonObject.has(TulingRequest.TEXT)) {
                        text = jsonObject.getString(TulingRequest.TEXT);
                    }
                    String url = "";
                    if (jsonObject.has(TulingRequest.URL)) {
                        url = jsonObject.getString(TulingRequest.URL);
                    }
                    voiceMessage.setMessage(text);
                    switch (code) {
                        case HandlerCode.TEXT:
                            voiceMessage.setType(VoiceMessage.Type.TEXT);
                            break;
                        case HandlerCode.URL:
                            voiceMessage.setUrl(url);
                            voiceMessage.setType(VoiceMessage.Type.URL);
                            break;
                        case HandlerCode.NEWS:
                            voiceMessage.setUrl(TulingRequest.NEWS_URL);
                            voiceMessage.setType(VoiceMessage.Type.URL);
                            break;
                        case HandlerCode.COOKBOOK:
                            if (jsonObject.has(TulingRequest.LIST)) {
                                try {
                                    JSONArray listJsonArray = jsonObject.optJSONArray(TulingRequest.LIST);
                                    ObjectMapper mapper = new ObjectMapper();
                                    JavaType type = mapper.getTypeFactory().constructParametricType(List.class, CookBook.class);
                                    List<CookBook> cookBooks = mapper.readValue(listJsonArray.toString(), type);
                                    voiceMessage.setUrl(TulingRequest.COOKBOOK + cookBooks.get(0).getName());
                                    voiceMessage.setType(VoiceMessage.Type.URL);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            voiceMessage.setDate(new Date(System.currentTimeMillis()));
            speaking(text);
            Message message = new Message();
            message.obj = voiceMessage;
            message.what = ADD_MESSAGE;
            uiHandler.sendMessage(message);
        }
    }

    @Override
    public void onFailureHttp(IOException e, int resultCode) {

    }

    @Override
    public void setData(Object obj) {

    }

    private void speaking(String data) {
        if (null != mTts) {
            //解析数据
            setItsParam();
            mTts.startSpeaking(data, mTtsListener);
        }
        //stop speech recognition and start voice wakeup
        MsgUtils.dispatchEvent(VoiceUtils.RECOGNIZE_WAKEUP, MsgUtils.PROTOCOL.NULL);
    }

    /**
     * 语音合成参数设置
     *
     * @return
     */
    private void setItsParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "10"));
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
            /**
             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            Utils.showLogE(TAG, "开始播放");
        }

        @Override
        public void onSpeakPaused() {
            Utils.showLogE(TAG, "暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            Utils.showLogE(TAG, "继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                Utils.showLogE(TAG, "播放完成");
                MsgUtils.dispatchEvent(VoiceUtils.WAKEUP_RECOGNIZE, MsgUtils.PROTOCOL.NULL);
            } else if (error != null) {
                Utils.showLogE(TAG, error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };
    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Utils.showLogE(TAG, "初始化失败,错误码：");
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };


    @Override
    public void onHandle(String eventName, Object... objs) {
        if (VoiceUtils.SHOW_ANSWER.equals(eventName)) {
            Message message = new Message();
            message.obj = objs[0];
            message.what = SHOW_ANSWER;
            uiHandler.sendMessage(message);
        } else if (VoiceUtils.SHOW_RECORD.equals(eventName)) {
            uiHandler.sendEmptyMessage(SHOW_RECORD);
        } else if (VoiceUtils.HIDE_RECORD.equals(eventName)) {
            uiHandler.sendEmptyMessage(HIDE_RECORD);
        }
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDE_RECORD:
                    rl_recording.setVisibility(View.GONE);
                    break;
                case SHOW_RECORD:
                    rl_recording.setVisibility(View.VISIBLE);
                    break;
                case SHOW_ANSWER:
                    Map map = (Map) msg.obj;
                    String text = (String) map.get(VoiceUtils.STRTEXT);
                    String time = (String) map.get(VoiceUtils.CURRENTTIME);
                    String transferTuling = (String) map.get(VoiceUtils.TRANSEFER_TULING);
                    if (!TextUtils.isEmpty(text)) {
                        showAnswer(text, time, transferTuling);
                    }
                    break;
                case ADD_MESSAGE:
                    VoiceMessage voiceMessage = (VoiceMessage) msg.obj;
                    add(voiceMessage);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 屏幕亮屏和熄屏广播
     * Created by Debby on 2016/11/24.
     */
    private class ScreenStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            VoiceAssistantActivity.this.finish();
        }
    }

}
