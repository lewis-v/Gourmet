package com.yw.gourmet.push;

import android.content.Context;
import android.content.Intent;

import com.yw.gourmet.service.MessageService;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;


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
        JPushInterface.init(context);
        context.startService(new Intent(context, MessageService.class));
        return this;
    }

    /**
     * 设置标签
     * @param alias 别名(用于指定用户,使用user_id,未登录的使用游客标记)
     * @param tags 标签(用于用户分组)
     */
    public PushManager setTag(Context context,String alias,String[] tags){
            //建议添加tag标签，发送消息的之后就可以指定tag标签来发送了
            Set<String> set = new HashSet<>();
            for (String str : tags) {
                set.add(str);
            }
            JPushInterface.setTags(context,100, set);//设置标签
            JPushInterface.setAlias(context, 100, alias);//设置别名
        return this;
    }

    /**
     * 检测push是否关闭,关闭了自动重启
     * @param context
     * @return
     */
    public PushManager checkPush(Context context){
        if (JPushInterface.isPushStopped(context)){
            JPushInterface.resumePush(context);
        }
        return this;
    }

    /**
     * 停止推送
     */
    public PushManager Pause(){

        return this;
    }

    /**
     * 重启推送
     */
    public PushManager reStart(){

        return this;
    }

    /**
     * 消除通知栏通知
     */
    public PushManager clearAllNotification(){

        return this;
    }

    /**
     * 移除置顶id的notification
     * @param id
     */
    public PushManager clearNotification(int id){

        return this;
    }

    /**
     * 清除所有标记
     */
    public PushManager clearTag(){

        return this;
    }
}
