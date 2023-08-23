package com.ashlikun.zxing.simple

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import com.ashlikun.zxing.Result
import com.ashlikun.zxing.view.style2.ZxingStyle2View
import com.ashlikun.zxing.core.ScanTypeConfig
import com.ashlikun.zxing.view.style0.ZxingStyle0View
import com.google.android.cameraview.AspectRatio


/**
 * @author　　: 李坤
 * 创建时间: 2022/5/3 21:00
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */

class CusScanView0 @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : ZxingStyle0View(context, attributeSet, def) {

    override fun resultBack(content: Result) {
        Toast.makeText(context, content.text, Toast.LENGTH_LONG).show()
    }

    /***
     * 返回扫码类型
     * 1 ScanTypeConfig.HIGH_FREQUENCY 高频率格式(默认)
     * 2 ScanTypeConfig.ALL  所有格式
     * 3 ScanTypeConfig.ONLY_QR_CODE 仅QR_CODE格式
     * 4 ScanTypeConfig.TWO_DIMENSION 所有二维码格式
     * 5 ScanTypeConfig.ONE_DIMENSION 所有一维码格式
     */
    override fun configScanType(): ScanTypeConfig {
        return ScanTypeConfig.HIGH_FREQUENCY
    }

    fun toParse(string: String) {
        parseFile(string)
    }


    override fun resultBackFile(content: Result?) {
        if (content == null)
            Toast.makeText(context, "未扫描到内容", Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, content.text, Toast.LENGTH_SHORT).show()
    }

    override fun isSupportAutoZoom(): Boolean {
        return false
    }

}