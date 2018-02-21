package com.yw.gourmet.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

import com.yw.gourmet.R;
import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.InitData;
import com.yw.gourmet.utils.DownloadUtil;
import com.yw.gourmet.utils.ToastUtils;

import java.io.File;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.yw.gourmet.Constant.UPDATE_ID;

public class UpdateService extends Service {
    public InitData initData;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;
    private RelativeLayout window;
    private RemoteViews mRemoteViews;
    private boolean isUpdating = false;//是否在更新中
    private BroadcastReceiver broadcastReceiver;
    private Notification notification;//目前显示的notification


    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initBroadcast();
        getVersion();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化广播接收
     */
    public void initBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("reUpdate");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                removeNotification();
                getVersion();
            }
        };
        registerReceiver(broadcastReceiver,intentFilter);
    }

    /**
     * 获取版本信息
     */
    public void getVersion(){
        Api.getInstance().Init().subscribeOn(Schedulers.io())//设置调用方法前在io线程中执行
                .unsubscribeOn(Schedulers.io())//设置取消订阅在io线程中执行
                .observeOn(AndroidSchedulers.mainThread())//设置调用方法后在主线程中执行
                .subscribe(new RxSubscriberCallBack<BaseData<InitData>>(new RxApiCallback<BaseData<InitData>>() {
                    @Override
                    public void onSuccess(BaseData<InitData> model) {
                        initData = model.getData();
                        PackageInfo packageInfo = null;
                        try {
                            packageInfo = getApplicationContext()
                                    .getPackageManager()
                                    .getPackageInfo(getPackageName(), 0);
                            String localVersion = packageInfo.versionName;
                            if (localVersion.equals(initData.getAndroid_version())){
                                //最新版本
                            }else {
                                downloadUpdate(initData.getUpdatePath(),localVersion);
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        ToastUtils.showSingleToast(msg);
                    }
                }));//设置订阅者
    }


    /**
     * 下载更新程序
     * @param path
     */
    public synchronized void downloadUpdate(String path,String version){
        if (isUpdating){
            removeNotification();
            initUpdateNotification();
            return;
        }
        isUpdating = true;
        DownloadUtil.get().download(path, "/data/gourmet/update", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(String path) {
                isUpdating = false;
                Log.i("---update","success");
                install(path);
                removeNotification();
            }

            @Override
            public void onDownloading(int progress) {
                if (null != notification) {
                    // 修改进度条
                    notification.contentView.setProgressBar(R.id.progress,100,progress,false);
                    notification.contentView.setTextViewText(R.id.tv_progress,progress+"%");
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(UPDATE_ID, notification);
                }
                Log.i("---update","progress"+progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                e.printStackTrace();
                isUpdating = false;
                Log.e("---update","fail");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showSingleToast("下载更新失败,请重试");
                    }
                });
                removeNotification();
                initUpdateErrNotification();
            }
        });
        initUpdateNotification();
    }

    /**
     * 初始化更新进度notification
     */
    public void initUpdateNotification(){
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.remote_notification_update);
        mRemoteViews.setImageViewResource(R.id.img_icon,R.mipmap.ic_launcher);
        mRemoteViews.setTextViewText(R.id.tv_progress,"0%");
        mRemoteViews.setProgressBar(R.id.progress,100,0,false);
        notification = new NotificationCompat
                .Builder(this,String.valueOf(UPDATE_ID))
                .setContent(mRemoteViews)
                .setSmallIcon(R.mipmap.dialog_back)
                .setAutoCancel(true)
                .build();
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(UPDATE_ID,notification);
    }

    /**
     * 更新出错
     */
    public void initUpdateErrNotification(){
        notification = new NotificationCompat
                .Builder(this,String.valueOf(UPDATE_ID))
                .setContentTitle("更新失败,请重试")
                .setContentText("点击重试")
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))//设置点击意图
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build();
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(UPDATE_ID,notification);
    }

    /**
     * 点击重新下载
     * @param flags
     * @return
     */
    public PendingIntent getDefalutIntent(int flags){
        Intent myintent = new Intent();
        myintent.setAction("reUpdate");
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this, 0, myintent, flags);
        return pendingIntent;
    }

    /**
     * 安装apk
     * @param filePath
     */
    private void install(String filePath) {
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this
                    , getPackageName()+".fileprovider"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    /**
     * 移除更新notification
     */
    public void removeNotification(){
        try {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(UPDATE_ID);
        }catch (Exception e){}
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        removeNotification();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.e("---des","1");
        removeNotification();
        DownloadUtil.get().pause();
        if (broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }
}
