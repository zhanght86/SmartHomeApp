package com.gatz.smarthomeapp.model.netty.common;

import java.util.concurrent.atomic.AtomicLong;

public class AppConstants {

    public static final String SSL_BKS_FILENAME = "cChat.bks";

    public static final String APP_PROPERTIES_FILENAME = "app.properties";

    public static final String SSL_MODEL = "";//"CA";
    /**
     * 请求session
     */
    public static final String REQUEST = "request";
    public static final String SYSTEM_V901 = "v901";
    public static final String SYSTEM_V902 = "v902";

    /**
     * 自增原子变量
     */
    public static AtomicLong SEQ = new AtomicLong();

}
