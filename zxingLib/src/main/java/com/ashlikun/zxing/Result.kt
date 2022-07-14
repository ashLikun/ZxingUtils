package com.ashlikun.zxing

import android.graphics.PointF
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/3 13:38
 * 邮箱　　：496546144@qq.com
 *
 *
 * 功能介绍：解析的内容
 */
class Result {
    //二维码内容
    var text: String = ""

    //二维码中心坐标(已转换)
    var qrPointF: PointF? = null

    //二维码边长(已转换)
    var qrLeng = 0

    //二维码旋转角度
    var qrRotate = 0f

    //是否旋转过
    var isRotate = false

    //二维码格式
    var format: BarcodeFormat? = null

    //原生数据
    var result: Result? = null

    override fun toString(): String {
        return text
    }
}