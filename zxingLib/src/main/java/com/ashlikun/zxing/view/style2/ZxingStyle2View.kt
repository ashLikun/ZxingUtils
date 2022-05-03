package com.ashlikun.zxing.view.style2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.ashlikun.zxing.R
import com.ashlikun.zxing.Result
import com.ashlikun.zxing.view.FreeZxingView
import com.ashlikun.zxing.view.ScanBarCallBack
import com.ashlikun.zxing.view.ScanLightViewCallBack
import com.ashlikun.zxing.view.ScanLocViewCallBack
import com.ashlikun.zxing.view.style1.ScanLightView

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 22:54
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：第二种样式扫描控件
 */

open class ZxingStyle2View @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    FreeZxingView(context, attributeSet, def) {
    val scanRectView by lazy {
        findViewById<View>(R.id.scanRectView)
    }
    val lightView by lazy {
        findViewById<ScanLightView>(R.id.lightView)
    }

    val locView by lazy {
        findViewById<Style2LocationView>(R.id.locView)
    }

    override fun resultBack(content: Result) {

    }

    override fun provideFloorView(): Int {
        return R.layout.xzxing_style2_floorview
    }

    override fun provideParseRectView(): View? {
        return scanRectView
    }

    override fun provideLightView(): ScanLightViewCallBack? {
        return lightView
    }

    override fun provideLocView(): ScanLocViewCallBack? {
        return locView
    }

    override fun provideScanBarView(): ScanBarCallBack? {
        return null
    }

}