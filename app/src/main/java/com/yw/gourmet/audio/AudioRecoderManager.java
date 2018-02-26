package com.yw.gourmet.audio;

/**
 * auth: lewis-v
 * time: 2018/2/25.
 */

public class AudioRecoderManager {
    private IAudioRecoder iAudioRecoder;
    private IAudioRecoderSetting iAudioRecoderSetting;

    private static final class Instance{
        private static final AudioRecoderManager instance = new AudioRecoderManager();
    }

    private AudioRecoderManager() {
        AudioRecoderImp audioRecoderImp = new AudioRecoderImp();
        iAudioRecoder = audioRecoderImp;
        iAudioRecoderSetting = audioRecoderImp;
    }

    public static AudioRecoderManager getInstance(){
        return Instance.instance;
    }

    public AudioRecoderManager setSaveFloder(String saveFloder) {
        iAudioRecoderSetting.setSaveFloder(saveFloder);
        return this;
    }

    public AudioRecoderManager setSAMPLEING_RATE(int SAMPLEING_RATE){
        iAudioRecoderSetting.setSAMPLEING_RATE(SAMPLEING_RATE);
        return this;
    }

    public AudioRecoderManager setMAX_LENGTH(int MAX_LENGTH){
        iAudioRecoderSetting.setMAX_LENGTH(MAX_LENGTH);
        return this;
    }

    public AudioRecoderManager setMIN_LENGTH(int MIN_LENGTH){
        iAudioRecoderSetting.setMIN_LENGTH(MIN_LENGTH);
        return this;
    }

    public AudioRecoderManager setAudioRecoderListener(AudioRecoderListener audioRecoderListener){
        iAudioRecoderSetting.setAudioRecoderListener(audioRecoderListener);
        return this;
    }

    public AudioRecoderManager start(){
        iAudioRecoder.start();
        return this;
    }

    public AudioRecoderManager stop(){
        iAudioRecoder.stop();
        return this;
    }

    public AudioRecoderManager destroy(){
        iAudioRecoder.destroy();
        return this;
    }

    public AudioRecoderManager cancel(){
        iAudioRecoder.cancel();
        return this;
    }
}
