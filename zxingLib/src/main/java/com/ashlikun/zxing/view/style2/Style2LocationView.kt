package com.ashlikun.zxing.view.style2

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import com.ashlikun.zxing.Result
import com.ashlikun.zxing.view.ScanLocViewCallBack


/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 22:52
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：扫描的自定义定位点
 */

class Style2LocationView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    androidx.appcompat.widget.AppCompatImageView(context, attributeSet, def), ScanLocViewCallBack {

    private var animator: ObjectAnimator? = null

    override fun cameraStartLaterInit() {
        startAnim()
    }

    fun startAnim() {
        animator?.cancel()
        val scaleYProper = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.8f, 1f)
        val scaleXProper = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.8f, 1f)
        animator = ObjectAnimator.ofPropertyValuesHolder(this, scaleYProper, scaleXProper)
        animator?.repeatMode = ValueAnimator.RESTART
        animator?.duration = 2000
        animator?.repeatCount = Int.MAX_VALUE - 1
        animator?.start()
    }

    override fun toLocation(result: Result, runnable: Runnable) {
        var params = layoutParams
        result.qrPointF ?: return
        result.let {
            it.qrPointF?.also { p ->
                translationX = p.x - it.qrLeng * 1f
                translationY = p.y - it.qrLeng * 1f
            }

            rotation = it.qrRotate
            params.width = (it.qrLeng * 2f).toInt()
            params.height = (it.qrLeng * 2f).toInt()
            (params as FrameLayout.LayoutParams).gravity = Gravity.TOP or Gravity.LEFT
            layoutParams = params
        }
        runnable?.run()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnim()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}