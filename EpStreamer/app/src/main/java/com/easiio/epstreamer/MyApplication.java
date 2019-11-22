package com.easiio.epstreamer;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;
import com.twitter.sdk.android.core.Twitter;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import net.ossrs.yasea.demo.MainActivity;

import java.lang.reflect.Field;

/**
 * Created by LiTingYao on 2018/8/11.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initTBS();
        UMShareAPI.get(this);//初始化sdk
        UMConfigure.setLogEnabled(true);
        Twitter.initialize(this);
        try {
            Class<?> aClass = Class.forName("com.umeng.commonsdk.UMConfigure");
            Field[] fs = aClass.getDeclaredFields();
            for (Field f:fs){
                Log.e("xxxxxx","ff="+f.getName()+"   "+f.getType().getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        UMConfigure.init(this,"5dc133b5570df398560000d8"
                ,"umeng", UMConfigure.DEVICE_TYPE_PHONE,"");
        platform();
    }
    public void platform()
    //各个平台的配置
    {
        //微信
        PlatformConfig.setWeixin("wx4a61892ca982411b", "d219ed7e511c53ca025d4f3800e84b45");
        //QQ
        PlatformConfig.setQQZone("101830528", "c7394704798a158208a74ab60104f0ba");

        PlatformConfig.setTwitter("jFRczHoA7jym7X9DYOG6IMQab", "X3hYoUjklg5c3QpLi6MPx487nmGSGjlMm77kTAWMeRJPt9Xmlu");

        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad", "http://sns.whalecloud.com");
        PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
        PlatformConfig.setAlipay("2015111700822536");
        PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
        PlatformConfig.setPinterest("1439206");
        PlatformConfig.setKakao("e4f60e065048eb031e235c806b31c70f");
        PlatformConfig.setDing("dingoalmlnohc0wggfedpk");
        PlatformConfig.setVKontakte("5764965", "5My6SNliAaLxEm3Lyd9J");
        PlatformConfig.setDropbox("oz8v5apet3arcdy", "h7p2pjbzkkxt02a");
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
