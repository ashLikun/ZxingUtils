package com.ashlikun.zxing.able;

import android.os.Handler;

import com.ashlikun.zxing.Config;
import com.ashlikun.zxing.helper.ScanHelper;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ResultPoint;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:49
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
public class XQRScanZoomAble extends XQRScanAble {

    long zoomTime = 0;

    int lastLenght = 0;

    XQRScanZoomAble(Handler handler) {
        super(handler);
    }

    @Override
    protected void needParseDeploy(PlanarYUVLuminanceSource source, boolean isNative) {
        super.needParseDeploy(source, isNative);
        if (result == null)
            return;

        if (result.getText() != null)
            return;

        ResultPoint[] points = result.getResultPoints();
        if (points == null || points.length < 3)
            return;

        int lenght = ScanHelper.getQrLenght(points);
        sendMessage(Config.RT_LOCATION,
                ScanHelper.rotatePoint(points));

        //自动变焦时间间隔为500ms
        if (System.currentTimeMillis() - zoomTime < 500)
            return;
        if (lenght < lastLenght * 0.8f) {
            Config.currentZoom = 0;
        } else if (lenght < Config.scanRect.getPreX() / 3 * 2) {
            Config.currentZoom += 0.07;
        }
        zoomTime = System.currentTimeMillis();
        lastLenght = lenght;
        sendMessage(Config.AUTO_ZOOM, Config.currentZoom + "");
    }
}
