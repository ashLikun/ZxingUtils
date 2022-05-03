package com.ashlikun.zxing.core;

import android.graphics.Rect;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:45
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：浅灰色灰度增强
 */
class LightGreyScale implements Dispatch {

    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        short random = (short) (Math.random() * 5 + 2);
        for (int i = 0; i < width * height; i++) {
            data[i] = (byte) (data[i] * random);
        }
        return data;
    }

    @Override
    public byte[] dispatch(byte[] data, int width, int height, Rect rect) {
        byte[] newByte = data.clone();
        short random = (short) (Math.random() * 4 + 3);
        for (int start_h = rect.top; start_h < rect.bottom; start_h++) {
            for (int start_w = rect.left; start_w < rect.right; start_w++) {
                int index = start_h * width + start_w;
                newByte[index] = (byte) (newByte[index] * random);
            }
        }
        return newByte;
    }
}
