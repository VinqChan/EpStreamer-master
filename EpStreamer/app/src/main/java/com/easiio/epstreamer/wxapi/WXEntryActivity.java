package com.easiio.epstreamer.wxapi;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.weixin.callback.WXCallbackActivity;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {
    public IWXAPI iwxapi;
    private ProgressDialog mProgressDialog;
    public static final String TAG  =  "WXEntryActivity";
    public static final String SECRET="d219ed7e511c53ca025d4f3800e84b45";
    public static final String APPID ="wx4a61892ca982411b";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    getSupportActionBar().hide();
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //接收到分享以及登录的intent传递handleIntent方法，处理结果
        iwxapi = WXAPIFactory.createWXAPI(this, SECRET, false);
        iwxapi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    //请求回调结果处理
    @Override
    public void onResp(BaseResp baseResp) {
        //登录回调
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) baseResp).code;
                //获取accesstoken
                getAccessToken(code);
                Log.d(TAG, code.toString()+ "");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED://用户拒绝授权
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    private void getAccessToken(String code) {
        //createProgressDialog();
        //获取授权
        StringBuffer loginUrl = new StringBuffer();
        loginUrl.append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=")
                .append(APPID)
                .append("&secret=")
                .append(SECRET)
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");
        Log.d(TAG, loginUrl.toString());

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(loginUrl.toString())
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
               // mProgressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseInfo= response.body().string();
                Log.d(TAG, "onResponse: " +responseInfo);
                String access = null;
                String openId = null;
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo);
                    access = jsonObject.getString("access_token");
                    openId = jsonObject.getString("openid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getUserInfo(access, openId);
            }
        });
    }
    private void createProgressDialog() {

        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//转盘
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("登录中，请稍后");
        mProgressDialog.show();
    }
    private void getUserInfo(String access, String openid) {
        String getUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access + "&openid=" + openid;
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(getUserInfoUrl)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
              //  mProgressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseInfo= response.body().string();
                Log.d(TAG, "onResponse: " + responseInfo);
                SharedPreferences.Editor editor= getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                editor.putString("responseInfo", responseInfo);
                editor.commit();
                finish();
                //mProgressDialog.dismiss();
            }
        });
    }

}
