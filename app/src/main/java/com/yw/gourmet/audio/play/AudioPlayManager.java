package com.yw.gourmet.audio.play;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;

import com.yw.gourmet.utils.ToastUtils;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

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
   public AudioPlayManager play(String audioPath ,Context context, AudioPlayMode mode){
       if (isPermission(context)) {
           if (((IAudioInfo) iAudioPlay).getStatus() == AudioPlayStatus.PLAYING && ((IAudioInfo) iAudioPlay).getAudioPath().equals(audioPath)) {
               stop();
           } else {
               Message message = new Message();
               message.what = PLAY;
               message.obj = audioPath;
               message.arg1 = mode.getTypeName();
               playThread.handler.sendMessage(message);
           }
       }else {
           ToastUtils.showSingleToast("无读取权限");
           iAudioPlay.putERR(new RuntimeException("no permission"),"无读取权限");
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
                                play(msg.obj.toString(),AudioPlayMode.getByTypeName(msg.arg1));
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
        private void play(String audioPath,AudioPlayMode mode){
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
            iAudioPlay.play(audioPath,mode);
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

    /**
     * 检测是否有权限
     * @param context
     * @return
     */
    public boolean isPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PERMISSION_DENIED ) {
            return false;
        }
        return true;
    }
}
