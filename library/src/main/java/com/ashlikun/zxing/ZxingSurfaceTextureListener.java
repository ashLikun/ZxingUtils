package com.ashlikun.zxing;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.TextureView;

import com.ashlikun.zxing.camera.CameraManager;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/9/28　10:37
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class ZxingSurfaceTextureListener implements TextureView.SurfaceTextureListener {
    SurfaceTexture surface;
    Zxing zxing;
    Context context;
    int width;
    int height;

    public ZxingSurfaceTextureListener(TextureView textureView, Zxing zxing) {
        this.context = textureView.getContext();
        this.zxing = zxing;
        surface = textureView.getSurfaceTexture();
        width = textureView.getWidth();
        height = textureView.getHeight();
        startPreview();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.surface = surface;
        this.width = width;
        this.height = height;
        startPreview();
    }

    public void startPreview() {
        if (CameraManager.get().isPreviewing() || this.surface == null || width == 0 || height == 0) {
            return;
        }
        try {
            //打开摄像头
            CameraManager.get().openCamera(width, height, surface);
            CameraManager.get().startPreview();
            zxing.neibuStart();
            zxing.callback.onOpenCamera();
        } catch (Exception e) {
            e.printStackTrace();
            if (zxing.callback != null) {
                zxing.callback.onOpenCameraFail();
            }
        }
    }

    public void stopPreview() {
        if (CameraManager.get().getCamera() != null && CameraManager.get().isPreviewing()) {
            CameraManager.get().stopPreview();
            CameraManager.get().getPreviewCallback().setHandler(null, 0);
            CameraManager.get().getAutoFocusCallback().setHandler(null, 0);
            CameraManager.get().setPreviewing(false);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        this.surface = null;
        stopPreview();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
