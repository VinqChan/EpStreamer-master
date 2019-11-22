package net.ossrs.yasea.demo;


import android.util.Log;

import java.util.HashMap;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {

    public static void post(String url, HashMap<String, String > paramsMap){  //这里没有返回，也可以返回string
        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Set<String> keySet = paramsMap.keySet();
        for(String key:keySet) {
            String value = paramsMap.get(key);
            formBodyBuilder.add(key,value);
        }
        FormBody formBody = formBodyBuilder.build();
        Request request = new Request
                .Builder()
                .post(formBody)
                .url(url)
                .build();
        try (Response response = mOkHttpClient.newCall(request).execute()) {
            Log.e("linkedin", "post: "+response.body().string() );
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void get(String url){  //这里没有返回，也可以返回string
        OkHttpClient mOkHttpClient = new OkHttpClient();


        Request request = new Request
                .Builder()
                .get()
                .url(url)
                .build();
        try (Response response = mOkHttpClient.newCall(request).execute()) {
            Log.e("linkedin", "get: "+response.body().string() );
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void postLinkedin() {
        HashMap<String,String> paramsMap = new HashMap<String, String>() ;
        paramsMap.put("grant_type","client_credentials");
        paramsMap.put("client_id","81asdn4xddasjg");
        paramsMap.put("client_secret","kd1Q8rocmV7ssMEU");
        post("https://www.linkedin.com/oauth/v2/accessToken",paramsMap);


    }
    public static void getAuthorization() {
        get("https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id=81asdn4xddasjg&redirect_uri=https://www.example.com/auth/linkedin&state=fooobar&scope=r_liteprofile%20r_emailaddress%20w_member_socia");

    }
}