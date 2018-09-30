package com.ashlikun.zxing;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/9/29　16:34
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：亮度改变监听
 */
public interface OnLightChangListener {
    /**
     * 光线变化
     *
     * @param dark 是否变暗
     */
    public void onLightChang(boolean dark);
}
