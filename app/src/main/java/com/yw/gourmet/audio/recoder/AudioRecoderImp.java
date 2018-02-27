package com.yw.gourmet.audio.recoder;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED;

/**
 * auth: lewis-v
 * time: 2018/2/25.
 */

public class AudioRecoderImp implements IAudioRecoder ,IAudioRecoderSetting{
    public static final String TAG = "AudioRecoderImp";
    private AudioRecoderData audioRecoderData;
    private MediaRecorder mMediaRecorder;
    private AudioRecoderListener audioRecoderListener;
    private Handler handler;
    private AudioRecoderThread audioRecoderThread;
    private final Object lock = new Object();//用来同步停止线程的锁
    private AudioRecoderStatus status = AudioRecoderStatus.FREE;//录音状态

    public AudioRecoderImp() {
        handler = new Handler(Looper.getMainLooper());
        audioRecoderData = new AudioRecoderData();
    }

    public void start(){
        if (status == AudioRecoderStatus.RECODERING){//录音中
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(new RuntimeException("AudioRecoder is Running"),"正在录音中");
            }
            return;
        }
        status = AudioRecoderStatus.RECODERING;
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    if (audioRecoderListener != null){
                        audioRecoderListener.onFail(new RuntimeException("fail"),"录音出错");
                    }
                }
            });
            mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if (what == MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {//达到最大的时间
                        stop();
                    }
                }
            });
        }
        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            audioRecoderData.setFilePath( audioRecoderData.getFolderPath() + "AUDIO_"+System.currentTimeMillis() + ".amr" );
            Log.e(TAG,audioRecoderData.getFilePath());
            File file = new File(audioRecoderData.getFolderPath());
            if (!file.exists()){
                file.mkdirs();
            }
            file = new File(audioRecoderData.getFilePath());
            if (!file.exists()){
                file.createNewFile();
            }
            /* ③准备 */
            mMediaRecorder.setOutputFile(audioRecoderData.getFilePath());
            mMediaRecorder.setMaxDuration(audioRecoderData.getMAX_LENGTH());
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
            // AudioRecord audioRecord.
            /* 获取开始时间* */
            audioRecoderData.setStartTime(System.currentTimeMillis());
            if (audioRecoderListener != null){
                audioRecoderListener.onStart();
            }
            synchronized (lock) {
                audioRecoderThread = new AudioRecoderThread();
                handler.postDelayed(audioRecoderThread, audioRecoderData.getSAMPLEING_RATE());
            }
            Log.e("fan", "startTime" + audioRecoderData.getStartTime());
        } catch (IllegalStateException e) {
            status = AudioRecoderStatus.FREE;
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(e,"录音出错");
            }
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        } catch (IOException e) {
            status = AudioRecoderStatus.FREE;
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(e,"录音出错");
            }
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

    /**
     * 停止录音
     */
    public void stop() {
        if (mMediaRecorder == null) {
            return;
        }if (status == AudioRecoderStatus.FREE ){//空闲中
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(new RuntimeException("AudioRecoder is FREE"),"录音空闲中,无需停止");
            }
            return;
        }else if (status == AudioRecoderStatus.STOPING){//停止中
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(new RuntimeException("AudioRecoder is STOPING"),"录音停止中");
            }
            return;
        }
        status = AudioRecoderStatus.STOPING;
        synchronized (lock) {
            handler.removeCallbacks(audioRecoderThread);
        }
        audioRecoderData.setEndTime(System.currentTimeMillis());
        //有一些网友反应在5.0以上在调用stop的时候会报错，翻阅了一下谷歌文档发现上面确实写的有可能会报错的情况，捕获异常清理一下就行了，感谢大家反馈！
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }catch (RuntimeException e){
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            File file = new File(audioRecoderData.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }
        try{
            if (audioRecoderData.getEndTime() - audioRecoderData.getStartTime() < audioRecoderData.getMIN_LENGTH()){
                if (audioRecoderListener != null){
                    audioRecoderListener.onFail(new RuntimeException("AudioRecoder too short"),"说话时间太短啦");
                }
            }else if (new File(audioRecoderData.getFilePath()).exists()){
                if (audioRecoderListener != null) {
                    audioRecoderListener.onStop((AudioRecoderData) audioRecoderData.clone());
                }
                Log.e(TAG, audioRecoderData.getFilePath());
            }

        }catch (CloneNotSupportedException e) {
            e.printStackTrace();
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(e,"录音出错");
            }
        }
        status = AudioRecoderStatus.FREE;
    }

    @Override
    public void cancel() {
        if (mMediaRecorder == null) {
            return;
        }
        if (status == AudioRecoderStatus.FREE ){//空闲中
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(new RuntimeException("AudioRecoder is FREE"),"录音空闲中,无需停止");
            }
            return;
        }else if (status == AudioRecoderStatus.STOPING){//停止中
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(new RuntimeException("AudioRecoder is STOPING"),"录音停止中");
            }
            return;
        }
        status = AudioRecoderStatus.STOPING;
        synchronized (lock) {
            handler.removeCallbacks(audioRecoderThread);
        }
        audioRecoderData.setEndTime(System.currentTimeMillis());
        //有一些网友反应在5.0以上在调用stop的时候会报错，翻阅了一下谷歌文档发现上面确实写的有可能会报错的情况，捕获异常清理一下就行了，感谢大家反馈！
        try{
            mMediaRecorder.stop();
        }catch (Exception e){

        }
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;

        File file = new File(audioRecoderData.getFilePath());
        if (file.exists()) {
            file.delete();
        }
        if (audioRecoderListener != null){
            audioRecoderListener.onCancel();
        }
        audioRecoderData.setFilePath("");
        status = AudioRecoderStatus.FREE;
    }

    @Override
    public void destroy() {
        if (status == AudioRecoderStatus.RECODERING){
            stop();
            audioRecoderListener = null;
        }
    }


    //获取录音分贝
    private class AudioRecoderThread implements Runnable{

        @Override
        public void run() {
            if (mMediaRecorder != null){
                int ratio = mMediaRecorder.getMaxAmplitude()/600;
                if (ratio > 1) {
                    int DB = (int)(20 * Math.log10(ratio));//分贝
                    if (audioRecoderListener != null){
                        audioRecoderListener.onSoundSize(DB);
                    }
                }
                synchronized (lock) {
                    audioRecoderThread = new AudioRecoderThread();
                    handler.postDelayed(audioRecoderThread, audioRecoderData.getSAMPLEING_RATE());
                }
            }
        }
    }

    public void setAudioRecoderListener(AudioRecoderListener audioRecoderListener) {
        this.audioRecoderListener = audioRecoderListener;
    }

    @Override
    public void setMIN_LENGTH(int MIN_LENGTH) {
        audioRecoderData.setMIN_LENGTH(MIN_LENGTH);
    }

    @Override
    public void setSaveFloder(String saveFloder) {
        audioRecoderData.setFolderPath(saveFloder);
    }

    @Override
    public void setSAMPLEING_RATE(int SAMPLEING_RATE) {
        audioRecoderData.setSAMPLEING_RATE(SAMPLEING_RATE);
    }

    @Override
    public void setMAX_LENGTH(int MAX_LENGTH) {
        audioRecoderData.setMAX_LENGTH(MAX_LENGTH);
    }
}
