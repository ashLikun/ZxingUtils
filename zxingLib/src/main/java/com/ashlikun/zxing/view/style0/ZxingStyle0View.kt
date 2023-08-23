package com.ashlikun.zxing.view.style0

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.ashlikun.zxing.R
import com.ashlikun.zxing.Result
import com.ashlikun.zxing.view.FreeZxingView
import com.ashlikun.zxing.view.ScanBarCallBack
import com.ashlikun.zxing.view.ScanLightViewCallBack
import com.ashlikun.zxing.view.ScanLocViewCallBack
import com.ashlikun.zxing.view.style1.ScanBarView
import com.ashlikun.zxing.view.style1.ScanLightView
import com.ashlikun.zxing.view.style1.Style1LocationView
import com.google.android.cameraview.CameraView

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 22:54
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：没有任何东西，全屏扫描
 */

open class ZxingStyle0View @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    FreeZxingView(context, attributeSet, def) {
    override val provideFloorViewById: Int? = null

    override fun resultBack(content: Result) {

    }
}