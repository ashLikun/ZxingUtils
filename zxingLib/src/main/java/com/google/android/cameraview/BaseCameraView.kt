package com.google.android.cameraview

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.ashlikun.zxing.Config
import com.ashlikun.zxing.Utils
import com.ashlikun.zxing.helper.ZoomHelper

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/4 1:43
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
abstract class BaseCameraView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : CameraView(context, attributeSet, def), DefaultLifecycleObserver {

    //保证避免多次调用start()
    private var isShoudCreateOpen = true

    //是否禁止相机
    private var isProscribeCamera = false

    init {
        Utils.init(context)
        Config.initConfig()
        autoFocus = true
        adjustViewBounds = false
        this.addCallback(object : Callback() {

            override fun onCameraOpened(cameraView: CameraView) {
                mainHand.post {
                    onCameraOpenBack(cameraView)
                }
            }

            override fun onCameraClosed(cameraView: CameraView) {
                mainHand.post {
                    onCameraCloseBack(cameraView)
                }
            }

            override fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
                mainHand.post {
                    onPictureTakeBack(cameraView, data)
                }
            }

            override fun onPreviewByte(cameraView: CameraView, data: ByteArray?) {
                if (data != null)
                    this@BaseCameraView.onPreviewByteBack(cameraView, data)
            }

        })
    }

    open fun onCameraOpenBack(camera: CameraView) {
        ZoomHelper.toAutoZoom(this)
    }

    open fun onCameraCloseBack(camera: CameraView) {
        ZoomHelper.close(this)
    }

    open fun onPictureTakeBack(camera: CameraView, data: ByteArray) {
    }

    open fun onPreviewByteBack(camera: CameraView, data: ByteArray) {
    }

    /***
     * 绑定生命周期并启动相机
     */
    fun synchLifeStart(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)

    }

    fun synchLifeStart(activity: AppCompatActivity) = this.synchLifeStart(activity.lifecycle)

    fun synchLifeStart(fragment: Fragment) = this.synchLifeStart(fragment.lifecycle)

    private fun onCameraCreate() {
        if (!isShoudCreateOpen)
            return
        if (Utils.checkPermissionCamera(context)) {
            openCameraBefore()
            openCamera()
        } else {
            Utils.requstPermission(context)
        }
    }

    /***
     *  外部使用该方法启动相机
     */
    fun onCameraResume() {
        if (isShoudCreateOpen) {
            return
        }
        if (Utils.checkPermissionCamera(context) && !isCameraOpened) {
            openCameraBefore()
            openCamera(if (cameraStartTime != 0L) cameraStartTime else 100L)
        }
    }

    /***
     *  外部使用该方法暂停相机
     */
    fun onCameraPause() {
        closeCameraBefore()
        closeCamera()
        isShoudCreateOpen = false
    }

    /***
     *  外部使用该方法动态调整相机输出比例
     */
    override fun setAspectRatio(ratio: AspectRatio) {
        super.setAspectRatio(ratio)
        //相机运行过程中切换比例
        if (isCameraOpened) {
            closeCameraBefore()
            openCameraBefore()
            cameraHandler.removeCallbacksAndMessages(null)
            cameraHandler.post {
                stop()
                start()
            }
        }
    }

    /***
     *  执行操作：
     *        1 ： 启动关闭相机
     *        2 ： 处理相机相关数据回调，拍照，YUV数据
     */
    private val cameraHandler by lazy {
        val handlerThread = HandlerThread("CameraProcessThread")
        handlerThread.start()
        Handler(handlerThread.looper)
            .apply {
                provideCameraHandler(this)
            }
    }

    var cameraStartTime = 0L

    private fun openCamera(delayValues: Long = 0L) {
        cameraHandler.removeCallbacksAndMessages(null)
        cameraHandler.postDelayed({
            if (!isProscribeCamera) {
                val var0 = System.currentTimeMillis()
                start()
                cameraStartTime = System.currentTimeMillis() - var0
            }
        }, delayValues)
    }

    private fun closeCamera() {
        cameraHandler.removeCallbacksAndMessages(null)
        cameraHandler.postDelayed({
            stop()
        }, cameraStartTime)
    }

    /***
     * 数字变焦
     */
    fun setZoom(percent: Float) {
        when {
            percent >= 1f -> mImpl.toZoomMax()
            percent <= 0f -> mImpl.toZoomMin()
            else -> mImpl.setZoom(percent)
        }
        //捕获当前倍率
        Config.currentZoom = percent.let {
            when {
                it <= 0 -> 0f
                it >= 1 -> 1f
                else -> it
            }
        }
    }

    /***
     * 打开/关闭 闪光灯
     */
    fun lightOperator(isOpen: Boolean) {
        mImpl.lightOperator(isOpen)
    }

    @CallSuper
    override fun onCreate(owner: LifecycleOwner) {
        onCameraCreate()
        super.onCreate(owner)
    }

    @CallSuper
    override fun onResume(owner: LifecycleOwner) {
        onCameraResume()
        super.onResume(owner)
    }

    @CallSuper
    override fun onPause(owner: LifecycleOwner) {
        onCameraPause()
        super.onPause(owner)
    }

    @CallSuper
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        cameraHandler.looper.quit()
    }

    /***
     * 禁止相机启用
     */
    fun proscribeCamera() {
        isProscribeCamera = true
        onCameraPause()
    }

    /***
     * 允许并启用
     */
    fun unProscibeCamera() {
        isProscribeCamera = false
        onCameraResume()
    }
}