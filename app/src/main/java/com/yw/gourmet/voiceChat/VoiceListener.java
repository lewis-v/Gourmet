package com.yw.gourmet.voiceChat;

import java.net.Socket;

/**
 * auth: lewis-v
 * time: 2018/5/4.
 */

public interface VoiceListener {
    /**
     * 链接成功
     */
    void onConnect(Socket socket);

    /**
     * 接收到语音数据
     *
     * @param data
     */
    @Deprecated
    void onGet(byte[] data);

    /**
     * 聊天被动停止
     *
     * @param length
     */
    void onStoped(long length, String errMsg);

    /**
     * 聊天主动停止
     *
     * @param length
     */
    void onCancel(long length);

    /**
     * 出现错
     *
     * @param msg
     */
    void Err(String msg);

    /**
     * 获取到命令
     *
     * @param cmd
     */
    void onGetCmd(CMD cmd);
}
