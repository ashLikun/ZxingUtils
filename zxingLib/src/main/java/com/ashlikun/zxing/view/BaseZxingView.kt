package com.ashlikun.zxing.view


import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.ashlikun.zxing.Config
import com.ashlikun.zxing.R
import com.ashlikun.zxing.Result
import com.ashlikun.zxing.able.AbleManager
import com.ashlikun.zxing.helper.ImgParseHelper
import com.ashlikun.zxing.core.ScanTypeConfig
import com.ashlikun.zxing.helper.VibrateHelper
import com.google.android.cameraview.AspectRatio
import com.google.android.cameraview.BaseCameraView
import com.google.android.cameraview.CameraView
import java.lang.ref.WeakReference

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 23:20
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：ZXing 扫码的基础view
 */
typealias OnResult = (Result) -> Unit

interface FreeInterface {
    /***
     * 提供一个扫码区域View, 将根据这个View剪裁数据
     */
    fun provideParseRectView(): View?

    /***
     * 提供一个扫描条View, 需实现[ScanBarCallBack]
     */
    fun provideScanBarView(): ScanBarCallBack?

    /***
     * 提供一个手电筒View,需实现[ScanLightViewCallBack]
     */
    fun provideLightView(): ScanLightViewCallBack?

    /***
     * 提供一个定位点View, 需实现[ScanLocViewCallBack]
     */
    fun provideLocView(): ScanLocViewCallBack?
}

abstract class FreeZxingView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : BaseCameraView(context, attributeSet, def), Handler.Callback, FreeInterface {

    private var ableCollect: AbleManager? = null

    /**
     * 结果回调
     */
    var onResult: OnResult? = null

    init {
        //使用后置相机
        facing = FACING_BACK

        //配置扫码类型
        initScanType()

    }

    /***
     * 自定义扫描条
     */
    private val scanBarView: ScanBarCallBack? get() = provideScanBarView()

    /**
     *自定义手电筒
     */
    private val lightView: ScanLightViewCallBack? get() = provideLightView()

    /***
     * 自定义定位点
     */
    private val locView: ScanLocViewCallBack? get() = provideLocView()

    /***
     * 自定义解析区域
     */
    private val parseRect: View? get() = provideParseRectView()


    private val busHandle by lazy {
        val thread = HandlerThread("BusHandle")
        thread.start()
        BusHandler(this, thread.looper)
    }

    //设定相机数据选取比例
    override fun provideAspectRatio(): AspectRatio {
        return AspectRatio.of(16, 9)
    }

    /***
     * Handler结果回调
     */
    override fun handleMessage(m: Message): Boolean {

        val message = Message.obtain(m)

        post {

            when (message.what) {

                //扫码结果回调
                Config.SCAN_RESULT -> {
                    scanSucHelper()
                    if (message.obj is Result) {
                        showQRLoc(message.obj as Result)
                    }
                }

                //环境亮度变换回调
                Config.LIGHT_CHANGE -> {
                    if (message.obj.toString().toBoolean()) lightView?.lightDark()
                    else lightView?.lightBrighter()
                }

                //放大回调
                Config.AUTO_ZOOM -> {
                    setZoom(message.obj.toString().toFloat())
                }

                //实时探测点位置
                Config.RT_LOCATION -> {
                    //数组长度为3
//                    poinF = message.obj as Array<PointF>
//                    invalidate()
                }
            }
        }

        return true
    }

//    var poinF: Array<PointF>? = null

//    val paint by lazy {
//        val paint = Paint()
//        paint.color = Color.RED
//        paint.textSize = 15f
//        paint.style = Paint.Style.FILL
//        paint
//    }
//
//
//    override fun draw(canvas: Canvas?) {
//        super.draw(canvas)
//        if (poinF != null)
//            poinF?.forEachIndexed { index, it ->
////                canvas?.drawText("$index", pointF.x, pointF.y, paint)
//                canvas?.drawCircle(it.x, it.y, 5f, paint)
//            }
//    }

    /***
     * 相机采集数据实时回调
     */
    override fun onPreviewByteBack(camera: CameraView, data: ByteArray) {
        super.onPreviewByteBack(camera, data)
        //解析数据
        ableCollect?.cusAction(data, Config.scanRect!!.dataX, Config.scanRect!!.dataY)
    }

    /***
     * 扫码成功后的一些动作
     */
    private fun scanSucHelper() {

        //关闭相机
        onCameraPause()

        //清理线程池任务缓存
        ableCollect?.clear()

        //关闭扫码条动画
        scanBarView?.stopScanAnimator()

        //播放音频
        VibrateHelper.playVibrate()

        //震动
        VibrateHelper.playBeep()

    }

    /***
     * Activity或Fragment创建，详见{@link BaseCameraView.synchLifeStart}
     */
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        ableCollect = AbleManager.createInstance(busHandle)
    }

    /***
     * Activity或Fragment不可视，详见{@link BaseCameraView.synchLifeStart}
     */
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        busHandle.enable(false)
        busHandle.removeCallbacksAndMessages(null)
        ableCollect?.clear()
        scanBarView?.stopScanAnimator()
    }

    /***
     * Activity或Fragment生命周期结束销毁，详见{@link BaseCameraView.synchLifeStart}
     */
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        busHandle.looper.quit()
        ableCollect?.release()
    }

    /***
     * 显示二维码位置, 动画播放完回调扫描结果
     */
    fun showQRLoc(result: Result) {
        locView?.toLocation(result) {
            resultBack(result)
            onResult?.invoke(result)
        }
    }

    /***
     * 相机启动后的一些配置初始化
     */
    private fun cameraStartLaterConfig() {
        //自定义
        locView?.cameraStartLaterInit()
        //控件
        scanBarView?.cameraStartLaterInit()
        //初始化
        lightView?.cameraStartLaterInit()
        //设定扫码区域
        post {
            defineScanParseRect(parseRect)
        }
        //注册打开手关闭电筒功能
        lightView?.regLightOperator({
            lightOperator(true)
        }, {
            lightOperator(false)
        })
        lightOperator(false)
        //扫码条开始播放动画
        scanBarView?.startScanAnimator()
        //重新装填AbleManager
        ableCollect?.loadAbility()
        //重新接收数据
        busHandle.enable(true)
        //音频资源加载
        com.ashlikun.zxing.helper.VibrateHelper.playInit()
    }

    /***
     * 扫码结果回调
     */
    abstract fun resultBack(content: Result)

    /***
     * 图片文件扫码
     * 扫码失败返回null，详见{ @link #parseBitmap}
     */
    protected open fun resultBackFile(content: com.google.zxing.Result?) {}

    /***
     * 启动相机后的操作
     */
    override fun onCameraOpenBack(camera: CameraView) {
        super.onCameraOpenBack(camera)
        findViewById<View>(R.id.xZxingProvideViewId)?.let {
            removeView(it)
        }
        LayoutInflater.from(context).inflate(provideFloorView(), this, false)
            .let {
                it.id = R.id.xZxingProvideViewId
                addView(it)
            }
        cameraStartLaterConfig()
    }

    abstract fun provideFloorView(): Int

    /***
     * 配置扫码类型
     */
    private fun initScanType() {
        Config.scanTypeConfig = configScanType()
        Config.isSupportBlackEdge = isSupportBlackEdgeQrScan()
        Config.isSupportAutoZoom = isSupportAutoZoom()
    }

    /***
     * 解析File， 目前默认大小压缩一半并转换ARGB_8888
     */
    protected fun parseFile(filePath: String) {

        proscribeCamera()

        busHandle.removeCallbacksAndMessages(null)
        busHandle.post {
            onParseResult(ImgParseHelper.parseFile(filePath))
        }
    }

    /***
     * 解析Bitmap
     * 解析过程中会关闭相机， 解析失败重新启动
     */
    protected fun parseBitmap(bitmap: Bitmap?) {

        proscribeCamera()

        busHandle.removeCallbacksAndMessages(null)
        busHandle.post {
            onParseResult(ImgParseHelper.parseBitmap(bitmap))
        }
    }

    private fun onParseResult(result: com.google.zxing.Result?) {

        if (result != null && !result.text.isNullOrEmpty()) {
            mainHand.post {
                resultBackFile(result)
                scanSucHelper()
            }
        } else {
            mainHand.post {
                resultBackFile(null)
                unProscibeCamera()
            }
        }
    }

    /***
     * 提供扫码类型
     */
    open fun configScanType(): ScanTypeConfig {
        return ScanTypeConfig.HIGH_FREQUENCY
    }

    /***
     * 是否支持黑边二维码识别-会导致缩放变得灵敏
     * 默认支持
     */
    open fun isSupportBlackEdgeQrScan(): Boolean {
        return true
    }


    /***
     *  是否支持缩放
     */
    open fun isSupportAutoZoom(): Boolean {
        return true
    }

    /***
     * 业务Handler
     */
    class BusHandler constructor(view: Callback, loop: Looper) : Handler(loop) {
        var hasResult = false
        var viewReference: WeakReference<Callback>? = null

        init {
            this@BusHandler.viewReference = WeakReference(view)
        }

        override fun handleMessage(msg: Message?) {
            if (!hasResult)
                return
            msg?.apply {
                if (msg.what == Config.SCAN_RESULT) {
                    enable(false)
                    removeCallbacksAndMessages(null)
                }
                this@BusHandler.viewReference?.get()?.handleMessage(msg)
            }
        }

        fun enable(enable: Boolean) {
            hasResult = enable
        }

    }

}