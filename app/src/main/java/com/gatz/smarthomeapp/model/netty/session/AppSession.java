package com.gatz.smarthomeapp.model.netty.session;

import java.io.Serializable;

/**
 * Description: 会话，默认超时时间为60秒<br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月25日 下午3:12:58
 */
public interface AppSession extends Serializable {

    /**
     * 获取会话ID
     *
     * @return
     */
    public String getId();

    /**
     * 获取会话中的参数
     *
     * @param name
     * @return
     */
    public Object getAttribute(String name);

    /**
     * 给会话设置参数
     *
     * @param name  不能为null或""
     * @param value
     */
    public void setAttribute(String name, Object value);

    /**
     * 清空值
     *
     * @param name
     */
    public void removeAttribute(String name);

    /**
     * 获取会话创建时间，单位为毫秒
     *
     * @return
     */
    public long getCreateTime();

    /**
     * 获取超时时间
     *
     * @return
     */
    public long getTimeOut();

    /**
     * 从会话管理器中移除会话并将valid置为false
     */
    public void invalidate();

    /**
     * 判断会话是否有效
     *
     * @return
     */
    public boolean isValid();

    /**
     * 刷新会话将有效期延长timeOut
     * 只有isVaild()是true的情况下刷新才有效
     */
    void refresh();
}
