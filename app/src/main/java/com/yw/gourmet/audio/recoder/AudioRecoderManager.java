package com.yw.gourmet.audio.recoder;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.yw.gourmet.utils.ToastUtils;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

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

    public AudioRecoderManager start(Context context){
        if (isPermission(context)) {
            iAudioRecoder.start();
        }else {
            ToastUtils.showSingleToast("无录音/读写权限");
            iAudioRecoder.putERR(new RuntimeException("no permission"),"无录音/读写权限");
        }
        return this;
    }

    public AudioRecoderManager stop(Context context){
        if (isPermission(context)) {
            iAudioRecoder.stop();
        }else {
            ToastUtils.showSingleToast("无录音/读写权限");
            iAudioRecoder.putERR(new RuntimeException("no permission"),"无录音/读写权限");
        }
        return this;
    }

    public AudioRecoderManager destroy(Context context){
        if (isPermission(context)) {
            iAudioRecoder.destroy();
        }else {
            ToastUtils.showSingleToast("无录音/读写权限");
            iAudioRecoder.putERR(new RuntimeException("no permission"),"无录音/读写权限");
        }
        return this;
    }

    public AudioRecoderManager cancel(Context context){
        if (isPermission(context)) {
            iAudioRecoder.cancel();
        }else {
            ToastUtils.showSingleToast("无录音/读写权限");
            iAudioRecoder.putERR(new RuntimeException("no permission"),"无录音/读写权限");
        }
        return this;
    }

    /**
     * 检测是否有权限
     * @param context
     * @return
     */
    public boolean isPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                        == PERMISSION_DENIED) {
            return false;
        }
        return true;
    }
}
