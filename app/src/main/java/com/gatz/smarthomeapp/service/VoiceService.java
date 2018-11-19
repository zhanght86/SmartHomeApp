package com.gatz.smarthomeapp.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.citic.zktd.saber.server.entity.json.enums.GreenCommandAddress;
import com.citic.zktd.saber.server.entity.protocol.enums.KnxCommandType;
import com.citic.zktd.saber.server.entity.protocol.enums.KnxControlType;
import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.base.MyAppliCation;
import com.gatz.smarthomeapp.bean.KnxProtocol;
import com.gatz.smarthomeapp.bean.Result;
import com.gatz.smarthomeapp.bean.UserInfoBean;
import com.gatz.smarthomeapp.model.http.ObserverCallBack;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;
import com.gatz.smarthomeapp.model.netty.manager.SessionManager;
import com.gatz.smarthomeapp.model.netty.session.AppSession;
import com.gatz.smarthomeapp.model.netty.session.AppSessionManager;
import com.gatz.smarthomeapp.model.netty.session.AppStandardSessionManager;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.utils.AppSessionListenerImpl;
import com.gatz.smarthomeapp.utils.DbUttil;
import com.gatz.smarthomeapp.utils.DeviceCommond;
import com.gatz.smarthomeapp.utils.HttpUtil;
import com.gatz.smarthomeapp.utils.JsonParser;
import com.gatz.smarthomeapp.utils.LinkedMsgQueue;
import com.gatz.smarthomeapp.utils.ToastUtil;
import com.gatz.smarthomeapp.utils.UploadFileUtil;
import com.gatz.smarthomeapp.utils.UrlUtils;
import com.gatz.smarthomeapp.utils.Utils;
import com.gatz.smarthomeapp.utils.VoiceUtils;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Debby on 2017/2/23.
 */
public class VoiceService extends Service implements UploadFileUtil.OnUploadFileForResultListener {
    private static final String TAG = "VoiceService";

    //语音助手activity是否显示了
    public static boolean voiceActivityShowed = false;

    //语音识别session管理
    private static AppSessionManager mAppSessionManager = AppStandardSessionManager.getInstance();
    private static AppSessionListenerImpl appSessionListener = AppSessionListenerImpl.getInstance();
    private static SessionManager mSessionManager = SessionManager.getInstance();
    private static AppSession mRecognizeSession = null;
    //语音唤醒之后语音识别间隔
    public static final int RECOGINZERTIME = 1000 * 12;


    private static String currenttime = "iat";
    // 用HashMap存储听写结果
    private static HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private static String strText; // 听写的文字
    private static UploadFileUtil uploadFile;

    // 语音唤醒对象
    public static VoiceWakeuper mIvw;
    // 唤醒结果内容
    private static String resultString;
    // 语音听写对象
    private static SpeechRecognizer mIat;
    private static SharedPreferences mSharedPreferences;
    // 引擎类型
    private static String mEngineType = SpeechConstant.TYPE_CLOUD;
    private final static int BASE_SCORE = 0;//语音唤醒得分
    public static Handler handler = null;

    private static final LinkedMsgQueue<String> QUEUE = new LinkedMsgQueue<>();
    public static Context ctx = null;
    private final static String tone = "了吧呢呗啵的啦哩咧咯啰喽吗嘛么呢呐否呵哈给哉阿啊呃哇呀耶哟噢";
    private static final String SYMBOL = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]";

    public static Intent intent = null;
    private static UserInfoBean bean;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private class RunTask implements Runnable {

        @Override
        public void run() {
            Utils.showLogE(TAG, "RunTask:" + mIat.isListening() + "wakeup" + mIvw.isListening());
            if (!mIat.isListening() && !mIvw.isListening()) {
                mIvw.startListening(mWakeuperListener);
                Utils.showLogE(TAG, "RunTask启动唤醒");
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        init();
        startWakeupTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initWakeup();
        return START_REDELIVER_INTENT;// 服务被异常kill后 系统会自动重启服务 并将intent 传入
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent mIntent = new Intent(this, VoiceService.class);
        startService(mIntent);
    }

    /**
     * Voice dictation parameter settings
     *
     * @return
     */
    public static void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        String lag = mSharedPreferences.getString("iat_language_preference", "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        currenttime = System.currentTimeMillis() + "";
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, VoiceUtils.getMscFilePath() + currenttime + ".wav");
        Utils.showLogE(TAG, "生成文件:" + currenttime + ".wav");
    }

    private void init() {
        uploadFile = new UploadFileUtil();
        uploadFile.setListener(this);
        bean = DbUttil.getUser(VoiceService.this);
        registerMsgEventListener();

        //timed task check wakeup
        RunTask runTask = new RunTask();
        scheduledExecutorService.scheduleAtFixedRate(runTask, 0, 60, TimeUnit.SECONDS);
    }

    //Voice wakeup
    private void startWakeupTask() {
        // To load the aware resource, resPath identifies the local resource path
        StringBuffer param = new StringBuffer();
        String resPath = ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, getResPath());
        param.append(SpeechConstant.IVW_RES_PATH + "=" + resPath);
        param.append("," + ResourceUtil.ENGINE_START + "=" + SpeechConstant.ENG_IVW);
        if (SpeechUtility.getUtility() != null) {
            boolean ret = SpeechUtility.getUtility().setParameter(ResourceUtil.ENGINE_START, param.toString());
            if (!ret) {
                Utils.showLogE(TAG, " Failed to start local microphone engine");
            }
            mIvw = VoiceWakeuper.createWakeuper(this, null);
            mIat = SpeechRecognizer.createRecognizer(this, null);
            mSharedPreferences = this.getSharedPreferences("speech", Activity.MODE_PRIVATE);
        }

    }

    private void registerMsgEventListener() {
        MsgUtils.addEventListener(VoiceUtils.START_WAKEUP, handleEventListener);
        MsgUtils.addEventListener(VoiceUtils.STOP_WAKEUP, handleEventListener);
        MsgUtils.addEventListener(VoiceUtils.RECOGNIZE_WAKEUP, handleEventListener);
        MsgUtils.addEventListener(VoiceUtils.WAKEUP_RECOGNIZE, handleEventListener);
        MsgUtils.addEventListener(VoiceUtils.VOICE_SESSION, handleEventListener);
    }

    private void initWakeup() {
        // 启动唤醒
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            resultString = "";
            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            /**
             * 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
             * 示例demo默认设置第一个唤醒词，建议开发者根据定制资源中唤醒词个数进行设置
             */
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:0");
            // 设置唤醒模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒 0 就监听一次就结束 1 持续进行监听
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE, "1");
            // 设置闭环优化网络模式
            mIvw.setParameter(SpeechConstant.IVW_NET_MODE, "0");
            // 设置唤醒资源路径
            String resource = ResourceUtil.generateResourcePath(VoiceService.this, ResourceUtil.RESOURCE_TYPE.assets, getResPath());
            Utils.showLogE(TAG, "resource:" + resource);
            mIvw.setParameter(SpeechConstant.IVW_RES_PATH, resource);
            mIvw.startListening(mWakeuperListener);
        } else {
            ToastUtil.makeShortText(VoiceService.this, getString(R.string.voice_tip01));
        }
    }

    //是否停止听写
    private static boolean stop = false;

    //启动听写监听队列线程
    public void startQueueListener() {
        if (null != mRecognizeSession && mRecognizeSession.isValid()) {
            Utils.showLogE(TAG, "startQueueListener mRecognizeSession");
            MsgUtils.dispatchEvent(VoiceUtils.SHOW_RECORD, MsgUtils.PROTOCOL.NULL);
            mRecognizeSession.refresh();
            stop = false;
            QUEUE.put("start");
            new Thread(runnable).start();
        } else {
            Utils.showLogE(TAG, "mRecognizeSession失效");
            if (!mIvw.isListening()) {
                mIvw.startListening(mWakeuperListener);
            }
        }
    }

    //停止听写监听队列线程
    public static void stopQueueListener() {
        stop = true;
        QUEUE.put("stop");
        MsgUtils.dispatchEvent(VoiceUtils.HIDE_RECORD, MsgUtils.PROTOCOL.NULL);
        Utils.showLogE(TAG, "stopQueueListener");
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (!stop) { //如果没有停止,出队列启动听写
                try {
                    String start = QUEUE.poll();
                    if ("start".equals(start)) {
                        QUEUE.removeAll();
                        //启动听写
                        setParam();
                        //开始录音
                        mIat.startListening(mRecognizerListener);
                        Utils.showLogE(TAG, "runnable");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mIat.stopListening();
            Utils.showLogE(TAG, "听写线程停止");
        }
    };

    /**
     * Local resource path of recogniton
     *
     * @return
     */
    private String getResPath() {
        StringBuilder builder = new StringBuilder();
        builder.append(VoiceUtils.IVM);
        builder.append(File.separator);
        builder.append(VoiceUtils.APPID);
        builder.append(VoiceUtils.JET);
        return builder.toString();
    }

    public WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            int score = 0;
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 " + text);
                buffer.append("\n");
                buffer.append("【操作类型】" + object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】" + object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                score = Integer.parseInt(object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString = buffer.toString();
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            Utils.showLogE("语音唤醒结果返回:", resultString);
            if (score > BASE_SCORE) {
                Utils.lightUpScreen(ctx);
                ToastUtil.makeShortText(ctx, "已准备好，请说话~~~");
                MediaPlayer mp = new MediaPlayer();
                try {
                    mp.setDataSource(ctx, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mAppSessionManager.register(appSessionListener);
                mRecognizeSession = mAppSessionManager.createSession(RECOGINZERTIME);
                mRecognizeSession.setAttribute(VoiceUtils.RECOGNIZE, this);
                mSessionManager.setRecognizeSession(mRecognizeSession);
                Utils.showLogE(TAG, "session第一次创建");
                mIvw.stopListening();
                //第一次启动听写
                QUEUE.put("start");
                startQueueListener();
            } else {
                mIvw.startListening(mWakeuperListener);
            }
        }

        @Override
        public void onError(SpeechError error) {
        }

        @Override
        public void onBeginOfSpeech() {
            Utils.showLogE(TAG, "开始说话");
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {

        }

        @Override
        public void onVolumeChanged(int volume) {

        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入

        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            Utils.deleteFile(new File(VoiceUtils.getMscFilePath() + currenttime + VoiceUtils.WAV));
            QUEUE.put("start");
            Utils.showLogE(TAG, "onError开始录音" + error.getMessage());
        }

        @Override
        public void onEndOfSpeech() {
//			 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            //showTip("语音转化成的文字" + resultBuffer.toString());
        }

        @SuppressWarnings("static-access")
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
            if (isLast) {
                StringBuffer resultBuffer = new StringBuffer();
                for (Map.Entry<String, String> entry : mIatResults.entrySet()) {
                    resultBuffer.append(entry.getValue());
                }
                mIatResults.clear();
                strText = null;
                strText = resultBuffer.toString();
                Utils.showLogE(TAG, "识别结果" + strText);
                //上传文字
                String tmpFilename = currenttime + VoiceUtils.WAV;
                if (!TextUtils.isEmpty(strText)) {
                    String text1 = strText.replaceAll(SYMBOL, "");
                    String arry[] = text1.split("");
                    int length = arry.length;
                    for (String s : arry) {
                        if (tone.contains(s)) {
                            length = length - 1;
                        }
                    }
                    if (length >= 2) {
                        ObserverCallBack mObserverCallBack = mObserverCallBack();
                        mObserverCallBack.setData(currenttime);
                        HttpUtil.uploadVoiceText(bean.getSessionId(), tmpFilename, strText, mObserverCallBack, UrlUtils.CONTROL_TEXT_CODE);
                    } else {
                        if (voiceActivityShowed)
                            showAnswer(strText, currenttime, VoiceUtils.NO_DEVICE);
                        else
                            startQueueListener();
                        Utils.deleteFile(new File(VoiceUtils.getMscFilePath() + tmpFilename));
                    }
                } else {
                    Utils.deleteFile(new File(VoiceUtils.getMscFilePath() + tmpFilename));
                }
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            // if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            // String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            // Log.d(TAG, "session id =" + sid);
            // }
        }

        @Override
        public void onVolumeChanged(int arg0, byte[] arg1) {
        }
    };

    /**
     * Transfer data to voice assistant
     *
     * @param text
     * @param time
     * @param transferTuling
     */
    private static void showAnswer(String text, String time, String transferTuling) {
        Map<String, String> map = new HashMap<>();
        map.put(VoiceUtils.STRTEXT, text);
        map.put(VoiceUtils.CURRENTTIME, time);
        map.put(VoiceUtils.TRANSEFER_TULING, transferTuling);
        MsgUtils.dispatchEvent(VoiceUtils.SHOW_ANSWER, map);
    }

    private ObserverCallBack mObserverCallBack() {
        return new ObserverCallBack() {
            private String fileName;

            @Override
            public void onSuccessHttp(String responseInofo, int resultCode) {
                String tempFilename = fileName + VoiceUtils.WAV;
                File file = new File(VoiceUtils.getMscFilePath() + tempFilename);
                if (file.length() > 0) {
                    Utils.showLogE(TAG, "上传文件" + fileName);
                    uploadFile.uploadBg(file, bean.getSessionId());
                }
                Result<String> result = VoiceUtils.analyzeVoiceWaker(responseInofo);
                try {
                    if (null != result) {
                        String status = result.getStatus();
                        String code = result.getCode();
                        String msg = result.getMsg();
                        int intCode = Integer.parseInt(code);
                        if (VoiceUtils.SUCCESS.equals(status)) {
                            if (201 == intCode) {
                                if (voiceActivityShowed)
                                    showAnswer(strText, fileName, VoiceUtils.NO_DEVICE);
                                else {
                                    Utils.showLogE(TAG, "---------重新开始识别--------");
                                    startQueueListener();
                                }
                            } else if (200 == intCode) {
                                controlLight(result.getT());
                                if (voiceActivityShowed) {
                                    showAnswer(strText, fileName, VoiceUtils.WITH_DEVICE);
                                    //stop wakeup and start recognize listenering
                                    MsgUtils.dispatchEvent(VoiceUtils.WAKEUP_RECOGNIZE, MsgUtils.PROTOCOL.NULL);
                                } else {
                                    Utils.showLogE(TAG, "---------重新开始识别--------");
                                    startQueueListener();
                                }
                            }
                        } else if (VoiceUtils.FAILED.equals(status)) {
                            if (400 == intCode) {
                                if (voiceActivityShowed) {
                                    showAnswer(strText, this.fileName, VoiceUtils.FAILED_UP);
                                    MsgUtils.dispatchEvent(VoiceUtils.HIDE_RECORD, MsgUtils.PROTOCOL.NULL);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    showAnswer(msg, this.fileName, VoiceUtils.FIALED_UP_MSG);
                                } else {
                                    startQueueListener();
                                    mIvw.startListening(mWakeuperListener);
                                }
                            } else {
                                ToastUtil.makeShortText(MyAppliCation.getInstance(), msg);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureHttp(IOException e, int resultCode) {
                stopQueueListener();
                mIvw.startListening(mWakeuperListener);
                ToastUtil.makeShortText(MyAppliCation.getInstance(), "录音文件上传失败，请稍后...");
            }

            @Override
            public void setData(Object obj) {
                this.fileName = (String) obj;
            }
        };
    }

    //control the light by voice
    private static void controlLight(String listString) {
        List<KnxProtocol> protocols = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JavaType type = mapper.getTypeFactory().constructParametricType(List.class, KnxProtocol.class);
            protocols = mapper.readValue(listString, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendManager sendManager = SendManager.getInstance();
        for (KnxProtocol knxProtocol : protocols) {
            String span = knxProtocol.getSpan();
            if (VoiceUtils.SPAN.equals(span)) {
                String cmdValue = knxProtocol.getCmdvaule();
                String value = null;
                if ("0".equals(cmdValue))
                    value = VoiceUtils.OFF;
                else if ("1".equals(cmdValue))
                    value = VoiceUtils.ON;
                DeviceCommond.sendKnxCommond(VoiceService.ctx, knxProtocol, KnxCommandType.POWER, KnxControlType.WRITE, value, sendManager);
            } else if (null != span && span.indexOf("-") > 1) {
                DeviceCommond.sendGreenCommand(VoiceService.ctx, GreenCommandAddress.valueOf(knxProtocol.getProtocolAddr()), Integer.valueOf(knxProtocol.getCmdvaule()), sendManager);
            }
        }

    }

    /**
     * 返回 语音转成的文字信息
     *
     * @param results
     * @return
     */
    private static void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        Utils.showLogE(TAG, "听写结果:" + text);
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);

    }

    /***
     * Voice dictation results uploaded successfully
     *
     * @param isUploadSuccess
     * @param result_code
     * @param text
     * @param file
     */
    @Override
    public void onResultListener(boolean isUploadSuccess, String result_code, String text, File file) {
        if (null != file) {
            Utils.deleteFile(file);
        }
    }

    private MsgUtils.HandleEventListener handleEventListener = new MsgUtils.HandleEventListener() {
        @Override
        public void onHandle(String eventName, Object... objs) {
            if (VoiceUtils.START_WAKEUP.equals(eventName)) {
                Utils.showLogE(TAG, "start wakeup");
                mIvw.startListening(mWakeuperListener);
            } else if (VoiceUtils.STOP_WAKEUP.equals(eventName)) {
                Utils.showLogE(TAG, "stop wakeup");
                mIvw.stopListening();
            } else if (VoiceUtils.START_RECOGNIZE.equals(eventName)) {
                Utils.showLogE(TAG, "stop wakeup");
                startQueueListener();
            } else if (VoiceUtils.STOP_RECOGNIZE.equals(eventName)) {
                Utils.showLogE(TAG, "stop wakeup");
                stopQueueListener();
            } else if (VoiceUtils.RECOGNIZE_WAKEUP.equals(eventName) || VoiceUtils.VOICE_SESSION.equals(eventName)) {
                Utils.showLogE(TAG, "stop speech recognition and start voice wakeup");
                stopQueueListener();
                if (null != mRecognizeSession && mRecognizeSession.isValid())
                    mRecognizeSession.isValid();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mIvw.startListening(mWakeuperListener);
            } else if (VoiceUtils.WAKEUP_RECOGNIZE.equals(eventName)) {
                Utils.showLogE(TAG, "stop voice wakeup and start speech recognition");
                mIvw.stopListening();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startQueueListener();
            }
        }
    };
}
