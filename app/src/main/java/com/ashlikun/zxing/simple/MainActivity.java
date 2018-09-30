package com.ashlikun.zxing.simple;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.widget.ImageView;

import com.ashlikun.zxing.OnLightChangListener;
import com.ashlikun.zxing.Zxing;
import com.ashlikun.zxing.ZxingCallback;
import com.ashlikun.zxing.view.ViewfinderView;
import com.google.zxing.Result;

public class MainActivity extends AppCompatActivity implements ZxingCallback, OnLightChangListener {

    TextureView textureView;
    ImageView imageView;
    ViewfinderView viewfinderView;
    Zxing zxing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView = findViewById(R.id.textureView);
        viewfinderView = findViewById(R.id.viewfinderView);
        imageView = findViewById(R.id.imageView);
        zxing = new Zxing(textureView,
                this,
                null,
                null,
                viewfinderView);
        //设置亮度改变监听
        zxing.setLightChangListener(this);
        //开启解码成功的音效
        zxing.setPlayBeep(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        zxing.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        zxing.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zxing.onDestroy();
    }

    @Override
    public void onOpenCameraFail() {
        Log.e("aaa", "onOpenCameraFail");
    }

    @Override
    public void onOpenCamera() {
        Log.e("aaa", "onOpenCamera");
    }

    @Override
    public void onDecodeSuccess(Result result, Bitmap barcode) {
        Log.e("aaa", "result = " + result.getText());
        zxing.reStart();
        imageView.setImageBitmap(barcode);
    }

    @Override
    public void onLightChang(boolean dark) {
        Log.e("aaa", "dark = " + dark);
    }
}
