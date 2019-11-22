package net.ossrs.yasea.demo;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.easiio.epstreamer.R;
import com.redking.util.InitX5;

import java.io.File;
import java.util.ArrayList;


public class PreviewPptListActivity extends Activity {
    private RecyclerView  pptRecycl;
    public static final String TAG = "PreviewPptListActivity";
    private PptListAdapter adapter;
    private String FilePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "epstrreamer";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pptlist);
        InitX5.initX5(this);
        pptRecycl = findViewById(R.id.ppt_list);
         findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
        pptRecycl.setLayoutManager(new LinearLayoutManager(this));
        pptRecycl.setItemAnimator(new DefaultItemAnimator());
        pptRecycl.addItemDecoration(new SimpleDividerItemDecoration(this));
        getFileMsg();
        getData();
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
            if(path.contains("external_files")){
                path = path.replace("/external_files", "");
            }
            if(path.contains("external")){
                path = path.replace("/external", "/storage/emulated/0");
            }
            Log.e(TAG, "getFileMsg: "+path );
            File source = new File(path);
            String fileName = "";
            if (source.exists()) {
                fileName = source.getName();
                Log.e(TAG, "getFileMsg: " + fileName);
            }
            Log.e(TAG, "getFileMsg: "+source.length()+","+source.getPath()+","+source.getAbsolutePath() );

            DownloadManager manager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.addCompletedDownload(source.getName(), source.getName(), true, "file/*", source.getPath(), source.length(),false);
            FileUtils.copyFile(source.getPath(), FilePath + File.separator + fileName, new FileUtils.OnReplaceListener() {

                @Override
                public boolean onReplace() {

                    return true;
                }
            });

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
