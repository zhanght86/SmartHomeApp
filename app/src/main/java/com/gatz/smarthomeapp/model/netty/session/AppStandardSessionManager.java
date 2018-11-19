package com.gatz.smarthomeapp.model.netty.session;


import com.gatz.smarthomeapp.model.netty.utils.Threads;
import com.gatz.smarthomeapp.model.netty.utils.Utils;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Description: 会话管理器<br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月25日 下午3:13:44
 */
public final class AppStandardSessionManager implements AppSessionManager {

    private static AppStandardSessionManager instance = null;

    public static AppStandardSessionManager getInstance() {
        if (instance == null) {
            instance = new AppStandardSessionManager();
        }
        return instance;
    }

    /**
     * 检查会话是否超时的线程池
     */
    private ScheduledExecutorService scheduled;

    // 用于分配处理业务线程的线程组个数
    private static final int corePoolSize = Runtime.getRuntime().availableProcessors() * 3; // 默认

    private final Map<String, AppSession> sessions = new ConcurrentHashMap<String, AppSession>();

    // 会话监听器
    private AppSessionListener listener;

    private AppStandardSessionManager() {
        this(corePoolSize);
    }

    public AppStandardSessionManager(final int threadSeize) {
        if (threadSeize < 1) {
            scheduled = Executors.newScheduledThreadPool(threadSeize, Threads.buildJobFactory("app-session-%d"));
        } else {
            scheduled = Executors.newScheduledThreadPool(corePoolSize, Threads.buildJobFactory("app-session-%d"));
        }
    }

    @Override
    public AppSession createSession() {
        String sessionID = createSessionId();
        return createSession(sessionID, 0L);
    }

    @Override
    public AppSession createSession(long timeOut) {
        String sessionID = createSessionId();
        return createSession(sessionID, timeOut);
    }

    @Override
    public AppSession createSession(final String sessionId, long timeOut) {
        if ((sessionId == null) || "".equals(sessionId.trim())) {
            throw new IllegalArgumentException("sessionId:" + sessionId + " is null");
        }
        if (sessions.containsKey(sessionId)) {
            throw new IllegalArgumentException("sessionId:" + sessionId + " already exists");
        }
        AppSession session = new AppStandardSession(sessionId, this, timeOut);
        Utils.showErrorLog("AppStandardSessionManager","sessionId:"+sessionId+"timeout:"+timeOut);
        sessions.put(session.getId(), session);
        return session;
    }

    void invalidate(String sessionId) {
        synchronized (sessions) {
            AppSession session = sessions.get(sessionId);
            if (null != session && listener != null) {
                listener.sessionRemoved(session);
            }
            sessions.remove(sessionId);
        }
    }

    @Override
    public AppSession getSession(String sessionId) {
        if ((sessionId == null) || "".equals(sessionId.trim())) {
            throw new IllegalArgumentException("sessionId:" + sessionId + " is null");
        }
        return sessions.get(sessionId);
    }

    ScheduledExecutorService getScheduled() {
        return scheduled;
    }

    AppSessionListener getSessionListener() {
        return listener;
    }

    @Override
    public void register(AppSessionListener sessionListener) {
        if (sessionListener == null) {
            throw new IllegalArgumentException("sessionListener is null");
        }
        listener = sessionListener;
    }

    @Override
    public int size() {
        return sessions.size();
    }

    @Override
    public void shutdown() {
        Iterator<Entry<String, AppSession>> it = sessions.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, AppSession> entry = it.next();
            AppSession session = entry.getValue();
            if (session != null) {
                session.invalidate();
            }
        }
        scheduled.shutdown();
    }

    private static String createSessionId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ENGLISH);
    }
//	private static String createSessionId() {
//		return String.valueOf(AppConstants.SEQ.incrementAndGet());
//	}

    public Map<String, AppSession> getSessions() {
        return sessions;
    }
}
