
package com.ashlikun.zxing.helper;

import android.hardware.Camera;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:50
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
public class LightHelper {
    
    static int lastAvDark = 0;

    /***
     * 根据像素点采集环境亮度
     */
    public static int getAvDark(byte[] data, int dataWidth, int dataheight) {

        if (data.length == 0)
            return lastAvDark;

        long pixelLightCount = 0L;
        long pixCount = 0L;
        int step = 20;
        for (int i = 0; i < data.length; i += step) {
            pixelLightCount += data[i] & 0xff;
            pixCount++;
        }
        lastAvDark = (int) (pixelLightCount / pixCount);
        return lastAvDark;
    }

    /***
     * camera1 打开/关闭闪光灯
     */
    public static void openLight(Camera mCamera, boolean isOpen) {
        if (mCamera == null)
            return;
        Camera.Parameters parameters = mCamera.getParameters();
        if (isOpen)
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        else parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameters);
    }

}
