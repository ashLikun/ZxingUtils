package com.ashlikun.zxing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.text.TextUtils;

import com.ashlikun.zxing.camera.CameraManager;
import com.ashlikun.zxing.camera.PlanarYUVLuminanceSource;
import com.ashlikun.zxing.decoding.DecodeFormatManager;
import com.ashlikun.zxing.luminance.RGBHuiLuminanceSource;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;
import java.util.Vector;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/28 9:50
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：二维码工具类
 */

public class CodeUtils {

    /**
     * 解析二维码图片工具类
     *
     * @param analyzeCallback
     */
    public static void analyzeBitmap(String path, AnalyzeCallback analyzeCallback) {

        /**
         * 首先判断图片的大小,若图片过大,则执行图片的裁剪操作,防止OOM
         */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap mBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小

        int sampleSize = (int) (options.outHeight / (float) 400);

        if (sampleSize <= 0) {
            sampleSize = 1;
        }
        options.inSampleSize = sampleSize;
        mBitmap = BitmapFactory.decodeFile(path, options);

        MultiFormatReader reader = new MultiFormatReader();

        // 解码的参数
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
        // 可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();

            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        // 设置继续的字符编码格式为UTF8
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 设置解析配置参数
        reader.setHints(hints);

        repetDecode(reader, mBitmap, analyzeCallback);
    }

    /**
     * 重复尝试解码
     *
     * @param reader
     * @param mBitmap
     * @param analyzeCallback
     */
    private static void repetDecode(MultiFormatReader reader, Bitmap mBitmap, AnalyzeCallback analyzeCallback) {

        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int[] pixels = new int[width * height];
        mBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        // 开始对图像资源解码
        Result rawResult = null;
        try {
            RGBHuiLuminanceSource source = new RGBHuiLuminanceSource(width, height, pixels);
            rawResult = reader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
        } catch (Exception e) {
            e.printStackTrace();
            if (width >= 100 && height >= 100) {
                //失败了再次尝试
                Matrix matrix = new Matrix();
                matrix.postScale(0.7f, 0.7f);
                Bitmap newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
                repetDecode(reader, newBitmap, analyzeCallback);
                mBitmap.recycle();
                return;
            }
        }
        if (rawResult != null) {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeSuccess(mBitmap, rawResult);
            }
        } else {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeFailed();
            }
        }
    }

    /**
     * 生成二维码图片
     */
    public static Bitmap createImage(String text, int w, int h, Bitmap logo) {
        return createImage(text, w, h, true, logo);
    }

    /**
     * 生成二维码图片
     *
     * @param text
     * @param w
     * @param h
     * @param isDeleteWhite 是否删除多余的白边
     * @param logo          中间图标
     * @return
     */
    public static Bitmap createImage(String text, int w, int h, boolean isDeleteWhite, Bitmap logo) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, w, h, hints);
            if (isDeleteWhite) {
                bitMatrix = deleteWhite(bitMatrix);
            }
            w = bitMatrix.getWidth();
            h = bitMatrix.getHeight();

            Bitmap scaleLogo = getScaleLogo(logo, w, h);
            int offsetX = w / 2;
            int offsetY = h / 2;
            int scaleWidth = 0;
            int scaleHeight = 0;
            if (scaleLogo != null) {
                scaleWidth = scaleLogo.getWidth();
                scaleHeight = scaleLogo.getHeight();
                offsetX = (w - scaleWidth) / 2;
                offsetY = (h - scaleHeight) / 2;
            }
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (scaleLogo != null && x >= offsetX && x < offsetX + scaleWidth && y >= offsetY && y < offsetY + scaleHeight) {
                        int pixel = scaleLogo.getPixel(x - offsetX, y - offsetY);
                        if (pixel == 0) {
                            if (bitMatrix.get(x, y)) {
                                pixel = 0xff000000;
                            } else {
                                pixel = 0xffffffff;
                            }
                        }
                        pixels[y * w + x] = pixel;
                    } else {
                        if (bitMatrix.get(x, y)) {
                            pixels[y * w + x] = 0xff000000;
                        } else {
                            pixels[y * w + x] = 0xffffffff;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除二维码白边
     *
     * @param matrix
     * @return
     */
    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }

    private static Bitmap getScaleLogo(Bitmap logo, int w, int h) {
        if (logo == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 / logo.getHeight());
        matrix.postScale(scaleFactor, scaleFactor);
        Bitmap result = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
        return result;
    }

    /**
     * 一种基于格式构建适当的亮度资源对象的工厂方法
     * 在预览缓冲区中，如Camera.Parameters所述。
     *
     * @param data   一个预览帧。
     * @param width  图像的宽度。
     * @param height 图像的高度
     * @return A PlanarYUVLuminanceSource instance.
     */
    public static PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height, Rect framingRect) {
        Rect rect = CameraManager.get().getFramingRectInPreview(framingRect);
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                rect.width(), rect.height());
    }
}
