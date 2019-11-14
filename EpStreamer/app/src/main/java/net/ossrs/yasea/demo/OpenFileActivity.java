package net.ossrs.yasea.demo;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

public class OpenFileActivity extends Activity {
    private static final String TAG = "OpenFileActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("file/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), 1);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == MainActivity.mUploadCallbackAboveFive) {
            return;
        }
        String dataString ="";
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                 dataString = data.getDataString();
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
                }
            }
        }
        Log.e(TAG, "onActivityResult: " + dataString);
        if (results[0] != null) {
            MainActivity.mUploadCallbackAboveFive.onReceiveValue(results);
            MainActivity.mUploadCallbackAboveFive = null;
        }
//        Intent i = new Intent();
//        i.putExtra("result", dataString);
//        setResult(2, i);
        //finish();

        super.onActivityResult(requestCode, resultCode, data);

    }
}
