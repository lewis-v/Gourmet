package com.yw.gourmet.push;

import android.content.Context;

import com.mob.MobSDK;
import com.mob.pushsdk.MobPush;
import com.mob.pushsdk.MobPushReceiver;

/**
 * auth: lewis-v
 * time: 2018/2/6.
 */

public class PushManager {
    static final String TAG = "PushManager";
    public static final String NOMAL_ALIAS = "Tourist";
    public static final String[] NOMAL_TAG = new String[]{"Gourmet"};

    private static class Instance {
        private static final PushManager instance = new PushManager();
    }

    public static PushManager getInstance() {
        return Instance.instance;
    }

    /**
     * 初始化
     * @param context
     */
    public PushManager initPush(Context context){
        MobSDK.init(context);
        return this;
    }

    /**
     * 设置标签
     * @param alias 别名(用于指定用户,使用user_id,未登录的使用游客标记)
     * @param tags 标签(用于用户分组)
     */
    public PushManager setTag(String alias,String[] tags){
        clearTag();
        MobPush.setAlias(alias);//设置别名
        MobPush.addTags(tags);//设置标签
        return this;
    }

    /**
     * 停止推送
     */
    public PushManager Pause(){
        if (!MobPush.isPushStopped()) {
            MobPush.stopPush();
        }
        return this;
    }

    /**
     * 重启推送
     */
    public PushManager reStart(){
        if (MobPush.isPushStopped()){
            MobPush.restartPush();
        }
        return this;
    }

    /**
     * 添加监听器
     * @param receiver
     */
    public PushManager addReceiver(MobPushReceiver receiver){
        MobPush.addPushReceiver(receiver);
        return this;
    }

    /**
     * 移除监听器
     * @param receiver
     */
    public PushManager removeReceiver(MobPushReceiver receiver){
        MobPush.removePushReceiver(receiver);
        return this;
    }

    /**
     * 消除通知栏通知
     */
    public PushManager clearAllNotification(){
        MobPush.clearLocalNotifications();
        return this;
    }

    /**
     * 移除置顶id的notification
     * @param id
     */
    public PushManager clearNotification(int id){
        MobPush.removeLocalNotification(id);
        return this;
    }

    /**
     * 清除所有标记
     */
    public PushManager clearTag(){
        MobPush.cleanTags();
        MobPush.deleteAlias();
        return this;
    }
}
