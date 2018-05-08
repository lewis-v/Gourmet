package com.yw.gourmet.voiceChat;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.yw.gourmet.voiceChat.VoiceChatData.AUDIO_ENCODING;
import static com.yw.gourmet.voiceChat.VoiceChatData.CHANNEL_OUT_CONFIG;
import static com.yw.gourmet.voiceChat.VoiceChatData.CODE;
import static com.yw.gourmet.voiceChat.VoiceChatData.FREQUENCE;

/**
 * auth: lewis-v
 * time: 2018/5/4.
 */

public class AudioPlay {
    private AudioTrack audioTrack;
    private DataInputStream dis;
    private VoiceListener voiceListener;
    private boolean isStart = false;
    private int bufferSize;
    private ExecutorService executorService;
    private InputStream inputStream;

    public AudioPlay() {
        bufferSize = AudioTrack.getMinBufferSize(FREQUENCE, CHANNEL_OUT_CONFIG, AUDIO_ENCODING);
        audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, FREQUENCE
                , CHANNEL_OUT_CONFIG, AUDIO_ENCODING, bufferSize, AudioTrack.MODE_STREAM);
        executorService = Executors.newCachedThreadPool();
    }

    public AudioPlay setVoiceListener(VoiceListener voiceListener) {
        this.voiceListener = voiceListener;
        return this;
    }

    public void setDis(InputStream inputStream) {
        dis = new DataInputStream(inputStream);
        this.inputStream = inputStream;
    }

    public void play() {
        if (dis != null && !isStart) {
            executorService.execute(recordRunnable);
        }
    }

    public void stop() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        if (audioTrack != null) {
            if (audioTrack.getState() == AudioRecord.STATE_INITIALIZED) {
                try {
                    audioTrack.stop();
                }catch (Exception e){}
            }
            if (audioTrack != null) {
                try {
                    audioTrack.release();
                }catch (Exception e){}
            }
        }
        executorService = null;
    }

    /**
     * 播放线程
     */
    Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                byte[] tempBuffer = new byte[bufferSize];
                int readCount = 0;
                isStart = true;
                while (true) {
                    if (dis.available() > 0) {
                        readCount = dis.read(tempBuffer);
                        if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                            Log.e("接受出问题", "");
                            continue;
                        }
                        if (readCount != 0 && readCount != -1) {
//                            if (readCount < 400) {
//                                String str = new String(tempBuffer, 0, readCount, CODE);
//                                try {
//                                    int cmd = Integer.parseInt(str);
//                                    if (cmd == CMD.STOP) {
//                                        executorService.execute(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                VoiceChatManager.getInstance().destroy();
//                                            }
//                                        });
//                                        return;
//                                    }
//                                } catch (Exception e) {
//                                }
//                            }
                            audioTrack.play();
                            audioTrack.write(tempBuffer, 0, readCount);
                            audioTrack.flush();
                        }
                    }
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                if (voiceListener != null) {
                    voiceListener.onStoped(-1, "语音播放出错");
                }
                e.printStackTrace();
            }
        }

    };
}
