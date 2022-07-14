package com.ashlikun.zxing.helper

import android.graphics.*
import com.google.zxing.EncodeHintType
import java.util.EnumMap
import kotlin.jvm.JvmOverloads
import com.google.zxing.MultiFormatWriter
import com.google.zxing.BarcodeFormat
import java.lang.Exception
import android.text.TextUtils
import java.util.HashMap
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import android.os.Build
import com.ashlikun.zxing.Utils
import com.ashlikun.zxing.core.BitmapLuminanceSource
import com.ashlikun.zxing.core.CustomMultiFormatReader
import com.ashlikun.zxing.toResult
import com.google.zxing.BinaryBitmap
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import java.io.File

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:50
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：创建二维码图片
 */
object QRCodeHelper {
    val HINTS: MutableMap<EncodeHintType, Any?> = EnumMap(EncodeHintType::class.java)

    init {
        HINTS[EncodeHintType.CHARACTER_SET] = "utf-8"
        HINTS[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        HINTS[EncodeHintType.MARGIN] = 0
    }

    /**
     * 同步创建指定前景色、白色背景色、带logo的二维码图片。该方法是耗时操作，请在子线程中调用。
     *
     * @param content         要生成的二维码图片内容
     * @param size            图片宽高，单位为px
     * @param foregroundColor 二维码图片的前景色
     * @param logo            二维码图片的logo
     */
    fun syncEncodeQRCode(content: String?, size: Int, foregroundColor: Int, logo: Bitmap?): Bitmap? {
        return syncEncodeQRCode(content, size, foregroundColor, Color.WHITE, logo)
    }

    /**
     * 同步创建指定前景色、指定背景色、带logo的二维码图片。该方法是耗时操作，请在子线程中调用。
     *
     * @param content         要生成的二维码图片内容
     * @param size            图片宽高，单位为px
     * @param foregroundColor 二维码图片的前景色
     * @param backgroundColor 二维码图片的背景色
     * @param logo            二维码图片的logo
     */
    @JvmOverloads
    fun syncEncodeQRCode(content: String?,
                         size: Int,
                         foregroundColor: Int = Color.BLACK,
                         backgroundColor: Int = Color.WHITE,
                         logo: Bitmap? = null): Bitmap? {
        return try {
            val matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, HINTS)
            val pixels = IntArray(size * size)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    if (matrix[x, y]) {
                        pixels[y * size + x] = foregroundColor
                    } else {
                        pixels[y * size + x] = backgroundColor
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
            addLogoToQRCode(bitmap, logo)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 添加logo到二维码图片上
     */
    private fun addLogoToQRCode(src: Bitmap?, logo: Bitmap?): Bitmap? {
        if (src == null || logo == null) {
            return src
        }
        val srcWidth = src.width
        val srcHeight = src.height
        val logoWidth = logo.width
        val logoHeight = logo.height
        val scaleFactor = srcWidth * 1.0f / 5 / logoWidth
        var bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
        try {
            val canvas = Canvas(bitmap!!)
            canvas.drawBitmap(src, 0f, 0f, null)
            canvas.scale(scaleFactor, scaleFactor, (srcWidth / 2).toFloat(), (srcHeight / 2).toFloat())
            canvas.drawBitmap(logo, ((srcWidth - logoWidth) / 2).toFloat(), ((srcHeight - logoHeight) / 2).toFloat(), null)
            canvas.save()
            canvas.restore()
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap = null
        }
        return bitmap
    }

    /**
     * 同步创建条形码图片
     *
     * @param content  要生成条形码包含的内容
     * @param width    条形码的宽度，单位px
     * @param height   条形码的高度，单位px
     * @param textSize 字体大小，单位px，如果等于0则不在底部绘制文字
     * @return 返回生成条形的位图
     */
    fun syncEncodeBarcode(content: String?, width: Int, height: Int, textSize: Int): Bitmap? {
        if (content.isNullOrEmpty()) {
            return null
        }
        val hints: MutableMap<EncodeHintType, Any?> = HashMap()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        hints[EncodeHintType.MARGIN] = 0
        try {
            val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, width, height, hints)
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitMatrix[x, y]) {
                        pixels[y * width + x] = -0x1000000
                    } else {
                        pixels[y * width + x] = -0x1
                    }
                }
            }
            var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap!!.setPixels(pixels, 0, width, 0, 0, width, height)
            if (textSize > 0) {
                bitmap = showContent(bitmap, content, textSize)
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 显示条形的内容
     *
     * @param barcodeBitmap 已生成的条形码的位图
     * @param content       条形码包含的内容
     * @param textSize      字体大小，单位px
     * @return 返回生成的新条形码位图
     */
    private fun showContent(barcodeBitmap: Bitmap?, content: String?, textSize: Int): Bitmap? {
        if (TextUtils.isEmpty(content) || null == barcodeBitmap) {
            return null
        }
        val paint = Paint()
        paint.color = Color.BLACK
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.textSize = textSize.toFloat()
        paint.textAlign = Paint.Align.CENTER
        val textWidth = paint.measureText(content).toInt()
        val fm = paint.fontMetrics
        val textHeight = (fm.bottom - fm.top).toInt()
        val scaleRateX = barcodeBitmap.width * 1.0f / textWidth
        if (scaleRateX < 1) {
            paint.textScaleX = scaleRateX
        }
        val baseLine = barcodeBitmap.height + textHeight
        val bitmap = Bitmap.createBitmap(barcodeBitmap.width, barcodeBitmap.height + 2 * textHeight, Bitmap.Config.ARGB_4444)
        val canvas = Canvas()
        canvas.drawColor(Color.WHITE)
        canvas.setBitmap(bitmap)
        canvas.drawBitmap(barcodeBitmap, 0f, 0f, null)
        canvas.drawText(content!!, (barcodeBitmap.width / 2).toFloat(), baseLine.toFloat(), paint)
        canvas.save()
        canvas.restore()
        return bitmap
    }

    /**
     * 解码
     */
    fun syncDecodeFile(filePath: String): com.ashlikun.zxing.Result? {
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

        return syncDecodeBitmap(bitmap)
    }

    /**
     * 解码
     */
    fun syncDecodeBitmap(bitmap: Bitmap?): com.ashlikun.zxing.Result? {
        if (bitmap == null)
            return null
        val source = BitmapLuminanceSource(bitmap)
        val result = CustomMultiFormatReader.getInstance()
            .decode(BinaryBitmap(GlobalHistogramBinarizer(source)))
            ?: CustomMultiFormatReader.getInstance()
                .decode(BinaryBitmap(HybridBinarizer(source)))
        return result?.toResult()
    }

}