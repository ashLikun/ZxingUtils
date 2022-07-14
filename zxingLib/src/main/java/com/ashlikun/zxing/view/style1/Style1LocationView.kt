package com.ashlikun.zxing.view.style1

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.ashlikun.zxing.R
import com.ashlikun.zxing.Result
import com.ashlikun.zxing.view.ScanLocViewCallBack

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:59
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：扫描的自定义定位点
 */

class Style1LocationView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    AppCompatImageView(context, attributeSet, def), ScanLocViewCallBack {

    override fun cameraStartLaterInit() {
        visibility = View.GONE
        setImageResource(R.drawable.xzxing_ic_qr_loc)
    }

    override fun toLocation(result: Result, run: Runnable) {
        var qrPoint = result.qrPointF ?: return
        visibility = View.VISIBLE
        translationX = (qrPoint.x - layoutParams.width / 2)
        translationY = (qrPoint.y - layoutParams.height / 2)
        scaleX = 0f
        scaleY = 0f
        animate().scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    run.run()
                }
            })
            .start()
    }


}