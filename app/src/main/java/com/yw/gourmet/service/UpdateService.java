package com.yw.gourmet.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.InitData;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.utils.WindowUtil;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UpdateService extends Service {
    public InitData initData;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;
    private RelativeLayout window;

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
                            if (localVersion.equals(initData.getAndroid_version())){
                                //最新版本
                            }else {
                                if (intent.getBooleanExtra("downloadUpdate",false)){//直接更新
                                    downloadUpdate(initData.getUpdatePath());
                                }else {
                                    showUpdateTip(initData);
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
    public void showUpdateTip(final InitData initData){
        layoutParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager)getApplicationContext()
                .getSystemService(WINDOW_SERVICE);
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST ;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        layoutParams.width = WindowUtil.width;
        layoutParams.height = WindowUtil.height;
        LayoutInflater inflater = LayoutInflater.from(this);
        window = (RelativeLayout) inflater.inflate(R.layout.fragment_my_dialog_tip,null);
        RelativeLayout rl = (RelativeLayout) window.findViewById(R.id.rl);
        rl.setGravity(Gravity.CENTER);
        TextView tv_enter = window.findViewById(R.id.tv_enter);
        TextView tv_cancel = window.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消
                windowManager.removeViewImmediate(window);
            }
        });
        tv_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更新
                windowManager.removeViewImmediate(window);
                downloadUpdate(initData.getUpdatePath());
            }
        });
        windowManager.addView(window,layoutParams);
        window.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                , View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    /**
     * 下载更新程序
     * @param path
     */
    public void downloadUpdate(String path){

    }
}
