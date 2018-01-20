package com.yw.gourmet.ui.collection;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
import com.yw.gourmet.dialog.MyDialogMoreFragment;
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

public class CollectionActivity extends BaseActivity<CollectionPresenter> implements CollectionContract.View
        ,OnRefreshListener,OnLoadMoreListener {
    private RecyclerView swipe_target;
    private SwipeToLoadLayout swipeToLoadLayout;
    private List<ShareListData<List<String>>> listData = new ArrayList<>();
    private ShareListAdapter adapter;
    private int type = Constant.CommentType.COMMENT;//显示类型,0,全部,1评论,2赞,3踩

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Override
    protected void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout)findViewById(R.id.swipeToLoadLayout);
        swipe_target = (RecyclerView)findViewById(R.id.swipe_target);
        swipe_target.setLayoutManager(new LinearLayoutManager(this));
        swipe_target.setItemAnimator(new DefaultItemAnimator());
        adapter = new ShareListAdapter(this,listData,getSupportFragmentManager());
        swipe_target.setAdapter(adapter);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        adapter.setListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                Intent intent = null;
                switch (listData.get(position).getType()) {
                    case Constant.TypeFlag.SHARE:
                        intent = new Intent(CollectionActivity.this, CommonDetailActivity.class);
                        break;
                    case Constant.TypeFlag.DIARY:
                        intent = new Intent(CollectionActivity.this, DiaryDetailActivity.class);
                        break;
                    case Constant.TypeFlag.MENU:
                        intent = new Intent(CollectionActivity.this, MenuDetailActivity.class);
                        break;
                    case Constant.TypeFlag.RAIDERS:
                        intent = new Intent(CollectionActivity.this, RaidersDetailActivity.class);
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
        adapter.setOnMoreListener(new OnMoreListener() {
            @Override
            public void OnMoreClick(View view, int position) {
                MyDialogMoreFragment myDialogMoreFragment = new MyDialogMoreFragment()
                        .setId(listData.get(position).getId());
                switch (listData.get(position).getType()) {
                    case Constant.TypeFlag.SHARE://普通分享
                        myDialogMoreFragment.setShare(false).setType(listData.get(position).getType())
                                .show(getSupportFragmentManager(), "share");
                        break;
                    case Constant.TypeFlag.DIARY://日记分享
                    case Constant.TypeFlag.MENU://食谱分享
                    case Constant.TypeFlag.RAIDERS://攻略分享
                        myDialogMoreFragment.setShareCoverUrl(listData.get(position).getCover())
                                .setType(listData.get(position).getType())
                                .setShareDescription(listData.get(position).getContent())
                                .setShareTitle(listData.get(position).getTitle())
                                .setShareUrl(Api.API_BASE_URL + "/Share/Other?id="
                                        + listData.get(position).getId() + "&type=" + listData.get(position).getType())
                                .show(getSupportFragmentManager(), "share");
                        break;
                }
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
                Intent intent = null;
                switch (listData.get(position).getType()) {
                    case Constant.TypeFlag.SHARE:
                        intent = new Intent(CollectionActivity.this, CommonDetailActivity.class);
                        break;
                    case Constant.TypeFlag.DIARY:
                        intent = new Intent(CollectionActivity.this, DiaryDetailActivity.class);
                        break;
                    case Constant.TypeFlag.MENU:
                        intent = new Intent(CollectionActivity.this, MenuDetailActivity.class);
                        break;
                    case Constant.TypeFlag.RAIDERS:
                        intent = new Intent(CollectionActivity.this, RaidersDetailActivity.class);
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
    public void onGetCollectionSuccess(BaseData<List<ShareListData<List<String>>>> model, LoadEnum flag) {
        if (flag == LoadEnum.REFRESH) {
//            listData.clear();
//            listData.addAll(model.getData());
            if ((model.getData() == null || model.getData().size() == 0) && listData.size()>0){
                ToastUtils.showSingleToast("已经是最新的啦");
            }else {
                for (int len = model.getData().size()-1;len >=0;len--){
                    listData.add(0,model.getData().get(len));
                    adapter.notifyItemInserted(0);
                }
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
    public void onGetCollectionFail(String msg, LoadEnum flag) {
        onFail(msg);
        if (flag == LoadEnum.REFRESH) {
            swipeToLoadLayout.setRefreshing(false);
        }else {
            swipeToLoadLayout.setLoadingMore(false);
        }
    }

    @Override
    public void onReMarkSuccess(BaseData<ShareListData<List<String>>> model, int position) {
        listData.set(position,model.getData());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMore() {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData == null?"0":Constant.userData.getToken());
        builder.addFormDataPart("type",String.valueOf(type));

        if (Constant.userData != null){
            builder.addFormDataPart("id",Constant.userData.getId());
        }else {
            ToastUtils.showSingleToast("请登陆后再进行操作");
        }
        if (listData.size()>0){
            builder.addFormDataPart("time_flag",listData.get(listData.size()-1).getTime_flag())
                    .addFormDataPart("act","-1");
        }
        mPresenter.getCollection(builder.build().parts(), LoadEnum.LOADMORE);
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
        builder.addFormDataPart("type",String.valueOf(type));
        if (Constant.userData != null){
            builder.addFormDataPart("id",Constant.userData.getId());
        }else {
            ToastUtils.showSingleToast("请登陆后再进行操作");
        }
        if (listData.size() > 0){
            builder.addFormDataPart("time_flag",listData.get(0).getTime_flag())
                    .addFormDataPart("act","1");
        }
        mPresenter.getCollection(builder.build().parts(), LoadEnum.REFRESH);
    }
}
