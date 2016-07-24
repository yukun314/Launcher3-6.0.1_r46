package com.zyk.launcher3.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zyk.launcher3.R;
import com.zyk.launcher3.safety.MD5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zyk on 2016/7/17.
 */
public class AboutActivity extends Activity {

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

        View save = findViewById(R.id.activity_about_dashang_tv3);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = AboutActivity.this.getApplicationContext();
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dashang);
                saveImageToGallery(context, bitmap);
            }
        });

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

    public void saveImageToGallery(Context context, Bitmap bmp) {

        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ISLauncher/");

        if (!appDir.exists()) {
            boolean isSuccess = appDir.mkdirs();
            System.out.println("文件夹不存在 创建:"+isSuccess);
        }
        System.out.println("appDir:" + appDir.getAbsolutePath());
        String fileName = MD5.md5("MyQRCode") + ".jpg";
        final File file = new File(appDir, fileName);
        System.out.println("file:"+file.getAbsolutePath());
        try {
            if (!file.exists()) {
                System.out.println("文件不存在 创建");
                file.createNewFile();

            }

            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        System.out.println("uri:" + uri);
        intent.setData(uri);
        context.sendBroadcast(intent);
        Toast.makeText(context, "保存成功!", Toast.LENGTH_SHORT).show();
    }

}
