package com.yw.gourmet.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yw.gourmet.Constant;
import com.yw.gourmet.dialog.MyDialogLoadFragment;
import com.yw.gourmet.push.PushManager;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
import com.yw.gourmet.rxbus.RxBusSubscriber;
import com.yw.gourmet.rxbus.RxSubscriptions;
import com.yw.gourmet.utils.ToastUtils;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by yw on 2017-08-07.
 */

public abstract class BaseFragment<P extends BasePresenter> extends Fragment {
    protected P mPresenter;
    protected View view;
    protected Toolbar toolbar;
    protected View view_parent;
    private MyDialogLoadFragment myDialogLoadFragment;
    protected List<Thread> threadList = new ArrayList<>();
    private boolean isReLogining = false;
    private Subscription mRxSubSticky;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        try {
            //getGenericSuperclass获取类的超类的类型即<P>p的类型,ParameterizedType参数化类型
            // ,getActualTypeArguments返回表示此类型实际类型参数的 ToolType 对象的数组
            mPresenter = ((Class<P>) ((ParameterizedType) (getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[0])
                    .newInstance();

        } catch (java.lang.InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassCastException e) {
        }
        if (mPresenter != null){
            mPresenter.setContext(getContext());
            mPresenter.setmView(this);
        }
        view = inflater.inflate(getLayoutId(), container, false);
        initView();
        setToolbarTop();
        return view;
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
     *  设置加载中提示框的显示
     * @param isLoadDialog
     */
    public void setLoadDialog(boolean isLoadDialog){
        if (isLoadDialog){
            if (myDialogLoadFragment == null){
                myDialogLoadFragment = new MyDialogLoadFragment();
                myDialogLoadFragment.show(getActivity().getSupportFragmentManager(),"");
            }else{
                myDialogLoadFragment.dismiss();
                myDialogLoadFragment = new MyDialogLoadFragment();
                myDialogLoadFragment.show(getActivity().getSupportFragmentManager(),"");
            }
        }else{
            if (myDialogLoadFragment != null) {
                myDialogLoadFragment.dismiss();
                myDialogLoadFragment = null;
            }
        }
    }


    /**
     * 设置事件总线
     */
    public void setRxBus(){
        if (mRxSubSticky != null && !mRxSubSticky.isUnsubscribed()) {
            RxSubscriptions.remove(mRxSubSticky);
        } else {
            EventSticky s = RxBus.getDefault().getStickyEvent(EventSticky.class);
            Log.i("FFF", "获取到StickyEvent--->" + s);

            mRxSubSticky = RxBus.getDefault().toObservableSticky(EventSticky.class)
                    .flatMap(new Func1<EventSticky, Observable<EventSticky>>() {
                        @Override
                        public Observable<EventSticky> call(EventSticky eventSticky) {
                            return Observable.just(eventSticky)
                                    .map(new Func1<EventSticky, EventSticky>() {
                                        @Override
                                        public EventSticky call(EventSticky eventSticky) {
                                            // 这里模拟产生 Error
                                            return eventSticky;
                                        }
                                    })
                                    .doOnError(new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Log.e("FFF", "onError--Sticky");
                                        }
                                    })
                                    .onErrorResumeNext(Observable.<EventSticky>empty());
                        }
                    })
                    .subscribe(new RxBusSubscriber<EventSticky>() {
                        @Override
                        protected void onEvent(EventSticky eventSticky) {
                            Log.i("FFF", "onNext--Sticky-->" + eventSticky.event);
                            onGetEvent(eventSticky);
                        }
                    });
            RxSubscriptions.add(mRxSubSticky);
        }
    }

    /**
     * 接收到事件
     * @param eventSticky
     */
    public void onGetEvent(EventSticky eventSticky){}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.onDestroy();
        }
        clearThread();
        if (mRxSubSticky != null){
            mRxSubSticky.unsubscribe();
            RxSubscriptions.remove(mRxSubSticky);
            mRxSubSticky = null;
        }
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
    }

    /**
     * 默认成功返回
     * @param msg
     */
    public void onSuccess(String msg){
        ToastUtils.showSingleToast(msg);
        setLoadDialog(false);
    }

    /**
     * 重新登录错误
     * @param msg
     */
    public void onReLoginFail(String msg){
        if (isReLogining){
            return;
        }
        isReLogining = true;
        PushManager.getInstance().clearAllNotification(getContext()).setTag(getContext(),PushManager.NOMAL_ALIAS,PushManager.NOMAL_TAG);
        RxBus.getDefault().postSticky(new EventSticky("out"));
        Constant.userData = null;
        onFail(msg);
        Intent i = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        ActivityStack.getScreenManager().clearAllActivity();
        isReLogining = false;
    }
}
