package com.ashlikun.zxing

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.hardware.Camera
import android.view.TextureView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.ashlikun.zxing.camera.CameraManager
import com.ashlikun.zxing.decoding.DecodeHandler
import com.ashlikun.zxing.decoding.DecodeParams
import com.ashlikun.zxing.view.IViewDecodeBridge
import com.google.zxing.BarcodeFormat
import java.util.*

/**
 * 作者　　: 李坤
 * 创建时间: 2018/9/28　10:03
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：封装zxing扫码的实现
 */
class Zxing(
    var textureView: TextureView,
    @JvmField
    var callback: ZxingCallback,
    var decodeFormats: Vector<BarcodeFormat?>?,
    var characterSet: String?,
    var bridge: IViewDecodeBridge?
) {
    private val context by lazy {
        textureView.context
    }
    protected var decodeHandler: DecodeHandler? = null
    private val decodeAudio: DecodeAudio?
    var decodeParams: DecodeParams
    var surfaceTextureListener: ZxingSurfaceTextureListener? = null

    /**
     * @param textureView   渲染的view
     * @param callback      事件回调
     * @param decodeFormats 解码格式，可以null
     * @param characterSet  字符编码，可以null
     * @param bridge        解码器与view的交互
     */
    init {
        decodeAudio = DecodeAudio(context)
        decodeParams = DecodeParams(
            decodeFormats, characterSet,
            bridge, null
        )
        //自动生命周期
        val activity = getActivity(context)
        if (activity is LifecycleOwner) {
            activity.lifecycle.addObserver(LifecycleEventObserver { source, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> onResume()
                    Lifecycle.Event.ON_PAUSE -> onPause()
                    Lifecycle.Event.ON_DESTROY -> onDestroy()
                }
            })
        }
    }

    private fun getActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) return context
        else if (context is ContextWrapper) return getActivity(context.baseContext)
        return null
    }

    /**
     * 监听亮度改变,不为null内部就会解析图片的亮度
     *
     * @param lightChangListener
     */
    fun setLightChangListener(lightChangListener: OnLightChangListener?) {
        decodeParams.setLightChangListener(lightChangListener)
    }

    /**
     * 设置是否开启音效
     * 只有响铃模式才开启
     *
     * @param playBeep
     */
    fun setPlayBeep(playBeep: Boolean) {
        decodeAudio!!.setPlayBeep(playBeep)
    }

    /**
     * 开始识别,内部调用,由SurfaceTextureListener监听调用
     */
    fun neibuStart() {
        decodeHandler = DecodeHandler(
            decodeParams,
            decodeAudio, callback
        )
        decodeHandler?.restartPreviewAndDecode()
    }

    /**
     * 设置完全部配置后调用
     */
    fun create() {
        //这个初始化可能直接启动摄像头了，放到最后
        surfaceTextureListener = ZxingSurfaceTextureListener(textureView, this)
        textureView.surfaceTextureListener = surfaceTextureListener
    }

    /**
     * 成功后可以调用这个方法继续识别
     */
    fun reStart() {
        decodeHandler?.restartPreviewAndDecode()
    }

    /**
     * 生命周期
     */
    fun onResume() {
        if (decodeHandler != null) {
            if (surfaceTextureListener != null) {
                surfaceTextureListener!!.startPreview()
            }
            decodeHandler!!.start()
        }
    }

    /**
     * 生命周期
     */
    fun onPause() {
        if (decodeHandler != null) {
            decodeHandler!!.stop()
            if (surfaceTextureListener != null) {
                surfaceTextureListener!!.stopPreview()
            }
        }
    }

    /**
     * 生命周期
     */
    fun onDestroy() {
        decodeAudio?.release()
        if (surfaceTextureListener != null) {
            surfaceTextureListener!!.onDestroy()
        }
    }

    companion object {
        /**
         * 闪光灯操作
         *
         * @param isEnable
         */
        fun isLightEnable(isEnable: Boolean) {
            if (isEnable) {
                val camera = CameraManager.get().camera
                if (camera != null) {
                    val parameter = camera.parameters
                    parameter.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                    camera.parameters = parameter
                }
            } else {
                val camera = CameraManager.get().camera
                if (camera != null) {
                    val parameter = camera.parameters
                    parameter.flashMode = Camera.Parameters.FLASH_MODE_OFF
                    camera.parameters = parameter
                }
            }
        }
    }


}