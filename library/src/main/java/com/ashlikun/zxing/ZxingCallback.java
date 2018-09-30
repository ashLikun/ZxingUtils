package com.ashlikun.zxing;

import android.graphics.Bitmap;

import com.google.zxing.Result;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/9/28　10:42
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：事件回调
 */
public interface ZxingCallback {
    /**
     * 摄像头打开失败
     */
    void onOpenCameraFail();

    /**
     * 摄像头打开了
     */
    void onOpenCamera();

    /**
     * 解码成功
     *
     * @param result
     * @param barcode
     */
    void onDecodeSuccess(Result result, Bitmap barcode);
}
