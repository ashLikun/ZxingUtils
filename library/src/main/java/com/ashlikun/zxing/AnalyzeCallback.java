package com.ashlikun.zxing;

import android.graphics.Bitmap;

import com.google.zxing.Result;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/9/28　17:25
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：对图片解码的回调
 */
public interface AnalyzeCallback {

    /**
     * 解码成功
     *
     * @param mBitmap
     * @param result
     */
    public void onAnalyzeSuccess(Bitmap mBitmap, Result result);

    /**
     * 解码失败
     */
    public void onAnalyzeFailed();
}
