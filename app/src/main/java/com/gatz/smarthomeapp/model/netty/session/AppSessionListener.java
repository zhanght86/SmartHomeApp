package com.gatz.smarthomeapp.model.netty.session;

/**
 * Description: 会话监听器，若会话管理器中注册了监听器，会话超时或会话销毁时会被监听器监听到并调用相应的方法<br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月25日 下午3:13:10
 */
public interface AppSessionListener {

    /**
     * 会话超时回调此方法，随后会调sessionListener.sessionRemoved()方法 此方法中session的的valid为true
     *
     * @param session
     */
    void sessionIde(AppSession session);

    /**
     * 监听会话attribute是否添加属性
     *
     * @param name
     * @param value
     */
    void attributeAdded(AppSession session, String name, Object value);

    /**
     * 从会话管理器中删除会话时会回调此方法，包括会话超时或调用session.invalidate()销毁会话。
     * 此方法中session的valid为false; 此方法被调用前，不一定已执行sessionIde(Session session)方法
     *
     * @param session
     */
    void sessionRemoved(AppSession session);

}
