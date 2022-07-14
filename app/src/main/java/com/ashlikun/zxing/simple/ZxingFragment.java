package com.ashlikun.zxing.simple;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ashlikun.zxing.Result;
import com.ashlikun.zxing.view.style1.ZxingStyle1View;

import org.jetbrains.annotations.NotNull;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/3 14:51
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public class ZxingFragment extends Fragment {

    private ZxingStyle1View nBZxingView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        nBZxingView = new ZxingStyle1View(container.getContext()) {
            @Override
            public void resultBack(@NotNull Result content) {
                Toast.makeText(getContext(), content.getText(), Toast.LENGTH_LONG).show();
                nBZxingView.onCameraResume();
            }
        };
        return nBZxingView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nBZxingView.synchLifeStart(this);
    }
}
