package com.yw.gourmet.audio.recoder;

/**
 * auth: lewis-v
 * time: 2018/2/26.
 */

public interface IAudioRecoderSetting {
    void setSaveFloder(String saveFloder);
    void setSAMPLEING_RATE(int SAMPLEING_RATE);
    void setMAX_LENGTH(int MAX_LENGTH);
    void setAudioRecoderListener(AudioRecoderListener audioRecoderListener);
    void setMIN_LENGTH(int MIN_LENGTH);
}
