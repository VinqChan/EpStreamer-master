package com.easiio.epstreamer.api;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public abstract class PbxApi {

    private static OkHttpClient sClient;
    public static final String BASEURL= "https://m.ipitch.cn/webapi/";

    @NonNull
    protected static OkHttpClient getClient() {
        if (sClient == null) {
            sClient = ApiUtils.newClient();
        }
        return sClient;
    }

    private static Gson sGson;

    @NonNull
    protected static Gson getGson() {
        if (sGson == null) {
            sGson = ApiUtils.newGson();
        }
        return sGson;
    }

    @NonNull
    protected static String getBasicAuth() {
        return "Basic cHJpbWVhcHA6aGFpY2FuZ3dlbmppYW8=";
    }

    @NonNull
    protected static String getBearerAuth(@NonNull final String accessToken) {
        return "Bearer " + accessToken;
    }

    private enum ScalarsConverter {
        SINGLETON;

        private final ScalarsConverterFactory mFactory = ScalarsConverterFactory.create();

        public ScalarsConverterFactory getFactory() {
            return mFactory;
        }
    }

    @NonNull
    protected static ScalarsConverterFactory getConverterFactory() {
        return ScalarsConverter.SINGLETON.getFactory();
    }

    @NonNull
    protected static String getBaseUrl(@NonNull final String url) {
        final int startIndex = url.indexOf("//");
        if (startIndex < 0) {
            return url;
        }
        final int endIndex = url.indexOf("/", startIndex + "//".length());
        if (endIndex < 0) {
            return url;
        }
        return url.substring(0, endIndex);
    }

    @NonNull
    protected static Retrofit newRetrofit(@NonNull final String url) {
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl(url))
                .addConverterFactory(getConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getClient())
                .build();
    }

}