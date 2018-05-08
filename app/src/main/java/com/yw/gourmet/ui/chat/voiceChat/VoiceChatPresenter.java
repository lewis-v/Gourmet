package com.yw.gourmet.ui.chat.voiceChat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.yw.gourmet.service.VoiceChatService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * auth: lewis-v
 * time: 2018/5/4.
 */

public class VoiceChatPresenter extends VoiceChatContract.Presenter {
    private ExecutorService executorService;
    private ServiceConnection serviceConnection;
    private VoiceChatService.MyBind bind;

    public VoiceChatPresenter() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                bind = (VoiceChatService.MyBind) service;
                if (bind.setListener(new VoiceChatService.ChatListener() {
                    @Override
                    public void onCancel(String msg) {
                        mView.onCancel(msg);
                    }

                    @Override
                    public void onConnect() {
                        mView.onApplySuccess();
                    }
                })) {//此时双方已经建立好通信了
                    mView.onApplySuccess();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        executorService = Executors.newCachedThreadPool();
        bind();
    }

    public void bind() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (context == null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                context.bindService(new Intent(context, VoiceChatService.class)
                        , serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });
    }

    @Override
    void apply() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (bind == null){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                bind.apply();
            }
        });
    }

    @Override
    void reject() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (bind == null){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                bind.reject();
            }
        });
    }

    @Override
    void stop(final String msg) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (bind == null){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                System.out.println("cancel_1");
                bind.stop(msg);
            }
        });
    }

    @Override
    long getTime() {
        return bind.getTime();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
        if (serviceConnection != null) {
            context.unbindService(serviceConnection);
            serviceConnection = null;
        }
    }
}
