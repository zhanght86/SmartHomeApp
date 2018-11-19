package com.gatz.smarthomeapp.model.netty.session;

import java.util.Map;

/**
 * Description: 会话管理器，管理所有的会话，如果会话超时或删除时需要回调，实现SessionListener接口<br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月25日 下午3:13:27
 */
public interface AppSessionManager {

    /**
     * 创建默认的会话，默认会话的ID采用UUID，默认超时时间为60秒
     *
     * @return 创建成功的会话
     */
    AppSession createSession();

    /**
     * 创建指定超时时间的会话
     *
     * @param timeOut 超时时间，至少应该大于10000毫秒，单位是毫秒
     * @return 创建成功的会话
     */
    AppSession createSession(long timeOut);

    /**
     * 创建指定会话ID，超时时间的会话，会话ID必须确保在一个会话管理器上唯一
     *
     * @param sessionId
     * @param timeOut   超时时间，至少应该大于10000毫秒，单位是毫秒
     * @return 创建成功的会话
     */
    AppSession createSession(String sessionId, long timeOut);

    /**
     * 获取会话，如果会话不存在返回null
     *
     * @param sessionId 必传，否则抛出NullPointException
     * @return
     */
    AppSession getSession(String sessionId);

    Map<String, AppSession> getSessions();

    /**
     * 注册会话监听器，当会话超时或被删除时会被回调
     *
     * @param listener
     */
    void register(AppSessionListener listener);

    /**
     * 获取会话管理器中的会话数量
     *
     * @return
     */
    int size();

    /**
     * 清空并释放会话管理器，如有注册SessionListener,会回调valid为true的会话sessionRemoved();
     */
    void shutdown();

}
