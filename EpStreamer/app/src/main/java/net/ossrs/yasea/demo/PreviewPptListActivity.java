package net.ossrs.yasea.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.easiio.epstreamer.R;
import com.redking.util.InitX5;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.easiio.epstreamer.wxapi.WXEntryActivity.TAG;

public class PreviewPptListActivity extends Activity {
    private RecyclerView  pptRecycl;
    private PptListAdapter adapter;
    private String FilePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "epstrreamer";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pptlist);
        InitX5.initX5(this);
        pptRecycl = findViewById(R.id.ppt_list);
        pptRecycl.setLayoutManager(new LinearLayoutManager(this));
        pptRecycl.setItemAnimator(new DefaultItemAnimator());
        pptRecycl.addItemDecoration(new SimpleDividerItemDecoration(this));
        getFileMsg();
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
            File source = new File(path.replace("/external_files", ""));
            String fileName = "";
            if (source.exists()) {
                fileName = source.getName();
                Log.e(TAG, "getFileMsg: " + fileName);
            }

            FileUtils.copyFile(source.getPath(), FilePath + File.separator + fileName, new FileUtils.OnReplaceListener() {

                @Override
                public boolean onReplace() {

                    return true;
                }
            });
            getData();
        }

    }
    public void getData(){
        ArrayList<PptModel> pptModels = new ArrayList<>();

        getFiles(pptModels,FilePath);
        adapter = new PptListAdapter(pptModels,this);
        pptRecycl.setAdapter(adapter);
    }

    // 遍历文件
    private void getFiles(ArrayList<PptModel> list, String filePath) {
        File[] allFiles = new File(filePath).listFiles();
        if (allFiles != null) { // 若文件不为空，则遍历文件长度
            for (int i = 0; i < allFiles.length; i++) {
                File file = allFiles[i];

                if (file.isFile()) {
                    PptModel model = new PptModel();
                    model.setPath(file.getPath());
                    model.setName(file.getName());
                    model.setSize(FileUtils.getFileSize(file.getPath()));
                    Log.e(TAG, "getFiles: "+file.getPath()+","+FileUtils.getFileSize(file.getPath()) );
                    model.setType(file.getName().endsWith("pdf")?"pdf":"ppt");
                    list.add(model);
                }
            }
        }
    }

}
