package com.ashlikun.zxing.able;

import android.graphics.PointF;
import android.os.Handler;

import com.ashlikun.zxing.Config;
import com.ashlikun.zxing.helper.ScanHelper;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;


/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:50
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */

public class XQRScanCrudeAble extends PixsValuesAble {

    protected Result result;

    XQRScanCrudeAble(Handler handler) {
        super(handler);
    }

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source, boolean isNative) {
        if (result != null && result.getText() != null)
            return;
        result = toLaunchParse(source.getHybridBinaryCurde());
        if (result != null && result.getText() != null && !"".equals(result.getText())) {
            sendMessage(Config.SCAN_RESULT, covertResult(result));
        }
    }

    protected com.ashlikun.zxing.Result covertResult(Result result) {
        com.ashlikun.zxing.Result result_ = new com.ashlikun.zxing.Result();
        result_.setText(result.getText());
        PointF[] pointFS = ScanHelper.rotatePoint(result.getResultPoints());
        result_.setQrPointF(ScanHelper.calCenterPointF(pointFS));
        result_.setQrLeng(ScanHelper.calQrLenghtShow(result.getResultPoints()));
        result_.setFormat(result.getBarcodeFormat());
        result_.setQrRotate(ScanHelper.calQrRotate(pointFS));
        return result_;
    }
}
