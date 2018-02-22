package com.yw.gourmet.ui.reject;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.ShareListAdapter;
import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.dialog.MyDialogAppealFragment;
import com.yw.gourmet.dialog.MyDialogMoreFragment;
import com.yw.gourmet.dialog.MyDialogTipFragment;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.listener.OnMoreListener;
import com.yw.gourmet.listener.OnReMarkListener;
import com.yw.gourmet.myenum.LoadEnum;
import com.yw.gourmet.ui.detail.common.CommonDetailActivity;
import com.yw.gourmet.ui.detail.diary.DiaryDetailActivity;
import com.yw.gourmet.ui.detail.menu.MenuDetailActivity;
import com.yw.gourmet.ui.detail.raiders.RaidersDetailActivity;
import com.yw.gourmet.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

public class RejectActivity extends BaseActivity<RejectPresenter> implements RejectContract.View
        ,View.OnClickListener, OnRefreshListener, OnLoadMoreListener {
    private RecyclerView swipe_target;
    private SwipeToLoadLayout swipeToLoadLayout;
    private ImageView img_back;
    private TextView tv_clear;
    private LinearLayout ll_nothing;
    private List<ShareListData<List<String>>> listData = new ArrayList<>();
    private ShareListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reject;
    }

    @Override
    protected void initView() {
        ll_nothing = findViewById(R.id.ll_nothing);
        img_back = findViewById(R.id.img_back);
        tv_clear = findViewById(R.id.tv_clear);

        img_back.setOnClickListener(this);
        tv_clear.setOnClickListener(this);

        swipe_target = findViewById(R.id.swipe_target);
        swipeToLoadLayout = findViewById(R.id.swipeToLoadLayout);
        swipe_target.setLayoutManager(new LinearLayoutManager(RejectActivity.this));
        swipe_target.setItemAnimator(new DefaultItemAnimator());
        adapter = new ShareListAdapter(RejectActivity.this,listData,getSupportFragmentManager());
        swipe_target.setAdapter(adapter);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        adapter.setListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                Intent intent = null;
                switch (listData.get(position).getType()) {
                    case Constant.TypeFlag.SHARE:
                        intent = new Intent(RejectActivity.this, CommonDetailActivity.class);
                        break;
                    case Constant.TypeFlag.DIARY:
                        intent = new Intent(RejectActivity.this, DiaryDetailActivity.class);
                        break;
                    case Constant.TypeFlag.MENU:
                        intent = new Intent(RejectActivity.this, MenuDetailActivity.class);
                        break;
                    case Constant.TypeFlag.RAIDERS:
                        intent = new Intent(RejectActivity.this, RaidersDetailActivity.class);
                        break;
                }
                if (intent != null){
                    intent.putExtra("id",listData.get(position).getId());
                    intent.putExtra("type",String.valueOf(listData.get(position).getType()));
                    startActivity(intent);
                }

            }

            @Override
            public boolean OnLongClick(View v, final int position) {
                new MyDialogTipFragment().setTextEnter("删除")
                        .setShowText("是否删除此条分享信息")
                        .setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                            @Override
                            public void OnEnter(String Tag) {
                                mPresenter.rejectDelete(new MultipartBody.Builder().setType(MultipartBody.FORM)
                                        .addFormDataPart("id",Constant.userData.getUser_id())
                                        .addFormDataPart("type", String.valueOf(listData.get(position).getType()))
                                        .addFormDataPart("act_id",listData.get(position).getId())
                                        .build().parts(),-1);
                            }
                        }).show(getSupportFragmentManager(),"delete");
                return true;
            }
        });
        adapter.setOnMoreListener(new OnMoreListener() {
            @Override
            public void OnMoreClick(View view, int position) {
                new MyDialogAppealFragment().setRejectCause(listData.get(position).getCause())
                        .setActId(listData.get(position).getId()).setType(String.valueOf(listData.get(position).getType()))
                        .show(getSupportFragmentManager(),"appeal");
            }
        });
        adapter.setOnReMarkListener(new OnReMarkListener() {
            @Override
            public void OnGoodClick(View view, int position) {
                if (Constant.userData == null){
                    ToastUtils.showSingleToast("请登陆后再进行操作");
                }else {
                    ToastUtils.showSingleToast("分享已违规,不可评论");
                }
            }

            @Override
            public void OnBadClick(View view, int position) {
                if (Constant.userData == null){
                    ToastUtils.showSingleToast("请登陆后再进行操作");
                }else {
                   ToastUtils.showSingleToast("分享已违规,不可评论");
                }
            }

            @Override
            public void OnCommentClick(View view, int position) {
                Intent intent = null;
                switch (listData.get(position).getType()) {
                    case Constant.TypeFlag.SHARE:
                        intent = new Intent(RejectActivity.this, CommonDetailActivity.class);
                        break;
                    case Constant.TypeFlag.DIARY:
                        intent = new Intent(RejectActivity.this, DiaryDetailActivity.class);
                        break;
                    case Constant.TypeFlag.MENU:
                        intent = new Intent(RejectActivity.this, MenuDetailActivity.class);
                        break;
                    case Constant.TypeFlag.RAIDERS:
                        intent = new Intent(RejectActivity.this, RaidersDetailActivity.class);
                        break;
                }
                if (intent != null){
                    intent.putExtra("id",listData.get(position).getId());
                    intent.putExtra("type",String.valueOf(listData.get(position).getType()));
                    intent.putExtra("isComment",true);
                    startActivity(intent);
                }
            }
        });

        refresh();
    }

    @Override
    public void onGetRejectSuccess(BaseData<List<ShareListData<List<String>>>> model,LoadEnum flag) {
        if (flag == LoadEnum.REFRESH) {
            if (model.getData() == null || model.getData().size() == 0){
//                ToastUtils.showSingleToast("已经是最新的啦");
                ll_nothing.setVisibility(View.VISIBLE);
            }else {
                ll_nothing.setVisibility(View.GONE);
                if (model.getData().size() < 10){
                    adapter.setEnd(true);
                }else {
                    adapter.setEnd(false);
                }
                adapter.notifyDataSetChanged();
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
                if (listData.size() > 0) {
                    ToastUtils.showSingleToast("没有更多啦");
                    adapter.setEnd(true);
                    adapter.notifyDataSetChanged();
                }else {
                    ll_nothing.setVisibility(View.VISIBLE);
                }
            }else {
                ll_nothing.setVisibility(View.GONE);
                if (model.getData().size() < 10){
                    adapter.setEnd(true);
                }else {
                    adapter.setEnd(false);
                }
                adapter.notifyDataSetChanged();
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

    @Override
    public void onRejectDeleteSuccess(BaseData model, int position) {
        if (position == -1){
            listData.clear();
            adapter.notifyDataSetChanged();
            refresh();
        }else if (position >= listData.size()){
            listData.clear();
            adapter.notifyDataSetChanged();
            refresh();
        }else {
            listData.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_clear:
                if (listData.size()>0) {
                    new MyDialogTipFragment().setShowText("是否清空被拒绝分享列表?")
                            .setTextEnter("清空").setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                        @Override
                        public void OnEnter(String Tag) {
                            mPresenter.rejectDelete(new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("id", Constant.userData.getUser_id())
                                    .addFormDataPart("type", "all").build().parts(), -1);
                        }
                    }).show(getSupportFragmentManager(), "clear");
                }else {
                    ToastUtils.showSingleToast("列表里什么都没有哟");
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onLoadMore() {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", Constant.userData.getUser_id())
                .addFormDataPart("user_id",Constant.userData.getUser_id())
                .addFormDataPart("status","0");
        if (listData.size()>0){
            builder.addFormDataPart("time_flag",listData.get(listData.size()-1).getTime_flag())
                    .addFormDataPart("act","-1");
        }
        mPresenter.getRejectList(builder.build().parts(),LoadEnum.LOADMORE);
    }

    /**
     * 刷新列表
     */
    public void refresh(){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", Constant.userData.getUser_id())
                .addFormDataPart("user_id",Constant.userData.getUser_id())
                .addFormDataPart("status","0");
        mPresenter.getRejectList(builder.build().parts(), LoadEnum.REFRESH);
    }
}
