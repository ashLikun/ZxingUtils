package com.ashlikun.zxing.helper;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;

import androidx.annotation.FloatRange;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:49
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍： Camera2 API中一些计算
 */
public class CameraHelper {


    static long startTime = 0L;

    /**
     * 检查是否支持设备自动对焦
     * 很多设备的前摄像头都有固定对焦距离，而没有自动对焦。
     */
    public static boolean checkAutoFocus(CameraCharacteristics characteristics) {
        int[] afAvailableModes = new int[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            afAvailableModes = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        }
        if (afAvailableModes.length == 0 || (afAvailableModes.length == 1 && afAvailableModes[0] == CameraMetadata.CONTROL_AF_MODE_OFF)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 匹配指定方向的摄像头，前还是后
     * LENS_FACING_FRONT是前摄像头标志
     */
    public static boolean matchCameraDirection(CameraCharacteristics cameraCharacteristics, int direction) {
        //这里设置后摄像头
        Integer facing = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
        }
        return (facing != null && facing == direction) ? true : false;
    }

    /**
     * 获取相机支持最大的调焦距离
     */
    public static Float getMinimumFocusDistance(CameraCharacteristics cameraCharacteristics) {
        Float distance = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                distance = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return distance;
    }

    /**
     * 获取最大的数字变焦值，也就是缩放值
     */
    public static Float getMaxZoom(CameraCharacteristics cameraCharacteristics) {
        Float maxZoom = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                maxZoom = cameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxZoom;
    }

    /**
     * 计算zoom所对应的rect
     */
    public static Rect getZoomRect(CameraCharacteristics cameraCharacteristics, @FloatRange(from = 0, to = 1) float currentZoom) {
        Float maxZoom = getMaxZoom(cameraCharacteristics);

        if (currentZoom == 0) {
            currentZoom = 1;
        } else {
            currentZoom = currentZoom * maxZoom + 1;
        }

        if (currentZoom > maxZoom)
            currentZoom = maxZoom;

        if (currentZoom < 1)
            currentZoom = 1;

        Rect originReact = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            originReact = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        }
        Rect zoomRect;
        try {
            if (originReact == null) {
                return null;
            } else {
                float ratio = (float) 1 / currentZoom;
                int cropWidth = originReact.width() - Math.round((float) originReact.width() * ratio);
                int cropHeight = originReact.height() - Math.round((float) originReact.height() * ratio);
                zoomRect = new Rect(cropWidth / 2, cropHeight / 2, originReact.width() - cropWidth / 2, originReact.height() - cropHeight / 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            zoomRect = null;
        }
        return zoomRect;
    }

    /***
     * ImageReader中读取YUV
     */
    public static byte[] readYuv(ImageReader reader) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return null;
        }
        Image image = null;
        image = reader.acquireLatestImage();
        if (image == null)
            return null;
        byte[] data = getByteFromImage(image);
        image.close();
        return data;
    }

    private static byte[] getByteFromImage(Image image) {

        byte[] nv21;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return null;
        }
        if (image == null || image.getPlanes() == null || image.getPlanes().length == 0)
            return null;

        nv21 = new byte[image.getWidth() * image.getHeight()];
        image.getPlanes()[0].getBuffer().get(nv21);
        return nv21;
//        try {
//           //获取完整数据
//            Image.Plane[] planes = image.getPlanes();
//            int remaining0 = planes[0].getBuffer().remaining();
//            int remaining2 = planes[2].getBuffer().remaining();
//            int w = image.getWidth();
//            int h = image.getHeight();
//            byte[] yRawSrcBytes = new byte[remaining0];
//            byte[] uvRawSrcBytes = new byte[remaining2];
//            nv21 = new byte[w * h * 3 / 2];
//            planes[0].getBuffer().get(yRawSrcBytes);
//            planes[2].getBuffer().get(uvRawSrcBytes);
//            //0b10000001 对应-127,YUV灰度操作
////            for (int i = 0; i < uvRawSrcBytes.length; i++)
////                nv21[yRawSrcBytes.length + i] = (byte) 0b10000001;
//            for (int i = 0; i < h; i++) {
//
//                System.arraycopy(yRawSrcBytes, planes[0].getRowStride() * i,
//                        nv21, w * i, w);
//
//                if (i > image.getHeight() / 2)
//                    continue;
//
//                int offset = w * (h + i);
//
//                if (offset + w >= nv21.length)
//                    continue;
//
//                System.arraycopy(uvRawSrcBytes, planes[2].getRowStride() * i,
//                        nv21, offset, w);
//            }
//            return nv21;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return nv21;
//        }
    }

    /***
     * camera1 zoom
     */
    public static void setZoom(@FloatRange(from = 0, to = 1) float z, Camera mCamera) {
        if (mCamera == null)
            return;
        Camera.Parameters p = mCamera.getParameters();
        if (p == null)
            return;
        if (!p.isZoomSupported())
            return;
        int zoom = (int) (z * p.getMaxZoom());
        if (zoom < 1)
            zoom = 1;
        p.setZoom(zoom);
        mCamera.setParameters(p);
    }

    /**
     * 检查相机支持哪几种focusMode
     */
    public void checkFocusMode(CameraCharacteristics cameraCharacteristics) {
        int[] availableFocusModes = new int[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            availableFocusModes = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        }
        for (int focusMode : availableFocusModes != null ? availableFocusModes : new int[0]) {
            if (focusMode == CameraCharacteristics.CONTROL_AF_MODE_OFF) {

            } else if (focusMode == CameraCharacteristics.CONTROL_AF_MODE_MACRO) {

            } else if (focusMode == CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_PICTURE) {

            } else if (focusMode == CameraCharacteristics.CONTROL_AF_MODE_AUTO) {

            }
        }
    }
}
