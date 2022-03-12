[![Release](https://jitpack.io/v/ashLikun/ZxingUtils.svg)](https://jitpack.io/#ashLikun/ZxingUtils)

# **ZxingUtils**
项目简介
    封装zxing扫码的实现,只负责解码，控制权完全交于开发者
## 使用方法

build.gradle文件中添加:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
并且:

```gradle
dependencies {
    compile 'com.github.ashLikun:ZxingUtils:{latest version}'
    //Zxing核心库
    com.google.zxing:core:3.3.0
}
```

## 详细介绍

* ### 自动扫码
```java
   Zxing zxing  = new Zxing(textureView,//渲染的view
                             this, //事件回调
                             null, //解码格式，可以null
                             null, //字符编码，可以null
                             viewfinderView// 解码器与view的交互);
   //监听亮度改变,不为null内部就会解析图片的亮度
   zxing.setLightChangListener(this);
   //开启解码成功的音效
   zxing.setPlayBeep(true);
   zxing.create();
   
   //成功后可以调用这个方法继续识别
   zxing.reStart();
   //生命周期,一定要在activity调用,如果context 是LifecycleOwner 那么生命周期是自动的
   zxing.onResume();
   zxing.onPause();
   zxing.onDestroy();
```

* ### 选择图片并解析
```java
    CodeUtils.analyzeBitmap(uri, new CodeUtils.AnalyzeCallback() {
            @Override
            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                Toast.makeText(MainActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAnalyzeFailed() {
                Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
            }
        });
```

