package com.gatz.smarthomeapp.model.netty.ssl;

import android.content.res.Resources;

import com.gatz.smarthomeapp.model.netty.common.AppConstants;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public final class SecureChatSslContextFactory {

    private static final String PROTOCOL = "TLS";
    private static SSLContext CLIENT_CONTEXT;

    public static SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }

    public static SSLContext getClientContext(String tlsMode, Resources resources) {
        if (CLIENT_CONTEXT == null) {
            InputStream tIN = null;
            try {
                TrustManagerFactory tf = null;
                if (resources != null) {
                    KeyStore tks = KeyStore.getInstance("BKS");

                    try {
                        tIN = resources.getAssets().open("bks/" + AppConstants.SSL_BKS_FILENAME);
                        tks.load(tIN, "cNetty".toCharArray());
                        tf = TrustManagerFactory.getInstance("X509");
                        tf.init(tks);
                    } catch (IOException e) {
                        throw new Error("打开自定义证书失败");
                    }
                }
                // Initialize the SSLContext to work with our key managers.
                CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
                if (SSLMODE.CA.toString().equals(tlsMode))
                    CLIENT_CONTEXT.init(null, tf == null ? null : tf.getTrustManagers(), null);
                else {
                    throw new Error("Failed to initialize the client-side SSLContext" + tlsMode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to initialize the client-side SSLContext", e);
            } finally {
                if (tIN != null)
                    try {
                        tIN.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                tIN = null;
            }
        }
        return CLIENT_CONTEXT;
    }
}
