package com.zyk.launcher3.config;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zyk.launcher3.Launcher;
import com.zyk.launcher3.LauncherModel;
import com.zyk.launcher3.R;
import com.zyk.launcher3.safety.LockInfo;
import com.zyk.launcher3.util.BitmapUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by zyk on 2016/6/26.
 * 在Launcher的oncreate方法中init，在onDestroy中close
 */
public class Config {
    private static Config mConfig;

    public static Config getInstance(){
        if(mConfig == null){
            throw new NullPointerException("Config 还没有初始化！");
        }
        return mConfig;
    }

    public static void close(){
        mConfig = null;
    }

    public static void init(Launcher launcher){
        mConfig = new Config(launcher);
    }

    private WeakReference<Launcher> mLauncherReference;
    public Bitmap iconShape;
    public int iconBg;
    public List<LockInfo> mLockList;

    private Config(Launcher launcher){
        mLauncherReference = new WeakReference<Launcher>(launcher);
        Resources res = launcher.getResources();
        //FIXME 这里的所有变量 都是可以设置的
//        iconBg = res.getColor(R.color.icon_bg_color);
//        iconShape = BitmapUtil.getBitmapFromResources(launcher,R.drawable.shape_rectangle1,launcher.getDeviceProfile().iconSizePx);

        mLockList = LauncherModel.loadLockFromDatabase(launcher);
    }

    public Launcher getLauncher(){
        return mLauncherReference.get();
    }

}
