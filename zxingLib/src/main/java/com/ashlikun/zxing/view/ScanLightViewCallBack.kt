package com.ashlikun.zxing.view

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:59
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：自定义手电筒
 */
interface ScanLightViewCallBack : CameraStarLater {
    //光线变亮
    fun lightBrighter()

    //光线变暗
    fun lightDark()

    //闪光灯打开关闭
    fun regLightOperator(open: Runnable, close: Runnable)
}