package com.ashlikun.zxing.core;

import android.graphics.Rect;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:45
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：过曝光处理使用伽马变换
 */
class OverBrightScale implements Dispatch {

    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        double random = (Math.random() * 10f) + 2f;
        for (int i = 0; i < width * height; i++)
            data[i] = (byte) (byte) (255 * Math.pow((data[i] & 0xff) / 255f, random));
        return data;
    }

    @Override
    public byte[] dispatch(byte[] data, int width, int height, Rect rect) {
        byte[] newByte = data.clone();
        double random = (Math.random() * 10f) + 2f;
        for (int start_h = rect.top; start_h < rect.bottom; start_h++) {
            for (int start_w = rect.left; start_w < rect.right; start_w++) {
                int index = start_h * width + start_w;
                newByte[index] = (byte) (byte) (255 * Math.pow((newByte[index] & 0xff) / 255f, random));
            }
        }
        return newByte;
    }
}
