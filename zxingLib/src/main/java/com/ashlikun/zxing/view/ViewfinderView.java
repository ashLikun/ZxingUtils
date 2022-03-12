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

package com.ashlikun.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.ashlikun.zxing.R;
import com.google.zxing.ResultPoint;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author　　: 李坤
 * 创建时间: 2018/9/28 9:46
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：自定义组件实现,扫描功能
 */

public final class ViewfinderView extends View implements IViewDecodeBridge {

    private static final int OPAQUE = 0xFF;

    private final Paint paint;

    private final int resultPointColor;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private DisplayMetrics displayMetrics;
    /**
     * 扫描框Size
     */
    private int frameWidth;
    private int frameHeight;
    /**
     * 扫描框顶部距离
     */
    private int innerMarginTop;
    /**
     * 扫描线移动的y
     */
    private int scanLineTop;
    /**
     * 扫描线移动速度
     */
    private int scan_velocity;
    /**
     * 扫码的zxing识别的边框在界面边框的比例
     */
    private float scan_ratio;
    /**
     * 扫描线
     */
    private Bitmap scanLight;
    /**
     * 是否展示小圆点
     */
    private boolean isCircle;
    /**
     * 是否绘制边框
     */
    private boolean drawFrame;
    /**
     * 扫描框边角颜色
     */
    private int innercornercolor = 0xff118eea;
    /**
     * 扫描框边角长度
     */
    private int innercornerlength;
    /**
     * 扫描框边角宽度
     */
    private int innercornerwidth;
    /**
     * 边框线的宽度
     */
    private int innerFrameLineWidth;
    /**
     * 蒙层颜色
     */
    private int maskColor;

    public ViewfinderView(Context context) {
        this(context, null);
    }

    public ViewfinderView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);

    }

    public ViewfinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        Resources resources = getResources();
        displayMetrics = resources.getDisplayMetrics();
        resultPointColor = resources.getColor(R.color.viewfinder_possible_result_points);
        initInnerRect(context, attrs);
    }

    /**
     * 初始化内部框的大小
     *
     * @param context
     * @param attrs
     */
    private void initInnerRect(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewfinderView);
        // 扫描框距离顶部
        innerMarginTop = (int) ta.getDimension(R.styleable.ViewfinderView_inner_margintop, 0);
        maskColor = ta.getColor(R.styleable.ViewfinderView_inner_maskColor, getResources().getColor(R.color.viewfinder_mask));
        // 扫描框的宽度
        frameWidth = ta.getLayoutDimension(R.styleable.ViewfinderView_inner_width, displayMetrics.widthPixels / 2);
        // 扫描框的高度
        frameHeight = ta.getLayoutDimension(R.styleable.ViewfinderView_inner_height, displayMetrics.widthPixels / 2);
        // 扫描框边角颜色
        innercornercolor = ta.getColor(R.styleable.ViewfinderView_inner_corner_color, innercornercolor);
        // 扫描框边角长度
        innercornerlength = (int) ta.getDimension(R.styleable.ViewfinderView_inner_corner_length, displayMetrics.density * 20);
        // 扫描框边角宽度
        innercornerwidth = (int) ta.getDimension(R.styleable.ViewfinderView_inner_corner_width, displayMetrics.density * 4);
        //边框线的宽度
        innerFrameLineWidth = (int) ta.getDimension(R.styleable.ViewfinderView_inner_frame_line_width, displayMetrics.density * 1);
        // 扫描控件
        scanLight = BitmapFactory.decodeResource(getResources(), ta.getResourceId(R.styleable.ViewfinderView_inner_scan_src, R.drawable.scan_light));
        // 扫描速度
        scan_velocity = ta.getInt(R.styleable.ViewfinderView_inner_scan_speed, 30);
        //扫码的zxing识别的边框在界面边框的比例
        scan_ratio = ta.getFloat(R.styleable.ViewfinderView_inner_scan_image_ratio, 1.1f);
        isCircle = ta.getBoolean(R.styleable.ViewfinderView_inner_scan_iscircle, false);
        drawFrame = ta.getBoolean(R.styleable.ViewfinderView_inner_drowFrame, true);
        ta.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (frameWidth == ViewGroup.LayoutParams.MATCH_PARENT || frameWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            frameWidth = getWidth();
        }
        if (frameHeight == ViewGroup.LayoutParams.MATCH_PARENT || frameHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            frameHeight = getHeight();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = getFramingRectShow();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        //蒙层
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
        drawFrameBounds(canvas, frame);

        drawScanLight(canvas, frame);

        if (isCircle && possibleResultPoints != null) {
            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);

                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
                }
            }
        }

        postInvalidateDelayed(scan_velocity, frame.left, frame.top, frame.right, frame.bottom);
    }


    /**
     * 绘制移动扫描线
     *
     * @param canvas
     * @param frame
     */
    private void drawScanLight(Canvas canvas, Rect frame) {

        if (scanLineTop == 0) {
            scanLineTop = frame.top;
        }

        if (scanLineTop >= frame.bottom - 30) {
            scanLineTop = frame.top;
        } else {
            scanLineTop += 5;
        }
        Rect scanRect = new Rect(frame.left, scanLineTop, frame.right,
                scanLineTop + 30);
        canvas.drawBitmap(scanLight, null, scanRect, paint);
    }


    /**
     * 绘制取景框边框
     *
     * @param canvas
     * @param frame
     */
    private void drawFrameBounds(Canvas canvas, Rect frame) {
        if (!drawFrame) {
            return;
        }

        paint.setColor(innercornercolor);
        paint.setStyle(Paint.Style.FILL);

        int corWidth = innercornerwidth;
        int corLength = innercornerlength;

        // 左上角
        canvas.drawRect(frame.left, frame.top, frame.left + corWidth, frame.top
                + corLength, paint);
        canvas.drawRect(frame.left, frame.top, frame.left
                + corLength, frame.top + corWidth, paint);
        // 右上角
        canvas.drawRect(frame.right - corWidth, frame.top, frame.right,
                frame.top + corLength, paint);
        canvas.drawRect(frame.right - corLength, frame.top,
                frame.right, frame.top + corWidth, paint);
        // 左下角
        canvas.drawRect(frame.left, frame.bottom - corLength,
                frame.left + corWidth, frame.bottom, paint);
        canvas.drawRect(frame.left, frame.bottom - corWidth, frame.left
                + corLength, frame.bottom, paint);
        // 右下角
        canvas.drawRect(frame.right - corWidth, frame.bottom - corLength,
                frame.right, frame.bottom, paint);
        canvas.drawRect(frame.right - corLength, frame.bottom - corWidth,
                frame.right, frame.bottom, paint);
        if (innerFrameLineWidth > 0) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(innerFrameLineWidth);
            //边框线
            canvas.drawRect(frame, paint);
        }
    }


    public void addPossibleResultPoint(ResultPoint point) {
        if (isCircle) {
            if (possibleResultPoints == null) {
                possibleResultPoints = new HashSet<>(5);
            }
            possibleResultPoints.add(point);
        }
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint point) {
        addPossibleResultPoint(point);
    }

    /**
     * 获取二维码放置的矩形区域
     */
    @Override
    public Rect getFramingRect() {
        try {
            int fWidth = Math.min((int) (frameWidth * scan_ratio), getWidth());
            int fHeight = Math.min((int) (frameHeight * scan_ratio), getHeight());
            int leftOffset = (getWidth() - fWidth) / 2;
            int topOffset = (getHeight() - fHeight) / 2 + innerMarginTop;
            return new Rect(leftOffset, topOffset, leftOffset + fWidth, topOffset + fHeight);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取二维码放置的矩形区域(用于识别时候页面的边框)
     */
    public Rect getFramingRectShow() {
        try {
            int leftOffset = (getWidth() - frameWidth) / 2;
            int topOffset = (getHeight() - frameHeight) / 2 + innerMarginTop;
            return new Rect(leftOffset, topOffset, leftOffset + frameWidth, topOffset + frameHeight);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取底部的线距离顶部距离。在测量完成后才有
     *
     * @return
     */
    public int getBottomFrame() {
        return getHeight() / 2 + frameHeight / 2;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
        requestLayout();
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
        requestLayout();
    }

    public int getInnerMarginTop() {
        return innerMarginTop;
    }

    public void setInnerMarginTop(int innerMarginTop) {
        this.innerMarginTop = innerMarginTop;
        requestLayout();
    }

    public boolean isDrawFrame() {
        return drawFrame;
    }

    public void setDrawFrame(boolean drawFrame) {
        this.drawFrame = drawFrame;
        requestLayout();
    }

    public int getInnercornercolor() {
        return innercornercolor;
    }

    public void setInnercornercolor(int innercornercolor) {
        this.innercornercolor = innercornercolor;
        requestLayout();
    }

    public int getInnercornerlength() {
        return innercornerlength;
    }

    public void setInnercornerlength(int innercornerlength) {
        this.innercornerlength = innercornerlength;
        requestLayout();
    }

    public int getInnercornerwidth() {
        return innercornerwidth;
    }

    public void setInnercornerwidth(int innercornerwidth) {
        this.innercornerwidth = innercornerwidth;
        requestLayout();
    }

    public int getInnerFrameLineWidth() {
        return innerFrameLineWidth;
    }

    public void setInnerFrameLineWidth(int innerFrameLineWidth) {
        this.innerFrameLineWidth = innerFrameLineWidth;
        requestLayout();
    }

    public int getResultPointColor() {
        return resultPointColor;
    }

    public int getScanLineTop() {
        return scanLineTop;
    }

    public void setScanLineTop(int scanLineTop) {
        this.scanLineTop = scanLineTop;
        requestLayout();
    }

    public int getScan_velocity() {
        return scan_velocity;
    }

    public void setScan_velocity(int scan_velocity) {
        this.scan_velocity = scan_velocity;
        requestLayout();
    }

    public float getScan_ratio() {
        return scan_ratio;
    }

    public void setScan_ratio(float scan_ratio) {
        this.scan_ratio = scan_ratio;
        requestLayout();
    }

    public Bitmap getScanLight() {
        return scanLight;
    }

    public void setScanLight(Bitmap scanLight) {
        this.scanLight = scanLight;
        requestLayout();
    }

    public boolean isCircle() {
        return isCircle;
    }

    public void setCircle(boolean circle) {
        isCircle = circle;
        requestLayout();
    }

    public int getMaskColor() {
        return maskColor;
    }

    public void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
        requestLayout();
    }
}
