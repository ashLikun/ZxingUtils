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

package com.ashlikun.zxing.decoding;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ashlikun.zxing.DecodeAudio;
import com.ashlikun.zxing.R;
import com.ashlikun.zxing.ZxingCallback;
import com.ashlikun.zxing.camera.CameraManager;
import com.google.zxing.Result;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/28 16:18
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：解码的结果处理
 */

public final class DecodeHandler extends Handler {
    /**
     * 解码器
     *
     * @param context
     */
    private DecodeThread decodeThread;
    private State state;
    ZxingCallback callback;
    DecodeAudio decodeAudio;
    DecodeParams decodeParams;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    public DecodeHandler(DecodeParams decodeParams, DecodeAudio decodeAudio, ZxingCallback callback) {
        this.decodeParams = decodeParams;
        this.callback = callback;
        this.decodeAudio = decodeAudio;
        state = State.SUCCESS;
        decodeParams.resultHandler = this;
        decodeThread = new DecodeThread(decodeParams);
        start();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.auto_focus) {
            if (state == State.PREVIEW) {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
        } else if (message.what == R.id.restart_preview) {
            restartPreviewAndDecode();
        } else if (message.what == R.id.decode_succeeded) {
            state = State.SUCCESS;
            Bundle bundle = message.getData();
            Bitmap barcode = bundle == null ? null :
                    (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
            if (decodeAudio != null) {
                decodeAudio.playBeep();
            }
            if (callback != null) {
                callback.onDecodeSuccess((Result) message.obj, barcode);
            }
        } else if (message.what == R.id.decode_failed) {
            state = State.PREVIEW;
            //失败了继续请求
            //开始请求数据
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
        } else if (message.what == R.id.decode_light_chang) {
            if (decodeParams.lightChangListener != null) {
                Bundle bundle = message.getData();
                boolean isDark = bundle == null ? null :
                        bundle.getBoolean(DecodeThread.BARCODE_LIGHT_CHANG);
                decodeParams.lightChangListener.onLightChang(isDark);
            }
        }
    }

    public void start() {
        decodeThread.start();
    }

    public void stop() {
        state = State.DONE;
        if (decodeThread != null) {
            decodeThread.stop();
        }
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    /**
     * 重新请求编码
     */
    public void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
        }
    }
}
