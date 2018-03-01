package com.yw.gourmet.audio.play;

import android.content.Context;

/**
 * auth: lewis-v
 * time: 2018/2/27.
 */

public interface IAudioInfo {
    void setAudioPlayListener(AudioPlayListener audioPlayListener);
    AudioPlayStatus getStatus();
    String getAudioPath();
}
