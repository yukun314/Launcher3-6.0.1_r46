package com.zyk.launcher3.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.zyk.launcher3.R;

/**
 * Created by zyk on 2016/6/26.
 */
public class BitmapUtil {

    /**
     * 把图片剪切成指定的形状
     * @param bitmap 需要剪切的图片
     * @param mask 形状图片
     * @param bgColor 背景颜色
     * @return
     */
    public static Bitmap cutPic(Bitmap bitmap, Bitmap mask, int bgColor){
        if(mask == null || bitmap == null) {
//            System.out.println("bitmap is null:"+(bitmap == null) +"  mask is null:"+(mask == null));
            return bitmap;
        }
        Bitmap mImage=bitmap;
        Bitmap mMask=mask;
        Bitmap result=null;
        Matrix matrix = new Matrix();
//        matrix.postScale((float)mMask.getWidth()/(float)mImage.getWidth(),(float)mMask.getHeight()/(float)mImage.getHeight() );
//        //将形状图片缩放到和需要剪切的图片一样的大小
//        mImageChanged = Bitmap.createBitmap(mImage, 0, 0, mImage.getWidth(),mImage.getHeight(), matrix, true);
        matrix.postScale((float)mImage.getWidth()/(float)mMask.getWidth(),
                (float)mImage.getHeight()/(float)mMask.getHeight());
        //将形状图片缩放到和需要剪切的图片一样的大小
        Bitmap mMask1 = Bitmap.createBitmap(mMask, 0, 0, mMask.getWidth(), mMask.getHeight(), matrix, true);
        result = Bitmap.createBitmap(mImage.getWidth(),mImage.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bg = Bitmap.createBitmap(mImage.getWidth(),mImage.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Canvas bgCanvas = new Canvas(bg);
        Paint paint =new Paint();
        paint.setColor(bgColor);
        paint.setAntiAlias(true);

        //切割模式，取交集上层
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        mCanvas.drawBitmap(mMask, 0, 0, null);
//        mCanvas.drawBitmap(mImageChanged, 0, 0, paint);
        mCanvas.drawBitmap(mMask1, 0, 0, null);
        mCanvas.drawBitmap(mImage, 0, 0, paint);
        bgCanvas.drawBitmap(mMask1, 0, 0, null);
        bgCanvas.drawRect(0,0,mImage.getWidth(),mImage.getHeight(),paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
//        mCanvas.drawBitmap(mMask1, 0, 0, paint);
        mCanvas.drawBitmap(bg, 0, 0, paint);
        paint.setXfermode(null);
        return result;
    }
}
