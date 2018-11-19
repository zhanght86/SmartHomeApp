package com.gatz.smarthomeapp.utils;

import android.os.Bundle;
import android.os.Environment;

import com.gatz.smarthomeapp.bean.KnxProtocol;
import com.gatz.smarthomeapp.bean.Result;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by Debby on 2017/2/24.
 */
public class VoiceUtils {

    public static final String APPID = "5809834f";

    // Speech recognition files in a directory
    public static final String IVM = "ivw";

    // Speech recognition configuration file suffix
    public static final String JET = ".jet";

    // Speech recognition file suffix
    public static final String WAV = ".wav";

    //Session parameters for speech recognition
    public static final String RECOGNIZE = "recognize";

    // msc directory
    public static final String MSG_DERICTORY = "/smarthome/msc/";

    public static final String CURRENTTIME = "currenttime";
    public static final String STRTEXT = "strtext";
    public static final String TRANSEFER_TULING = "transefer_tuling";


    public static final String NO_DEVICE = "no_device";//no devices and request tuling if the activity showed
    public static final String WITH_DEVICE = "with_device";//find devices then control the devices
    public static final String FAILED_UP = "failed_up";// up the file failed
    public static final String FIALED_UP_MSG = "failed_up_msg";//the tip msg of failed up
    public static final String FAILED = "failed";
    public static final String SUCCESS = "success";
    public static final String SPAN = "on/off";
    public static final String ON = "on";
    public static final String OFF = "off";

    // voice messaging
    public static final String SHOW_RECORD = "show_record";//display message dialog
    public static final String HIDE_RECORD = "hide_record";//hide message dialog
    public static final String SHOW_ANSWER = "show_answer";//voice dialog activity display dialogue content
    public static final String START_WAKEUP = "start_wakeup";//start voice wakeup
    public static final String STOP_WAKEUP = "stop_wakeup";//stop voice wakeup
    public static final String START_RECOGNIZE = "start_recognize";//start speech recognition
    public static final String STOP_RECOGNIZE = "stop_recognize";//Stop speech recognition
    public static final String RECOGNIZE_WAKEUP = "recognize_wakeup";//stop speech recognition and start voice wakeup
    public static final String WAKEUP_RECOGNIZE = "wakeup_recognize";//stop voice wakeup and start speech recognition
    public static final String VOICE_SESSION = "voice_session";
    //http request constant
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String STATUS = "status";
    public static final String NULL = "null";

    //answer constant
    public static final String CONTROL_SUCCESS = "控制成功";
    public static final String WELCOMTIP = "欢迎使用语音助手";
    public static final String REQUEST_FALSE = "请求失败";

    /**
     * Get msc directory
     *
     * @return
     */
    public static String getMscFilePath() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory() + MSG_DERICTORY;
        } else {
            return Environment.getRootDirectory() + MSG_DERICTORY;
        }

    }

    /***
     * parse voice request results
     *
     * @param wsResponse
     * @return
     */
    public static Result<String> analyzeVoiceWaker(String wsResponse) {
        Result<String> result = new Result<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(wsResponse,result.getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
