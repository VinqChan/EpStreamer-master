package com.easiio.epstreamer;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by LiTingYao on 2018/8/11.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initTBS();
        UMShareAPI.get(this);//初始化sdk
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        Config.DEBUG = true;
    }
    //各个平台的配置
    {
        //微信
        PlatformConfig.setWeixin("wx4a61892ca982411b", "d219ed7e511c53ca025d4f3800e84b45");
        //新浪微博(第三个参数为回调地址)
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com/sina2/callback");
        //QQ
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
    }
    private void initTBS() {
        
        QbSdk.PreInitCallback preInitCallback = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.e("tbs", "onCoreInitFinished: ");
            }
            
            @Override
            public void onViewInitFinished(boolean b) {
                Log.e("tbs", "onViewInitFinished: " + b);
            }
        };
        
        //tbs内核下载跟踪
        TbsListener tbsListener = new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                //tbs内核下载完成回调
                Log.e("tbs", "内核下载完成回调 onDownloadFinish: " + i);
            }
            
            @Override
            public void onInstallFinish(int i) {
                //内核安装完成回调，
                Log.e("tbs", "内核安装完成回调 onInstallFinish: " + i);
            }
            
            @Override
            public void onDownloadProgress(int i) {
                //下载进度监听
                Log.e("tbs", "下载进度监听 onDownloadProgress: " + i);
            }
        };
        QbSdk.initX5Environment(this, preInitCallback);
        //tbs内核下载跟踪
        QbSdk.setTbsListener(tbsListener);
        //判断是否要自行下载内核
        QbSdk.setDownloadWithoutWifi(true);
        boolean needDownload = TbsDownloader.needDownload(this, TbsDownloader.DOWNLOAD_OVERSEA_TBS);
        if (needDownload) {
            TbsDownloader.startDownload(this);
        }
    }
}
