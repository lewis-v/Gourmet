package com.yw.gourmet.audio.play;

/**
 * auth: lewis-v
 * time: 2018/2/27.
 */

public interface IAudioPlay {
    void play(String audioPath);
    void pause();
    void continuePlay();
    void stop();
}
