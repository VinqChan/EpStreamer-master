package net.ossrs.yasea.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.ImageView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.easiio.epstreamer.R;

public class BootActivity extends Activity {
    private static final String TAG = "BootActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏

        setContentView(R.layout.activity_splash);
        Utils.init(this);
        ImageView logo = findViewById(R.id.icon_splash);
        boolean language = SPUtils.getInstance().getBoolean("language", false);
        if (language) {
            logo.setImageResource(R.mipmap.icon_welcome_en);
        } else {
            logo.setImageResource(R.mipmap.icon_welcom);
        }
        Thread myThread = new Thread() {//创建子线程
            @Override
            public void run() {
                try {
                    sleep(2000);//使程序休眠五秒
                    Intent it = new Intent(getApplicationContext(), MainActivity.class);//启动MainActivity
                    startActivity(it);
                    finish();//关闭当前活动
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();//启动线程

    }
}
