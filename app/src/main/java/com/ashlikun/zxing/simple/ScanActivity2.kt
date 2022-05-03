package com.ashlikun.zxing.simple

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ashlikun.zxing.simple.databinding.ActivityMain2Binding
import com.google.android.cameraview.AspectRatio
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine

class ScanActivity2 : AppCompatActivity() {
    val binding by lazy {
        ActivityMain2Binding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        }
        setContentView(binding.root)
        binding.zxingview2.synchLifeStart(this)
        initView()
//        ZxingFragment fragment = new ZxingFragment();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.parent, fragment)
//                .commit();
    }

    companion object {
        fun startSelf(context: Context) {
            context.startActivity(Intent(context, ScanActivity2::class.java))
        }
    }

    fun initView() {

        findViewById<TextView>(R.id.vTitle).text = "扫一扫"

        findViewById<View>(R.id.vLeftImage)
            .setOnClickListener { v: View? ->
                finish()
            }

        findViewById<TextView>(R.id.vRightTextView).text = "相册"
        findViewById<TextView>(R.id.vRightTextView)
            .setOnClickListener { v: View? ->
                if (!checkPermissionRW()) {
                    requstPermissionRW()
                    return@setOnClickListener
                }
                Matisse.from(this)
                    .choose(MimeType.ofAll())
                    .countable(true)
                    .maxSelectable(9)
                    .gridExpectedSize(300)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(GlideEngine())
                    .showPreview(false) // Default is `true`
                    .forResult(1)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val path = Matisse.obtainPathResult(data)[0]
                findViewById<CusScanView2>(R.id.zxingview2).toParse(path)
            }
        }
    }

    fun checkPermissionRW(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        } else {
            return true
        }
    }

    fun requstPermissionRW() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 200)
        }
    }

    var a = 0

    fun change(view: View) {
        if (a % 2 == 0)
            binding.zxingview2.setAspectRatio(AspectRatio.of(1, 1))
        else binding.zxingview2.setAspectRatio(AspectRatio.of(16, 9))
        a++;
    }


}