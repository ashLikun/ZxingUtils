package com.ashlikun.zxing;

import android.content.Context;
import android.hardware.Camera;
import android.view.TextureView;

import com.ashlikun.zxing.camera.CameraManager;
import com.ashlikun.zxing.decoding.DecodeHandler;
import com.ashlikun.zxing.decoding.DecodeParams;
import com.ashlikun.zxing.view.IViewDecodeBridge;
import com.google.zxing.BarcodeFormat;

import java.util.Vector;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/9/28　10:03
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：封装zxing扫码的实现
 */
public class Zxing {
    private Context context;
    protected ZxingCallback callback;
    protected DecodeHandler decodeHandler;
    private DecodeAudio decodeAudio;
    DecodeParams decodeParams;
    ZxingSurfaceTextureListener zxingSurfaceTextureListener;

    /**
     * @param textureView   渲染的view
     * @param callback      事件回调
     * @param decodeFormats 解码格式，可以null
     * @param characterSet  字符编码，可以null
     * @param bridge        解码器与view的交互
     */
    public Zxing(TextureView textureView, ZxingCallback callback, Vector<BarcodeFormat> decodeFormats,
                 String characterSet,
                 IViewDecodeBridge bridge) {
        this.context = textureView.getContext();
        this.callback = callback;
        decodeAudio = new DecodeAudio(context);
        textureView.setSurfaceTextureListener(zxingSurfaceTextureListener = new ZxingSurfaceTextureListener(context, this));
        decodeParams = new DecodeParams(decodeFormats, characterSet,
                bridge, null);
    }

    /**
     * 监听亮度改变,不为null内部就会解析图片的亮度
     *
     * @param lightChangListener
     */
    public void setLightChangListener(OnLightChangListener lightChangListener) {
        decodeParams.setLightChangListener(lightChangListener);
    }

    /**
     * 设置是否开启音效
     * 只有响铃模式才开启
     *
     * @param playBeep
     */
    public void setPlayBeep(boolean playBeep) {
        decodeAudio.setPlayBeep(playBeep);
    }


    /**
     * 开始识别,内部调用,由SurfaceTextureListener监听调用
     */
    protected void neibuStart() {
        decodeHandler = new DecodeHandler(decodeParams,
                decodeAudio, callback);
        decodeHandler.restartPreviewAndDecode();
    }

    /**
     * 成功后可以调用这个方法继续识别
     */
    public void reStart() {
        if (decodeHandler != null) {
            decodeHandler.restartPreviewAndDecode();
        }
    }

    /**
     * 生命周期
     */
    public void onResume() {
        if (decodeHandler != null) {
            zxingSurfaceTextureListener.startPreview();
            decodeHandler.start();
        }
    }

    /**
     * 生命周期
     */
    public void onPause() {
        if (decodeHandler != null) {
            decodeHandler.stop();
            zxingSurfaceTextureListener.stopPreview();
        }

    }

    /**
     * 生命周期
     */
    public void onDestroy() {
        if (decodeAudio != null) {
            decodeAudio.release();
        }
        zxingSurfaceTextureListener.stopPreview();
    }

    /**
     * 闪光灯操作
     *
     * @param isEnable
     */
    public static void isLightEnable(boolean isEnable) {
        if (isEnable) {
            Camera camera = CameraManager.get().getCamera();
            if (camera != null) {
                Camera.Parameters parameter = camera.getParameters();
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameter);
            }
        } else {
            Camera camera = CameraManager.get().getCamera();
            if (camera != null) {
                Camera.Parameters parameter = camera.getParameters();
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameter);
            }
        }
    }
}
