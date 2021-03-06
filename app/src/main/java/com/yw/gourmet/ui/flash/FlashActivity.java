package com.yw.gourmet.ui.flash;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.dao.data.messageData.MessageDataUtil;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.InitData;
import com.yw.gourmet.data.UserData;
import com.yw.gourmet.dialog.MyDialogTipFragment;
import com.yw.gourmet.push.PushManager;
import com.yw.gourmet.ui.channel.ChannelActivity;
import com.yw.gourmet.ui.chat.ChatActivity;
import com.yw.gourmet.ui.main.MainActivity;
import com.yw.gourmet.utils.SPUtils;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.voiceChat.VoiceChatData;

import java.util.List;

import okhttp3.MultipartBody;

public class FlashActivity extends BaseActivity<FlashPresenter> implements FlashConstract.View {
    private final static String TAG = "FlashActivity";

    private final long TIME = 3000;//3秒的最少停留时间
    private volatile boolean isFinish = false;
    private ProgressBar progress;
    private ValueAnimator valueAnimator;
    private int num = 0;//重新连接次数

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        progress = findViewById(R.id.progress);

        mPresenter.Init();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                isFinish = true;
            }
        },TIME);
        valueAnimator = ValueAnimator.ofInt(1,100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private IntEvaluator mEvaluator = new IntEvaluator();
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                progress.setProgress(mEvaluator.evaluate(fraction,0,100));
            }
        });
        valueAnimator.setDuration(1000).setRepeatCount(-1);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();
    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_flash;
    }

    @Override
    public void onLoginSuccess(final BaseData<UserData> model) {
        SPUtils.setSharedStringData(getApplicationContext(),"token",model.getData().getToken());
        Constant.userData = model.getData();
        MessageDataUtil.setSendingToFail();
        Thread thread = new WaitThread(new Runnable() {
            @Override
            public void run() {
                toChannelOrMain();
                finish();
            }
        });
        threadList.add(thread);
        thread.start();
    }

    @Override
    public void onInitSuccess(BaseData<InitData> model) {
        Constant.serviceTime = model.getData().getTime() - System.currentTimeMillis();
        mPresenter.getAreaDetail();
    }

    @Override
    public void onGetAreaSuccess(BaseData<List<String>> model) {
        Constant.areaList = model.getData();
        String token = null;
        token = SPUtils.getSharedStringData(getApplicationContext(),"token");
        if (token == null || token.length() == 0) {
            Thread thread = new WaitThread(new Runnable() {
                @Override
                public void run() {
                    toChannelOrMain();
                    finish();
                }
            });
            threadList.add(thread);
            thread.start();
        }else {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("token", token);
            mPresenter.onLogin(builder.build().parts());
        }
    }

    @Override
    public void onInitFail(String msg) {
        super.onFail(msg);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //因服务器迁移的临时做法
                num++;
                if (num == 4){
                    Api.reSet("http://120.79.65.50:47423");
                    VoiceChatData.IP = "120.79.65.50";
                    ToastUtils.showSingleToast("由于种种原因,服务器可能过期了\n,现尝试连接到新的服务器");
                }
                mPresenter.Init();
            }
        },3000);//失败后每3000ms重试一次
    }

    @Override
    public void onFail(String msg) {
        Thread thread = new WaitThread(new Runnable() {
            @Override
            public void run() {
                toChannelOrMain();
                finish();
            }
        });
        threadList.add(thread);
        thread.start();
    }

    /**
     * 进入引导或者主界面
     */
    public void toChannelOrMain(){
        PushManager.getInstance().setTag(getApplicationContext()
                ,Constant.userData == null?PushManager.NOMAL_ALIAS:Constant.userData.getUser_id()
                ,PushManager.NOMAL_TAG);
        try {
            if (SPUtils.getSharedBooleanData(FlashActivity.this
                    ,getPackageManager().getPackageInfo(getPackageName(),0).versionName))
            {
                startActivity(new Intent(FlashActivity.this, MainActivity.class));
            }else{
                startActivity(new Intent(FlashActivity.this, ChannelActivity.class));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            //出错了不进入引导
            startActivity(new Intent(FlashActivity.this, MainActivity.class));
        }
    }

    /**
     * 等待结束线程
     */
    private class WaitThread extends Thread{
        private Runnable runnable;

        public WaitThread(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            while (!isFinish){
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }
            runnable.run();
        }
    }

    @Override
    public boolean isFeedBack() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueAnimator != null && valueAnimator.isRunning()){
            valueAnimator.cancel();
        }
    }
}
