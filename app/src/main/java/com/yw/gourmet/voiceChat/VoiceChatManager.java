package com.yw.gourmet.voiceChat;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * auth: lewis-v
 * time: 2018/5/4.
 */

public class VoiceChatManager {
    private VoiceTcpManager voiceTcpManager;
    private AudioRecode audioRecode;
    private AudioPlay audioPlay;
    private VoiceListener mVoiceListener;
    private VoiceListener voiceListener;
    private static final String TAG = "VoiceChatManager";

    private static final class Holder {
        static final VoiceChatManager instance = new VoiceChatManager();
    }

    private VoiceChatManager() {
        voiceTcpManager = new VoiceTcpManager();
        mVoiceListener = new VoiceListener() {
            @Override
            public void onConnect(Socket socket) {
                try {
                    audioRecode.setDos(socket.getOutputStream());
                    audioRecode.startRecode();
                    audioPlay.setDis(socket.getInputStream());
                    audioPlay.play();
                    if (voiceListener != null) {
                        voiceListener.onConnect(socket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onGet(byte[] data) {

            }

            @Override
            public void onStoped(long length, String errMsg) {
                Log.i("cancel",errMsg);
                if (voiceListener != null) {
                    voiceListener.onStoped(length == -1 ? voiceTcpManager.getTime() : length, errMsg);
                }
                destroy();
            }

            @Override
            public void onCancel(long length) {
                if (voiceListener != null) {
                    voiceListener.onCancel(length == -1 ? voiceTcpManager.getTime() : length);
                }
                destroy();
            }

            @Override
            public void Err(String msg) {
                if (voiceListener != null) {
                    voiceListener.Err(msg);
                }
                destroy();
            }

            @Override
            public void onGetCmd(CMD cmd) {
                if (voiceListener != null) {
                    voiceListener.onGetCmd(cmd);
                }
            }
        };
        voiceTcpManager.addVoiceListener(this.mVoiceListener);
    }

    public void destroy() {
        if (audioRecode != null) {
            audioRecode.stopRecode();
        }
        if (audioPlay != null) {
            audioPlay.stop();
        }
        audioRecode = null;
        audioPlay = null;
        voiceTcpManager.stop();
        if (voiceListener != null) {
            voiceTcpManager.removeListener(voiceListener);
            voiceListener = null;
        }
    }

    public void stop(String msg){
        if (voiceListener != null) {
            voiceListener.onStoped(0,msg);
        }
        destroy();
    }

    public static VoiceChatManager getInstance() {
        return Holder.instance;
    }

    /**
     * 申请语音聊天
     *
     * @param voiceListener
     * @param applyId
     * @param receiverId
     */
    public void applyVoiceChat(VoiceListener voiceListener, String applyId
            , String receiverId) {
        init();
        this.voiceListener = voiceListener;

        voiceTcpManager.apply(applyId, receiverId);

    }


    /**
     * 接受准备
     */
    public void prepare(VoiceListener voiceListener, final int port) {
        this.voiceListener = voiceListener;
        voiceTcpManager.prepare(port);
    }

    /**
     * 接受语聊
     *
     * @param voiceListener
     * @param port
     */
    public void accept(VoiceListener voiceListener, int port) {
        init();
        this.voiceListener = voiceListener;
        voiceTcpManager.accept(port);
    }

    /**
     * 拒绝语聊
     *
     * @param voiceListener
     * @param port
     */
    public void reject(VoiceListener voiceListener, int port) {
        this.voiceListener = voiceListener;
        voiceTcpManager.reject(port);
    }

    private void init() {
        if (audioRecode == null) {
            audioRecode = new AudioRecode();
        }
        audioRecode.setVoiceListener(mVoiceListener);
        if (audioPlay == null) {
            audioPlay = new AudioPlay();
        }
        audioPlay.setVoiceListener(mVoiceListener);
    }

    /**
     * 是否正在语聊中
     *
     * @return
     */
    public boolean isVoiceChatting() {
        return voiceTcpManager.isRun();
    }

    /**
     * 获取已进行的时间
     *
     * @return
     */
    public long getTime() {
        return voiceTcpManager.getTime();
    }
}
