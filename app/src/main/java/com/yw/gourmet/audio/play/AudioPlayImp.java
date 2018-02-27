package com.yw.gourmet.audio.play;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;

/**
 * auth: lewis-v
 * time: 2018/2/27.
 */

public class AudioPlayImp implements IAudioPlay,IAudioInfo{
    public final static String TAG = "AudioPlayImp";
    private AudioPlayListener audioPlayListener;
    private MediaPlayer mPlayer;
    private AudioPlayStatus status = AudioPlayStatus.FREE;
    private String audioPath;//播放地址

    public AudioPlayImp() {

    }

    public void play(String audioFile) {
        if (status == AudioPlayStatus.PLAYING){
            if (audioPlayListener != null){
                audioPlayListener.onFail(new RuntimeException("is Playing"),"播放中");
            }
            return;
        }
        if (status == AudioPlayStatus.PAUSE){
            if (audioPlayListener != null){
                audioPlayListener.onFail(new RuntimeException("is Pause"),"播放已暂停");
            }
        }
        if (status == AudioPlayStatus.STOPING){
            if (audioPlayListener != null){
                audioPlayListener.onFail(new RuntimeException("is Stoping"),"播放停止中");
            }
        }
        status = AudioPlayStatus.PLAYING;
        try {
            Log.e(TAG,audioFile);
            this.audioPath = audioFile;
            if (mPlayer != null){
                stop();
            }
            mPlayer = getmPlayer();
            File file = new File(audioFile);
            FileInputStream fis = new FileInputStream(file);
            mPlayer.setDataSource(fis.getFD());
            mPlayer.prepare();
            mPlayer.start();
            if (audioPlayListener != null){
                audioPlayListener.onPlay(audioFile);
            }
        }catch (Exception e){
            e.printStackTrace();
            status = AudioPlayStatus.FREE;
            mPlayer = null;
            if (audioPlayListener != null){
                audioPlayListener.onFail(e,"播放失败");
            }
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void continuePlay() {

    }

    @Override
    public void stop() {
        if (status == AudioPlayStatus.FREE){
            if (audioPlayListener != null){
                audioPlayListener.onFail(new RuntimeException("is Free"),"未在播放中");
            }
            return;
        }
        if (status == AudioPlayStatus.PAUSE){
            if (audioPlayListener != null){
                audioPlayListener.onFail(new RuntimeException("is Pause"),"播放已暂停");
            }
            return;
        }
        if (status == AudioPlayStatus.STOPING){
            if (audioPlayListener != null){
                audioPlayListener.onFail(new RuntimeException("is Stoping"),"播放停止中");
            }
            return;
        }
        status = AudioPlayStatus.STOPING;
        try {
            mPlayer.stop();
        }catch (Exception e){}
        mPlayer.reset();
        mPlayer.release();
        status = AudioPlayStatus.FREE;
        mPlayer = null;
        if (audioPlayListener != null){
            audioPlayListener.onStop();
        }
    }

    /**
     * 获取播放对象
     * @return
     */
    public MediaPlayer getmPlayer(){
        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (audioPlayListener != null){
                    audioPlayListener.onFail(new RuntimeException("ERR"),"播放出错");
                }
                return false;
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {//播放结束
                stop();
            }
        });
        return mPlayer;
    }

    public void setAudioPlayListener(AudioPlayListener audioPlayListener) {
        this.audioPlayListener = audioPlayListener;
    }

    public AudioPlayStatus getStatus() {
        return status;
    }

    @Override
    public String getAudioPath() {
        return audioPath;
    }
}
