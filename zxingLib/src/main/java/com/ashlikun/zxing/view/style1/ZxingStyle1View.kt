package com.ashlikun.zxing.view.style1

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import com.ashlikun.zxing.R
import com.ashlikun.zxing.Result
import com.ashlikun.zxing.core.ScanTypeConfig
import com.ashlikun.zxing.view.FreeZxingView
import com.ashlikun.zxing.view.ScanBarCallBack
import com.ashlikun.zxing.view.ScanLightViewCallBack
import com.ashlikun.zxing.view.ScanLocViewCallBack
import com.google.android.cameraview.AspectRatio

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 21:00
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：第一种样式扫描控件
 */
open class ZxingStyle1View @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    FreeZxingView(context, attributeSet, def) {

    val scanRectView by lazy {
        findViewById<View>(R.id.scanRectView)
    }
    val lightView by lazy {
        findViewById<ScanLightView>(R.id.lightView)
    }

    val locView by lazy {
        findViewById<Style1LocationView>(R.id.locView)
    }

    val scanBarView by lazy {
        findViewById<ScanBarView>(R.id.scanBarView)
    }


    override fun resultBack(content: Result) {

    }

    override fun provideFloorView(): Int {
        return R.layout.xzxing_style1_floorview
    }

    override fun provideParseRectView() = scanRectView

    override fun provideLightView(): ScanLightViewCallBack? {
        return lightView
    }

    override fun provideLocView(): ScanLocViewCallBack? {
        return locView
    }

    override fun provideScanBarView(): ScanBarCallBack? {
        return scanBarView
    }

    /***
     * 返回扫码类型
     * 1 ScanTypeConfig.HIGH_FREQUENCY 高频率格式(默认)
     * 2 ScanTypeConfig.ALL  所有格式
     * 3 ScanTypeConfig.ONLY_QR_CODE 仅QR_CODE格式
     * 4 ScanTypeConfig.TWO_DIMENSION 所有二维码格式
     * 5 ScanTypeConfig.ONE_DIMENSION 所有一维码格式
     */
    override fun configScanType(): ScanTypeConfig {
        return ScanTypeConfig.HIGH_FREQUENCY
    }

    open fun toParse(string: String) {
        parseFile(string)
    }

    open fun toParse(bitmap: Bitmap) {
        parseBitmap(bitmap)
    }

    override fun provideAspectRatio(): AspectRatio {
        return AspectRatio.of(16, 9)
    }

}