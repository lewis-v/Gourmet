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

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MessageListAdapter;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.center.MessageCenter;
import com.yw.gourmet.center.event.IMessageGet;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
import com.yw.gourmet.rxbus.RxBusSubscriber;
import com.yw.gourmet.rxbus.RxSubscriptions;
import com.yw.gourmet.ui.chat.ChatActivity;
import com.yw.gourmet.ui.login.LoginActivity;
import com.yw.gourmet.ui.registered.RegisteredActivity;

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
    private IMessageGet iMessageGet;

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
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                String put_id,get_id;
                if (Constant.userData.getUser_id().equals(listData.get(position).getPut_id())){
                    get_id = listData.get(position).getGet_id();
                }else {
                    get_id = listData.get(position).getPut_id();
                }
                put_id = Constant.userData.getUser_id();
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("put_id",put_id);
                intent.putExtra("get_id",get_id);
                intent.putExtra("nickname",listData.get(position).getNickname());
                startActivity(intent);
            }

            @Override
            public boolean OnLongClick(View v, int position) {
                return false;
            }
        });

        swipeToLoadLayout = (SwipeToLoadLayout)view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);

        ll_message = (LinearLayout)view.findViewById(R.id.ll_message);

        bt_register = (Button)view.findViewById(R.id.bt_register_message);
        bt_login = (Button)view.findViewById(R.id.bt_login_message);
        bt_register.setOnClickListener(this);
        bt_login.setOnClickListener(this);

        constraint_message = (ConstraintLayout)view.findViewById(R.id.constraint_message);
        setRxBus();
        iMessageGet = new IMessageGet() {
            @Override
            public boolean onGetMessage(MessageListData message) {
                for (int len = listData.size(),num = 0;num < len;num++){
                    if(listData.get(num).getPut_id().equals(message.getPut_id())
                            || listData.get(num).getGet_id().equals(message.getPut_id())){//消息是这里的
                        listData.get(num).setContent(message.getContent())
                                .setPut_time(message.getPut_time())
                                .addUnReadNum();
                        final int finalNum = num;
                        swipe_target.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemChanged(finalNum);
                            }
                        });
                    }
                }
                return false;
            }
        };
        MessageCenter.getInstance().addMessageHandle(iMessageGet);
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
        refresh();
    }

    public void refresh(){
        if (Constant.userData != null) {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", Constant.userData.getUser_id());
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
                startActivity(new Intent(getContext(), RegisteredActivity.class));
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
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iMessageGet != null){
            MessageCenter.getInstance().removeMessageHandle(iMessageGet);
        }
    }

    @Override
    public void onGetEvent(EventSticky eventSticky) {
        super.onGetEvent(eventSticky);
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
}
