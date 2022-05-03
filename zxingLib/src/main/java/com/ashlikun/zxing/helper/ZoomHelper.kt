package com.ashlikun.zxing.helper

import android.annotation.SuppressLint
import com.ashlikun.zxing.Config
import com.ashlikun.zxing.OnGestureListener
import com.google.android.cameraview.BaseCameraView
/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:52
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：变焦Helper
 */
object ZoomHelper {

    var currentOnce: Float = 0f

    @SuppressLint("ClickableViewAccessibility")
    fun toAutoZoom(view: BaseCameraView) {
        Config.currentZoom = 0f
        view.setOnTouchListener(object : OnGestureListener(view.context) {
            override fun onStepFingerChange(total: Float, offset: Float) {

                if (currentOnce == 0f)
                    currentOnce = Config.currentZoom

                currentOnce += offset / 8000
                view.setZoom(currentOnce.let {
                    when {
                        it > 1f -> 1f
                        it < 0f -> 0f
                        else -> it
                    }
                })
            }

            override fun onDoubleClick() {
                view.setZoom(Config.currentZoom + 0.03f)
            }

            override fun onStepEnd() {
                currentOnce = 0f
            }

        })
    }

    fun close(view: BaseCameraView) {
        view.setOnTouchListener(null)
    }

}