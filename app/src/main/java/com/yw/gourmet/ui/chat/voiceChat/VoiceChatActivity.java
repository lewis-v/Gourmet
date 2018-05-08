package com.yw.gourmet.ui.chat.voiceChat;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.utils.TimeUtils;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.utils.WindowUtil;

import static com.yw.gourmet.voiceChat.VoiceChatData.ACTION;

/**
 * auth: lewis-v
 * time: 2018/5/4.
 */

public class VoiceChatActivity extends BaseActivity<VoiceChatPresenter> implements View.OnClickListener, VoiceChatContract.View {
    private static final int DEFAULT = 0;
    private static final int APPLY = 1;
    private static final int REJECT = 2;
    private static final int CHANGE = 3;//切换中

    private ImageView img_header, img_apply, img_reject;
    private TextView tv_name, tv_tip;
    private int mode = DEFAULT;
    private Handler handler;
    private DelayRun delayRun;
    private SensorManager sm;
    private boolean isWake = true;
    private float light = -1f;
    private volatile boolean isEnd = false;//是否已经进入结束环节


    @Override
    protected int getLayoutId() {
        return R.layout.activity_voice_chat;
    }

    @Override
    protected void initView() {
        WindowUtil.initWindowUtil(getApplicationContext());
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        handler = new Handler(getMainLooper());
        img_apply = findViewById(R.id.img_apply);
        img_header = findViewById(R.id.img_header);
        img_reject = findViewById(R.id.img_reject);

        img_reject.setOnClickListener(this);
        img_apply.setOnClickListener(this);

        tv_name = findViewById(R.id.tv_name);
        tv_tip = findViewById(R.id.tv_tip);

        tv_name.setText(getIntent().getStringExtra("name"));
        GlideApp.with(this).load(getIntent().getStringExtra("img"))
                .error(R.mipmap.loading).into(img_header);
        if (getIntent().getBooleanExtra("apply", false)) {//发起请求者直接进入APPLY模式
            mode = APPLY;
            img_apply.setVisibility(View.GONE);
            tv_tip.setText("等待对方接听");
        } else {
            tv_tip.setText("点击接听");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor s = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (s != null) {
            sm.registerListener(mSensorListener, s, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    /**
     * 感应器监听
     */
    private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (mode == APPLY) {
                float[] its = event.values;
                if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                    if (its[0] == 0.0) {//近距离,息屏
                        wakeLock();

                    } else {
                        wakeUnLock();
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * 熄屏
     */
    public synchronized void wakeLock() {
        if (isWake) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            if (light == -1f){
                light = lp.screenBrightness;
            }
            lp.screenBrightness = 0f;
            getWindow().setAttributes(lp);
            isWake = false;
        }
    }

    /**
     * 亮屏
     */
    public synchronized void wakeUnLock() {
        if (!isWake) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = light;
            getWindow().setAttributes(lp);
            isWake = true;
        }
    }

    @Override
    public void onClick(View v) {
        if (mode == CHANGE || !isWake) {//切换状态和未低亮度状态下,无效
            return;
        }
        switch (v.getId()) {
            case R.id.img_apply:
                if (mode == DEFAULT) {//默认状态下为接收
                    mode = CHANGE;
                    mPresenter.apply();
                } //其余状态都无效
                break;
            case R.id.img_reject:
                if (mode == DEFAULT) {
                    mode = CHANGE;
                    mPresenter.reject();
                } else {//其他状态为挂断
                    mPresenter.stop("语聊已取消");
                }
                break;
        }
    }

    @Override
    public void onApplySuccess() {
        Log.i("success", "");
        mode = APPLY;
        showChatTime();
        if (!getIntent().getBooleanExtra("apply", false)) {//接受者才有隐藏动画
            hideApply();
        }
    }

    @Override
    public boolean isFeedBack() {
        return false;
    }

    public void hideApply() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                img_apply.setVisibility(View.GONE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img_reject, "x"
                        , WindowUtil.width / 2 - img_reject.getWidth() / 2);
                ObjectAnimator hideAnimator = ObjectAnimator.ofFloat(img_apply, "alpha"
                        , 0);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(objectAnimator).before(hideAnimator);
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        img_apply.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animatorSet.start();
            }
        });

    }

    @Override
    public void onRejectSuccess() {
        mode = REJECT;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_tip.setText("拒绝接听");
                voiceEnd();
            }
        });
    }

    @Override
    public void onStopSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_tip.setText("结束语聊");
                voiceEnd();
            }
        });
    }

    @Override
    public void onCancel(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isEnd){
                    return;
                }
                isEnd = true;
                tv_tip.setText(msg);
                Intent intent = new Intent();
                intent.setAction(ACTION);
                intent.putExtra("recevice_id", getIntent().getStringExtra("recevice_id"));
                intent.putExtra("apply_id", getIntent().getStringExtra("apply_id"));
                intent.putExtra("content", msg);
                intent.putExtra("img", getIntent().getStringExtra("img"));
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("time", (int)(mPresenter.getTime()/1000));
                sendBroadcast(intent);
                destroy();
                voiceEnd();
            }
        });
    }

    /**
     * 结束语聊,关闭进程
     */
    public synchronized void voiceEnd() {
        finish();
        Process.killProcess(Process.myPid());
    }

    @Override
    public void onBackPressed() {
        //不给你用返回键
    }

    /**
     * 语聊时间显示
     */
    public void showChatTime() {
        delayRun = new DelayRun();
        handler.postDelayed(delayRun, 600);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
    }

    public void destroy() {
        if (handler != null && delayRun != null) {
            handler.removeCallbacks(delayRun);
        }
        handler = null;
        delayRun = null;
        if (sm != null) {
            try {
                sm.unregisterListener(mSensorListener);//注销传感器监听
            } catch (Exception e) {
            }
        }
    }

    class DelayRun implements Runnable {

        @Override
        public void run() {
            tv_tip.setText(TimeUtils.getVoiceChatLength((int)(mPresenter.getTime()/1000)));
            showChatTime();
        }
    }
}
