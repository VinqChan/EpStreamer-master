package com.easiio.epstreamer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.TextView;

import com.redking.view.fragment.DocOpenFragment;

public class DocOpenActivity extends Activity {

    FragmentManager fragmentManager;

    FragmentTransaction beginTransaction;

    DocOpenFragment docOpenFragment;

    //String fileUrl="http://sources.ikeepstudying.com/jquery.media/guice.pdf";
    String fileUrl;//= Environment.getExternalStorageDirectory().getPath() + File.separator + "test.pptx";

    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_act_doc_demo);
        initView();
        initData();
    }

    private void initView(){
        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    private void initData(){
        if(getIntent().getStringExtra("fileUrl") != null && getIntent().getStringExtra("fileUrl") != "" )
            fileUrl = getIntent().getStringExtra("fileUrl");
        fragmentManager = getFragmentManager();
        beginTransaction = fragmentManager.beginTransaction();
        docOpenFragment = new DocOpenFragment();

        //设置文件路径
        docOpenFragment.setFilePath(fileUrl);

        beginTransaction.add(R.id.ll_doc_frag, docOpenFragment);
        beginTransaction.commit();
        tvTitle.setText(docOpenFragment.getFileName(this));
    }
}