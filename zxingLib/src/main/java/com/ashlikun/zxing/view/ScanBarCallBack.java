package com.ashlikun.zxing.view;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:59
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：自定义扫描条
 */

public interface ScanBarCallBack extends CameraStarLater {

    void startScanAnimator();

    void stopScanAnimator();

}
