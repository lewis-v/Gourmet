package com.yw.gourmet.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MessageService extends Service {
    public static final String TAG = "MessageService";
    public static final int NOMAL_PUSH_ID = 49573;
    private NotificationManager notifyManager ;

    public MessageService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
