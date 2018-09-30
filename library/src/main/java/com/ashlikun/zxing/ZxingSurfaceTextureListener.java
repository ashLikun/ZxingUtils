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
    Zxing zxing;
    Context context;

    public ZxingSurfaceTextureListener(Context context, Zxing zxing) {
        this.context = context;
        this.zxing = zxing;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
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

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (CameraManager.get().getCamera() != null && CameraManager.get().isPreviewing()) {
            CameraManager.get().stopPreview();
            CameraManager.get().getPreviewCallback().setHandler(null, 0);
            CameraManager.get().getAutoFocusCallback().setHandler(null, 0);
            CameraManager.get().setPreviewing(false);
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
