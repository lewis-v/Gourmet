package com.yw.gourmet.base;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yw.gourmet.App;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.dialog.MyDialogFeedBackFragment;
import com.yw.gourmet.dialog.MyDialogLoadFragment;
import com.yw.gourmet.push.PushManager;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
import com.yw.gourmet.utils.SPUtils;
import com.yw.gourmet.utils.ShakeUtils;
import com.yw.gourmet.utils.ToastUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by yw on 2017-08-07.
 */

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView{
    protected P mPresenter;
    private MyDialogLoadFragment myDialogLoadFragment;
    protected List<Thread> threadList = new ArrayList<>();
    protected Toolbar toolbar;
    protected View view_parent;
    private boolean isReLogining = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.getScreenManager().pushActivity(this);
        try {
            //getGenericSuperclass获取类的超类的类型即<P>p的类型,ParameterizedType参数化类型
            // ,getActualTypeArguments返回表示此类型实际类型参数的 ToolType 对象的数组
            mPresenter = ((Class<P>) ((ParameterizedType) (getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[0])
                    .newInstance();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        if (mPresenter != null){
            mPresenter.setContext(this);
            mPresenter.setmView(this);
        }
        changeWindow();
        setContentView(getLayoutId());
        initView();
        setToolbarTop();
    }

    /**
     * 设置布局文件
     */
    protected abstract int getLayoutId();

    /**
     * 初始化UI
     */
    protected abstract void initView();


    /**
     * 设置在全屏下toolbar与顶部的距离
     * 4.4以上可全屏,将会设置padding,以下的版本不设置
     */
    public void setToolbarTop(){
        try {
            view_parent = findViewById(R.id.view_parent);
            toolbar = findViewById(R.id.toolbar);
        }catch (Exception e){
            return;
        }
        if (toolbar != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view_parent != null){
                    view_parent.setFitsSystemWindows(true);
                }else {
                    toolbar.post(new Runnable() {
                        @Override
                        public void run() {
                            toolbar.setPadding(toolbar.getPaddingLeft(), toolbar.getHeight() * 25 / 40
                                    , toolbar.getPaddingRight(), toolbar.getPaddingBottom());
                            ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
                            layoutParams.height = toolbar.getHeight() * 65 / 40;
                            toolbar.setLayoutParams(layoutParams);
                        }
                    });
                    if (view_parent != null){
                        view_parent.setFitsSystemWindows(false);
                    }
                }
            }
        }
    }

    /**
     * 仅支持4.4以上的版本
     * 改变通知栏颜色
     */
    public void changeWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isWhile()) {
                Window window = getWindow();
                window.requestFeature(Window.FEATURE_NO_TITLE);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
                else{
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));

                    window.setStatusBarColor(Color.TRANSPARENT);
                    window.setNavigationBarColor(Color.TRANSPARENT);
                }
            }
        }
        if (isWhile()) {
            setMiuiStatusBarDarkMode(this, true);
            setMeizuStatusBarDarkIcon(this, true);
        }
    }

    /**
     * 获取是否设置状态栏颜色
     * @return
     */
    public boolean isWhile(){
        return true;
    }

    //设置成白色的背景，字体颜色为黑色。miui
    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    //设置成白色的背景，字体颜色为黑色。flyme
    public static boolean setMeizuStatusBarDarkIcon(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     *  设置加载中提示框的显示
     * @param isLoadDialog
     */
    public void setLoadDialog(boolean isLoadDialog){
        try {
            if (isLoadDialog) {
                if (myDialogLoadFragment == null) {
                    myDialogLoadFragment = new MyDialogLoadFragment();
                    myDialogLoadFragment.show(getSupportFragmentManager(), "");
                } else {
                    myDialogLoadFragment.dismiss();
                    myDialogLoadFragment = new MyDialogLoadFragment();
                    myDialogLoadFragment.show(getSupportFragmentManager(), "");
                }
            } else {
                if (myDialogLoadFragment != null) {
                    myDialogLoadFragment.dismiss();
                    myDialogLoadFragment = null;
                }
            }
        }catch (Exception e){}
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShakeUtils.getInstance(this).setOnShakeListener(new ShakeUtils.OnShakeListener() {
            @Override
            public void onShake() {
                if (Constant.userData != null) {//登录后才可反馈信息
                    if (!MyDialogFeedBackFragment.getInstance().isShow()) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (!MyDialogFeedBackFragment.getInstance().isShow()) {
                                    Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                                    if (vibrator != null) {
                                        vibrator.vibrate(500);
                                    }
                                    MyDialogFeedBackFragment.getInstance().show(getSupportFragmentManager(), "");
                                }
                            }
                        });
                    }
                }
            }
        });
        ShakeUtils.getInstance(this).onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ShakeUtils.getInstance(this).onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.onDestroy();
        }
        clearThread();
        ActivityStack.getScreenManager().popActivity(this);
    }

    public void clearThread(){
        for (Thread thread: threadList){
            if (thread != null){
                thread.interrupt();
            }
        }
    }
    /**
     * 默认失败返回
     * @param msg
     */
    public void onFail(String msg){
        ToastUtils.showSingleToast(msg);
        setLoadDialog(false);

    }

    /**
     * 重新登录错误
     * @param msg
     */
    public synchronized void onReLoginFail(String msg){
        if (isReLogining){
            return;
        }
        isReLogining = true;
        PushManager.getInstance().clearAllNotification(this).setTag(this,PushManager.NOMAL_ALIAS,PushManager.NOMAL_TAG);
        RxBus.getDefault().postSticky(new EventSticky("out"));
        Constant.userData = null;
        onFail(msg);
        SPUtils.setSharedStringData(getApplicationContext(),"token","");
        Intent i = getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        ActivityStack.getScreenManager().clearAllActivity();
        isReLogining = false;
    }
    /**
     * 默认成功返回
     * @param msg
     */
    public void onSuccess(String msg){
        ToastUtils.showSingleToast(msg);
        setLoadDialog(false);
    }


}
