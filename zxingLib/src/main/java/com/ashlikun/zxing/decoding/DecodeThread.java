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

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ashlikun.zxing.CodeUtils;
import com.ashlikun.zxing.R;
import com.ashlikun.zxing.camera.PlanarYUVLuminanceSource;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Hashtable;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/28 14:41
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：解码线程
 */

public final class DecodeThread {

    public static final String BARCODE_BITMAP = "BARCODE_BITMAP";
    /**
     * 光线改变
     */
    public static final String BARCODE_LIGHT_CHANG = "BARCODE_LIGHT_CHANG";
    /**
     * 亮度达到这个值就是黑色
     */
    public static float LIGHT_CHANG_PERCENT = 0.78f;

    private final Hashtable<DecodeHintType, Object> hints;
    private final MultiFormatReader multiFormatReader;
    private final Object LOCK = new Object();
    private Handler handler;
    private HandlerThread thread;
    private DecodeParams decodeParams;
    private boolean running = false;
    /**
     * 每解析N次数据，就解析一次亮度
     */
    private int decodeLightCount = 0;
    /**
     * 处理相机数据的handle
     */
    private final Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == R.id.decode) {
                decode((byte[]) message.obj, message.arg1, message.arg2);
            }
            return true;
        }
    };

    /**
     *
     */
    public DecodeThread(DecodeParams decodeParams) {
        this.decodeParams = decodeParams;
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints = decodeParams.getHashtable());

    }

    /**
     * Start decoding.
     * <p>
     * This must be called from the UI thread.
     */
    public void start() {
        if (!running) {
            validateMainThread();
            thread = new HandlerThread(DecodeThread.class.getName());
            thread.start();
            handler = new Handler(thread.getLooper(), callback);
            running = true;
        }
    }

    /**
     * Stop decoding.
     * <p>
     * This must be called from the UI thread.
     */
    public void stop() {
        validateMainThread();
        synchronized (LOCK) {
            if (running) {
                running = false;
                handler.removeCallbacksAndMessages(null);
                handler.getLooper().quit();
                thread.quit();
                thread = null;
                handler = null;
            }
        }
    }

    public Handler getHandler() {
        return handler;
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   YUV数据
     * @param width  摄像头画面宽度
     * @param height 摄像头画面高度
     */
    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        Result rawResult = null;

        //modify here
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }
        int tmp = width;
        width = height;
        height = tmp;
        Rect fram = decodeParams.bridge.getFramingRect();
        PlanarYUVLuminanceSource source = CodeUtils.buildLuminanceSource(rotatedData, width, height, fram);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException re) {
        } finally {
            multiFormatReader.reset();
        }
        if (rawResult != null) {
            long end = System.currentTimeMillis();
            Log.d(DecodeThread.class.getName(), "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
            Message message = Message.obtain(decodeParams.resultHandler, R.id.decode_succeeded, rawResult);
            Bundle bundle = new Bundle();
            bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
            message.setData(bundle);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(decodeParams.resultHandler, R.id.decode_failed);
            message.sendToTarget();
            if (decodeParams.lightChangListener != null) {
                if (decodeLightCount >= 10) {
                    decodeLightCount = 0;
                    //解析光线强度 用更小的图
//                    PlanarYUVLuminanceSource sourceLight = new PlanarYUVLuminanceSource(rotatedData, width, height, 0, 0,
//                            width, height);
                    float color = source.getAverageColor();
                    float floatPercent = color / -16777216f;
                    boolean isLightDrak = floatPercent >= LIGHT_CHANG_PERCENT && floatPercent <= 1.00;
                    Message messageLight = Message.obtain(decodeParams.resultHandler, R.id.decode_light_chang);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(DecodeThread.BARCODE_LIGHT_CHANG, isLightDrak);
                    messageLight.setData(bundle);
                    messageLight.sendToTarget();
                } else {
                    decodeLightCount++;
                }
            }
        }
    }

    /**
     * 主线程限制
     */
    public void validateMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Must be called from the main thread.");
        }
    }
}
