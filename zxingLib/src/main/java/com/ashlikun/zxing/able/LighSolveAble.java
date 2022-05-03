package com.ashlikun.zxing.able;

import android.os.Handler;

import com.ashlikun.zxing.Config;
import com.ashlikun.zxing.TypeRunnable;
import com.ashlikun.zxing.helper.LightHelper;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:49
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：根据像素值计算周围环境亮度
 */
public class LighSolveAble extends PixsValuesAble {

    private int STANDVALUES = 100;

    private boolean isBright = true;

    //上次记录的时间戳
    static long lastRecordTime = System.currentTimeMillis();

    //扫描间隔
    static int waitScanTime = 1000;

    public LighSolveAble(Handler handler) {
        super(handler);
    }

    @Override
    protected void cusAction(byte[] data, int dataWidth, int dataHeight, boolean isNative) {
        super.cusAction(data, dataWidth, dataHeight, isNative);
        //非原始数据不采集亮度
        if (!isNative)
            return;
        int avDark = LightHelper.getAvDark(data, dataWidth, dataHeight);
        if (avDark > STANDVALUES && !isBright) {
            isBright = true;
            sendMessage(Config.LIGHT_CHANGE, true);
        }
        if (avDark < STANDVALUES && isBright) {
            isBright = false;
            sendMessage(Config.LIGHT_CHANGE, false);
        }
    }

    @Override
    public boolean isCycleRun(boolean isNative) {
        //非原始数据不走采集亮度任务
        if (!isNative)
            return false;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRecordTime < waitScanTime) {
            return false;
        }
        lastRecordTime = currentTime;
        return true;
    }

    @Override
    public int provideType(boolean isNative) {
        return TypeRunnable.OTHER;
    }
}
