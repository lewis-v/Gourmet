package com.yw.gourmet.voiceChat;

import android.media.AudioFormat;
import android.net.Uri;

import com.yw.gourmet.R;

/**
 * auth: lewis-v
 * time: 2018/5/3.
 */

public class VoiceChatData {
    public static final String IP = "192.168.0.100";//"39.108.236.30";
    public static final int PORT = 47999;
    public static final String CODE = "utf-8";
    public static final String ACTION = "VOICE_CHAT_END";
    public static final Uri receiveUri=Uri.parse("android.resource://com.yw.gourmet/"+ R.raw.voice_chat_music);//语聊铃声
    public static final Uri duduUri=Uri.parse("android.resource://com.yw.gourmet/"+ R.raw.voice_chat_dudu);//等待对方接听声音



    public static final int FREQUENCE = 8000;
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    public static final int CHANNEL_OUT_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    public static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

}
