package com.ashlikun.zxing.view.style1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashlikun.zxing.R;
import com.ashlikun.zxing.view.ScanLightViewCallBack;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/2 21:03
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class ScanLightView extends FrameLayout implements ScanLightViewCallBack {

    private TextView tv;
    private ImageView iv;

    private boolean isOpen;

    Runnable open;
    Runnable close;
    //轻触照亮
    public String lightTextOpen;
    //轻触关闭
    public String lightTextOff;

    public ScanLightView(Context context) {
        super(context);
        initView();
    }

    public ScanLightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScanLightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        lightTextOpen = getContext().getString(R.string.xzxing_light_open);
        lightTextOff = getContext().getString(R.string.xzxing_light_off);
        View v = LayoutInflater.from(getContext()).inflate(R.layout.xzxing_light_layout, null);
        iv = v.findViewById(R.id.light_img);
        tv = v.findViewById(R.id.light_text);
        addView(v);
        setOnClickListener(v1 -> toggle());
        setVisibility(View.GONE);
    }

    public void toggle() {
        if (tv.getText().equals(lightTextOpen))
            open();
        else close();
    }

    private void open() {
        if (open != null)
            open.run();
        isOpen = true;
        tv.setText(lightTextOff);
        iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.xzxing_light_open));
    }

    private void close() {
        if (close != null)
            close.run();
        isOpen = false;
        tv.setText(lightTextOpen);
        iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.xzxing_light_close));

    }

    @Override
    public void lightBrighter() {
        setVisibility(View.VISIBLE);
    }

    @Override
    public void lightDark() {
        if (!isOpen)
            setVisibility(View.GONE);
    }

    @Override
    public void regLightOperator(Runnable open, Runnable close) {
        this.open = open;
        this.close = close;
    }

    @Override
    public void cameraStartLaterInit() {
        close();
        setVisibility(View.GONE);
    }

}
