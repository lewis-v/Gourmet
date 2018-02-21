package com.yw.gourmet.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.InitData;
import com.yw.gourmet.utils.ToastUtils;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UpdateService extends Service {
    public InitData initData;

    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getVersion(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取版本信息
     */
    public void getVersion(final Intent intent){
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
                            Log.e("---version",localVersion);
                            if (localVersion.equals(initData.getAndroid_version())){
                                //最新版本
                            }else {
                                if (intent.getBooleanExtra("update",false)){//直接更新

                                }else {
                                    //todo 提示是否要更新
                                }
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
     * 提示需要更新
     * @param initData
     */
    public void showUpdateTip(InitData initData){

    }
}
