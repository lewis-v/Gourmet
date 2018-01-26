package com.yw.gourmet.ui.setTop;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MySetTopAdapter;
import com.yw.gourmet.adapter.ShareListAdapter;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.myenum.LoadEnum;
import com.yw.gourmet.ui.detail.common.CommonDetailActivity;
import com.yw.gourmet.ui.detail.diary.DiaryDetailActivity;
import com.yw.gourmet.ui.detail.menu.MenuDetailActivity;
import com.yw.gourmet.ui.detail.raiders.RaidersDetailActivity;
import com.yw.gourmet.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyShareFragment extends BaseFragment<MySharePresenter> implements SetTopContract.View
        ,OnRefreshListener,OnLoadMoreListener {
    private RecyclerView swipe_target;
    private SwipeToLoadLayout swipeToLoadLayout;
    private MySetTopAdapter adapter;
    private List<ShareListData<List<String>>> listData = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_share2;
    }

    @Override
    protected void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout)view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipe_target = view.findViewById(R.id.swipe_target);
        swipe_target.setItemAnimator(new DefaultItemAnimator());
        swipe_target.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MySetTopAdapter(getContext(),listData);
        swipe_target.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                Intent intent = null;
                switch (listData.get(position).getType()) {
                    case Constant.TypeFlag.SHARE:
                        intent = new Intent(getContext(), CommonDetailActivity.class);
                        break;
                    case Constant.TypeFlag.DIARY:
                        intent = new Intent(getContext(), DiaryDetailActivity.class);
                        break;
                    case Constant.TypeFlag.MENU:
                        intent = new Intent(getContext(), MenuDetailActivity.class);
                        break;
                    case Constant.TypeFlag.RAIDERS:
                        intent = new Intent(getContext(), RaidersDetailActivity.class);
                        break;
                }
                if (intent != null){
                    intent.putExtra("id",listData.get(position).getId());
                    intent.putExtra("type",String.valueOf(listData.get(position).getType()));
                    startActivity(intent);
                }

            }

            @Override
            public boolean OnLongClick(View v, int position) {
                return false;
            }
        });
        refresh();
    }

    public MyShareFragment setListData(List<ShareListData<List<String>>> listData) {
        this.listData = listData;
        return this;
    }

    @Override
    public void onLoadMore() {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData == null?"0":Constant.userData.getToken());
        if (Constant.userData != null){
            builder.addFormDataPart("id",Constant.userData.getId());
        }else {
            ToastUtils.showSingleToast("请登陆后再进行操作");
        }
        if (listData.size()>0){
            builder.addFormDataPart("time_flag",listData.get(listData.size()-1).getTime_flag())
                    .addFormDataPart("act","-1");
        }
        mPresenter.getShareList(builder.build().parts(), LoadEnum.LOADMORE);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    /**
     * 刷新列表
     */
    public void refresh(){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData == null ? "0" :  Constant.userData.getToken());
        if (Constant.userData.getId() != null){
            builder.addFormDataPart("id",Constant.userData.getId());
        }else {
            ToastUtils.showSingleToast("请登陆后再进行操作");
        }
        if (Constant.userData != null){
            builder.addFormDataPart("user_id",Constant.userData.getId());
        }
        mPresenter.getShareList(builder.build().parts(), LoadEnum.REFRESH);
    }

    @Override
    public void onSetTopSuccess(String msg, int position) {

    }

    @Override
    public void onGetListSuccess(BaseData<List<ShareListData<List<String>>>> model, LoadEnum flag) {
        if (flag == LoadEnum.REFRESH) {
            if ((model.getData() == null || model.getData().size() == 0) && listData.size()>0){
                ToastUtils.showSingleToast("已经是最新的啦");
            }else {
                listData.clear();
                listData.addAll(model.getData());
                adapter.notifyDataSetChanged();
            }
            swipeToLoadLayout.setRefreshing(false);
            if (listData.size()>0) {
                swipe_target.smoothScrollToPosition(0);
            }
        }else {
            if (model.getData() == null || model.getData().size() == 0){
                ToastUtils.showSingleToast("没有更多啦");
            }else {
                for (int i = 0,len = model.getData().size();i<len;i++){
                    listData.add(model.getData().get(i));
                    adapter.notifyItemChanged(listData.size() -1);
                    if (listData.size()>=2){
                        adapter.notifyItemChanged(listData.size() -2);
                    }
                }
            }
            swipeToLoadLayout.setLoadingMore(false);
        }
    }

    @Override
    public void onGetListFail(String msg, LoadEnum flag) {
        onFail(msg);
        if (flag == LoadEnum.REFRESH) {
            swipeToLoadLayout.setRefreshing(false);
        }else {
            swipeToLoadLayout.setLoadingMore(false);
        }
    }

}
