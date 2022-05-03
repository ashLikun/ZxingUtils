package com.ashlikun.zxing.view.style1

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.ashlikun.zxing.R
import com.ashlikun.zxing.Result
import com.ashlikun.zxing.view.FreeZxingView
import com.ashlikun.zxing.view.ScanBarCallBack
import com.ashlikun.zxing.view.ScanLightViewCallBack
import com.ashlikun.zxing.view.ScanLocViewCallBack

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 21:00
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：第一种样式扫描控件
 */
open class NBZxingView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    FreeZxingView(context, attributeSet, def) {

    val scanRectView by lazy {
        findViewById<View>(R.id.scanRectView)
    }
    val lightView by lazy {
        findViewById<ScanLightView>(R.id.lightView)
    }

    val locView by lazy {
        findViewById<LocationView>(R.id.locView)
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

}