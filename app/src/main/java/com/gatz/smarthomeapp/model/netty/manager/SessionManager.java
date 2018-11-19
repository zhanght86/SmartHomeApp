package com.gatz.smarthomeapp.model.netty.manager;


import com.gatz.smarthomeapp.model.netty.session.AppSession;

public class SessionManager {
    private static SessionManager instance = null;

    /**
     * 语音识别session
     */
    private AppSession recognizeSession = null;
    private AppSession heartSession = null;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }


    public void setRecognizeSession(AppSession recognizeSession) {
        this.recognizeSession = recognizeSession;
    }

    public void setHeartSession(AppSession heartSession) {
        this.heartSession = heartSession;
    }

    public AppSession getRecognizeSession() {
        return recognizeSession;
    }

    public void removeRecognizeSession() {
        if (null != recognizeSession) {
            recognizeSession.invalidate();
            recognizeSession = null;
        }
    }
}
