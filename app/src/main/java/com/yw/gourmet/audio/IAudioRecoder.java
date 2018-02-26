package com.yw.gourmet.audio;

/**
 * auth: lewis-v
 * time: 2018/2/25.
 */

public interface IAudioRecoder {
    void start();
    void stop();
    void cancel();
    void destroy();
}
