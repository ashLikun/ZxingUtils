package com.ashlikun.zxing

import com.ashlikun.zxing.core.Config
import com.ashlikun.zxing.core.GrayScaleDispatch
import com.ashlikun.zxing.zxing.ScanRect
import com.ashlikun.zxing.zxing.ScanTypeConfig

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/3 13:37
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：
 */
object Config {
    //当前变焦倍率
    @JvmField
    var currentZoom = 0f
    /*###############################################*/
    /***
     * Handler 返回标识
     */
    //扫码结果回调
    const val SCAN_RESULT = 0

    //环境亮度变化
    const val LIGHT_CHANGE = 1

    //自动缩放
    const val AUTO_ZOOM = 2

    //实时二维码探测点位置
    const val RT_LOCATION = 3

    //###############################################
    //扫码类型
    @JvmField
    var scanTypeConfig = ScanTypeConfig.HIGH_FREQUENCY

    //扫码区域
    @JvmField
    var scanRect: ScanRect? = null
    fun initConfig() {
        currentZoom = 0f
        displayOrientation = 0
        scanRect = ScanRect()
    }

    /*###############################################*/
    @JvmField
    var displayOrientation = 0

    //屏幕方向
    fun is0(): Boolean {
        return displayOrientation == 0
    }

    @JvmStatic
    fun is90(): Boolean {
        return displayOrientation == 90
    }

    @JvmStatic
    fun is270(): Boolean {
        return displayOrientation == 270
    }

    /*###############################################*/
    //灰度算法
    fun hasDepencidesScale(): Boolean {
        try {
            Class.forName(GrayScaleDispatch::class.java.name)
        } catch (e: ClassNotFoundException) {
            return false
        }
        return true
    }

    /*###############################################*/

    //是否支持黑边二维码识别
    var isSupportBlackEdge = true
        set(value) {
            field = value
            runCatching {
                Config.isSupportBlackEdge = value
            }
        }

    //是否支持自动缩放
    var isSupportAutoZoom = true
        set(value) {
            field = value
            runCatching {
                Config.isSupportAutoZoom = value
            }
        }
}