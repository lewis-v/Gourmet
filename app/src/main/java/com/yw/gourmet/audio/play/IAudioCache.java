package com.yw.gourmet.audio.play;

/**
 * auth: lewis-v
 * time: 2018/3/11.
 */

public interface IAudioCache {
    String handlePlayPath(String audioPath);
    void setAudioPlayData(AudioPlayData audioPlayData);
    AudioPlayData getAudioPlayData();
}
