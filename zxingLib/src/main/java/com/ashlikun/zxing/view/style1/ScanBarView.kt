package com.ashlikun.zxing.view.style1

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.ashlikun.zxing.R
import com.ashlikun.zxing.Utils
import com.ashlikun.zxing.view.ScanBarCallBack

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 21:03
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：自定义扫描条
 */
class ScanBarView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    FrameLayout(context, attributeSet, def), ScanBarCallBack {

    private val BAR_HEIGHT = Utils.dp2px(20f)

    private val ALPHA_LENGHT = 0.2f

    private val barView: ImageView by lazy {
        val view = ImageView(context)
        view.layoutParams = ViewGroup.LayoutParams(-1, BAR_HEIGHT)
        view.setBackgroundResource(R.drawable.xzxing_scan_light)
        view
    }

    init {
        post {
            addView(barView)
            startAnim()
        }
    }

    private var animator: ValueAnimator? = null

    fun startAnim() {
        if (animator != null && animator?.isRunning!!) {
            return
        }

        if (measuredHeight == 0)
            return

        val alpha_height = ALPHA_LENGHT * measuredHeight

        visibility = View.VISIBLE
        animator = ValueAnimator.ofFloat((-BAR_HEIGHT).toFloat(), measuredHeight.toFloat())
            .setDuration(4000)
        animator?.addUpdateListener { it ->
            val values = it.animatedValue as Float
            barView.alpha = if (values <= alpha_height) {
                values / alpha_height
            } else {
                (measuredHeight - values) / alpha_height
            }
            barView.translationY = values
        }
        animator?.repeatCount = Int.MAX_VALUE - 1
        animator?.repeatMode = ValueAnimator.RESTART
        animator?.start()
    }

    fun stopAnim() {
        visibility = View.INVISIBLE
        animator?.cancel()
    }

    override fun startScanAnimator() {
        startAnim()
    }

    override fun cameraStartLaterInit() {

    }

    override fun stopScanAnimator() {
        stopAnim()
    }
}