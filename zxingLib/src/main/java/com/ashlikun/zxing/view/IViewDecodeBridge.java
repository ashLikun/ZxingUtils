package com.ashlikun.zxing.view;

import android.graphics.Rect;

import com.google.zxing.ResultPointCallback;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/9/29　10:53
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：扫描框和解码器交互接口
 */
public interface IViewDecodeBridge extends ResultPointCallback {
    /**
     * 获取二维码放置的矩形区域
     *
     * @return
     */
    public Rect getFramingRect();

}
