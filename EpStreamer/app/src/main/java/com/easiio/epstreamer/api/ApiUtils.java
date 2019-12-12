package com.easiio.epstreamer.api;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class ApiUtils {

    private static final String TAG = "ApiUtils";

    @NonNull
    public static OkHttpClient newClient() {
        try {
            return new OkHttpClient.Builder()
                    .hostnameVerifier(newHostnameVerifier())
                    .sslSocketFactory(newSSLSocketFactory(), newTrustManager())
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .pingInterval(60, TimeUnit.SECONDS)
                    .build();
        } catch (Throwable t) {
            LogUtils.d(TAG, "newClient error", t);
            return new OkHttpClient.Builder().build();
        }
    }

    private static HostnameVerifier newHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private static X509TrustManager newTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    private static SSLSocketFactory newSSLSocketFactory() {
        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{newTrustManager()}, null);
            return sslContext.getSocketFactory();
        } catch (Throwable t) {
            return (SSLSocketFactory) SSLSocketFactory.getDefault();
        }
    }

    @NonNull
    public static Gson newGson() {
        return new GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    @NonNull
    public static String[] getStunAddresses(@NonNull final String address) {
        if (TextUtils.isEmpty(address)) {
            return new String[]{};
        }
        return address.split(",");
    }

    public static int getTurnTypeCode(@NonNull final String type) {
        switch (type) {
            case "TCP":
                return 6;
            case "UDP":
                return 17;
            case "TLS":
                return 255;
            default:
                return 6;
        }
    }

}