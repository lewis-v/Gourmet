package com.yw.gourmet.ui.message;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MessageListAdapter;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
import com.yw.gourmet.rxbus.RxBusSubscriber;
import com.yw.gourmet.rxbus.RxSubscriptions;
import com.yw.gourmet.swipAnim.LoadMoreFooterView;
import com.yw.gourmet.ui.login.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/10/22.
 */

public class MessageFragment extends BaseFragment<MessagePresenter> implements MessageContract.View
        ,OnRefreshListener,View.OnClickListener{
    private RecyclerView swipe_target;
    private SwipeToLoadLayout swipeToLoadLayout;
    private List<MessageListData> listData = new ArrayList<>();
    private MessageListAdapter adapter;
    private LinearLayout ll_message;
    private ConstraintLayout constraint_message;
    private Button bt_login,bt_register;
    private Subscription mRxSubSticky;

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);

        swipe_target = (RecyclerView)view.findViewById(R.id.swipe_target);
        swipe_target.setItemAnimator(new DefaultItemAnimator());
        swipe_target.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageListAdapter(getContext(),listData);
        swipe_target.setAdapter(adapter);

        swipeToLoadLayout = (SwipeToLoadLayout)view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);

        ll_message = (LinearLayout)view.findViewById(R.id.ll_message);

        bt_register = (Button)view.findViewById(R.id.bt_register_message);
        bt_login = (Button)view.findViewById(R.id.bt_login_message);
        bt_register.setOnClickListener(this);
        bt_login.setOnClickListener(this);

        constraint_message = (ConstraintLayout)view.findViewById(R.id.constraint_message);
        if (Constant.userData == null){
            constraint_message.setVisibility(View.VISIBLE);
            ll_message.setVisibility(View.GONE);
        }else {
            constraint_message.setVisibility(View.GONE);
            ll_message.setVisibility(View.VISIBLE);
            swipeToLoadLayout.setRefreshing(true);
        }
        setRxBus();

    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }



    @Override
    public void onRefresh() {
        if (Constant.userData != null) {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                .addFormDataPart("id", Constant.userData.getId());
            mPresenter.LoadMessageList(builder.build().parts());
        }else {
            swipeToLoadLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoadMessageListSuccess(BaseData<List<MessageListData>> model) {
        listData.clear();
        listData.addAll(model.getData());
        adapter.notifyDataSetChanged();
        swipeToLoadLayout.setRefreshing(false);
    }

    @Override
    public void onFail(String msg) {
        super.onFail(msg);
        swipeToLoadLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_register_message:

                break;
            case R.id.bt_login_message:
                startActivity(new Intent(getContext(), LoginActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constant.userData == null){
            constraint_message.setVisibility(View.VISIBLE);
            ll_message.setVisibility(View.GONE);
        }else {
            constraint_message.setVisibility(View.GONE);
            ll_message.setVisibility(View.VISIBLE);
            swipeToLoadLayout.setRefreshing(true);
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
                                        constraint_message.setVisibility(View.VISIBLE);
                                        ll_message.setVisibility(View.GONE);
                                    }else {
                                        constraint_message.setVisibility(View.GONE);
                                        ll_message.setVisibility(View.VISIBLE);
                                        swipeToLoadLayout.setRefreshing(true);
                                    }
                                    break;
                            }
                        }
                    });
            RxSubscriptions.add(mRxSubSticky);
        }
    }
}
