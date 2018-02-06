package com.yw.gourmet;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.Glide;
import com.mob.MobSDK;
import com.mob.pushsdk.MobPush;
import com.mob.pushsdk.MobPushCustomMessage;
import com.mob.pushsdk.MobPushCustomNotification;
import com.mob.pushsdk.MobPushNotifyMessage;
import com.mob.pushsdk.MobPushReceiver;
import com.yw.gourmet.dao.GreenDaoManager;
import com.yw.gourmet.push.PushManager;

/**
 * Created by yw on 2017/10/21.
 */

public class App extends Application{
    private static final String TAG = "APP";

    private static App app;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //初始化百度地图
        SDKInitializer.initialize(getApplicationContext());
        initPush();
    }

    /**
     * 初始化推送
     */
    public void initPush(){
        PushManager.getInstance().initPush(this);

        MobPush.setClickNotificationToLaunchMainActivity(false);
        PushManager.getInstance().addReceiver(new MobPushReceiver() {
            @Override
            public void onCustomMessageReceive(Context context, MobPushCustomMessage message) {
                //接收自定义消息
                Log.e(TAG, "onCustomMessageReceive: "+message.getContent());
            }
            @Override
            public void onNotifyMessageReceive(Context context, MobPushNotifyMessage message) {
                //接收通知消息
                Log.e(TAG, "onNotifyMessageReceive: "+message.getContent() );
            }

            @Override
            public void onNotifyMessageOpenedReceive(Context context, MobPushNotifyMessage message) {
                //接收通知消息被点击事件
                Log.e(TAG, "onNotifyMessageOpenedReceive: "+message.getContent() );
            }
            @Override
            public void onTagsCallback(Context context, String[] tags, int operation, int errorCode) {
                //接收tags的增改删查操作
                Log.e(TAG, "onNotifyMessageReceive: "+tags);
            }
            @Override
            public void onAliasCallback(Context context, String alias, int operation, int errorCode) {
                //接收alias的增改删查操作
                Log.e(TAG, "onNotifyMessageReceive: "+alias );
            }
        });
    }

    public static App getApp(){
        if (app == null){
            app = new App();
        }
        return app;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN){
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    public Context getContext(){
        return context;
    }
}
