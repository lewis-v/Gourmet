package com.yw.gourmet.audio.play;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * auth: lewis-v
 * time: 2018/2/27.
 */

public class AudioPlayManager {
    public static final String TAG = "AudioPlayManager";
    private static final int PLAY = 0;//播放
    private static final int PAUSE = 1;//暂停
    private static final int CONTINUE = 2;//继续播放
    private static final int STOP = 3;//停止
    private static final int QUIT = -1;//退出消息队列

    private IAudioPlay iAudioPlay;
    private PlayThread playThread;


    private static final class Instance{
        private static final AudioPlayManager instance = new AudioPlayManager();
    }

    public static AudioPlayManager getInstance(){
        return Instance.instance;
    }

    private AudioPlayManager() {
        iAudioPlay = new AudioPlayCacheImp();
        playThread = new PlayThread();
        playThread.start();
    }

    public AudioPlayManager setPlayListener(AudioPlayListener audioPlayListener){
        ((IAudioInfo)iAudioPlay).setAudioPlayListener(audioPlayListener);
        return this;
    }

    /**
     * 播放音频
     * @param audioPath
     * @return
     */
   public AudioPlayManager play(String audioPath){
        if (((IAudioInfo)iAudioPlay).getStatus() == AudioPlayStatus.PLAYING && ((IAudioInfo)iAudioPlay).getAudioPath().equals(audioPath)){
            stop();
        }else {
            Message message = new Message();
            message.what = PLAY;
            message.obj = audioPath;
            playThread.handler.sendMessage(message);
        }
        return this;
   }

    /**
     * 停止播放
     * @return
     */
   public AudioPlayManager stop(){
       playThread.handler.sendEmptyMessage(STOP);
       return this;
   }

    /**
     * 获取当前播放状态
     * @return
     */
   public AudioPlayStatus getPlayStatus(){
       return ((IAudioInfo)iAudioPlay).getStatus();
   }

    private class PlayThread extends Thread{
        Handler handler;
        @Override
        public void run() {
            Looper.prepare();
            handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case PLAY:
                            if (msg.obj != null) {
                                play(msg.obj.toString());
                            }
                            break;
                        case PAUSE:

                            break;
                        case CONTINUE:

                            break;
                        case STOP:
                            stopPLay();
                            break;
                        case QUIT:
                            Looper.myLooper().quit();
                            break;
                    }
                    super.handleMessage(msg);
                }
            };
            Looper.loop();
        }
        private void play(String audioPath){
            while (((IAudioInfo)iAudioPlay).getStatus() == AudioPlayStatus.STOPING){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
            if (((IAudioInfo)iAudioPlay).getStatus() != AudioPlayStatus.FREE ){
                iAudioPlay.stop();
            }
            iAudioPlay.play(audioPath);
        }

        private void stopPLay(){
            iAudioPlay.stop();
        }
    }

    /**
     * 释放资源
     */
    public void destory(){
        if (playThread != null){
            playThread.handler.sendEmptyMessage(QUIT);
            playThread.interrupt();
        }

    }
}
