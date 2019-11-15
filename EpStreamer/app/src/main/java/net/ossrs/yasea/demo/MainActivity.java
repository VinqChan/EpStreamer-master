package net.ossrs.yasea.demo;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.Utils;
import com.easiio.epstreamer.R;
import com.easiio.epstreamer.SharePopwindow;
import com.easiio.epstreamer.event.NetWorkStateEvent;
import com.easiio.epstreamer.receive.NetworkStateReceiver;
import com.github.faucamp.simplertmp.RtmpHandler;
import com.google.gson.Gson;
import com.seu.magicfilter.utils.MagicFilterType;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.Map;
import java.util.Random;


public class MainActivity extends Activity implements RtmpHandler.RtmpListener,
        SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener {

    private static final String TAG = "EpStream";
    public static final String APP_ID = "wx4a61892ca982411b";
    private Button btnPublish;
    private Button btnSwitchCamera;
    private Button btnRecord;
    private Button btnSwitchEncoder;
    private Button btnPause;
    private ImageView backiv;

    private SharedPreferences sp;
    private String rtmpUrl = "rtmp://live.ipitch.cn/live/7980000000_deck?token=123456789f123456789f123456789012";
    private String recPath = Environment.getExternalStorageDirectory().getPath() + "/test.mp4";
    private String FilePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "epstrreamer";
    private SrsPublisher mPublisher;
    private SrsCameraView mCameraView;
    private WebView webview;
    private boolean isPublish = false;
    private boolean isScale = false;
    private String nickname;
    private String headimgurl;
    private IWXAPI api;
    public static ValueCallback<Uri[]> mUploadCallbackAboveFive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
        // response screen rotation event
        NetworkStateReceiver.register(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        Utils.init(this);
        // restore data.
        sp = getSharedPreferences(TAG, MODE_PRIVATE);
        // rtmpUrl = sp.getString("rtmpUrl", rtmpUrl);

        // initialize url.
        final EditText efu = (EditText) findViewById(R.id.url);
        efu.setText(rtmpUrl);

        btnPublish = (Button) findViewById(R.id.publish);
        btnSwitchCamera = (Button) findViewById(R.id.swCam);
        btnRecord = (Button) findViewById(R.id.record);
        btnSwitchEncoder = (Button) findViewById(R.id.swEnc);
        btnPause = (Button) findViewById(R.id.pause);
        backiv = (ImageView) findViewById(R.id.back);
        btnPause.setEnabled(false);
        mCameraView = (SrsCameraView) findViewById(R.id.glsurfaceview_camera);
        webview = (WebView) findViewById(R.id.webview);

        mPublisher = new SrsPublisher(mCameraView);
        mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        mPublisher.setRtmpHandler(new RtmpHandler(this));
        mPublisher.setRecordHandler(new SrsRecordHandler(this));
        mPublisher.setPreviewResolution(1280, 720);
        mPublisher.setOutputResolution(1280, 720);//w =1380，h=1050
        mPublisher.setVideoHDMode();
        mPublisher.startCamera();


        PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.MICROPHONE).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied() {

            }
        });
        mCameraView.setCameraCallbacksHandler(new SrsCameraView.CameraCallbacksHandler() {
            @Override
            public void onCameraParameters(Camera.Parameters params) {
                //params.setFocusMode("custom-focus");
                //params.setWhiteBalance("custom-balance");
                //etc...
            }
        });
        mCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setScale(isScale ? 0 : 1);
                isScale = !isScale;
            }
        });

        backiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webview.goBack();
            }
        });
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: ");
                if (btnPublish.getText().toString().contentEquals("publish")) {
                    rtmpUrl = efu.getText().toString();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("rtmpUrl", rtmpUrl);
                    editor.apply();

                    mPublisher.startPublish(rtmpUrl);
                    mPublisher.startCamera();

                    if (btnSwitchEncoder.getText().toString().contentEquals("soft encoder")) {
                        Toast.makeText(getApplicationContext(), "Use hard encoder", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Use soft encoder", Toast.LENGTH_SHORT).show();
                    }
                    btnPublish.setText("stop");
                    btnSwitchEncoder.setEnabled(false);
                    btnPause.setEnabled(true);
                } else if (btnPublish.getText().toString().contentEquals("stop")) {
                    mPublisher.stopPublish();
                    mPublisher.stopRecord();
                    btnPublish.setText("publish");
                    btnRecord.setText("record");
                    btnSwitchEncoder.setEnabled(true);
                    btnPause.setEnabled(false);
                }
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnPause.getText().toString().equals("Pause")) {
                    mPublisher.pausePublish();
                    btnPause.setText("resume");
                } else {
                    mPublisher.resumePublish();
                    btnPause.setText("Pause");
                }
            }
        });

        btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPublisher.switchCameraFace((mPublisher.getCameraId() + 1) % Camera.getNumberOfCameras());
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnRecord.getText().toString().contentEquals("record")) {
                    if (mPublisher.startRecord(recPath)) {
                        btnRecord.setText("pause");
                    }
                } else if (btnRecord.getText().toString().contentEquals("pause")) {
                    mPublisher.pauseRecord();
                    btnRecord.setText("resume");
                } else if (btnRecord.getText().toString().contentEquals("resume")) {
                    mPublisher.resumeRecord();
                    btnRecord.setText("pause");
                }
            }
        });

        btnSwitchEncoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSwitchEncoder.getText().toString().contentEquals("soft encoder")) {
                    mPublisher.switchToSoftEncoder();
                    btnSwitchEncoder.setText("hard encoder");
                } else if (btnSwitchEncoder.getText().toString().contentEquals("hard encoder")) {
                    mPublisher.switchToHardEncoder();
                    btnSwitchEncoder.setText("soft encoder");
                }
            }
        });
        //mCameraView.setVisibility(View.GONE);
        //webview.loadUrl("file:///android_asset/test.html");
        webview.clearCache(true);
        webview.loadUrl("https://m.ipitch.cn/admin/index");
        //webview.loadUrl("https://m.ipitch.cn/test-01");
        WebSettings webSettings = webview.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("fromApp");
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setAllowFileAccess(true);
        webview.addJavascriptInterface(new JsInteration(), "android");
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mUploadCallbackAboveFive = filePathCallback;

                return true;
            }
        });

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        }
        webview.setWebViewClient(new WebViewClient() {
                                     @Override
                                     public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                         Log.e(TAG, "shouldOverrideUrlLoading: " + url);
                                         if (url.equals("file:///android_asset/test2.html")) {
                                             Log.e(TAG, "shouldOverrideUrlLoading: " + url);
                                             //   startActivity(new Intent(MainActivity.this,Main2Activity.class));
                                             return true;
                                         } else {
                                             webview.loadUrl(url);
                                             return false;
                                         }
                                     }

                                     @Override
                                     public void onPageFinished(WebView view, String url) {
                                         super.onPageFinished(view, url);
                                         Log.e(TAG, "onPageFinished: " + url);

                                     }

                                     @Override
                                     public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                         super.onPageStarted(view, url, favicon);
                                         Log.e(TAG, "onPageStarted: " + url);
                                     }

                                 }
        );
        regToWx();
        //getFileMsg();
        //openFile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == mUploadCallbackAboveFive) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // String dataString = data.getStringExtra("result");
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    int itemCount = clipData.getItemCount();
                    results = new Uri[itemCount];
                    for (int i = 0; i < itemCount; i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                        Log.e(TAG, "onActivityResult: getUri:" + item.getUri());
                    }
                }
                if (dataString != null) {
                    Log.e(TAG, "onActivityResult: dataString:" + dataString);
                    results = new Uri[]{Uri.parse(dataString)};

                    mUploadCallbackAboveFive.onReceiveValue(results);
                    mUploadCallbackAboveFive = null;

                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    public void openFile() {
        //调用系统文件管理器打开指定路径目录
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setDataAndType(Uri.fromFile(new File(FilePath).getParentFile()), "file/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent, 1);
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("file/*");
        // i.setDataAndType(Uri.fromFile(new File(FilePath).getParentFile()), "*/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), 1);
    }

    public void getFileMsg() {
        Intent intent = getIntent();

        Uri uri = intent.getData();

        if (uri != null) {

            String scheme = uri.getScheme();

            String host = uri.getHost();

            String port = uri.getPort() + "";

            String path = uri.getPath();

            String query = uri.getQuery();

            FileUtils.createOrExistsDir(FilePath);
            final File source = new File(path.replace("/external_files", ""));
            String fileName = "";
            if (source.exists()) {
                fileName = source.getName();
                Log.e(TAG, "getFileMsg: " + fileName);
            }
            Log.e(TAG, "getFileMsg: " + source.length() + "," + source.getPath() + "," + source.getAbsolutePath());
            FileUtils.copyFile(source.getPath(), FilePath + File.separator + fileName, new FileUtils.OnReplaceListener() {

                @Override
                public boolean onReplace() {
                    return true;
                }
            });


            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.addCompletedDownload(source.getName(), source.getName(), true, "file/*", source.getPath(), source.length(), false);
            startActivity(new Intent(MainActivity.this, PreviewPptListActivity.class));
        }

    }

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        api.registerApp(APP_ID);
    }

    public void setScale(int state) {
        ViewGroup.LayoutParams Params = mCameraView.getLayoutParams();
        switch (state) {

            case 0:
                Params.width = SizeUtils.dp2px(140);
                Params.height = SizeUtils.dp2px(80);
                mCameraView.setLayoutParams(Params);
                break;
            case 1:
                Params.width = Params.MATCH_PARENT;
                Params.height = Params.MATCH_PARENT;
                mCameraView.setLayoutParams(Params);
                break;
            case 2:
                Params.width = SizeUtils.dp2px(1);
                Params.height = SizeUtils.dp2px(1);
                mCameraView.setLayoutParams(Params);
                break;
        }

    }


    /**
     * 原生调用webView JS
     * 注意：需要在 onPageFinished 后调用
     */
    public void callwebJS() {
        webview.evaluateJavascript("javascript:testMethod()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String str) {
                android.util.Log.i("tag", "js返回的结果:" + str);
            }
        });
    }

    //授权
    private void authorization(SHARE_MEDIA share_media) {
        UMShareAPI.get(this).getPlatformInfo(this, share_media, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Log.d(TAG, "onStart " + "授权开始");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                Log.d(TAG, "onComplete " + "授权完成");

                //sdk是6.4.4的,但是获取值的时候用的是6.2以前的(access_token)才能获取到值,未知原因
                String uid = map.get("uid");
                String openid = map.get("openid");//微博没有
                String unionid = map.get("unionid");//微博没有
                String access_token = map.get("access_token");
                String refresh_token = map.get("refresh_token");//微信,qq,微博都没有获取到
                String expires_in = map.get("expires_in");
                String name = map.get("name");
                String gender = map.get("gender");
                String iconurl = map.get("iconurl");
                Log.e(TAG, "onComplete: " + gender + "," + iconurl);
                // {"openid":"ohcJlv9Cdce2gM6g-jqhKgSnL5vI","nickname":"test2","sex":0,"language":"zh_CN","city":"","province":"","country":"","headimgurl":"","privilege":[],"unionid":"om3WA5qsSpTxtWqTJ6vWzSZ_cv_E"}
                //Toast.makeText(getApplicationContext(), "name=" + name + ",gender=" + gender, Toast.LENGTH_SHORT).show();
                WXinfoModel model = new WXinfoModel();
                model.setOpenid(openid);
                model.setNickname(name);
                model.setHeadimgurl(iconurl);
                model.setUnionid(unionid);

                Log.e(TAG, "responseInfo: " + new Gson().toJson(model));
                final String responseInfo = new Gson().toJson(model);
                webview.post(new Runnable() {
                    @Override
                    public void run() {
                        webview.loadUrl("javascript:loginWithWeChatByApp(" + responseInfo + ")");
                    }
                });
                //拿到信息去请求登录接口。。。
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Log.d(TAG, "onError " + "授权失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Log.d(TAG, "onCancel " + "授权取消");
            }
        });
    }

    public class JsInteration {

        @JavascriptInterface
        public String setStreamUrl(String url) {
            Log.e(TAG, "setStreamUrl: " + url);
            rtmpUrl = url;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            return url;
        }

        @JavascriptInterface
        public void showVideo() {
            Log.e(TAG, "showVideo: ");
        }

        @JavascriptInterface
        public void openFile() {
            Log.e(TAG, "openFile: ");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, PreviewPptListActivity.class));
                }
            });
        }

        @JavascriptInterface
        public void wxlogin() {
            Log.e(TAG, "wxlogin: ");
            if (!api.isWXAppInstalled()) {
                Toast.makeText(MainActivity.this, "您的设备未安装微信客户端", Toast.LENGTH_SHORT).show();
            } else {
                authorization(SHARE_MEDIA.WEIXIN);
//                final SendAuth.Req req = new SendAuth.Req();
//                req.scope = "snsapi_userinfo";
//                req.state = "wechat_sdk_demo_test";
//                api.sendReq(req);
            }
        }

        @JavascriptInterface
        public void exit() {
            Log.e(TAG, "exit: ");
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        @JavascriptInterface
        public void startStream() {
            Log.e(TAG, "startStream: ");


            MainActivity.this.startStream();

        }

        @JavascriptInterface
        public void stopStream() {
            Log.e(TAG, "stopStream: ");
            MainActivity.this.stopStream();
        }

        @JavascriptInterface
        public void UploadFile() {
            Log.e(TAG, "UploadFile: ");
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("file/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), 1);

            // startActivityForResult(new Intent(MainActivity.this,OpenFileActivity.class),2);
        }

        @JavascriptInterface
        public void switchCamera() {
            Log.e(TAG, "switchCamera: ");
            mPublisher.switchCameraFace((mPublisher.getCameraId() + 1) % Camera.getNumberOfCameras());
        }

        @JavascriptInterface
        public void SharePitch(String url, String title) {
            Log.e(TAG, "SharePitch: " + url + "," + title);
            SharePopwindow.showPopupWindow(MainActivity.this, url, title);
        }

        @JavascriptInterface
        public void setResolution(String resolution) {
            Log.e(TAG, "setResolution: " + resolution);
            switch (resolution) {
                case "VGA":
                    mPublisher.setOutputResolution(640, 480);//w =1380，h=1050
                    break;
                case "720p":
                    mPublisher.setOutputResolution(1280, 720);//w =1380，h=1050
                    break;
                case "1080p":
                    mPublisher.setOutputResolution(1920, 1080);//w =1380，h=1050
                    break;
            }
        }
    }

    private void stopStream() {
        mPublisher.stopPublish();

        mPublisher.stopRecord();
        isPublish = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setScale(2);
            }
        });
    }

    private void startStream() {
        if (isPublish) {
            return;
        }
        mPublisher.startPublish(rtmpUrl);
        mPublisher.startCamera();
        //mPublisher.switchCameraFace((mPublisher.getCameraId() + 1) % Camera.getNumberOfCameras());
        if (btnSwitchEncoder.getText().toString().contentEquals("soft encoder")) {
            Toast.makeText(getApplicationContext(), "Use hard encoder", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Use soft encoder", Toast.LENGTH_SHORT).show();
        }
        isPublish = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setScale(0);
            }
        });
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//全局定义
    private long lastClickTime = 0L;
    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e(TAG, "dispatchKeyEvent: " + event.getKeyCode());

        //拦截返回键
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || (event.getKeyCode() == 87)) { //右翻页
            //判断触摸UP事件才会进行返回事件处理
//            if (event.getAction() == KeyEvent.ACTION_UP) {
//                onBackPressed();
//            }
            //只要是返回事件，直接返回true，表示消费掉
            if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {

            } else {
                lastClickTime = System.currentTimeMillis();
                webview.post(new Runnable() {
                    @Override
                    public void run() {
                        webview.loadUrl("javascript:changePage('right')");
                    }
                });
            }

            return true;
        } else if ((event.getKeyCode() == 88)) { //左翻页

            if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {

            } else {
                lastClickTime = System.currentTimeMillis();
                webview.post(new Runnable() {
                    @Override
                    public void run() {
                        webview.loadUrl("javascript:changePage('left')");
                    }
                });
            }
            return true;
        } else if ((event.getKeyCode() == 89 || event.getKeyCode() == 90)) { //切换摄像头
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: ");

                    if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {

                    } else {
                        lastClickTime = System.currentTimeMillis();
                        mPublisher.switchCameraFace((mPublisher.getCameraId() + 1) % Camera.getNumberOfCameras());
                    }


                    Log.e(TAG, "run: " + (mPublisher.getCameraId() + 1) % Camera.getNumberOfCameras());
                }
            });
            return true;
        }
//        else if (event.getKeyCode() == 66) { //暂停
//           // stopStream();
//            return true;
//        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (webview != null) {
            //如果h5页面可能返回，跳转到上个页面
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                //不能返回上个页面，直接finish当前Activity
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else {
            switch (id) {
                case R.id.cool_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.COOL);
                    break;
                case R.id.beauty_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.BEAUTY);
                    break;
                case R.id.early_bird_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.EARLYBIRD);
                    break;
                case R.id.evergreen_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.EVERGREEN);
                    break;
                case R.id.n1977_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.N1977);
                    break;
                case R.id.nostalgia_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.NOSTALGIA);
                    break;
                case R.id.romance_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.ROMANCE);
                    break;
                case R.id.sunrise_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.SUNRISE);
                    break;
                case R.id.sunset_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.SUNSET);
                    break;
                case R.id.tender_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.TENDER);
                    break;
                case R.id.toast_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.TOASTER2);
                    break;
                case R.id.valencia_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.VALENCIA);
                    break;
                case R.id.walden_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.WALDEN);
                    break;
                case R.id.warm_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.WARM);
                    break;
                case R.id.original_filter:
                default:
                    mPublisher.switchCameraFilter(MagicFilterType.NONE);
                    break;
            }
        }
        setTitle(item.getTitle());

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPublisher.getCamera() == null) {
            //if the camera was busy and available again
            mPublisher.startCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Button btn = (Button) findViewById(R.id.publish);
        btn.setEnabled(true);
        mPublisher.resumeRecord();
//        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
//        final String responseInfo = sp.getString("responseInfo", "");
//
//        if (!responseInfo.isEmpty()) {
//            try {
//                JSONObject jsonObject = new JSONObject(responseInfo);
//                nickname = jsonObject.getString("nickname");
//                headimgurl = jsonObject.getString("headimgurl");
//                webview.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        webview.loadUrl("javascript:loginWithWeChatByApp('" + responseInfo + "')");
//                    }
//                });
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            Log.e(TAG, "onResume: " + responseInfo);
//            SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
//            editor.clear();
//            editor.commit();
//        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkStateEvent(NetWorkStateEvent message) {

        boolean isConnetcted = message.isConnected;
        Log.e(TAG, "onNetWorkStateEvent: " + isConnetcted);
        if (isConnetcted) {
           webview.reload();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPublisher.pauseRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        NetworkStateReceiver.unregister(this);
        mPublisher.stopPublish();
        mPublisher.stopRecord();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPublisher.stopEncode();
        mPublisher.stopRecord();
        btnRecord.setText("record");
        mPublisher.setScreenOrientation(newConfig.orientation);
        if (btnPublish.getText().toString().contentEquals("stop")) {
            mPublisher.startEncode();
        }
        mPublisher.startCamera();
    }

    private static String getRandomAlphaString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    private static String getRandomAlphaDigitString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    private void handleException(Exception e) {
        try {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            mPublisher.stopPublish();
            mPublisher.stopRecord();
            btnPublish.setText("publish");
            btnRecord.setText("record");
            btnSwitchEncoder.setEnabled(true);
        } catch (Exception e1) {
            //
        }
    }

    // Implementation of SrsRtmpListener.

    @Override
    public void onRtmpConnecting(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpConnected(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoStreaming() {
    }

    @Override
    public void onRtmpAudioStreaming() {
    }

    @Override
    public void onRtmpStopped() {
        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpDisconnected() {
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoFpsChanged(double fps) {
        Log.i(TAG, String.format("Output Fps: %f", fps));
    }

    @Override
    public void onRtmpVideoBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            Log.i(TAG, String.format("Video bitrate: %f kbps", bitrate / 1000));
        } else {
            Log.i(TAG, String.format("Video bitrate: %d bps", rate));
        }
    }

    @Override
    public void onRtmpAudioBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            Log.i(TAG, String.format("Audio bitrate: %f kbps", bitrate / 1000));
        } else {
            Log.i(TAG, String.format("Audio bitrate: %d bps", rate));
        }
    }

    @Override
    public void onRtmpSocketException(SocketException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {
        handleException(e);
    }

    // Implementation of SrsRecordHandler.

    @Override
    public void onRecordPause() {
        Toast.makeText(getApplicationContext(), "Record paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordResume() {
        Toast.makeText(getApplicationContext(), "Record resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordStarted(String msg) {
        Toast.makeText(getApplicationContext(), "Recording file: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordFinished(String msg) {
        Toast.makeText(getApplicationContext(), "MP4 file saved: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    // Implementation of SrsEncodeHandler.

    @Override
    public void onNetworkWeak() {
        Toast.makeText(getApplicationContext(), "Network weak", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNetworkResume() {
        Toast.makeText(getApplicationContext(), "Network resume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }
}
