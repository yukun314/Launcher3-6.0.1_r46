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
import android.graphics.Rect;

import com.zyk.launcher3.R;
import com.zyk.launcher3.config.Config;

/**
 * Created by zyk on 2016/6/26.
 */
public class BitmapUtil {

    /**
     * FIXME mask 大小已经是icon的大小
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

    /**
     * 把两个位图覆盖合成为一个位图，以底层位图的长宽为基准
     * @param backBitmap 在底部的位图
     * @param frontBitmap 盖在上面的位图
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap) {
//        System.out.println("mergeBitmap");
        if (frontBitmap == null || frontBitmap.isRecycled()) {
            return backBitmap;
        }

//        Matrix matrix = new Matrix();
//        //将形状图片缩放到和需要剪切的图片一样的大小
//        mImageChanged = Bitmap.createBitmap(mImage, 0, 0, mImage.getWidth(),mImage.getHeight(), matrix, true);
//        matrix.postScale((float)backBitmap.getWidth()/(float)frontBitmap.getWidth() * 4.0f,
//                (float)backBitmap.getHeight()/(float)frontBitmap.getHeight()* 4.0f) ;
//        //将形状图片缩放到和需要剪切的图片一样的大小
//        Bitmap mMask1 = Bitmap.createBitmap(frontBitmap, 0, 0, frontBitmap.getWidth(), frontBitmap.getHeight(), matrix, true);
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
//        Rect baseRect  = new Rect(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
//        Rect frontRect = new Rect(0, 0, mMask1.getWidth(), mMask1.getHeight());
//        canvas.drawBitmap(mMask1, frontRect, baseRect, null);
//        canvas.drawBitmap(frontBitmap,new Rect(0,0,frontBitmap.getWidth(),frontBitmap.getHeight()),
//                new Rect(0,0,backBitmap.getWidth()/2,backBitmap.getHeight()/2),null);
        canvas.drawBitmap(frontBitmap,backBitmap.getWidth() - frontBitmap.getWidth(),backBitmap.getHeight() - frontBitmap.getHeight(),null);
        return bitmap;
    }

    public static Bitmap getBitmapFromResources(Context context, int id,int imageWidth){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), id, options);
        int sampleSize = Math.round((float) options.outWidth / (float) imageWidth);
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), id, options);
    }
}
