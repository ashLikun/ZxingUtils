/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.google.android.cameraview;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import androidx.collection.SparseArrayCompat;

import com.ashlikun.zxing.helper.CameraHelper;
import com.ashlikun.zxing.helper.LightHelper;
import com.ashlikun.zxing.helper.ScanHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicBoolean;


class Camera1 extends CameraViewImpl {

    private static final int INVALID_CAMERA_ID = -1;

    private static final SparseArrayCompat<String> FLASH_MODES = new SparseArrayCompat<>();

    static {
        FLASH_MODES.put(Constants.FLASH_OFF, Camera.Parameters.FLASH_MODE_OFF);
        FLASH_MODES.put(Constants.FLASH_ON, Camera.Parameters.FLASH_MODE_ON);
        FLASH_MODES.put(Constants.FLASH_TORCH, Camera.Parameters.FLASH_MODE_TORCH);
        FLASH_MODES.put(Constants.FLASH_AUTO, Camera.Parameters.FLASH_MODE_AUTO);
        FLASH_MODES.put(Constants.FLASH_RED_EYE, Camera.Parameters.FLASH_MODE_RED_EYE);
    }

    private int mCameraId;

    private final AtomicBoolean isPictureCaptureInProgress = new AtomicBoolean(false);

    volatile Camera mCamera;

    private Camera.Parameters mCameraParameters;

    private final Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();

    private final SizeMap mPreviewSizes = new SizeMap();

    private final SizeMap mPictureSizes = new SizeMap();

    private AspectRatio mAspectRatio;

    private boolean mShowingPreview;

    private boolean mAutoFocus;

    private int mFacing;

    private int mFlash;

    private int mDisplayOrientation;

    private static final int MAX_PREVIEW_WIDTH = 1920;

    private static final int MAX_PREVIEW_HEIGHT = 1080;

    Camera1(Callback callback) {
        super(callback);
    }


    @Override
    public void updatePreView(PreviewImpl preview) {
        super.updatePreView(preview);
        mPreview.setCallback(() -> {
            if (mCamera != null) {
                setUpPreview();
            }
        });
    }

    @Override
    boolean start() {
        synchronized (Camera1.class) {

            if (isCameraOpened())
                return true;

            chooseCamera();

            if (mCameraId == INVALID_CAMERA_ID) {
                return false;
            }
            if (!openCamera())
                return false;
            if (mPreview.isReady()) {
                setUpPreview();
            }
            mShowingPreview = true;
            if (mCamera != null) {
                try {
                    mCamera.startPreview();
                } catch (Exception ignored) {
                }
            }
            return true;
        }
    }

    @Override
    void stop() {
        synchronized (Camera1.class) {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
            }
            mShowingPreview = false;
            releaseCamera();
        }
    }

    // Suppresses Camera#setPreviewTexture
    @SuppressLint("NewApi")
    void setUpPreview() {
        synchronized (Camera1.class) {
            if (mCamera == null)
                return;
            try {
                if (mPreview.getOutputClass() == SurfaceHolder.class) {
                    mCamera.setPreviewDisplay(mPreview.getSurfaceHolder());
                } else {
                    mCamera.setPreviewTexture((SurfaceTexture) mPreview.getSurfaceTexture());
                }
                mCamera.setPreviewCallback((data, camera) ->
                        mCallback.onPreviewByte(data));
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    boolean isCameraOpened() {
        return mCamera != null;
    }

    @Override
    void setFacing(int facing) {
        if (mFacing == facing) {
            return;
        }
        mFacing = facing;
        if (isCameraOpened()) {
            stop();
            start();
        }
    }

    @Override
    int getFacing() {
        return mFacing;
    }

    @Override
    Set<AspectRatio> getSupportedAspectRatios() {
        SizeMap idealAspectRatios = mPreviewSizes;
        for (AspectRatio aspectRatio : idealAspectRatios.ratios()) {
            if (mPictureSizes.sizes(aspectRatio) == null) {
                idealAspectRatios.remove(aspectRatio);
            }
        }
        return idealAspectRatios.ratios();
    }

    @Override
    boolean setAspectRatio(AspectRatio ratio) {
        mAspectRatio = ratio;
//        if (mAspectRatio == null || !isCameraOpened()) {
//            // Handle this later when camera is opened
//
//            return true;
//        } else if (!mAspectRatio.equals(ratio)) {
//            final Set<Size> sizes = mPreviewSizes.sizes(ratio);
//            if (sizes == null) {
//                return false;
//            } else {
//                mAspectRatio = ratio;
//                try {
//                    adjustCameraParameters();
//                } catch (Exception ignored) {
//                }
//                return true;
//            }
//        }
//        return false;
        return true;
    }

    @Override
    AspectRatio getAspectRatio() {
        return mAspectRatio;
    }

    @Override
    void setAutoFocus(boolean autoFocus) {
        if (mAutoFocus == autoFocus) {
            return;
        }
        if (setAutoFocusInternal(autoFocus)) {
            mCamera.setParameters(mCameraParameters);
        }
    }

    @Override
    boolean getAutoFocus() {
        if (!isCameraOpened()) {
            return mAutoFocus;
        }
        String focusMode = mCameraParameters.getFocusMode();
        return focusMode != null && focusMode.contains("continuous");
    }

    @Override
    void setFlash(int flash) {
        if (flash == mFlash) {
            return;
        }
        if (setFlashInternal(flash)) {
            mCamera.setParameters(mCameraParameters);
        }
    }

    @Override
    int getFlash() {
        return mFlash;
    }

    @Override
    void takePicture() {
        if (!isCameraOpened()) {
            return;
        }
        if (getAutoFocus()) {
            mCamera.cancelAutoFocus();
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    takePictureInternal();
                }
            });
        } else {
            takePictureInternal();
        }
    }

    void takePictureInternal() {
        if (!isPictureCaptureInProgress.getAndSet(true)) {
            mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    isPictureCaptureInProgress.set(false);
                    mCallback.onPictureTaken(data);
                    if (isCameraOpened()) {
                        camera.cancelAutoFocus();
                        camera.startPreview();
                    }
                }
            });
        }
    }

    @Override
    void setDisplayOrientation(int displayOrientation) {
        if (mDisplayOrientation == displayOrientation) {
            return;
        }
        if (mPreview != null)
            mPreview.setDisplayOrientation(displayOrientation);
//        mDisplayOrientation = displayOrientation;
//        try {
//            if (isCameraOpened()) {
//                mCameraParameters.setRotation(calcCameraRotation(displayOrientation));
//                mCamera.setParameters(mCameraParameters);
//                mCamera.setDisplayOrientation(calcDisplayOrientation(displayOrientation));
//            }
//        } catch (Exception ignored) {
//        }
    }

    @Override
    void toZoomMax() {
        setZoom(1);
    }

    @Override
    void toZoomMin() {
        setZoom(0);
    }

    @Override
    void setZoom(float percent) {
        synchronized (Camera1.class) {
            CameraHelper.setZoom(percent, mCamera);
        }
    }

    @Override
    void lightOperator(boolean isOpen) {
        synchronized (Camera1.class) {
            LightHelper.openLight(mCamera, isOpen);
        }
    }

    /**
     * This rewrites {@link #mCameraId} and {@link #mCameraInfo}.
     */
    private void chooseCamera() {
        for (int i = 0, count = Camera.getNumberOfCameras(); i < count; i++) {
            Camera.getCameraInfo(i, mCameraInfo);
            if (mCameraInfo.facing == mFacing) {
                mCameraId = i;
                return;
            }
        }
        mCameraId = INVALID_CAMERA_ID;
    }

    private boolean openCamera() {
        if (mCamera != null) {
            releaseCamera();
        }
        try {
            mCamera = Camera.open(mCameraId);
        } catch (Exception e) {
            return false;
        }

        if (mCamera == null) {
            return false;
        }

        mCameraParameters = mCamera.getParameters();
        // Supported preview sizes
        mPreviewSizes.clear();
        for (Camera.Size size : mCameraParameters.getSupportedPreviewSizes()) {
            mPreviewSizes.add(new Size(size.width, size.height));
        }
        // Supported picture sizes;
        mPictureSizes.clear();
        for (Camera.Size size : mCameraParameters.getSupportedPictureSizes()) {
            mPictureSizes.add(new Size(size.width, size.height));
        }

        for (AspectRatio ratio : mPreviewSizes.ratios()) {
            if (!mPictureSizes.ratios().contains(ratio)) {
                mPreviewSizes.remove(ratio);
            }
        }

        // AspectRatio
        if (mAspectRatio == null) {
            mAspectRatio = Constants.DEFAULT_ASPECT_RATIO;
        }
        try {
            adjustCameraParameters();
        } catch (Exception ignored) {
        }
        mCamera.setDisplayOrientation(calcDisplayOrientation(mDisplayOrientation));
        mCallback.onCameraOpened();
        return true;
    }

    void adjustCameraParameters() {
        // Supported preview sizes
        if (mPictureSizes.ratios().size() == 0) {
            return;
        }
        SortedSet<Size> sizes = mPreviewSizes.sizes(mAspectRatio);
        if (sizes == null) { // Not supported
            mAspectRatio = AspectRatio.of(4, 3);
            if (mPreviewSizes.sizes(mAspectRatio) == null)
                mAspectRatio = findNeartoDefaultAspectRatio(mPreviewSizes);
            mPreview.updateAspectRatio(mAspectRatio);
            sizes = mPreviewSizes.sizes(mAspectRatio);
        }

        Size size = chooseOptimalSize(sizes);
        // Always re-apply camera parameters
        // Largest picture size in this ratio
        final Size pictureSize = mPictureSizes.sizes(mAspectRatio).last();
        if (mShowingPreview) {
            mCamera.stopPreview();
        }
        mPreview.setBufferSize(size.getWidth(), size.getHeight());
        mCameraParameters.setPreviewSize(size.getWidth(), size.getHeight());
        mCameraParameters.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
        mCameraParameters.setRotation(calcCameraRotation(mDisplayOrientation));
        setAutoFocusInternal(mAutoFocus);
        setFlashInternal(mFlash);
        mCamera.setParameters(mCameraParameters);
        if (mShowingPreview) {
            mCamera.startPreview();
        }
    }

    private AspectRatio findNeartoDefaultAspectRatio(SizeMap sizeMap) {
        float minRation = Float.MAX_VALUE;
        AspectRatio minAspectRatio = sizeMap.ratios().iterator().next();
        for (AspectRatio ratio : sizeMap.ratios()) {
            for (Size size : mPreviewSizes.sizes(ratio)) {
                float currentRatio = Math.abs(1 - ((float) size.getWidth() / MAX_PREVIEW_WIDTH) *
                        ((float) size.getHeight() / MAX_PREVIEW_HEIGHT));
                if (currentRatio < minRation) {
                    minRation = currentRatio;
                    minAspectRatio = ratio;
                }
            }
        }
        return minAspectRatio;
    }

    private Size chooseOptimalSize(SortedSet<Size> sizes) {
        int desiredWidth;
        int desiredHeight;
        int surfaceWidth = mPreview.getWidth();
        int surfaceHeight = mPreview.getHeight();
        if (surfaceWidth == 0 || surfaceHeight == 0) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            } finally {
                surfaceWidth = mPreview.getWidth();
                surfaceHeight = mPreview.getHeight();
            }
        }
        if (surfaceWidth == 0 || surfaceHeight == 0) {
            surfaceWidth = MAX_PREVIEW_HEIGHT;
            surfaceHeight = MAX_PREVIEW_WIDTH;
        }
        if (isLandscape(mDisplayOrientation)) {
            desiredWidth = surfaceHeight;
            desiredHeight = surfaceWidth;
        } else {
            desiredWidth = surfaceWidth;
            desiredHeight = surfaceHeight;
        }

        float minRation = Float.MAX_VALUE;
        Size suitSize = null;
        for (Size size : sizes) { // Iterate from small to large
            float currentRation = Math.abs(1 - (float) desiredWidth / size.getWidth() *
                    (float) desiredHeight / size.getHeight());
            if (currentRation < minRation) {
                minRation = currentRation;
                suitSize = size;
            }
        }
        return suitSize;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            Camera temp = mCamera;
            mCamera = null;
            temp.release();
            mCallback.onCameraClosed();
        }
    }

    /**
     * Calculate display orientation
     * https://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
     * <p>
     * This calculation is used for orienting the preview
     * <p>
     * Note: This is not the same calculation as the camera rotation
     *
     * @param screenOrientationDegrees Screen orientation in degrees
     * @return Number of degrees required to rotate preview
     */
    private int calcDisplayOrientation(int screenOrientationDegrees) {
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (360 - (mCameraInfo.orientation + screenOrientationDegrees) % 360) % 360;
        } else {  // back-facing
            return (mCameraInfo.orientation - screenOrientationDegrees + 360) % 360;
        }
    }

    /**
     * Calculate camera rotation
     * <p>
     * This calculation is applied to the output JPEG either via Exif Orientation tag
     * or by actually transforming the bitmap. (Determined by vendor camera API implementation)
     * <p>
     * Note: This is not the same calculation as the display orientation
     *
     * @param screenOrientationDegrees Screen orientation in degrees
     * @return Number of degrees to rotate image in order for it to view correctly.
     */
    private int calcCameraRotation(int screenOrientationDegrees) {
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (mCameraInfo.orientation + screenOrientationDegrees) % 360;
        } else {  // back-facing
            final int landscapeFlip = isLandscape(screenOrientationDegrees) ? 180 : 0;
            return (mCameraInfo.orientation + screenOrientationDegrees + landscapeFlip) % 360;
        }
    }

    /**
     * Test if the supplied orientation is in landscape.
     *
     * @param orientationDegrees Orientation in degrees (0,90,180,270)
     * @return True if in landscape, false if portrait
     */
    private boolean isLandscape(int orientationDegrees) {
        return (orientationDegrees == Constants.LANDSCAPE_90 ||
                orientationDegrees == Constants.LANDSCAPE_270);
    }

    /**
     * @return {@code true} if {@link #mCameraParameters} was modified.
     */
    private boolean setAutoFocusInternal(boolean autoFocus) {
        mAutoFocus = autoFocus;
        if (isCameraOpened()) {
            final List<String> modes = mCameraParameters.getSupportedFocusModes();
            if (autoFocus && modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (modes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
            } else if (modes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            } else {
                mCameraParameters.setFocusMode(modes.get(0));
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return {@code true} if {@link #mCameraParameters} was modified.
     */
    private boolean setFlashInternal(int flash) {
        if (isCameraOpened()) {
            List<String> modes = mCameraParameters.getSupportedFlashModes();
            String mode = FLASH_MODES.get(flash);
            if (modes != null && modes.contains(mode)) {
                mCameraParameters.setFlashMode(mode);
                mFlash = flash;
                return true;
            }
            String currentMode = FLASH_MODES.get(mFlash);
            if (modes == null || !modes.contains(currentMode)) {
                mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mFlash = Constants.FLASH_OFF;
                return true;
            }
            return false;
        } else {
            mFlash = flash;
            return false;
        }
    }


    @Override
    protected void rectMeteringWithFocus(RectF rectF) {
        synchronized (Camera1.class) {
            if (mCamera == null || mCamera.getParameters() == null)
                return;
            if (mCamera.getParameters().getMaxNumMeteringAreas() == 0)
                return;
            if (rectF == null)
                return;

            RectF cropRect = ScanHelper.copyRect(rectF);
            cropRect.left += (cropRect.right - cropRect.left) / 4;
            cropRect.right -= (cropRect.right - cropRect.left) / 4;
            cropRect.top += (cropRect.bottom - cropRect.top) / 4;
            cropRect.bottom -= (cropRect.bottom - cropRect.top) / 4;

            int left = (int) (2000 * cropRect.top) - 1000;
            int top = (int) (2000 * (1 - cropRect.right)) - 1000;
            int right = (int) (2000 * cropRect.bottom) - 1000;
            int bottom = (int) (2000 * (1 - cropRect.left)) - 1000;

            Rect realRect = new Rect(left, top, right, bottom);
            List<Camera.Area> areas = Collections.singletonList(new Camera.Area(realRect, 1000));
            mCameraParameters.setFocusAreas(areas);
            mCameraParameters.setMeteringAreas(areas);
            try {
                mCamera.setParameters(mCameraParameters);
            } catch (Exception ignored) {
            }
        }
    }
}
