package com.ashlikun.zxing;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/9/28　17:14
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：识别成功后的音效
 */
public class DecodeAudio {
    /**
     * 是否播放音效
     */
    private boolean playBeep = true;
    /**
     * 音效对象
     */
    private SoundPool beep;
    Context context;

    public DecodeAudio(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        if (playBeep && beep == null) {
            beep = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
            //加载音频文件
            beep.load(context, R.raw.beep, 1);
        }
    }

    public void playBeep() {
        if (playBeep) {
            init();
            if (beep != null) {
                beep.play(1, 1, 1, 0, 0, 1);
            }
        }
    }

    /**
     * 设置是否开启音效
     * 只有响铃模式才开启
     *
     * @param playBeep
     */
    public void setPlayBeep(boolean playBeep) {
        AudioManager audioService = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            //不是响铃模式不开
            this.playBeep = false;
        } else {
            this.playBeep = playBeep;
        }
    }

    public void release() {
        if (beep != null) {
            beep.release();
            beep = null;
        }
    }
}
