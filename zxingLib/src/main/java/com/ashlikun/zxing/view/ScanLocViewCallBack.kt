package com.ashlikun.zxing.view

import com.ashlikun.zxing.Result

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:59
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：扫描的自定义定位点
 */
interface ScanLocViewCallBack : CameraStarLater {
    /***
     * @param result 扫码结果
     * @param runnable 扫码结果回调
     */
    fun toLocation(result: Result, runnable: Runnable)
}