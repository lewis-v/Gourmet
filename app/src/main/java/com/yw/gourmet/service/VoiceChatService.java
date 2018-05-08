package com.yw.gourmet.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.yw.gourmet.ui.chat.voiceChat.VoiceChatActivity;
import com.yw.gourmet.voiceChat.CMD;
import com.yw.gourmet.voiceChat.VoiceChatData;
import com.yw.gourmet.voiceChat.VoiceChatManager;
import com.yw.gourmet.voiceChat.VoiceListener;

import java.io.IOException;
import java.net.Socket;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class VoiceChatService extends Service {
    private static final String TAG = "VoiceChatService";
    private ChatListener chatListener;
    private String apply_id, recevice_id;//发起和接听者id
    private String name, img;//对方的名字与头像
    private int port;
    private VoiceListener voiceListener;
    private boolean isConnect = false;
    private MediaPlayer mediaPlayer;

    public VoiceChatService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        voiceListener = new VoiceListener() {
            @Override
            public void onConnect(Socket socket) {
                Log.i(TAG,"链接成功");
                stopMusic();
                isConnect = true;
                if (chatListener != null) {
                    chatListener.onConnect();
                }
            }

            @Override
            public void onGet(byte[] data) {

            }

            @Override
            public void onStoped(long length, String errMsg) {
                isConnect = false;
                stopMusic();
                if (chatListener != null) {
                    chatListener.onCancel(errMsg);
                    stopSelf();
                }
            }

            @Override
            public void onCancel(long length) {
                isConnect = false;
                stopMusic();
                if (chatListener != null) {
                    chatListener.onCancel("语聊结束");
                    stopSelf();
                }
            }

            @Override
            public void Err(String msg) {
                isConnect = false;
                stopMusic();
                if (chatListener != null) {
                    chatListener.onCancel(msg);
                    stopSelf();
                }
            }

            @Override
            public void onGetCmd(CMD cmd) {
                Log.i("get", cmd.toString());
                if (cmd.CMD_CODE == CMD.STOP) {
                    stopMusic();
                    if (chatListener != null) {
                        chatListener.onCancel("对方已挂断");
                        stopSelf();
                    }
                }
            }
        };
    }

    /**
     * 停止音乐的播放
     */
    public void stopMusic(){
        if (mediaPlayer != null){
            try {
                mediaPlayer.stop();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            apply_id = intent.getStringExtra("apply_id");
            recevice_id = intent.getStringExtra("recevice_id");
            name = intent.getStringExtra("name");
            img = intent.getStringExtra("img");
            port = intent.getIntExtra("port", -1);
            Intent chatIntent = new Intent(this, VoiceChatActivity.class);
            if (port == -1) {//为-1说明是自己发起语聊的
                try {
                    mediaPlayer.setDataSource(this,VoiceChatData.duduUri);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                VoiceChatManager.getInstance().applyVoiceChat(voiceListener, apply_id, recevice_id);
                chatIntent.putExtra("apply", true);
            } else {
                try {
                    mediaPlayer.setDataSource(this,VoiceChatData.receiveUri);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                VoiceChatManager.getInstance().prepare(voiceListener, port);
                chatIntent.putExtra("apply", false);
            }
            try {
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            chatIntent.putExtra("name", name);
            chatIntent.putExtra("img", img);
            chatIntent.putExtra("apply_id", apply_id);
            chatIntent.putExtra("recevice_id", recevice_id);

            chatIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(chatIntent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        chatListener = null;
        return super.onUnbind(intent);
    }

    public class MyBind extends Binder {
        public void apply() {
            VoiceChatManager.getInstance().accept(voiceListener, port);
        }

        public void reject() {
            VoiceChatManager.getInstance().reject(voiceListener, port);
        }

        public void stop(String msg) {
            VoiceChatManager.getInstance().stop(msg);
        }

        public boolean setListener(ChatListener cancel) {
            chatListener = cancel;
            return isConnect;
        }

        public long getTime() {
            return VoiceChatManager.getInstance().getTime();
        }
    }

    public interface ChatListener {
        void onCancel(String msg);

        void onConnect();
    }
}
