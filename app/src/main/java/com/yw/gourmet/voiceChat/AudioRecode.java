package com.yw.gourmet.voiceChat;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.yw.gourmet.voiceChat.VoiceChatData.AUDIO_ENCODING;
import static com.yw.gourmet.voiceChat.VoiceChatData.CHANNEL_CONFIG;
import static com.yw.gourmet.voiceChat.VoiceChatData.FREQUENCE;

/**
 * auth: lewis-v
 * time: 2018/4/27.
 */

public class AudioRecode {
    private int bufferSize;
    private AudioRecord record;
    private DataOutputStream dos;
    private ExecutorService executorService;
    private VoiceListener voiceListener;

    public AudioRecode() {
        executorService = Executors.newCachedThreadPool();
        bufferSize = AudioRecord.getMinBufferSize(FREQUENCE,
                CHANNEL_CONFIG, AUDIO_ENCODING);
        // 实例化AudioRecord
        record = new AudioRecord(
                MediaRecorder.AudioSource.MIC, FREQUENCE,
                CHANNEL_CONFIG, AUDIO_ENCODING, bufferSize);
    }

    public AudioRecode setVoiceListener(VoiceListener voiceListener) {
        this.voiceListener = voiceListener;
        return this;
    }


    public void setDos(OutputStream outputStream) {
        dos = new DataOutputStream(outputStream);
    }

    public void startRecode() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 定义缓冲
                    byte[] buffer = new byte[bufferSize];
                    // 开始录制
                    record.startRecording();
                    // 定义循环，根据isRecording的值来判断是否继续录制
                    while (true) {
                        Thread.sleep(1);
                        // 从bufferSize中读取字节，返回读取的short个数
                        int bufferReadResult = record
                                .read(buffer, 0, buffer.length);
                        // 循环将buffer中的音频数据写入到OutputStream中
                        byte[] data = new byte[bufferReadResult];
                        System.arraycopy(buffer, 0, data, 0, bufferReadResult);
                        dos.write(data);
                        dos.flush();

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    if (voiceListener != null) {
                        voiceListener.onStoped(-1, "对方已挂断");
                    }
                }
            }
        });
    }

    public void stopRecode() {
        try {
            if (executorService != null) {
                executorService.shutdownNow();
                executorService = null;
            }
            if (record != null) {
                // 录制结束
                record.stop();
                record = null;
            }
            if (dos != null) {
                dos.close();
                dos = null;
            }
            voiceListener = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
