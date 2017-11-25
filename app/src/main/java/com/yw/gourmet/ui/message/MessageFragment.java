package com.yw.gourmet.ui.message;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MessageListAdapter;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.swipAnim.LoadMoreFooterView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/22.
 */

public class MessageFragment extends BaseFragment<MessagePresenter> implements MessageContract.View
        ,OnRefreshListener{
    private RecyclerView swipe_target;
    private SwipeToLoadLayout swipeToLoadLayout;
    private List<MessageListData> listData = new ArrayList<>();
    private MessageListAdapter adapter;

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        swipe_target = (RecyclerView)view.findViewById(R.id.swipe_target);
        swipe_target.setItemAnimator(new DefaultItemAnimator());
        swipe_target.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageListAdapter(getContext(),listData);
        swipe_target.setAdapter(adapter);

        swipeToLoadLayout = (SwipeToLoadLayout)view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
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
        listData.add(new MessageListData("11","11","11","11","http://172.16.113.114:47423/img/def_personal_back.jpg"));
        adapter.notifyDataSetChanged();
        swipeToLoadLayout.setRefreshing(false);
    }
}
