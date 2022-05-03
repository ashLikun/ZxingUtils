package com.ashlikun.zxing.view

import android.view.View
/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:52
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
interface FreeInterface {

    /***
     * 提供一个扫码区域View, 将根据这个View剪裁数据
     */
    fun provideParseRectView(): View?

    /***
     * 提供一个扫描条View, 需实现[ScanBarCallBack]
     */
    fun provideScanBarView(): com.ashlikun.zxing.view.ScanBarCallBack?

    /***
     * 提供一个手电筒View,需实现[ScanLightViewCallBack]
     */
    fun provideLightView(): com.ashlikun.zxing.view.ScanLightViewCallBack?

    /***
     * 提供一个定位点View, 需实现[ScanLocViewCallBack]
     */
    fun provideLocView(): com.ashlikun.zxing.view.ScanLocViewCallBack?


}