package com.zyk.launcher3.widget;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zyk.launcher3.R;
import com.zyk.launcher3.config.Config;
import com.zyk.launcher3.json.ApkVersion;

import java.io.File;
import java.lang.ref.SoftReference;

/**
 * Created by zhuyk on 2016/2/20.
 */
public class UpdateDialog extends PopupWindow {
//    private Activity activity;
    private SoftReference<Activity> mActivity;
    private ApkVersion versionInfo;
    private boolean isdownloaded = false;//检测是否已经下载最新版本

    public UpdateDialog(Activity activity,ApkVersion versionInfo) {
        mActivity = new SoftReference<Activity>(activity);
        this.versionInfo = versionInfo;
        init();
    }

    private void init(){
        LayoutInflater inflater = (LayoutInflater) mActivity.get()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View conentView = inflater.inflate(R.layout.update_dialog, null);
//        int h = activity.getWindowManager().getDefaultDisplay().getHeight();
//        int w = activity.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimationPreview);
        initView(conentView);
    }

    private void initView(View view){
        ImageView wifi = (ImageView) view.findViewById(R.id.update_wifi_indicator);
        if(isWifi(mActivity.get())){
           wifi.setVisibility(View.GONE);
        }

//        CheckBox cb = (CheckBox) view.findViewById(R.id.update_id_check);
//        cb.setVisibility(View.GONE);

        String path = mActivity.get().getExternalFilesDir("download").getAbsolutePath();
//        System.out.println("path:"+path);
        final File file = new File(path,"IS桌面"+versionInfo.versionName+".apk");
//        System.out.println("file:"+file.exists()+"  file.path:"+file.getAbsolutePath());
        if(file != null && file.exists()){
            isdownloaded = true;
        }else{
            isdownloaded = false;
        }

        //显示更新版本
        TextView  version = (TextView) view.findViewById(R.id.update_version);
        version.setText("最新版本:V"+versionInfo.versionName);
        //显示更新版本大小
        TextView  size = (TextView) view.findViewById(R.id.update_size);
        size.setText("新版本大小:"+versionInfo.versionSize);
        //显示更新日志
        TextView  content = (TextView) view.findViewById(R.id.update_content);
        content.setText("更新内容\n"+versionInfo.versionContent);

        Button ok = (Button) view.findViewById(R.id.update_id_ok);
        if(isdownloaded){
            ok.setText(R.string.install_now);
        }else{
            ok.setText(R.string.now_upate);
        }
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(isdownloaded){
//                    UmengUpdateAgent.startInstall(activity,downFile);
//                }else{
//                    UmengUpdateAgent.startDownload(activity,updateInfo);
//                }
                if(isdownloaded){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file),
                            "application/vnd.android.package-archive");
                    mActivity.get().startActivity(intent);
                }else {
                    downlodApk();
                }
                dismiss();
            }
        });

        Button cancel = (Button) view.findViewById(R.id.update_id_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void downlodApk(){
        DownloadManager downloadManager = (DownloadManager)mActivity.get().getSystemService(Context.DOWNLOAD_SERVICE);
        String apkUrl = Config.baseURL+"/"+versionInfo.downloadUrl;
//        System.out.println("apkUrl:"+apkUrl);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        File file = mActivity.get().getExternalFilesDir("download");
//        System.out.println("file.path:"+file.getAbsolutePath());
        if(!file.exists()){
            file.mkdirs();
        }
//        request.setDestinationInExternalPublicDir("", "IS桌面"+versionInfo.versionName+".apk");
        request.setDestinationInExternalFilesDir(mActivity.get(), "download", "IS桌面"+versionInfo.versionName+".apk");
//        System.out.println("versionInfo:"+versionInfo.toString());
        request.setTitle("IS桌面"+versionInfo.versionName);
        request.setDescription(versionInfo.versionContent);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
// request.setMimeType("application/cn.trinea.download.file");
        long downloadId = downloadManager.enqueue(request);
    }

    public void show(){
        if (!this.isShowing()) {
            Window window = mActivity.get().getWindow();
            View  parent = window.getDecorView();
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
        }
    }


    private static boolean isWifi(Context mContext) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null
                    && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }catch (Exception e){}
        return false;
    }
}
