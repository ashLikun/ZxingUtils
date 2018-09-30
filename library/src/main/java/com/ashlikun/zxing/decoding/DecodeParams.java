package com.ashlikun.zxing.decoding;

import android.os.Handler;

import com.ashlikun.zxing.OnLightChangListener;
import com.ashlikun.zxing.view.IViewDecodeBridge;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;

import java.util.Hashtable;
import java.util.Vector;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/9/29　16:09
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：解码内部相关参数
 */
public class DecodeParams {
    Vector<BarcodeFormat> decodeFormats;
    String characterSet;
    IViewDecodeBridge bridge;
    Handler resultHandler;
    OnLightChangListener lightChangListener;

    /**
     * @param decodeFormats 解码格式，可以null
     * @param characterSet  字符编码，可以null
     * @param bridge        扫描框和解码器交互接口
     * @param resultHandler 解码结果回调  R.id.decode_succeeded   R.id.decode_failed(失败了会继续解析)
     */
    public DecodeParams(Vector<BarcodeFormat> decodeFormats, String characterSet, IViewDecodeBridge bridge, Handler resultHandler) {
        this.decodeFormats = decodeFormats;
        this.characterSet = characterSet;
        this.bridge = bridge;
        this.resultHandler = resultHandler;
    }

    public void setDecodeFormats(Vector<BarcodeFormat> decodeFormats) {
        this.decodeFormats = decodeFormats;
    }

    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }

    public void setBridge(IViewDecodeBridge bridge) {
        this.bridge = bridge;
    }


    public void setResultHandler(Handler resultHandler) {
        this.resultHandler = resultHandler;
    }

    /**
     * 设置监听光线改变
     *
     * @param lightChangListener
     */
    public void setLightChangListener(OnLightChangListener lightChangListener) {
        this.lightChangListener = lightChangListener;
    }

    public Hashtable getHashtable() {
        Hashtable hints = new Hashtable(3);
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        }

        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        if (characterSet != null) {
            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }

        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, bridge);
        return hints;
    }
}
