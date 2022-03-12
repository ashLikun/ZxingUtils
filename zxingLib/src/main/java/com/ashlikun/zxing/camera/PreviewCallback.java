/*
 * Copyright (C) 2010 ZXing authors
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
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/28 10:48
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：预览回调的callback
 */

public final class PreviewCallback implements Camera.PreviewCallback {
    private final CameraConfigurationManager configManager;
    private Handler previewHandler;
    private int previewMessage;

    PreviewCallback(CameraConfigurationManager configManager) {
        this.configManager = configManager;
    }

    public void setHandler(Handler previewHandler, int previewMessage) {
        this.previewHandler = previewHandler;
        this.previewMessage = previewMessage;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //摄像头画面大小
        Point cameraResolution = configManager.getCameraResolution();
        if (previewHandler != null) {
            Message message = previewHandler.obtainMessage(previewMessage, cameraResolution.x,
                    cameraResolution.y, data);
            message.sendToTarget();
            previewHandler = null;
        }
    }

}
