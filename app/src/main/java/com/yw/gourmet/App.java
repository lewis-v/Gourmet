package com.yw.gourmet;

import android.app.ActivityManager;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.Glide;
import com.squareup.leakcanary.LeakCanary;
import com.yw.gourmet.audio.play.AudioPlayManager;
import com.yw.gourmet.center.MessageCenter;
import com.yw.gourmet.push.PushManager;
import com.yw.gourmet.utils.SPUtils;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by yw on 2017/10/21.
 */
@ReportsCrashes(
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.app_error,
        // 更换默认的发送器
        reportSenderFactoryClasses = {com.yw.gourmet.acra.CrashSenderFactory.class}
)
public class App extends MultiDexApplication {
    private static final String TAG = "APP";

    private static App app;
    private static Context context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process: manager.getRunningAppProcesses()) {
            if(process.pid == pid)
            {
               if (process.processName.equals(getPackageName())){//主进程
                   if (!LeakCanary.isInAnalyzerProcess(this)) {
                       LeakCanary.install(this);
                   }

                   //初始化百度地图
                   SDKInitializer.initialize(getApplicationContext());
                   initPush();
                   MessageCenter.getInstance().init(this);
                   AudioPlayManager.getInstance().init(this);
                   initNight();
               }
            }
        }

    }

    /**
     * 初始化推送
     */
    public void initPush(){
        PushManager.getInstance().initPush(this);
    }

    /**
     * 初始化夜间模式
     */
    public void initNight(){
        boolean isNight = SPUtils.getSharedBooleanData(this,"NIGHT");
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
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
