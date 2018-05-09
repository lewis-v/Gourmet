package com.yw.gourmet.voiceChat;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.yw.gourmet.voiceChat.CMD.NO;
import static com.yw.gourmet.voiceChat.CMD.STOP;
import static com.yw.gourmet.voiceChat.CMD.YES;
import static com.yw.gourmet.voiceChat.CMD.handleCMD;
import static com.yw.gourmet.voiceChat.VoiceChatData.CODE;
import static com.yw.gourmet.voiceChat.VoiceChatData.IP;

/**
 * auth: lewis-v
 * time: 2018/5/3.
 */

public class VoiceTcpManager {
    private static final String TAG = "VoiceTcpManager";
    public static final int DEFAULT = 0;//默认模式
    public static final int CONNECT = 1;//已连接服务器
    public static final int CONNECT_ERR = -1;//连接失败

    private List<VoiceListener> voiceListeners = new ArrayList<>();
    private Socket socket, voiceSocket;
    private ExecutorService executors;
    private Future future, cmdFuture;
    private long time = 0;
    private boolean isApply = false;//是否接受
    private volatile int mode = DEFAULT;

    public VoiceTcpManager() {
        executors = Executors.newCachedThreadPool();
    }

    public VoiceTcpManager addVoiceListener(VoiceListener voiceListener) {
        if (voiceListener != null) {
            voiceListeners.add(voiceListener);
        }
        return this;
    }

    /**
     * 释放资源
     */
    public void destory() {
        if (voiceListeners != null) {
            voiceListeners.clear();
            voiceListeners = null;
        }
        executors.shutdownNow();
        try {
            if (socket != null) {
                if (!socket.isClosed()) {
                    socket.close();
                }
                socket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止语聊
     */
    public void stop() {
        if (future != null) {
            future.cancel(true);
            future = null;
        }
        if (cmdFuture != null) {
            cmdFuture.cancel(true);
            cmdFuture = null;
        }
        if (socket != null) {
            executors.execute(new Runnable() {
                @Override
                public void run() {
                    String data = String.valueOf(STOP);
                    try {
                        sendData(getOutStream(), data.getBytes(CODE));
                        if (socket != null && !socket.isClosed()) {
                            socket.close();
                            socket = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 申请语音聊天
     *
     * @param applyId    申请者id
     * @param receiverId 接收者id
     */
    public void apply(final String applyId, final String receiverId) {
        if (isRun()) {
            return;
        }
        future = executors.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(IP, VoiceChatData.PORT);
                    socket.getOutputStream().write((CMD.INITIATOR + ":" + applyId + ":" + receiverId).getBytes(CODE));
                    socket.getOutputStream().flush();
                    Log.i("send:", CMD.INITIATOR + ":" + applyId + ":" + receiverId);
                    //等待新端口号返回
                    CMD cmd = handleCMD(getData(socket.getInputStream()));
                    Log.i("get", cmd.toString());
                    if (cmd.CMD_CODE == CMD.PORT) {
                        int port = Integer.parseInt(cmd.data[0].toString());
                        socket.close();
                        socket = new Socket(IP, port);
                    }
                    //等待对方接受语聊
                    cmd = handleCMD(getData(socket.getInputStream()));
                    Log.i("get", cmd.toString());
                    if (cmd.CMD_CODE == YES) {
                        isApply = true;
                        voiceSocket = new Socket(IP, Integer.parseInt(cmd.data[0].toString()));
                        cmdFuture = executors.submit(new CMDRunnable(socket));
                        time = System.currentTimeMillis();
                        if (voiceListeners != null) {
                            for (VoiceListener voiceListener : voiceListeners) {
                                voiceListener.onConnect(voiceSocket);
                            }
                        }
                    } else if (cmd.CMD_CODE == CMD.ERR) {
                        if (voiceListeners != null) {
                            for (VoiceListener voiceListener : voiceListeners) {
                                voiceListener.onGetCmd(cmd);
                            }
                        }
                        Log.i("result:", "拒绝语聊");
                    } else {
                        if (voiceListeners != null) {
                            for (VoiceListener voiceListener : voiceListeners) {
                                voiceListener.onStoped(0, "拒绝语聊");
                            }
                        }
                        Log.i("result:", "拒绝语聊");
                    }
                } catch (IOException e) {
                    if (voiceListeners != null) {
                        for (VoiceListener voiceListener : voiceListeners) {
                            voiceListener.onStoped(0, "链接已断开");
                        }
                    }
                    e.printStackTrace();
                } catch (Exception e) {
                    if (voiceListeners != null) {
                        for (VoiceListener voiceListener : voiceListeners) {
                            voiceListener.Err("语聊出错");
                        }
                    }
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 接受准备
     */
    public void prepare(final int port) {
        executors.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(IP, port);
                    mode = CONNECT;
                } catch (IOException e) {
                    e.printStackTrace();
                    mode = CONNECT_ERR;
                }
                cmdFuture = executors.submit(new CMDRunnable(socket));
            }
        });
    }

    public int getMode() {
        return mode;
    }

    /**
     * 接受语音聊天
     *
     * @param port
     */
    public void accept(final int port) {
        if (isRun()) {
            return;
        }
        future = executors.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("---test", port + "同意");
                    isApply = true;
                    socket.getOutputStream().write((YES + "").getBytes(CODE));
                    socket.getOutputStream().flush();

                } catch (IOException e) {
                    if (voiceListeners != null) {
                        for (VoiceListener voiceListener : voiceListeners) {
                            voiceListener.onStoped(0, "链接已断开");
                        }
                    }
                    e.printStackTrace();
                } catch (Exception e) {
                    if (voiceListeners != null) {
                        for (VoiceListener voiceListener : voiceListeners) {
                            voiceListener.Err("语聊出错");
                        }
                    }
                    e.printStackTrace();
                }
            }
        });

    }


    /**
     * 拒绝语聊
     *
     * @param port
     */
    public void reject(final int port) {
        if (isRun()) {
            return;
        }
        time = System.currentTimeMillis();
        future = executors.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.getOutputStream().write((NO + "").getBytes(CODE));
                    socket.getOutputStream().flush();
                    Log.i("cancel", "拒绝语聊");
                    if (voiceListeners != null) {
                        for (VoiceListener voiceListener : voiceListeners) {
                            voiceListener.onStoped(0, "拒绝语聊");
                        }
                    }
                } catch (IOException e) {
                    if (voiceListeners != null) {
                        for (VoiceListener voiceListener : voiceListeners) {
                            voiceListener.onStoped(0, "链接已断开");
                        }
                    }
                    e.printStackTrace();
                } catch (Exception e) {
                    if (voiceListeners != null) {
                        for (VoiceListener voiceListener : voiceListeners) {
                            voiceListener.Err("语聊出错");
                        }
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 是否已在语聊中
     *
     * @return
     */
    public boolean isRun() {
        if (future != null) {
            if (voiceListeners != null) {
                for (VoiceListener voiceListener : voiceListeners) {
                    voiceListener.Err("正在语音聊天中");
                }
            }
            return true;
        }
        return false;
    }

    public OutputStream getOutStream() {
        if (socket != null) {
            try {
                return socket.getOutputStream();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    public InputStream getInputStream() {
        if (socket != null) {
            try {
                return socket.getInputStream();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    public void sendVoiceData(final byte[] data) {
        if (socket != null) {
            executors.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendData(socket.getOutputStream(), data, data.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 获取聊天时间
     *
     * @return
     */
    public long getTime() {
        return isApply ? System.currentTimeMillis() - time : 0;
    }

    public void removeListener(VoiceListener voiceListener) {
        if (voiceListeners != null) {
            voiceListeners.remove(voiceListener);
        }
    }

    /**
     * 发送数据
     *
     * @param outputStream
     * @param data
     * @param length
     */
    public static void sendData(OutputStream outputStream, byte[] data, int length) throws IOException {
        if (outputStream != null) {
            outputStream.write(data, 0, length);
            outputStream.flush();
        }
    }

    /**
     * 发送数据
     *
     * @param outputStream
     * @param data
     */
    public static void sendData(OutputStream outputStream, byte[] data) throws IOException {
        if (outputStream != null) {
            outputStream.write(data);
            outputStream.flush();
        }
    }

    /**
     * 获取数据,会阻塞
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    static byte[] getData(InputStream inputStream) throws IOException {
        int size;
        while ((size = inputStream.available()) == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        byte[] data = new byte[size];
        Log.i("get", new String(data, CODE));
        inputStream.read(data);
        return data;
    }


    public class CMDRunnable implements Runnable {
        private Socket socket;

        public CMDRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                    int size = 0;
                    if ((size = socket.getInputStream().available()) > 0) {
                        byte[] data = new byte[size];
                        socket.getInputStream().read(data);
                        CMD cmd = handleCMD(data);
                        if (cmd.CMD_CODE == CMD.PORT) {
                            Log.i(TAG, IP + ":" + Integer.parseInt(cmd.data[0].toString()));
                            voiceSocket = new Socket(IP, Integer.parseInt(cmd.data[0].toString()));
                            time = System.currentTimeMillis();
                            Log.i(TAG, "通知");
                            if (voiceListeners != null) {
                                for (VoiceListener voiceListener : voiceListeners) {
                                    voiceListener.onConnect(voiceSocket);
                                }
                            }
                        }
                        if (voiceListeners != null) {
                            for (VoiceListener voiceListener : voiceListeners) {
                                voiceListener.onGetCmd(cmd);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (voiceListeners != null) {
                        for (VoiceListener voiceListener : voiceListeners) {
                            voiceListener.onStoped(0, "断开连接");
                        }
                    }
                    return;
                }
            }
        }
    }


}
