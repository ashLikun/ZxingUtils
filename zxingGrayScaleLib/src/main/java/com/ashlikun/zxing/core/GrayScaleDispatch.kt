package com.ashlikun.zxing.core

import android.graphics.Rect
import java.util.*


/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:44
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */

object GrayScaleDispatch : Dispatch {

    private var grayScaleProcess = ArrayList<Dispatch>()

    var random = Random()

    init {
        grayScaleProcess.add(LightGreyScale())
        grayScaleProcess.add(OverBrightScale())
        grayScaleProcess.add(OverDarkScale())
        grayScaleProcess.add(RevGrayScale())
//        grayScaleProcess.add(OverlyGrayScale())
        grayScaleProcess.add(InterruptGrayScale())
//        grayScaleProcess.add(ReductionAreaScale(this))
    }

    fun getScaleList(): List<Dispatch> {
        return grayScaleProcess
    }

    override fun dispatch(data: ByteArray?, width: Int, height: Int): ByteArray {
        return grayScaleProcess[random.nextInt(grayScaleProcess.size)].dispatch(
                data, width, height)
    }

    override fun dispatch(data: ByteArray?, width: Int, height: Int, rect: Rect?): ByteArray {

        if (rect == null || (rect.left == 0 && rect.right == 0) ||
                (rect.top == 0 && rect.bottom == 0))
            return dispatch(data, width, height)

        return grayScaleProcess[random.nextInt(grayScaleProcess.size)].dispatch(
                data, width, height, rect)
    }

}