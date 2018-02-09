package com.yw.gourmet.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.GlideRequest;
import com.yw.gourmet.R;
import com.yw.gourmet.center.MessageCenter;
import com.yw.gourmet.center.event.IMessageGet;
import com.yw.gourmet.data.MessageListData;

public class MessageService extends Service {
    public static final String TAG = "MessageService";
    public static final int NORMAL_PUSH_ID = 49573;
    private NotificationManager notifyManager ;
    private IMessageGet iMessageGet;

    public MessageService() {

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
                                            .setSmallIcon(R.mipmap.dialog_back)
                                            .setLargeIcon(resource)
                                            .setContentText(message.getContent())
                                            .setContentTitle(message.getNickname())
                                            .setAutoCancel(true).build();

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        notifyManager.notify(id,notification);
                                    }else {
                                        notifyManager.notify(id,notification);

                                    }
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

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MessageCenter.getInstance().removeMessageHandle(iMessageGet);
    }
}
