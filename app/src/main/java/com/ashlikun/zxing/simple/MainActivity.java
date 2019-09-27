package com.ashlikun.zxing.simple;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ashlikun.zxing.CodeUtils;
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
        requestPermissions(new String[]{Manifest.permission.CAMERA}, 111);
        textureView = findViewById(R.id.textureView);
        viewfinderView = findViewById(R.id.viewfinderView);
        imageView = findViewById(R.id.imageView);
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                zxing = new Zxing(textureView,
                        MainActivity.this,
                        null,
                        null,
                        viewfinderView);
                //设置亮度改变监听
                zxing.setLightChangListener(MainActivity.this);
                //开启解码成功的音效
                zxing.setPlayBeep(true);
                zxing.create();
            }
        }, 1000);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = CodeUtils.createImage("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 200, 200, null);
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (zxing != null) {
            zxing.onResume();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (zxing != null) {
            zxing.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (zxing != null) {
            zxing.onDestroy();
        }
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
