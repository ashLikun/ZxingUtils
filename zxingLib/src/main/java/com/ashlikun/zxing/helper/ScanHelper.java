package com.ashlikun.zxing.helper;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.ashlikun.zxing.Config;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ResultPoint;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 20:51
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：Zxing相关数据转换
 */
public class ScanHelper {

    /***
     * 旋转矩形 -90
     * @param rect
     * @return
     */
    public static RectF rotateUnCloseWise(RectF rect) {
        RectF rect1 = new RectF();
        rect1.left = rect.top;
        rect1.top = rect.left;
        rect1.right = rect.bottom;
        rect1.bottom = rect.right;
        return rect1;
    }

    private static RectF adapterRect(RectF rect) {
        RectF rect1 = new RectF();
        rect1.top = rect.left;
        rect1.right = rect.bottom;
        rect1.bottom = rect.right;
        rect1.left = rect.top;
        return rect1;
    }

    public static RectF adapter90(RectF rect) {
        rect = adapterRect(rect);
        RectF rect1 = new RectF();
        rect1.left = 1 - rect.right;
        rect1.top = rect.top;
        rect1.right = 1 - rect.left;
        rect1.bottom = rect.bottom;
        return rect1;
    }

    public static RectF adapter270(RectF rectF) {
        rectF = adapterRect(rectF);
        RectF rect1 = new RectF();
        rect1.left = rectF.left;
        rect1.top = 1 - rectF.bottom;
        rect1.right = rectF.right;
        rect1.bottom = 1 - rectF.top;
        return rect1;
    }

    public static Rect copyRect(Rect rect) {
        Rect result = new Rect();
        result.left = rect.left;
        result.top = rect.top;
        result.right = rect.right;
        result.bottom = rect.bottom;
        return result;
    }

    public static RectF copyRect(RectF rectF) {
        RectF result = new RectF();
        result.left = rectF.left;
        result.top = rectF.top;
        result.right = rectF.right;
        result.bottom = rectF.bottom;
        return result;
    }


    /***
     * 放大矩形
     */
    public static RectF scaleRectF(RectF rectF, float ratio) {

        RectF newRectF = copyRect(rectF);

        newRectF.left = newRectF.left - (ratio - 1f) * newRectF.width() / 2;
        newRectF.top = newRectF.top - (ratio - 1f) * newRectF.height() / 2;
        newRectF.right = newRectF.right + (ratio - 1f) * newRectF.width() / 2;
        newRectF.bottom = newRectF.bottom + (ratio - 1f) * newRectF.height() / 2;

        if (newRectF.left < 0 || newRectF.left > 1)
            newRectF.left = newRectF.left < 0 ? 0 : 1;

        if (newRectF.top < 0 || newRectF.top > 1)
            newRectF.top = newRectF.top < 0 ? 0 : 1;

        if (newRectF.right < 0 || newRectF.right > 1)
            newRectF.right = newRectF.right < 0 ? 0 : 1;

        if (newRectF.bottom < 0 || newRectF.bottom > 1)
            newRectF.bottom = newRectF.bottom < 0 ? 0 : 1;

        return newRectF;
    }


    /***
     * 放大矩形
     */
    public static Rect scaleRect(Rect rect, float ratio, int maxWidth, int maxHeight) {

        Rect newRect = copyRect(rect);

        newRect.left = (int) (newRect.left - (ratio - 1f) * newRect.width() / 2);
        newRect.top = (int) (newRect.top - (ratio - 1f) * newRect.height() / 2);
        newRect.right = (int) (newRect.right + (ratio - 1f) * newRect.width() / 2);
        newRect.bottom = (int) (newRect.bottom + (ratio - 1f) * newRect.height() / 2);

        if (newRect.left < 0 || newRect.left > maxWidth)
            newRect.left = newRect.left < 0 ? 0 : maxWidth;

        if (newRect.top < 0 || newRect.top > maxHeight)
            newRect.top = newRect.top < 0 ? 0 : maxHeight;

        if (newRect.right < 0 || newRect.right > maxWidth)
            newRect.right = newRect.right < 0 ? 0 : maxWidth;

        if (newRect.bottom < 0 || newRect.bottom > maxHeight)
            newRect.bottom = newRect.bottom < 0 ? 0 : maxHeight;

        return newRect;
    }


    /***
     * 二维码坐标转换屏幕坐标
     * @param point
     * @return
     */
    public static PointF[] rotatePoint(ResultPoint[] point) {

        if (point == null || point.length == 0)
            return null;

        if (Config.scanRect.getScanR() == null)
            return null;

        float preX = Config.scanRect.getPreX();
        float preY = Config.scanRect.getPreY();
        float extraX = Config.scanRect.getExtraX();
        float extraY = Config.scanRect.getExtraY();

        float aspeX, aspeY;

        if (Config.is90() || Config.is270()) {
            aspeX = (preX + extraX) / (float) Config.scanRect.getDataX();
            aspeY = (preY + extraY) / (float) Config.scanRect.getDataY();
        } else {
            aspeX = (preX + extraX) / (float) Config.scanRect.getDataY();
            aspeY = (preY + extraY) / (float) Config.scanRect.getDataX();
        }

        PointF[] pointFS = new PointF[point.length];

        for (int i = 0; i < point.length; i++) {

            float relatPointX, relatPointY;

            if (Config.is90()) {
                relatPointX = (Config.scanRect.getScanR().left + point[i].getX()) * aspeX - extraX / 2;
                relatPointY = (Config.scanRect.getScanR().top + point[i].getY()) * aspeY - extraY / 2;
            } else if (Config.is270()) {
                relatPointX = preX - (Config.scanRect.getScanR().left + point[i].getX()) * aspeX + extraX / 2;
                relatPointY = preY - (Config.scanRect.getScanR().top + point[i].getY()) * aspeY + extraY / 2;
            } else {
                relatPointX = preX + extraX / 2 - (Config.scanRect.getScanR().top + point[i].getY()) * aspeX;
                relatPointY = (Config.scanRect.getScanR().left + point[i].getX()) * aspeY - extraY / 2;
            }

            pointFS[i] = new PointF(relatPointX, relatPointY);
        }

        return pointFS;
    }

    /***
     * 二维码坐标转换屏幕坐标(旋转后)
     * @param point
     * @return
     */
    public static PointF[] rotatePointR(ResultPoint[] point) {

        if (point == null || point.length == 0)
            return null;

        if (Config.scanRect.getScanR() == null)
            return null;

        float preX = Config.scanRect.getPreX();
        float preY = Config.scanRect.getPreY();
        float extraX = Config.scanRect.getExtraX();
        float extraY = Config.scanRect.getExtraY();
//        float parseDataX = Config.scanRect.getScanR().height();
//        float parseDataY = Config.scanRect.getScanR().width();

        float aspeX, aspeY;

        if (Config.is90() || Config.is270()) {
            aspeX = (preX + extraX) / (float) Config.scanRect.getDataX();
            aspeY = (preY + extraY) / (float) Config.scanRect.getDataY();
        } else {
            aspeX = (preX + extraX) / (float) Config.scanRect.getDataY();
            aspeY = (preY + extraY) / (float) Config.scanRect.getDataX();
        }

        PointF[] pointFS = new PointF[point.length];

        for (int i = 0; i < point.length; i++) {

            float relatPointX, relatPointY;

            if (Config.is90()) {
                relatPointX = (Config.scanRect.getScanR().left + point[i].getY()) * aspeX - extraX / 2;
                relatPointY = (Config.scanRect.getScanR().top + point[i].getX()) * aspeY - extraY / 2;
            } else if (Config.is270()) {
                relatPointX = preX - (Config.scanRect.getScanR().left + point[i].getY()) * aspeX + extraX / 2;
                relatPointY = preY - (Config.scanRect.getScanR().top + point[i].getX()) * aspeY + extraY / 2;
            } else {
                relatPointX = preX + extraX / 2 - (Config.scanRect.getScanR().top + point[i].getX()) * aspeX;
                relatPointY = (Config.scanRect.getScanR().left + point[i].getY()) * aspeY - extraY / 2;
            }

            pointFS[i] = new PointF(relatPointX, relatPointY);
        }
        return pointFS;
    }


    /***
     * 二维码边长转屏幕px
     */
    public static int calQrLenghtShow(ResultPoint[] point) {

        if (point == null || point.length == 0)
            return 0;

        PointF avargPoint = new PointF();
        for (ResultPoint item : point) {
            avargPoint.x += Math.abs(item.getX());
            avargPoint.y += Math.abs(item.getY());
        }
        avargPoint.x /= point.length;
        avargPoint.y /= point.length;

        int x = (int) Math.pow((avargPoint.x - point[0].getX()), 2);
        int y = (int) Math.pow((avargPoint.y - point[0].getY()), 2);

        float asp = Math.max(Config.scanRect.getPreX(), Config.scanRect.getPreY()) /
                (float) Math.max(Config.scanRect.getDataX(), Config.scanRect.getDataY());

        return (int) (Math.sqrt(x + y) / Math.sqrt(2) * 2 * asp);
    }


    /***
     * 计算中心点
     * @param point
     * @return
     */
    public static PointF calCenterPointF(PointF[] point) {

        if (point == null || point.length == 0)
            return new PointF();

        PointF avargPoint = new PointF();
        for (PointF item : point) {
            avargPoint.x += Math.abs(item.x);
            avargPoint.y += Math.abs(item.y);
        }
        avargPoint.x /= point.length;
        avargPoint.y /= point.length;

        return avargPoint;
    }


    /***
     * 二维码旋转角度(相对对角线与x轴45度夹角)
     */
    public static float calQrRotate(PointF[] point) {

        PointF avargPoint = calCenterPointF(point);

        int x = (int) Math.abs(avargPoint.x - point[0].x);
        int y = (int) Math.abs(avargPoint.y - point[0].y);

        float maxRotate = (float) ((float) Math.atan2(x, y) / 2 / Math.PI * 360);
        return (float) (maxRotate - 45);
    }


    /**
     * A factory method to build the appropriate LuminanceSource object based on the format
     * of the preview buffers, as described by Camera.Parameters.
     *
     * @param data A preview frame.
     * @return A PlanarYUVLuminanceSource instance.
     */
    public static PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width,
                                                                int height, Rect rect) {
        if ((rect.left == 0 && rect.right == 0) || (rect.top == 0 && rect.bottom == 0)) {
            try {
                throw new Exception("扫码解析区域异常");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                rect.width(), rect.height());
    }

    /***
     * 获取显示区域对应的相机源数据解码区域
     * @return
     */
    public static Rect getScanByteRect(int dataWidth, int dataHeight) {

        try {
            if (Config.scanRect.getRect() == null)
                return new Rect(0, 0, 0, 0);

            RectF cropRect = Config.scanRect.getRect();
            //默认采集的数据
            if (dataWidth >= dataHeight) {
                if (Config.scanRect.getScanR() == null) {
                    Config.scanRect.setScanR(new Rect());
                    Config.scanRect.getScanR().left = (int) (cropRect.top * dataWidth);
                    Config.scanRect.getScanR().top = (int) ((1f - cropRect.right) * dataHeight);
                    Config.scanRect.getScanR().right = (int) (cropRect.bottom * dataWidth);
                    Config.scanRect.getScanR().bottom = (int) ((1f - (cropRect.left)) * dataHeight);
                }
                return Config.scanRect.getScanR();
            }
        } catch (Exception ignored) {
        }
        return new Rect(0, 0, 0, 0);
    }

    /***
     * 计算探测器获取二维码大小
     */
    public static int getQrLenght(ResultPoint[] point) {

        if (Config.scanRect.getScanR() == null)
            return 0;

        if (point.length < 3)
            return 0;

        //计算中心点坐标
        PointF avargPoint = new PointF();
        for (ResultPoint item : point) {
            avargPoint.x += item.getX();
            avargPoint.y += item.getY();
        }
        avargPoint.x /= point.length;
        avargPoint.y /= point.length;
        //根据中心点到一点举例计算二维码边长
        int sideA = (int) (avargPoint.x - point[0].getX());
        int sideB = (int) (avargPoint.y - point[0].getY());

        //数据长度转显示长度
        float preX = Config.scanRect.getPreX();
        float aspX = preX / (float) Config.scanRect.getScanR().height();

        return (int) (Math.sqrt(sideA * sideA + sideB * sideB) / Math.sqrt(2) * 2 * aspX);
    }

}
