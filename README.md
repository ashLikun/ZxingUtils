[![Release](https://jitpack.io/v/ashLikun/ZxingUtils.svg)](https://jitpack.io/#ashLikun/ZxingUtils)

# **ZxingUtils**
项目简介
- 基于google-cameraView调整优化，大量机型测试，可稳定流畅启动关闭相机
- Camera2-Camera1分别实现扫码, 高版本默认走Camera2， 低版本Camera1， Camera2启动失败走Camera1
- 灰度算法处理， 可应付一些特殊场景二维码并可拓展
- 自定义探测器支持非白边等异形二维码识别
- zxing源码修改，彻底解决复杂二维码扫出一堆不相干数字问题
- 可能是目前最完善的扫码横竖屏切换，可配置不同布局，可动态切换
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
https://github.com/ailiwean/NBZxing.wiki.git
## 详细介绍

* ### 自动扫码
https://github.com/ailiwean/NBZxing/wiki
* ### 选择图片并解析

