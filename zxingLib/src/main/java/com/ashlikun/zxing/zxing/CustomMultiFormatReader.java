package com.ashlikun.zxing.zxing;

import com.ashlikun.zxing.Config;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.aztec.AztecReader;
import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.maxicode.MaxiCodeReader;
import com.google.zxing.oned.MultiFormatOneDReader;
import com.google.zxing.pdf417.PDF417Reader;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


/**
 * @author　　: 李坤
 * 创建时间: 2022/5/3 13:36
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：ZXing 扫码的核心类，内部可以嵌入多种扫描模式
 */
public class CustomMultiFormatReader implements Reader {

    private Map<DecodeHintType, ?> hints;
    private Reader[] readers;
    private ScanTypeConfig mBarcodeType;

    /**
     * This version of decode honors the intent of Reader.decode(BinaryBitmap) in that it
     * passes null as a hint to the decoders. However, that makes it inefficient to call repeatedly.
     * Use setHints() followed by decodeWithState() for continuous scan applications.
     *
     * @param image The pixel data to decode
     * @return The contents of the image
     * @throws NotFoundException Any errors which occurred
     */
    @Override
    public Result decode(BinaryBitmap image) {
        return decodeInternal(image);
    }

    /**
     * Decode an image using the hints provided. Does not honor existing state.
     *
     * @param image The pixel data to decode
     * @param hints The hints to use, clearing the previous state.
     * @return The contents of the image
     * @throws NotFoundException Any errors which occurred
     */
    @Override
    public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
        return decodeInternal(image);
    }

    /**
     * Decode an image using the state set up by calling setHints() previously. Continuous scan
     * clients will get a <b>large</b> speed increase by using this instead of decode().
     *
     * @param image The pixel data to decode
     * @return The contents of the image
     * @throws NotFoundException Any errors which occurred
     */
    public Result decodeWithState(BinaryBitmap image) throws NotFoundException {
        // Make sure to set up the default state so we don't crash
        if (readers == null) {
            setHints(null);
        }
        return decodeInternal(image);
    }

    /**
     * This method adds state to the MultiFormatReader. By setting the hints once, subsequent calls
     * to decodeWithState(image) can reuse the same set of readers without reallocating memory. This
     * is important for performance in continuous scan clients.
     *
     * @param hints The set of hints to use for subsequent calls to decode(image)
     */
    public void setHints(Map<DecodeHintType, ?> hints) {
        this.hints = hints;

        boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        @SuppressWarnings("unchecked")
        Collection<BarcodeFormat> formats =
                hints == null ? null : (Collection<BarcodeFormat>) hints.get(DecodeHintType.POSSIBLE_FORMATS);
        Collection<Reader> readers = new ArrayList<>();
        if (formats != null) {
            boolean addOneDReader =
                    formats.contains(BarcodeFormat.UPC_A) ||
                            formats.contains(BarcodeFormat.UPC_E) ||
                            formats.contains(BarcodeFormat.EAN_13) ||
                            formats.contains(BarcodeFormat.EAN_8) ||
                            formats.contains(BarcodeFormat.CODABAR) ||
                            formats.contains(BarcodeFormat.CODE_39) ||
                            formats.contains(BarcodeFormat.CODE_93) ||
                            formats.contains(BarcodeFormat.CODE_128) ||
                            formats.contains(BarcodeFormat.ITF) ||
                            formats.contains(BarcodeFormat.RSS_14) ||
                            formats.contains(BarcodeFormat.RSS_EXPANDED);
            // Put 1D readers upfront in "normal" mode
            if (addOneDReader && !tryHarder) {
                readers.add(new MultiFormatOneDReader(hints));
            }
            if (formats.contains(BarcodeFormat.QR_CODE)) {
                readers.add(new QRCodeReader());
            }
            if (formats.contains(BarcodeFormat.DATA_MATRIX)) {
                readers.add(new DataMatrixReader());
            }
            if (formats.contains(BarcodeFormat.AZTEC)) {
                readers.add(new AztecReader());
            }
            if (formats.contains(BarcodeFormat.PDF_417)) {
                readers.add(new PDF417Reader());
            }
            if (formats.contains(BarcodeFormat.MAXICODE)) {
                readers.add(new MaxiCodeReader());
            }
            // At end in "try harder" mode
            if (addOneDReader && tryHarder) {
                readers.add(new MultiFormatOneDReader(hints));
            }
        }
        if (readers.isEmpty()) {
            if (!tryHarder) {
                readers.add(new MultiFormatOneDReader(hints));
            }

            readers.add(new QRCodeReader());
            readers.add(new DataMatrixReader());
            readers.add(new AztecReader());
            readers.add(new PDF417Reader());
            readers.add(new MaxiCodeReader());

            if (tryHarder) {
                readers.add(new MultiFormatOneDReader(hints));
            }
        }
        this.readers = readers.toArray(new Reader[readers.size()]);
    }

    @Override
    public void reset() {
        if (readers != null) {
            for (Reader reader : readers) {
                reader.reset();
            }
        }
    }

    private Result decodeInternal(BinaryBitmap image) {
        Result resultFinal = null;
        if (readers != null) {
            for (Reader reader : readers) {
                try {
                    Result result = reader.decode(image, hints);
                    if (result != null)
                        resultFinal = result;
                    if (result != null && result.getText() != null)
                        return resultFinal;
                } catch (ReaderException re) {
                    // continue
                } catch (Exception ignored) {
                }
            }
        }
        return resultFinal;
    }


    /**
     * 设置识别的格式
     *
     * @param barcodeType 识别的格式
     * @param hintMap     barcodeType 为 BarcodeType.CUSTOM 时，必须指定该值
     */
    public void setType(ScanTypeConfig barcodeType, Map<DecodeHintType, Object> hintMap) {
        mBarcodeType = barcodeType;
        hints = hintMap;
        if (barcodeType == ScanTypeConfig.CUSTOM && (hintMap == null || hintMap.isEmpty())) {
            throw new RuntimeException("barcodeType 为 BarcodeType.CUSTOM 时 hintMap 不能为空");
        }
        setupReader();
    }

    protected void setupReader() {

        if (mBarcodeType == ScanTypeConfig.ONE_DIMENSION) {
            setHints(QRTypeConfig.ONE_DIMENSION_HINT_MAP);
        } else if (mBarcodeType == ScanTypeConfig.TWO_DIMENSION) {
            setHints(QRTypeConfig.TWO_DIMENSION_HINT_MAP);
        } else if (mBarcodeType == ScanTypeConfig.ONLY_QR_CODE) {
            setHints(QRTypeConfig.QR_CODE_HINT_MAP);
        } else if (mBarcodeType == ScanTypeConfig.ONLY_CODE_128) {
            setHints(QRTypeConfig.CODE_128_HINT_MAP);
        } else if (mBarcodeType == ScanTypeConfig.ONLY_EAN_13) {
            setHints(QRTypeConfig.EAN_13_HINT_MAP);
        } else if (mBarcodeType == ScanTypeConfig.HIGH_FREQUENCY) {
            setHints(QRTypeConfig.HIGH_FREQUENCY_HINT_MAP);
        } else if (mBarcodeType == ScanTypeConfig.CUSTOM) {
            setHints(hints);
        } else {
            setHints(QRTypeConfig.ALL_HINT_MAP);
        }
    }

    static CustomMultiFormatReader customMultiFormatReader;

    //获取解析的核心类
    public static CustomMultiFormatReader getInstance() {
        if (customMultiFormatReader == null) {
            synchronized (CustomMultiFormatReader.class) {
                if (customMultiFormatReader == null)
                    customMultiFormatReader = new CustomMultiFormatReader();
            }
        }
        return customMultiFormatReader;
    }

    private CustomMultiFormatReader() {
        setType(Config.scanTypeConfig, null);
    }
}
