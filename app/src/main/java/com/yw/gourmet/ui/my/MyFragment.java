package com.yw.gourmet.ui.my;

import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
import com.yw.gourmet.rxbus.RxBusSubscriber;
import com.yw.gourmet.rxbus.RxSubscriptions;
import com.yw.gourmet.ui.commentMy.CommentMyActivity;
import com.yw.gourmet.ui.login.LoginActivity;
import com.yw.gourmet.ui.myShare.MyShareActivity;
import com.yw.gourmet.ui.personal.PersonalActivity;
import com.yw.gourmet.ui.set.SetActivity;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/10/22.
 */

public class MyFragment extends BaseFragment implements View.OnClickListener,MyContract.View{
    private LinearLayout ll_set,ll_share,ll_menu,ll_diary,ll_raiders,ll_comment;
    private ScrollView scroll_my;
    private TextView tv_nickname,tv_set,tv_diary,tv_menu,tv_raiders,tv_share;
    private Button bt_login,bt_register;
    private ConstraintLayout constraint_my;
    private Subscription mRxSubSticky;
    private ImageView img_header;

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);

        ll_share = view.findViewById(R.id.ll_share);
        ll_menu = view.findViewById(R.id.ll_menu);
        ll_diary = view.findViewById(R.id.ll_diary);
        ll_raiders = view.findViewById(R.id.ll_raiders);
        ll_set = (LinearLayout)view.findViewById(R.id.ll_set);
        ll_comment = view.findViewById(R.id.ll_comment);
        ll_set.setOnClickListener(this);
        ll_menu.setOnClickListener(this);
        ll_diary.setOnClickListener(this);
        ll_raiders.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        ll_comment.setOnClickListener(this);

        img_header = (ImageView) view.findViewById(R.id.img_header);

        constraint_my = (ConstraintLayout)view.findViewById(R.id.constraint_my);

        tv_nickname = (TextView)view.findViewById(R.id.tv_nickname);
        tv_diary = (TextView)view.findViewById(R.id.tv_diary);
        tv_menu = (TextView)view.findViewById(R.id.tv_menu);
        tv_raiders = (TextView)view.findViewById(R.id.tv_raiders);
        tv_set = (TextView)view.findViewById(R.id.tv_set);
        tv_share = view.findViewById(R.id.tv_share);
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
        tv_nickname.setText(Constant.userData.getNickname());
        Glide.with(this).load(Constant.userData.getImg_header()).into(img_header);
        tv_raiders.setText(String.valueOf(Constant.userData.getRaiders_num()));
        tv_menu.setText(String.valueOf(Constant.userData.getMenu_num()));
        tv_diary.setText(String.valueOf(Constant.userData.getDiary_num()));
        tv_share.setText(String.valueOf(Constant.userData.getCommon_num()));
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
        Intent intent = null;
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
            case R.id.ll_raiders:
                intent = new Intent(getContext(), MyShareActivity.class);
                intent.putExtra("type",Constant.TypeFlag.RAIDERS);
                break;
            case R.id.ll_share:
                intent = new Intent(getContext(), MyShareActivity.class);
                intent.putExtra("type",Constant.TypeFlag.SHARE);
                break;
            case R.id.ll_diary:
                intent = new Intent(getContext(), MyShareActivity.class);
                intent.putExtra("type",Constant.TypeFlag.DIARY);
                break;
            case R.id.ll_menu:
                intent = new Intent(getContext(), MyShareActivity.class);
                intent.putExtra("type",Constant.TypeFlag.MENU);
                break;
            case R.id.ll_comment:
                intent = new Intent(getContext(), CommentMyActivity.class);
                break;
        }
        if (intent != null){
            startActivity(intent);
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
