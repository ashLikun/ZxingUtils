package com.ashlikun.zxing.simple

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ashlikun.zxing.simple.databinding.ActivitySelectBinding

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/3 21:00
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */

class MainActiviy : AppCompatActivity() {
    val binding by lazy {
        ActivitySelectBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun toClick(view: View) {
        if (view == binding.style1)
            ScanActivity.startSelf(this)
        if (view == binding.style2)
            ScanActivity2.startSelf(this)
    }

}