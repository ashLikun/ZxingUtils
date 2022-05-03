package com.ashlikun.zxing.core;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/28 9:52
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：自定义解析Bitmap LuminanceSource
 */
public class BitmapLuminanceSource extends LuminanceSource {

    RGBLuminanceSource source;

    public BitmapLuminanceSource(Bitmap bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight());
        // 首先，要取得该图片的像素数组内容
        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), data);
    }

    @Override
    public byte[] getMatrix() {
        // 返回我们生成好的像素数据
        return source.getMatrix();
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        return source.getRow(y, row);
    }

}
