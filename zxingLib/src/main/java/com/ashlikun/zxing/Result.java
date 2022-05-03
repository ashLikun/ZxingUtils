package com.ashlikun.zxing;

import android.graphics.PointF;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/3 13:38
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：解析的内容
 */

public class Result {

    //二维码内容
    String text;

    //二维码中心坐标(已转换)
    PointF qrPointF;

    //二维码边长(已转换)
    int qrLeng;

    //二维码旋转角度
    float qrRotate;

    //是否旋转过
    boolean isRotate;

    //二维码格式
    BarcodeFormat format;

    public String getText() {
        return text;
    }

    public Result setText(String text) {
        this.text = text;
        return this;
    }

    public PointF getQrPointF() {
        return qrPointF;
    }

    public Result setQrPointF(PointF qrPointF) {
        this.qrPointF = qrPointF;
        return this;
    }

    public boolean isRotate() {
        return isRotate;
    }

    public Result setRotate(boolean rotate) {
        isRotate = rotate;
        return this;
    }

    public int getQrLeng() {
        return qrLeng;
    }

    public Result setQrLeng(int qrLeng) {
        this.qrLeng = qrLeng;
        return this;
    }

    public float getQrRotate() {
        return qrRotate;
    }

    public Result setQrRotate(float qrRotate) {
        this.qrRotate = qrRotate;
        return this;
    }

    public BarcodeFormat getFormat() {
        return format;
    }

    public Result setFormat(BarcodeFormat format) {
        this.format = format;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}
