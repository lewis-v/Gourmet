package com.yw.gourmet.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.GlideRequest;
import com.yw.gourmet.R;
import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxManager;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.center.MessageCenter;
import com.yw.gourmet.center.event.IMessageGet;
import com.yw.gourmet.center.event.IMessageSendEvent;
import com.yw.gourmet.dao.data.messageData.MessageDataUtil;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.ui.chat.ChatActivity;
import com.yw.gourmet.utils.ToastUtils;

import java.util.List;

import okhttp3.MultipartBody;

import static com.yw.gourmet.Constant.NORMAL_PUSH_ID;

public class MessageService extends Service {
    public static final String TAG = "MessageService";
    private NotificationManager notifyManager ;
    private IMessageGet iMessageGet;
    private IMessageSendEvent iMessageSendEvent;
    private RxManager rxManager;

    public MessageService() {
        rxManager = new RxManager();
        iMessageSendEvent = new IMessageSendEvent() {
            @Override
            public boolean onSendMessageResult(final BaseData message,MessageListData messageListData, int position) {
                if (message.getStatus() == 0){
                    messageListData.setSendStatus(MessageListData.SEND_SUCCESS);
                    MessageDataUtil.updata(messageListData);
                }else {
                    sendMessageFail(messageListData,message.getMessage());
                }
                return false;
            }

            @Override
            public boolean onSendMessageFail(MessageListData message, int position, final String msg) {
                sendMessageFail(message,msg);
                return false;
            }
        };
    }

    /**
     * 消息发送失败通知
     * @param messageListData
     */
    public void sendMessageFail(MessageListData messageListData, final String msg){
        messageListData.setSendStatus(MessageListData.SEND_FAIL);
        MessageDataUtil.updata(messageListData);
        Notification notification = new NotificationCompat
                .Builder(MessageService.this,String.valueOf(NORMAL_PUSH_ID))
                .setSmallIcon(R.mipmap.dialog_back)
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL
                        ,messageListData.getGet_id(),messageListData.getPut_id()))
                .setContentTitle("有一条消息发送失败")
                .setAutoCancel(true).build();
        notifyManager.notify(NORMAL_PUSH_ID,notification);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showSingleToast(msg);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (notifyManager == null){
            notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (iMessageGet == null || !MessageCenter.getInstance().isExist(iMessageGet)){
            iMessageGet = new IMessageGet() {
                @Override
                public boolean onGetMessage(final MessageListData message) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            GlideApp.with(MessageService.this)
                                    .asBitmap().load(message.getImg_header())
                                    .error(R.mipmap.ic_launcher).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    int id ;
                                    try {
                                        id = Integer.parseInt(message.getPut_id());
                                    }catch (Exception e){
                                        id = NORMAL_PUSH_ID;
                                    }
                                    Notification notification = new NotificationCompat
                                            .Builder(MessageService.this,String.valueOf(id))
                                            .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL
                                                    ,message.getPut_id(),message.getGet_id()))
                                            .setSmallIcon(R.mipmap.dialog_back)
                                            .setLargeIcon(resource)
                                            .setContentText(message.getContent())
                                            .setContentTitle(message.getNickname())
                                            .setAutoCancel(true).build();
                                        notifyManager.notify(id,notification);

                                }
                            });
                        }
                    });
                    return false;
                }
            };
            MessageCenter.getInstance().addMessageHandle(iMessageGet);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 点击消息
     * @param flags
     * @return
     */
    public PendingIntent getDefalutIntent(int flags,String putId,String getId){
        Intent myintent = new Intent(this, ChatActivity.class);
        myintent.putExtra("put_id",putId);
        myintent.putExtra("get_id",getId);
        Log.e("---touch",putId+getId);
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, myintent, flags);
        return pendingIntent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }

    public class MyBind extends Binder {
        public void sendMessage(final List<MultipartBody.Part> parts, final MessageListData messageListData, final int position){
            rxManager.add(Api.getInstance().SendMessage(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
                @Override
                public void onSuccess(BaseData model) {
                    MessageCenter.getInstance().pushSendMessageResult(model,messageListData,position);
                }

                @Override
                public void onFailure(int code, String msg) {
                    MessageCenter.getInstance().pushSendMessageFail(messageListData,position,msg);
                }
            }));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iMessageGet != null) {
            MessageCenter.getInstance().removeMessageHandle(iMessageGet);
        }
        if (iMessageSendEvent != null){
            MessageCenter.getInstance().removeMessageHandle(iMessageSendEvent);
        }
        if (rxManager != null) {
            rxManager.clear();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        notifyManager.cancelAll();
    }
}
