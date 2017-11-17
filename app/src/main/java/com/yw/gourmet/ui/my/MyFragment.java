package com.yw.gourmet.ui.my;

import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
import com.yw.gourmet.rxbus.RxBusSubscriber;
import com.yw.gourmet.rxbus.RxSubscriptions;
import com.yw.gourmet.ui.login.LoginActivity;
import com.yw.gourmet.ui.personal.PersonalActivity;
import com.yw.gourmet.ui.set.SetActivity;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/10/22.
 */

public class MyFragment extends BaseFragment implements View.OnClickListener{
    private LinearLayout ll_set;
    private ScrollView scroll_my;
    private TextView tv_nickname,tv_set;
    private Button bt_login,bt_register;
    private ConstraintLayout constraint_my;
    private Subscription mRxSubSticky;

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        ll_set = (LinearLayout)view.findViewById(R.id.ll_set);
        ll_set.setOnClickListener(this);

        constraint_my = (ConstraintLayout)view.findViewById(R.id.constraint_my);

        tv_nickname = (TextView)view.findViewById(R.id.tv_nickname);
        tv_set = (TextView)view.findViewById(R.id.tv_set);
        tv_set.setOnClickListener(this);

        bt_login = (Button)view.findViewById(R.id.bt_login);
        bt_register = (Button)view.findViewById(R.id.bt_register);
        bt_login.setOnClickListener(this);
        bt_register.setOnClickListener(this);

        scroll_my = (ScrollView)view.findViewById(R.id.scroll_my);
        if (Constant.userData!=null){
            constraint_my.setVisibility(View.GONE);
            scroll_my.setVisibility(View.VISIBLE);
            setData();
        }else {
            scroll_my.setVisibility(View.GONE);
            constraint_my.setVisibility(View.VISIBLE);
        }
        setRxBus();
    }

    /**
     * 设置个人信息参数显示
     */
    public void setData(){
        tv_nickname.setText(Constant.userData.getNike_name());
    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_set:
                startActivity(new Intent(getContext(), PersonalActivity.class));
                break;
            case R.id.bt_register:

                break;
            case R.id.bt_login:
                startActivity(new Intent(getContext(), LoginActivity.class));
                break;
            case R.id.tv_set:
                startActivity(new Intent(getContext(), SetActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constant.userData == null){
            scroll_my.setVisibility(View.GONE);
            constraint_my.setVisibility(View.VISIBLE);
        }else if (Constant.userData != null
                && scroll_my.getVisibility() != View.VISIBLE){
            scroll_my.setVisibility(View.VISIBLE);
            setData();
            constraint_my.setVisibility(View.GONE);
        }
    }

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
                            switch (eventSticky.event){
                                case "out":
                                    if (Constant.userData == null){
                                        scroll_my.setVisibility(View.GONE);
                                        constraint_my.setVisibility(View.VISIBLE);
                                    }else if (Constant.userData != null
                                            && scroll_my.getVisibility() != View.VISIBLE){
                                        scroll_my.setVisibility(View.VISIBLE);
                                        setData();
                                        constraint_my.setVisibility(View.GONE);
                                    }
                                    break;
                            }
                        }
                    });
            RxSubscriptions.add(mRxSubSticky);
        }
    }

}
