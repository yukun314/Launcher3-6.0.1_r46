package com.zyk.launcher3.setting;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PatternMatcher;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zyk.launcher3.BuildConfig;
import com.zyk.launcher3.CellLayout;
import com.zyk.launcher3.Launcher;
import com.zyk.launcher3.R;
import com.zyk.launcher3.config.Config;
import com.zyk.launcher3.json.ApkVersion;
import com.zyk.launcher3.util.LongArrayMap;
import com.zyk.launcher3.widget.UpdateDialog;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by zyk on 2016/7/2.
 * 设置界面
 */
public class SettingActivity extends Activity implements View.OnClickListener{

    private Switch mDefaultLauncherSwitch;

    private PackageManager pm;
    private boolean isDefault;
    private ResolveInfo mResolveInfo;

    private ProgressDialog mProgressDialog;
    private UpdateDialog mUpdateDialog;
    private Thread checkUpdateThread;

    private final int noupdate = 0;
    private final int haveupdate = 1;
    private final int error = 3;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if(what == noupdate){
                Toast.makeText(SettingActivity.this, "您安装的已是最新版本了!", Toast.LENGTH_SHORT).show();
                System.out.println("您安装的已是最新版本了");
            }else if(what == error){
                Toast.makeText(SettingActivity.this, "遇到了错误!"+msg.obj, Toast.LENGTH_SHORT).show();
                System.out.println("遇到了错误"+msg.obj);
            }else if(what == haveupdate){
                ApkVersion versionInfo = (ApkVersion) msg.obj;
                System.out.println("有新版本");
                showUpdateDialog((ApkVersion) msg.obj);
            }
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        pm = getPackageManager();
        init();
        initNavigation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        if(checkUpdateThread!= null && checkUpdateThread.isAlive()){
            checkUpdateThread.interrupt();
            checkUpdateThread = null;
        }
    }

    private void init(){
        View SettingDL = findViewById(R.id.activity_setting_default_launcher);
        SettingDL.setOnClickListener(this);
        mDefaultLauncherSwitch = (Switch) findViewById(R.id.activity_setting_default_launcher_switch);

        View about = findViewById(R.id.activity_setting_about);
        about.setOnClickListener(this);

        View update = findViewById(R.id.activity_about_version);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdate();
            }
        });

        initValue();
    }

    private void initNavigation(){
        View view = findViewById(R.id.activity_setting_navigation);
        ImageView back = (ImageView) view.findViewById(R.id.activity_navigation_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView message = (TextView) view.findViewById(R.id.activity_navigation_message);
        message.setText(R.string.settings_button_text);
    }

    private void initValue(){
        Intent intent = new Intent(Intent.ACTION_MAIN);//Intent.ACTION_VIEW
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        isDefault = isDefault(SettingActivity.this, intent);
        if(isDefault) {
            mDefaultLauncherSwitch.setChecked(true);
        } else {
            mDefaultLauncherSwitch.setChecked(false);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.activity_setting_default_launcher) {
            if(!isDefault) {
                setDefaultL();
            }else {
                Toast.makeText(SettingActivity.this, "IS桌面已经是默认桌面不需要再设置", Toast.LENGTH_SHORT).show();
            }
        } else if(id == R.id.activity_setting_about) {
            Intent intent = new Intent();
            intent.setClass(SettingActivity.this, AboutActivity.class);
            SettingActivity.this.startActivity(intent);
        }
    }

    /**
     * 判断自己是否为默认
     * @return
     */
    public final boolean isDefault(final Context context, final Intent intent) {
        PackageManager pm = context.getPackageManager();
        ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isDefault = context.getPackageName().equals(info.activityInfo.packageName);
        return isDefault;
    }

    private void setDefaultL(){
//        Intent intent = new Intent(Intent.ACTION_MAIN);//Intent.ACTION_VIEW
//                intent.addCategory("android.intent.category.HOME");
//                intent.addCategory("android.intent.category.DEFAULT");
//                SettingActivity.this.startActivity(intent);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory("android.intent.category.HOME");
        try {
            intent.setComponent(new ComponentName("android","com.android.internal.app.ResolverActivity"));
            SettingActivity.this.startActivity(intent);
        }catch (Exception e){
            try {
                intent.setComponent(new ComponentName("com.huawei.android.internal.app", "com.huawei.android.internal.app.HwResolverActivity"));
                SettingActivity.this.startActivity(intent);
            } catch (Exception e1){
                intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                SettingActivity.this.startActivity(intent);
            }
        }
    }

    //检测是否有新版本
    private void checkUpdate() {
        showDialog();
        checkUpdateThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Message message = new Message();
                try {
                    URL url = new URL(Config.baseURL + "/appVersion.json");
                    //打开到url的连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //以下为java IO部分，大体来说就是先检查文件夹是否存在，不存在则创建,然后的文件名重复问题，没有考虑
                    InputStream in = connection.getInputStream();
                    StringBuffer out = new StringBuffer();
                    byte[] b = new byte[512];
                    for (int n; (n = in.read(b)) != -1; ) {
                        out.append(new String(b, 0, n));
                    }
                    in.close();
                    String returnStr = out.toString();

                    if (returnStr != null && !returnStr.equals("error")) {
                        Gson returnM = new Gson();
                        ApkVersion apkVersion = returnM.fromJson(returnStr, ApkVersion.class);
                        int versionCode = BuildConfig.VERSION_CODE;
                        int newVersionCode = Integer.parseInt(apkVersion.versionCode);
                        if (versionCode < newVersionCode) {
                            message.what = haveupdate;
                            message.obj = apkVersion;
                        } else {
                            message.what = noupdate;
                        }
                    } else {
                        message.what = error;
                        message.obj = "超时或服务器没有返回";
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    message.what = error;
                    message.obj = "服务器无响应，请稍后再试！";;
                } catch (IOException e) {
                    e.printStackTrace();
                    message.what = error;
                    message.obj = "服务器无响应，请稍后再试！";
                }
                mHandler.sendMessage(message);
            }
        };
        checkUpdateThread.start();

    }

    private void showUpdateDialog(ApkVersion versionInfo){
        if(mUpdateDialog == null){
            mUpdateDialog = new UpdateDialog(this, versionInfo);
        }
        mUpdateDialog.show();
    }

    private void showDialog(){
        if(mProgressDialog == null) {
            //创建ProgressDialog对象
            mProgressDialog = new ProgressDialog(this);
            // 设置进度条风格，风格为圆形，旋转的
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // 设置ProgressDialog 标题
            mProgressDialog.setTitle(null);
            // 设置ProgressDialog 提示信息
            mProgressDialog.setMessage("正在检测新版本");
            // 设置ProgressDialog 标题图标
//        mProgressDialog.setIcon(R.drawable.a);
            // 设置ProgressDialog 的进度条是否不明确
            mProgressDialog.setIndeterminate(false);
            // 设置ProgressDialog 是否可以按退回按键取消
            mProgressDialog.setCancelable(true);
            //设置ProgressDialog 的一个Button
//        mProgressDialog.setButton("确定", new SureButtonListener());
            // 让ProgressDialog显示
        }
        mProgressDialog.show();
    }

    /**
     * 获取所有launcher型应用
     * @return
     */
    private List<ResolveInfo> getAllHomeApp(){
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
        return resolveInfoList;
    }

//    /**
//     * 清除默认桌面（采用先设置一个空的桌面为默认然后在将该空桌面禁用的方式来实现）
//     * 在网上查的，结果没鸟用
//     */
//    public void clearDefaultLauncher() {
//        PackageManager pm = SettingActivity.this.getPackageManager();
////        String pn = SettingActivity.this.getPackageName();
////        String hn = SettingActivity.class.getName();
//        String pn = "com.zyk.launcher3";
//        String hn = "com.zyk.launcher3.Launcher";
//        ComponentName mhCN = new ComponentName(pn, hn);
//        Intent homeIntent = new Intent("android.intent.action.MAIN");
//        homeIntent.addCategory("android.intent.category.HOME");
//        homeIntent.addCategory("android.intent.category.DEFAULT");
//        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        pm.setComponentEnabledSetting(mhCN, 1, 1);
//        SettingActivity.this.startActivity(homeIntent);
//        pm.setComponentEnabledSetting(mhCN, 0, 1);
//    }

//    /**
//     * 这是 系统设置默认的代码 需要权限
//     * android.permission.SET_PREFERRED_APPLICATIONS
//     * 非系统应用不能获得该权限
//     */
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    private void aa(ResolveInfo ri, Intent intent){
//        if (true) {
//            // Build a reasonable intent filter, based on what matched.
//            IntentFilter filter = new IntentFilter();
//
//            if (intent.getAction() != null) {
//                filter.addAction(intent.getAction());
//            }
//            Set<String> categories = intent.getCategories();
//            if (categories != null) {
//                for (String cat : categories) {
//                    filter.addCategory(cat);
//                }
//            }
//            filter.addCategory(Intent.CATEGORY_DEFAULT);
//
//            int cat = ri.match&IntentFilter.MATCH_CATEGORY_MASK;
//            Uri data = intent.getData();
//            if (cat == IntentFilter.MATCH_CATEGORY_TYPE) {
//                String mimeType = intent.resolveType(this);
//                if (mimeType != null) {
//                    try {
//                        filter.addDataType(mimeType);
//                    } catch (IntentFilter.MalformedMimeTypeException e) {
//                        Log.w("ResolverActivity", e);
//                        filter = null;
//                    }
//                }
//            }
//            if (data != null && data.getScheme() != null) {
//                // We need the data specification if there was no type,
//                // OR if the scheme is not one of our magical "file:"
//                // or "content:" schemes (see IntentFilter for the reason).
//                if (cat != IntentFilter.MATCH_CATEGORY_TYPE
//                        || (!"file".equals(data.getScheme())
//                        && !"content".equals(data.getScheme()))) {
//                    filter.addDataScheme(data.getScheme());
//
//                    // Look through the resolved filter to determine which part
//                    // of it matched the original Intent.
//                    Iterator<PatternMatcher> pIt = ri.filter.schemeSpecificPartsIterator();
//                    if (pIt != null) {
//                        String ssp = data.getSchemeSpecificPart();
//                        while (ssp != null && pIt.hasNext()) {
//                            PatternMatcher p = pIt.next();
//                            if (p.match(ssp)) {
//                                filter.addDataSchemeSpecificPart(p.getPath(), p.getType());
//                                break;
//                            }
//                        }
//                    }
//                    Iterator<IntentFilter.AuthorityEntry> aIt = ri.filter.authoritiesIterator();
//                    if (aIt != null) {
//                        while (aIt.hasNext()) {
//                            IntentFilter.AuthorityEntry a = aIt.next();
//                            if (a.match(data) >= 0) {
//                                int port = a.getPort();
//                                filter.addDataAuthority(a.getHost(),
//                                        port >= 0 ? Integer.toString(port) : null);
//                                break;
//                            }
//                        }
//                    }
//                    pIt = ri.filter.pathsIterator();
//                    if (pIt != null) {
//                        String path = data.getPath();
//                        while (path != null && pIt.hasNext()) {
//                            PatternMatcher p = pIt.next();
//                            if (p.match(path)) {
//                                filter.addDataPath(p.getPath(), p.getType());
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (filter != null) {
//                List<ResolveInfo> list = getAllHomeApp();
//                final int N = list.size();
//                ComponentName[] set = new ComponentName[N];
//                int bestMatch = 0;
//                for (int i=0; i<N; i++) {
//                    ResolveInfo r = list.get(i);
//                    set[i] = new ComponentName(r.activityInfo.packageName,
//                            r.activityInfo.name);
//                    if (r.match > bestMatch) bestMatch = r.match;
//                }
////                if (alwaysCheck) {
//                getPackageManager().addPreferredActivity(filter, bestMatch, set,
//                        intent.getComponent());
////                } else {
////                    try {
////                        AppGlobals.getPackageManager().setLastChosenActivity(intent,
////                                intent.resolveTypeIfNeeded(getContentResolver()),
////                                PackageManager.MATCH_DEFAULT_ONLY,
////                                filter, bestMatch, intent.getComponent());
////                    } catch (RemoteException re) {
////                        Log.d(TAG, "Error calling setLastChosenActivity\n" + re);
////                    }
////                }
//            }
//        }
//
//        if (intent != null) {
//            startActivity(intent);
//        }
//    }
}
