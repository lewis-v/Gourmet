package com.yw.gourmet.ui.gourmet;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.ShareListAdapter;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by Administrator on 2017/10/22.
 */

public class GourmetFragment extends BaseFragment<GourmetPresenter> implements GourmetContract.View
        ,OnRefreshListener,OnLoadMoreListener{
    private RecyclerView swipe_target;
    private SwipeToLoadLayout swipeToLoadLayout;
    private List<ShareListData<String>> listData = new ArrayList<>();
    private ShareListAdapter adapter;

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout)view.findViewById(R.id.swipeToLoadLayout);
        swipe_target = (RecyclerView)view.findViewById(R.id.swipe_target);
        swipe_target.setLayoutManager(new LinearLayoutManager(getContext()));
        swipe_target.setItemAnimator(new DefaultItemAnimator());
        adapter = new ShareListAdapter(getContext(),listData);
        swipe_target.setAdapter(adapter);
        if (Constant.userData != null) {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .addFormDataPart("token", Constant.userData.getToken());
            mPresenter.load(builder.build().parts(), 0);
        }
    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onSuccess(String msg) {

    }

    @Override
    public void onLoadSuccess(BaseData<List<ShareListData<String>>> model, int flag) {
        Log.i("---success---",model.getData().toString());
        listData = model.getData();
        adapter.notifyDataSetChanged();
        swipeToLoadLayout.setRefreshing(false);
    }

    @Override
    public void onLoadFail(String msg, int flag) {
        onFail(msg);
        swipeToLoadLayout.setRefreshing(false);
    }

    @Override
    public void onLoadMore() {
        swipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void onRefresh() {
        if (Constant.userData != null) {
            Log.i("---refresh---","");
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .addFormDataPart("token", Constant.userData.getToken());
            mPresenter.load(builder.build().parts(), 0);
        }else {
            swipeToLoadLayout.setRefreshing(false);
        }
    }
}
