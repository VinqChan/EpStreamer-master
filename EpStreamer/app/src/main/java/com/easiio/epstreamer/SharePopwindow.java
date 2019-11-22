package com.easiio.epstreamer;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.umeng.socialize.bean.SHARE_MEDIA;

public class SharePopwindow {
    public static PopupWindow popupWindow;
    public static View contentView;
    public static Activity mActivity;
    public static View qqShare;
    public static View weixinShare;
    public static View facebookShare;
    public static View linkedInShare;
    public static View twitterShare;
    public static View cancel;
    public static String url;
    public static String title;

    public static void showPopupWindow(final Activity activity,String murl,String mtitle) {
        //加载弹出框的布局
        contentView = LayoutInflater.from(activity).inflate(
                R.layout.popwindow, null);

        url =murl;
        title = mtitle;
        mActivity = activity;
        qqShare = contentView.findViewById(R.id.qq);
        weixinShare = contentView.findViewById(R.id.weixin);
        facebookShare = contentView.findViewById(R.id.facebook);
        linkedInShare = contentView.findViewById(R.id.linkedIn);
        twitterShare = contentView.findViewById(R.id.twitter);
        cancel = contentView.findViewById(R.id.cancel_tv);
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
//        ColorDrawable dw = new ColorDrawable(-00000);
//        popupWindow.setBackgroundDrawable(dw);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        setBackgroundAlpha(0.5f);//设置屏幕透明度
        //设置可以点击
        popupWindow.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        popupWindow.setAnimationStyle(R.style.share_animation);
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);

        qqShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareWeb(activity, url, title
                        , Defaultcontent.text, Defaultcontent.imageurl, R.mipmap.ep_launcher, SHARE_MEDIA.QQ

                );
                popupWindow.dismiss();
            }
        });

        weixinShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtils.shareWeb(activity, url, title
                        , Defaultcontent.text, Defaultcontent.imageurl, R.mipmap.ep_launcher, SHARE_MEDIA.WEIXIN
                );
                popupWindow.dismiss();
            }
        });

        facebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ShareLinkContent content = new ShareLinkContent.Builder()
//                        .setContentUrl(Uri.parse(url))
//                        .build();
//                ShareDialog.show(activity, content);
                ShareUtils.shareWeb(activity, url, title
                        , Defaultcontent.text, Defaultcontent.imageurl, R.mipmap.ep_launcher, SHARE_MEDIA.FACEBOOK
                );
                popupWindow.dismiss();
            }
        });

        linkedInShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        twitterShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
                setBackgroundAlpha(1.0f);
            }
        });
    }
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     *            屏幕透明度0.0-1.0 1表示完全不透明
     */
    public static void setBackgroundAlpha(float bgAlpha) {
        final WindowManager.LayoutParams lp = mActivity.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.getWindow().setAttributes(lp);
            }
        });

    }
}
