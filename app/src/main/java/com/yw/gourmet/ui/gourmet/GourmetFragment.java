package com.yw.gourmet.ui.gourmet;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.ShareListAdapter;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.listener.OnReMarkListener;
import com.yw.gourmet.myenum.LoadEnum;
import com.yw.gourmet.ui.detail.common.CommonDetailActivity;
import com.yw.gourmet.utils.ToastUtils;

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
    private List<ShareListData<List<String>>> listData = new ArrayList<>();
    private ShareListAdapter adapter;

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);

        swipeToLoadLayout = (SwipeToLoadLayout)view.findViewById(R.id.swipeToLoadLayout);
        swipe_target = (RecyclerView)view.findViewById(R.id.swipe_target);
        swipe_target.setLayoutManager(new LinearLayoutManager(getContext()));
        swipe_target.setItemAnimator(new DefaultItemAnimator());
        adapter = new ShareListAdapter(getContext(),listData,getFragmentManager());
        swipe_target.setAdapter(adapter);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        adapter.setListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                Intent intent = null;
                switch (listData.get(position).getType()) {
                    case Constant.TypeFlag.SHARE:
                        intent = new Intent(getContext(), CommonDetailActivity.class);
                        break;
                    case Constant.TypeFlag.DIARY:
                        break;
                    case Constant.TypeFlag.MENU:
                        break;
                    case Constant.TypeFlag.RAIDERS:
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
        adapter.setOnReMarkListener(new OnReMarkListener() {
            @Override
            public void OnGoodClick(View view, int position) {
                if (Constant.userData == null){
                    ToastUtils.showSingleToast("请登陆后再进行操作");
                }else {
                    MultipartBody.Builder builder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id", Constant.userData.getId())
                            .addFormDataPart("type",listData.get(position).getType()+"")
                            .addFormDataPart("act_id",listData.get(position).getId())
                            .addFormDataPart("act","1");
                    mPresenter.reMark(builder.build().parts(),position);
                }
            }

            @Override
            public void OnBadClick(View view, int position) {
                if (Constant.userData == null){
                    ToastUtils.showSingleToast("请登陆后再进行操作");
                }else {
                    MultipartBody.Builder builder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id", Constant.userData.getId())
                            .addFormDataPart("type",listData.get(position).getType()+"")
                            .addFormDataPart("act_id",listData.get(position).getId())
                            .addFormDataPart("act","0");
                    mPresenter.reMark(builder.build().parts(),position);
                }
            }

            @Override
            public void OnCommentClick(View view, int position) {

            }
        });
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        builder.addFormDataPart("token", Constant.userData == null ? "0" :  Constant.userData.getToken());
        mPresenter.load(builder.build().parts(), LoadEnum.REFRESH);
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
    public void onLoadSuccess(BaseData<List<ShareListData<List<String>>>> model, LoadEnum flag) {
        if (flag == LoadEnum.REFRESH) {
            Log.i("---list---",model.getData().toString());
            listData.clear();
            listData.addAll(model.getData());
            adapter.notifyDataSetChanged();
            swipeToLoadLayout.setRefreshing(false);
            if (listData.size()>0) {
                swipe_target.smoothScrollToPosition(0);
            }
        }else {
            swipeToLoadLayout.setLoadingMore(false);
        }
    }

    @Override
    public void onLoadFail(String msg, LoadEnum flag) {
        onFail(msg);
        if (flag == LoadEnum.REFRESH) {
            swipeToLoadLayout.setRefreshing(false);
        }else {
            swipeToLoadLayout.setLoadingMore(false);
        }
    }

    @Override
    public void onReMarkSuccess(BaseData<ShareListData<List<String>>> model,int position) {
        listData.set(position,model.getData());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMore() {
        swipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void onRefresh() {
        Log.i("---refresh---","");
        if (Constant.userData != null) {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("token", Constant.userData.getToken());
            mPresenter.load(builder.build().parts(), LoadEnum.REFRESH);
        }else {
            swipeToLoadLayout.setRefreshing(false);
        }
    }
}
