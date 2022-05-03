package com.ashlikun.zxing.able;

import android.os.Handler;
import android.os.Message;

import com.ashlikun.zxing.TypeRunnable;
import com.ashlikun.zxing.core.CustomMultiFormatReader;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;

import java.lang.ref.WeakReference;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:49
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */
public abstract class PixsValuesAble {

    WeakReference<Handler> handlerHolder;
    CustomMultiFormatReader reader = CustomMultiFormatReader.getInstance();

    public PixsValuesAble(Handler handler) {
        this.handlerHolder = new WeakReference<>(handler);
    }

    /***
     * 其他操作重写这个
     * @param data
     * @param dataWidth
     * @param dataHeight
     */
    protected void cusAction(byte[] data, int dataWidth, int dataHeight) {
    }

    protected void cusAction(byte[] data, int dataWidth, int dataHeight, boolean isNative) {
    }

    /***
     * 需要解析二维码子类重写这个
     * @param source
     */
    protected void needParseDeploy(PlanarYUVLuminanceSource source, boolean isNative) {
    }

    Result toLaunchParse(Binarizer binarizer) {
        return reader.decode(new BinaryBitmap(binarizer));
    }

    protected void sendMessage(int type, Object obj) {
        if (handlerHolder != null && handlerHolder.get() != null) {
            Message.obtain(handlerHolder.get(), type, obj)
                    .sendToTarget();
        }
    }

    public void release() {
        handlerHolder.clear();
        handlerHolder = null;
    }

    /***
     *  根据时间控制周期
     * @return
     */
    public boolean isCycleRun(boolean isNative) {
        return true;
    }

    public @TypeRunnable.Range
    int provideType(boolean isNative) {
        if (isNative) return TypeRunnable.NORMAL;
        else return TypeRunnable.SCALE;
    }

}
