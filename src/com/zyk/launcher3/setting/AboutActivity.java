package com.zyk.launcher3.setting;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.zyk.launcher3.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by zyk on 2016/7/17.
 */
public class AboutActivity extends Activity {

    private final int CODE_FOR_WRITE_PERMISSION = 101;
    private Bitmap mBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();
        initNavigation();
    }

    private void init(){

        TextView image = (TextView) findViewById(R.id.activity_about_dashang_tv2);
        Drawable topImage = getResources().getDrawable(R.drawable.dashang);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthPixels= dm.widthPixels/2;
        float density = dm.density;
        int imageSize = (int)(150*density);
        if(imageSize>widthPixels){
            imageSize = widthPixels;
        }
        topImage.setBounds(0,0,imageSize,imageSize);
        image.setCompoundDrawables(null, topImage, null, null);
//        image.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                openApp("tencent.mm");
//                return true;
//            }
//        });

        View save = findViewById(R.id.activity_about_dashang_tv3);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = AboutActivity.this.getApplicationContext();
                mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dashang);
                saveImageToGallery();
            }
        });

    }

    //不能 默认 打开支付的url
    private void openApp(String str) {
        //应用过滤条件
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager mPackageManager = this.getPackageManager();
        List<ResolveInfo> mAllApps = mPackageManager.queryIntentActivities(mainIntent, 0);
        //按报名排序
        Collections.sort(mAllApps, new ResolveInfo.DisplayNameComparator(mPackageManager));

        for (ResolveInfo res : mAllApps) {
            //该应用的包名和主Activity
            String pkg = res.activityInfo.packageName;
            String cls = res.activityInfo.name;
            System.out.println("pkg---" + pkg);

            // 打开QQ pkg中包含"qq"，打开微信，pkg中包含"mm"
            if (pkg.contains(str)) {
                ComponentName componet = new ComponentName(pkg, cls);
                Intent intent = new Intent();
                Uri url = Uri.parse("www.baidu.com");//https://wx.tenpay.com/f2f?t=AQAAACllJFzmijxxxcqSyoIKnns%3D
                intent.setData(url);
                intent.setComponent(componet);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
            }
        }
    }

    private void initNavigation(){
        View view = findViewById(R.id.activity_about_navigation);
        ImageView back = (ImageView) view.findViewById(R.id.activity_navigation_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView message = (TextView) view.findViewById(R.id.activity_navigation_message);
        message.setText(R.string.setting_about);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_FOR_WRITE_PERMISSION){
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //用户同意使用write
                saveImageToGallery();
            }else{
                //用户不同意，自行处理即可
                Toast.makeText(getApplicationContext(),"保存失败，没有使用sd卡的权限",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void saveImageToGallery() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CODE_FOR_WRITE_PERMISSION);
                return;
            }
        }
//        String str = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "MyQRCode", "MyQRCode");
        // 首先保存图片
        File appDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/isLauncher");

        if (!appDir.exists()) {
            boolean isSuccess = appDir.mkdir();
        }
        String fileName = "MyQRCode.jpg";
        final File file = new File(appDir, fileName);
        System.out.println("file:"+file.getAbsolutePath());
        try {
            if (!file.exists()) {
                file.createNewFile();

            }

            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(mBitmap.isRecycled()){
                mBitmap.recycle();
            }
        }
//        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),
//                    file.getAbsolutePath(), fileName, "MyQRCode");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//         //最后通知图库更新 FIXME 没有添加下面的通知也会再图库中显示
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        getApplicationContext().sendBroadcast(intent);
        Toast.makeText(getApplicationContext(), "二维码保存成功!", Toast.LENGTH_SHORT).show();
    }

}
