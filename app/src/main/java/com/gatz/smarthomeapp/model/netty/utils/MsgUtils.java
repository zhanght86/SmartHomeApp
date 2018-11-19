package com.gatz.smarthomeapp.model.netty.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MsgUtils {
    private static final String TAG = "MsgUtils";

    public static class PROTOCOL {
        public static final String NULL = "null";
    }

    /**
     * 缓存所有注册、分发事件
     */
    private static HashMap<String, List<HandleEventListener>> mEventMap = new HashMap<>();

    /**
     * 处理消息分发的监听
     *
     * @author Debby
     */
    public interface HandleEventListener {
        /**
         * 处理消息分发事件
         *
         * @param eventName 事件名称
         * @param objs      事件传输对象
         */
        void onHandle(String eventName, Object... objs);

    }

    /**
     * @param eventName 事件名称
     * @param listener  处理事件的监听
     */
    public static synchronized void addEventListener(String eventName, HandleEventListener listener) {
        try {
            boolean isContain = false;
            if (null == listener) {
                Utils.showDebugLog(TAG, "注册【" + eventName + "】事件的侦听器函数为空");
                return;
            }
            List<HandleEventListener> events = mEventMap.get(eventName);
            if (null == events) {
                events = new ArrayList<>();
                mEventMap.put(eventName, events);
            }
            for (HandleEventListener event : events) {
                if (event == listener) {
                    Utils.showDebugLog(TAG, "名为【" + eventName + "】的事件重复注册侦听器");
                    isContain = true;
                }
            }
            if (!isContain) {
                mEventMap.get(eventName).add(listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param eventName 事件名称
     * @param listener  处理事件的监听
     */
    public static synchronized void removeEventListener(String eventName, HandleEventListener listener) {
        try {
            if (null == listener) {
                Utils.showDebugLog(TAG, "需移除的【" + eventName + "】事件为空");
                return;
            }
            List<HandleEventListener> events = mEventMap.get(eventName);
            int eventLength = events.size();
            for (int i = 0; i < eventLength; i++) {
                HandleEventListener event = events.get(i);
                if (event == listener) {
                    mEventMap.get(eventName).remove(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 事件分发
     *
     * @param eventName 事件名称
     * @param obj       事件分发对象参数
     */
    public static synchronized void dispatchEvent(String eventName, Object... obj) {
        try {
            if (null == mEventMap) {
                Utils.showDebugLog(TAG, "事件集合为空");
                return;
            }
            List<HandleEventListener> events = mEventMap.get(eventName);
            if (null != events) {
                for (HandleEventListener event : events) {
                    event.onHandle(eventName, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
