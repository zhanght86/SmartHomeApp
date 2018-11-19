package com.gatz.smarthomeapp.model.netty.session;


import com.gatz.smarthomeapp.model.netty.utils.Threads;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Description: <br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月25日 下午3:13:40
 */
public class AppStandardSession implements AppSession {

    private static final long serialVersionUID = 2308274319492197412L;

    // 保存会话中的参数
    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    // 会话ID，在一个会话管理器中唯一
    private String id;

    // 创建时间
    private long createTime;

    // 最后一次更新时间，当会话有效且调用了refresh()时，此值会被更新成当前时间
    private long lastUpdateTime;

    // 默认超时间为60秒
    private static final long DEFAULT_TIME_OUT = 60000;

    private long timeOut = DEFAULT_TIME_OUT;

    // 会话监听器
    private AppSessionListener listener;

    //
    private volatile ScheduledFuture<?> readerIdleTimeout;

    // 会话管理器
    private AppStandardSessionManager manager;
    // 会话是否有效
    private volatile boolean isValid = true;

    protected AppStandardSession(String sessionId, AppSessionManager manager) {
        this(sessionId, manager, DEFAULT_TIME_OUT);
    }

    protected AppStandardSession(String sessionId, AppSessionManager manager, long timeOut) {
        this.id = sessionId;
        createTime = System.currentTimeMillis();
        this.manager = (AppStandardSessionManager) manager;
        this.timeOut = timeOut;
        initialize();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getAttribute(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        if ((name == null) || (value == null)) {
            throw new NullPointerException("name or value is null");
        }
        attributes.put(name, value);
        if (listener != null)
            listener.attributeAdded(this, name, value);
    }

    private void initialize() {
        lastUpdateTime = System.currentTimeMillis();
        listener = manager.getSessionListener();
        readerIdleTimeout = executor().schedule(new Threads.WrapExceptionRunnable(new SessionTimeoutTask()), timeOut,
                TimeUnit.MILLISECONDS);
    }

    private void destroy() {
        isValid = false;
        if (readerIdleTimeout != null) {
            // 取消定时器
            readerIdleTimeout.cancel(false);
            readerIdleTimeout = null;
        }
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public void invalidate() {
        // 销毁会话
        destroy();
        // 从会话管理器上删除
        manager.invalidate(getId());
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public String toString() {
        return new StringBuffer("StandardSession=[").append("id=").append(getId()).append(",isValid=").append(isValid)
                .append(",timeOut=").append(timeOut).append(",createTime=").append(createTime)
                .append(",lastUpdateTime=").append(lastUpdateTime).append("]").toString();
    }

    private void sessionIdle() {
        if (listener != null) {
            listener.sessionIde(this);
        }
        invalidate();
    }

    @Override
    public long getTimeOut() {
        return timeOut;
    }

    @Override
    public void refresh() {
        if (isValid()) {
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    /**
     * 获取会话管理器中是线程池
     *
     * @return
     */
    private ScheduledExecutorService executor() {
        return manager.getScheduled();
    }

    /**
     * 在会话超时时间后检查会话是否超时，若超时调用sessionIdle()销毁会话，若没有超时，设定下一次超时检测时间点
     *
     * @author wuhui
     */
    private final class SessionTimeoutTask implements Runnable {

        @Override
        public void run() {
            if (!isValid) {
                return;
            }
            long currentTime = System.currentTimeMillis();
            // 计算是否超时
            long nextDelay = timeOut - (currentTime - lastUpdateTime);
            if (nextDelay <= 0) {
                // 会话空闲
                sessionIdle();
            } else {
                // 未超时，延时nextDelay再检查
                readerIdleTimeout = executor().schedule(this, nextDelay, TimeUnit.MILLISECONDS);
            }
        }
    }

}
