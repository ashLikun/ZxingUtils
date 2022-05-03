package com.ashlikun.zxing.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import com.ashlikun.zxing.Utils
import com.google.zxing.BinaryBitmap
import com.google.zxing.Result
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import java.io.File


/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:50
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
object ImgparseHelper {

    fun parseFile(filePath: String): Result? {
        val file = File(filePath)
        if (!file.exists())
            return null
        val bitmap: Bitmap = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Utils.getContext()?.let { it ->
                it.contentResolver.let { reso ->
                    ImageDecoder.createSource(reso, Utils.getMediaUriFromPath(it, filePath))
                }.let {
                    ImageDecoder.decodeBitmap(it) { decoder, _, _ ->
                        decoder.setTargetSampleSize(2)
                        decoder.isMutableRequired = true
                    }
                }
            }
        } else
            BitmapFactory.decodeFile(filePath, BitmapFactory.Options().apply {
                inSampleSize = 2
            })) ?: return null

        return parseBitmap(bitmap)
    }

    fun parseBitmap(bitmap: Bitmap?): Result? {
        if (bitmap == null)
            return null
        val source = com.ashlikun.zxing.zxing.BitmapLuminanceSource(bitmap)
        return com.ashlikun.zxing.zxing.CustomMultiFormatReader.getInstance()
                .decode(BinaryBitmap(GlobalHistogramBinarizer(source)))
                ?: com.ashlikun.zxing.zxing.CustomMultiFormatReader.getInstance()
                        .decode(BinaryBitmap(HybridBinarizer(source)))
    }

}