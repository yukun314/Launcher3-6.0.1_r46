package com.zyk.launcher3.setting;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.zyk.launcher3.CellLayout;
import com.zyk.launcher3.Launcher;
import com.zyk.launcher3.R;
import com.zyk.launcher3.config.Config;
import com.zyk.launcher3.util.LongArrayMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        pm = getPackageManager();
        init();
        initNavigation();
    }

    private void init(){
        View SettingDL = findViewById(R.id.activity_setting_default_launcher);
        SettingDL.setOnClickListener(this);
        mDefaultLauncherSwitch = (Switch) findViewById(R.id.activity_setting_default_launcher_switch);

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
        if(v.getId() == R.id.activity_setting_default_launcher) {
            if(!isDefault) {
                setDefaultL();
            }
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
