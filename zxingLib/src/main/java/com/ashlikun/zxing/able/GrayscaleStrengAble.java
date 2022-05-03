package com.ashlikun.zxing.able;

import android.graphics.PointF;
import android.os.Handler;

import com.ashlikun.zxing.Config;
import com.ashlikun.zxing.helper.ScanHelper;
import com.ashlikun.zxing.core.LightGreySource;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:48
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
class GrayscaleStrengAble extends PixsValuesAble {

    public GrayscaleStrengAble(Handler handler) {
        super(handler);
    }

    protected Result result;

    int i = -1;

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source, boolean isNative) {
        if (result != null)
            return;

        i++;
        if (i % 2 != 1) {
            return;
        }

        //浅色二维码增强
        result = toLaunchParse(new HybridBinarizer(new LightGreySource(source)));

        if (result != null && !"".equals(result.getText())) {
            sendMessage(Config.SCAN_RESULT, covertResult(result));
        }
    }

    protected com.ashlikun.zxing.Result covertResult(Result result) {
        com.ashlikun.zxing.Result result_ = new com.ashlikun.zxing.Result();
        PointF[] pointFS = ScanHelper.rotatePoint(result.getResultPoints());
        result_.setQrPointF(ScanHelper.calCenterPointF(pointFS));
        result_.setQrLeng(ScanHelper.calQrLenghtShow(result.getResultPoints()));
        result_.setFormat(result.getBarcodeFormat());
        result_.setQrRotate(ScanHelper.calQrRotate(pointFS));
        return result_;
    }

}
