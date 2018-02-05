package com.yw.gourmet;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.Glide;
import com.mob.MobSDK;
import com.yw.gourmet.dao.GreenDaoManager;

/**
 * Created by yw on 2017/10/21.
 */

public class App extends Application{
    private static App app;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        SDKInitializer.initialize(getApplicationContext());
        MobSDK.init(this);
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
