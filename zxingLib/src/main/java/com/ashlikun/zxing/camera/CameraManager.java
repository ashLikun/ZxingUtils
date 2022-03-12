/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ashlikun.zxing.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;

import java.io.IOException;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/28 10:26
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：这个对象包装了相机服务对象，并期望是唯一一个与它对话的对象。那
 * 实现封装了使用预先视图大小的图像所需的步骤，这些图像用于
 * 预览和解码。
 */
public final class CameraManager {

    private static CameraManager cameraManager;

    private final CameraConfigurationManager configManager;
    private Camera camera;
    private Rect framingRectInPreview;
    private boolean initialized;
    private boolean previewing;
    /**
     * 预览帧在这里传递，我们将它传递给注册的处理程序。一定要
     * 清除处理程序，使它只接收一条消息。
     */
    private final PreviewCallback previewCallback;
    /**
     * 自动对焦回调到达这里，并被发送到请求它们的处理程序。
     */
    private final AutoFocusCallback autoFocusCallback;

    public static void init() {
        if (cameraManager == null) {
            cameraManager = new CameraManager();
        }
    }

    public static CameraManager get() {
        init();
        return cameraManager;
    }

    private CameraManager() {
        this.configManager = new CameraConfigurationManager();
        previewCallback = new PreviewCallback(configManager);
        autoFocusCallback = new AutoFocusCallback();
    }

    /**
     * 打开相机驱动程序并初始化硬件参数。
     */
    public void openCamera(int width, int height, SurfaceTexture texture) throws Exception {
        if (camera == null) {
            camera = Camera.open();
            if (camera == null) {
                throw new IOException();
            }
        }
        camera.setPreviewTexture(texture);
        if (!initialized) {
            initialized = true;
            configManager.initFromCameraParameters(width, height, camera);
        }
        configManager.setDesiredCameraParameters(camera);
        FlashlightManager.enableFlashlight();
    }

    /**
     * 如果仍在使用，则关闭相机驱动程序。
     */
    public void releaseCamera() {
        if (camera != null) {
            FlashlightManager.disableFlashlight();
            camera.release();
            camera = null;
            initialized = false;
        }
    }

    /**
     * 要求相机硬件开始将预览帧绘制到屏幕上。
     */
    public void startPreview() {
        if (camera != null && !previewing) {
            camera.startPreview();
            previewing = true;
        }
    }

    /**
     * 告诉相机停止绘制预览帧。
     */
    public void stopPreview() {
        if (camera != null && previewing) {
            camera.stopPreview();
            previewCallback.setHandler(null, 0);
            autoFocusCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    /**
     * 请求获取摄像头一帧数据，并且解析。
     */
    public void requestPreviewFrame(Handler handler, int message) {
        if (camera != null && previewing) {
            previewCallback.setHandler(handler, message);
            camera.setOneShotPreviewCallback(previewCallback);
        }
    }

    /**
     * 请求自动对焦
     */
    public void requestAutoFocus(Handler handler, int message) {
        if (camera != null && previewing) {
            autoFocusCallback.setHandler(handler, message);
            camera.autoFocus(autoFocusCallback);
        }
    }


    public Camera getCamera() {
        return camera;
    }

    public boolean isPreviewing() {
        return previewing;
    }

    public PreviewCallback getPreviewCallback() {
        return previewCallback;
    }

    public AutoFocusCallback getAutoFocusCallback() {
        return autoFocusCallback;
    }

    public void setPreviewing(boolean previewing) {
        this.previewing = previewing;
    }

    public Rect getFramingRectInPreview(Rect framingRect) {
        if (framingRectInPreview == null) {
            Rect rect = new Rect(framingRect);
            Point cameraResolution = configManager.getCameraResolution();
            Point surfaceTextureSize = configManager.getSurfaceTextureSize();
            rect.left = rect.left * cameraResolution.y / surfaceTextureSize.x;
            rect.right = rect.right * cameraResolution.y / surfaceTextureSize.x;
            rect.top = rect.top * cameraResolution.x / surfaceTextureSize.y;
            rect.bottom = rect.bottom * cameraResolution.x / surfaceTextureSize.y;
            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }

}
