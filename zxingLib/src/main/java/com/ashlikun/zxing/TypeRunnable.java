package com.ashlikun.zxing;

import androidx.annotation.IntDef;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/3 13:38
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */

public class TypeRunnable implements Runnable {

    public static final int NORMAL = 0;
    public static final int SCALE = 1;
    public static final int OTHER = 2;
    private final Runnable runnable;
    final int type;

    private TypeRunnable(int type,  Runnable runnable) {
        this.type = type;
        this.runnable = runnable;
    }

    public static TypeRunnable create(@Range int type,
                                      Runnable runnable) {
        return new TypeRunnable(type, runnable);
    }

    @Override
    public void run() {
        runnable.run();
    }

    public int getType() {
        return type;
    }

    @IntDef(value = {NORMAL, SCALE, OTHER})
    public @interface Range {
    }
}
