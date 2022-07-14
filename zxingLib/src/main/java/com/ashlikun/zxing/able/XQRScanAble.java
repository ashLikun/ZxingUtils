package com.ashlikun.zxing.able;

import android.graphics.PointF;
import android.os.Handler;

import com.ashlikun.zxing.Config;
import com.ashlikun.zxing.helper.ScanHelper;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:49
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class XQRScanAble extends PixsValuesAble {

    protected Result result;

    XQRScanAble(Handler handler) {
        super(handler);
    }

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source, boolean isNative) {
        if (result != null && result.getText() != null)
            return;
        result = toLaunchParse(source.getHybridBinary());
        if (result != null && result.getText() != null && !"".equals(result.getText())) {
            sendMessage(Config.RT_LOCATION,
                    ScanHelper.rotatePoint(result.getResultPoints()));
            sendMessage(Config.SCAN_RESULT, covertResult(result));
        }
    }

    protected com.ashlikun.zxing.Result covertResult(Result result) {
        com.ashlikun.zxing.Result result_ = new com.ashlikun.zxing.Result();
        if (result.getText() != null) {
            result_.setText(result.getText());
        }
        PointF[] pointFS = ScanHelper.rotatePoint(result.getResultPoints());
        result_.setQrPointF(ScanHelper.calCenterPointF(pointFS));
        result_.setQrLeng(ScanHelper.calQrLenghtShow(result.getResultPoints()));
        result_.setFormat(result.getBarcodeFormat());
        result_.setQrRotate(ScanHelper.calQrRotate(pointFS));
        result_.setResult(result);
        return result_;
    }
}
