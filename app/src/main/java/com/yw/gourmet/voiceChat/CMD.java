package com.yw.gourmet.voiceChat;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static com.yw.gourmet.voiceChat.VoiceChatData.CODE;

public class CMD<T> {
    public static final String TAG = "CMD";

    public static final int ERR = -1;//错误
    public static final int INITIATOR = 0;//发起语音命令
    public static final int YES = 1;//接收语音
    public static final int NO = 2;//拒绝接收语音
    public static final int STOP = 3;//断开语音
    public static final int PORT = 4;//UDP端口号


    public int CMD_CODE = -1;
    public T[] data;

    public CMD(int CMD_CODE, T[] data) {
        this.CMD_CODE = CMD_CODE;
        this.data = data;
    }

    /**
     * 处理接收的数据,转换为字符数组
     *
     * @param data
     * @return
     */
    public static CMD handleCMD(byte[] data) {
        if (data != null) {
            try {
                String str = new String(data, CODE);
                String[] sp = str.split(":");
                String[] result = null;
                if (sp.length > 0) {
                    result = new String[sp.length - 1];
                    System.arraycopy(sp, 1, result, 0, result.length);
                }
                Log.i(TAG, str);
                return new CMD(Integer.parseInt(sp[0]), result);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return new CMD(CMD.ERR, null);
    }

    @Override
    public String toString() {
        return "CMD{" +
                "CMD_CODE=" + CMD_CODE +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}