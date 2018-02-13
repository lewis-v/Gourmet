package com.yw.gourmet.center;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxManager;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.center.event.IMessageGet;
import com.yw.gourmet.center.event.IMessageSendEvent;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/2/7.
 */

public class MessageCenter {
    public static final String TAG = "MessageCenter";

    private List<IMessageGet> messageEvent = new ArrayList<>();
    private List<IMessageSendEvent> messageSendEvents = new ArrayList<>();
    private ExecutorService executorService;
    private MessageService.MyBind myBind;

    private static class Instance{
        private static final MessageCenter instance = new MessageCenter();
    }

    private MessageCenter() {
         executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 初始化
     * @param context
     * @return
     */
    public MessageCenter init(Context context){
        if (myBind == null) {
            context.bindService(new Intent(context, MessageService.class), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    myBind = (MessageService.MyBind) service;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            }, Context.BIND_AUTO_CREATE);
        }
        return this;
    }

    public static MessageCenter getInstance(){
        return Instance.instance;
    }

    /**
     * 添加消息处理,在末尾
     * @param iMessageGet
     * @return
     */
    public MessageCenter addMessageHandle(IMessageGet iMessageGet){
        int index = messageEvent.indexOf(iMessageGet);
        if (index > -1){//防止重复添加同一个事件处理
            messageEvent.remove(index);
        }
        messageEvent.add(iMessageGet);
        return this;
    }

    /**
     * 添加消息处理,在顶部
     * @param iMessageGet
     * @return
     */
    public MessageCenter addMessageHandleTop(IMessageGet iMessageGet){
        int index = messageEvent.indexOf(iMessageGet);
        if (index > -1){//防止重复添加同一个事件处理
            messageEvent.remove(index);
        }
        messageEvent.add(0,iMessageGet);
        return this;
    }

    /**
     * 移除消息处理
     * @return
     */
    public MessageCenter removeMessageHandle(IMessageGet iMessageGet){
        int index = messageEvent.indexOf(iMessageGet);
        if (index > -1){
            messageEvent.remove(index);
        }
        return this;
    }

    /**
     * 发送消息
     * @param message
     * @return
     */
    public MessageCenter pushMessage(final MessageListData message){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (IMessageGet iMessageGet : messageEvent){
                    if (iMessageGet.onGetMessage(message)){//返回true为处理完毕,不在向后发送
                        return;
                    }
                }
            }
        });
        return this;
    }

    /**
     * 查看是否存在对应处理
     * @param iMessageGet
     * @return
     */
    public boolean isExist(IMessageGet iMessageGet){
        if (messageEvent != null && messageEvent.indexOf(iMessageGet)>-1){
            return true;
        }
        return false;
    }

    /**
     * 添加消息处理,在末尾
     * @param iMessageSendEvent
     * @return
     */
    public MessageCenter addMessageHandle(IMessageSendEvent iMessageSendEvent){
        int index = messageSendEvents.indexOf(iMessageSendEvent);
        if (index > -1){//防止重复添加同一个事件处理
            messageSendEvents.remove(index);
        }
        messageSendEvents.add(iMessageSendEvent);
        return this;
    }

    /**
     * 添加消息处理,在顶部
     * @param iMessageSendEvent
     * @return
     */
    public MessageCenter addMessageHandleTop(IMessageSendEvent iMessageSendEvent){
        int index = messageSendEvents.indexOf(iMessageSendEvent);
        if (index > -1){//防止重复添加同一个事件处理
            messageSendEvents.remove(index);
        }
        messageSendEvents.add(0,iMessageSendEvent);
        return this;
    }

    /**
     * 移除消息处理
     * @return
     */
    public MessageCenter removeMessageHandle(IMessageSendEvent iMessageSendEvent){
        int index = messageSendEvents.indexOf(iMessageSendEvent);
        if (index > -1){
            messageSendEvents.remove(index);
        }
        return this;
    }

    /**
     * 发送消息
     * @param message
     * @return
     */
    public MessageCenter pushSendMessageResult(final BaseData message, final MessageListData MessageListData, final int position){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                    for (IMessageSendEvent iMessageSendEvent : messageSendEvents) {
                        if (iMessageSendEvent.onSendMessageResult(message,MessageListData,position)) {//返回true为处理完毕,不在向后发送
                            return;
                        }
                    }
            }
        });
        return this;
    }

    /**
     * 发送消息
     * @param message
     * @return
     */
    public MessageCenter pushSendMessageFail(final MessageListData message, final int position, final String errMsg){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (IMessageSendEvent iMessageSendEvent : messageSendEvents) {
                    if (iMessageSendEvent.onSendMessageFail(message,position,errMsg)) {//返回true为处理完毕,不在向后发送
                        return;
                    }
                }
            }
        });
        return this;
    }

    /**
     * 查看是否存在对应处理
     * @param iMessageSendEvent
     * @return
     */
    public boolean isExist(IMessageSendEvent iMessageSendEvent){
        if (messageSendEvents != null && messageSendEvents.indexOf(iMessageSendEvent)>-1){
            return true;
        }
        return false;
    }

    /**
     * 发送消息
     * @param parts
     * @param messageListData
     * @param position
     */
    public void sendMessage(final List<MultipartBody.Part> parts, final MessageListData messageListData, final int position){
       executorService.execute(new Runnable() {
           @Override
           public void run() {
               while (myBind == null){
                   try {
                       Thread.sleep(100);
                       Log.e(TAG,"bindNULL");
                   } catch (InterruptedException e) {
                       return;
                   }
               }
               myBind.sendMessage(parts,messageListData,position);
           }
       });
    }


}
