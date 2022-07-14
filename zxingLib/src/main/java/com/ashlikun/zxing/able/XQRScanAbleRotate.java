package com.ashlikun.zxing.able;

import android.graphics.PointF;
import android.os.Handler;

import com.ashlikun.zxing.Config;
import com.ashlikun.zxing.helper.ScanHelper;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:49
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class XQRScanAbleRotate extends PixsValuesAble {

    protected Result result;

    XQRScanAbleRotate(Handler handler) {
        super(handler);
    }

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source, boolean isNative) {
        result = toLaunchParse(new HybridBinarizer(source.onlyCopyWarpRotate()));
        if (result != null && result.getText() != null && !"".equals(result.getText())) {
            sendMessage(Config.SCAN_RESULT, covertResultRotate(result));
        }
    }

    private byte[] rotateByte(byte[] data, int dataWidth, int dataHeight) {
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < dataHeight; y++) {
            for (int x = 0; x < dataWidth; x++) {
                int i = x * dataHeight + dataHeight - y - 1;
                if (i >= data.length || x + y * dataWidth >= data.length) {
                    return null;
                }
                rotatedData[i] = data[x + y * dataWidth];
            }
        }
        return rotatedData;
    }

    protected com.ashlikun.zxing.Result covertResultRotate(Result result) {
        com.ashlikun.zxing.Result result_ = new com.ashlikun.zxing.Result();
        if (result.getText() != null) {
            result_.setText(result.getText());
        }
        PointF[] pointFS = ScanHelper.rotatePointR(result.getResultPoints());
        result_.setQrPointF(ScanHelper.calCenterPointF(pointFS));
        result_.setQrLeng(ScanHelper.calQrLenghtShow(result.getResultPoints()));
        result_.setFormat(result.getBarcodeFormat());
        result_.setQrRotate(ScanHelper.calQrRotate(pointFS));
        result_.setResult(result);
        result_.setRotate(true);
        return result_;
    }
}
