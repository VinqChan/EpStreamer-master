package net.ossrs.yasea.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class BootActivity  extends Activity {
    private static final String TAG = "BootActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(BootActivity.this,MainActivity.class));
        finish();
    }
}
