package com.yw.gourmet.audio.play;

import android.content.Context;

/**
 * auth: lewis-v
 * time: 2018/2/27.
 */

public interface IAudioPlay {
    void init(Context context);
    void play(String audioPath,AudioPlayMode mode);
    void pause();
    void continuePlay();
    void stop();
    void putERR(Exception e,String msg);
}
