package com.ashlikun.zxing.core;

import android.graphics.Rect;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:44
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */

public interface Dispatch {

    byte[] dispatch(byte[] data, int width, int height);

    byte[] dispatch(byte[] data, int width, int height, Rect rect);
}
