package com.ashlikun.zxing.core;

import android.graphics.Rect;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:45
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
class OverlyGrayScale implements Dispatch {

    TranslationScale translationScale;

    OverlyGrayScale() {
        translationScale = new TranslationScale(10, 10);
    }

    @Override
    public byte[] dispatch(byte[] data, int width, int height) {
        return data;
    }

    @Override
    public byte[] dispatch(byte[] data, int width, int height, Rect rect) {
        byte[] newByte = data.clone();
        byte[] tranByte = translationScale.dispatch(data, width, height, rect);

        int stepX = 2;
        int stepY = 2;

        for (int start_h = rect.top; start_h < rect.bottom; start_h += stepY) {
            for (int start_w = rect.left; start_w < rect.right; start_w += stepX) {
                int oriAvage = 0;
                int tranAvage = 0;
                int min = Integer.MAX_VALUE;
                for (int i = start_h; i < start_h + stepY; i++)
                    for (int j = start_w; j < start_w + stepX; j++) {
                        oriAvage += (newByte[i * width + j] & 0xff);
                        tranAvage += (tranByte[i * width + j] & 0xff);
                        if ((tranByte[i * width + j] & 0xff) < min)
                            min = tranByte[i * width + j] & 0xff;
                    }

                if (oriAvage <= tranAvage) {
                    continue;
                }

                for (int i = start_h; i < start_h + stepY; i++)
                    System.arraycopy(tranByte, i * width + start_w, newByte, i * width + start_w, start_w + stepX - start_w);
            }
        }
        return newByte;
    }
}
