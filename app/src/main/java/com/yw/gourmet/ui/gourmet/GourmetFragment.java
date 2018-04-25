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
import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.dialog.MyDialogMoreFragment;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.listener.OnMoreListener;
import com.yw.gourmet.listener.OnReMarkListener;
import com.yw.gourmet.myenum.LoadEnum;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
import com.yw.gourmet.rxbus.RxBusSubscriber;
import com.yw.gourmet.rxbus.RxSubscriptions;
import com.yw.gourmet.ui.detail.common.CommonDetailActivity;
import com.yw.gourmet.ui.detail.diary.DiaryDetailActivity;
import com.yw.gourmet.ui.detail.menu.MenuDetailActivity;
import com.yw.gourmet.ui.detail.raiders.RaidersDetailActivity;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.widget.YWRecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.yw.gourmet.Constant.CommentType.BAD;
import static com.yw.gourmet.Constant.CommentType.COMMENT;
import static com.yw.gourmet.Constant.CommentType.GOOD;

/**
 * Created by Administrator on 2017/10/22.
 */

public class GourmetFragment extends BaseFragment<GourmetPresenter> implements GourmetContract.View
        ,OnRefreshListener{
    private YWRecyclerView swipe_target;
    private SwipeToLoadLayout swipeToLoadLayout;
    private List<ShareListData<List<String>>> listData = new ArrayList<>();
    private ShareListAdapter adapter;
    private boolean isLoadMore = false;//是否在加载更多中
    private Subscription mRxSubSticky;

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);

        swipeToLoadLayout = (SwipeToLoadLayout)view.findViewById(R.id.swipeToLoadLayout);
        swipe_target = view.findViewById(R.id.swipe_target);
        swipe_target.setLayoutManager(new LinearLayoutManager(getContext()));
        swipe_target.setItemAnimator(new DefaultItemAnimator());
        adapter = new ShareListAdapter(getContext(),listData,getFragmentManager());
        swipe_target.setAdapter(adapter);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipe_target.setOnScrollListener(new YWRecyclerView.OnScrollListener() {
            @Override
            public void onLoadFirst() {

            }

            @Override
            public void onLoadLast() {
                if (!adapter.isEnd() && !isLoadMore) {
                    isLoadMore = true;
                    MultipartBody.Builder builder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("token", Constant.userData == null ? "0" : Constant.userData.getToken());
                    if (listData.size() > 0) {
                        builder.addFormDataPart("time_flag", listData.get(listData.size() - 1).getTime_flag())
                                .addFormDataPart("act", "-1");
                    }

                    if (Constant.userData != null) {
                        builder.addFormDataPart("user_id", Constant.userData.getUser_id());
                    }
                    mPresenter.load(builder.build().parts(), LoadEnum.LOADMORE);
                }
            }
        });
        adapter.setListener(new OnItemClickListener() {
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
        adapter.setOnMoreListener(new OnMoreListener() {
            @Override
            public void OnMoreClick(View view, int position) {
                MyDialogMoreFragment myDialogMoreFragment = new MyDialogMoreFragment()
                        .setId(listData.get(position).getId());
                switch (listData.get(position).getType()) {
                    case Constant.TypeFlag.SHARE://普通分享
                        myDialogMoreFragment.setShare(false).setType(listData.get(position).getType())
                                .show(getFragmentManager(), "share");
                        break;
                    case Constant.TypeFlag.DIARY://日记分享
                    case Constant.TypeFlag.MENU://食谱分享
                    case Constant.TypeFlag.RAIDERS://攻略分享
                        myDialogMoreFragment.setShareCoverUrl(listData.get(position).getCover())
                                .setId(listData.get(position).getId())
                                .setType(listData.get(position).getType())
                                .setShareDescription(listData.get(position).getContent())
                                .setShareTitle(listData.get(position).getTitle())
                                .setShareUrl(Api.API_BASE_URL + "/Share/Other?id="
                                        + listData.get(position).getId() + "&type=" + listData.get(position).getType())
                                .show(getFragmentManager(), "share");
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
                            .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                            .addFormDataPart("id", Constant.userData.getUser_id())
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
                            .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                            .addFormDataPart("id", Constant.userData.getUser_id())
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
                    intent.putExtra("isComment",true);
                    startActivity(intent);
                }
            }
        });
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        builder.addFormDataPart("token", Constant.userData == null ? "0" :  Constant.userData.getToken());
        if (Constant.userData != null){
            builder.addFormDataPart("user_id",Constant.userData.getUser_id());
        }
        mPresenter.load(builder.build().parts(), LoadEnum.REFRESH);
        setRxBus();
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
        super.onSuccess(msg);
    }

    @Override
    public void onLoadSuccess(BaseData<List<ShareListData<List<String>>>> model, LoadEnum flag) {
        if (flag == LoadEnum.REFRESH) {
            if ((model.getData() == null || model.getData().size() == 0) && listData.size()>0){
//                ToastUtils.showSingleToast("已经是最新的啦");
            }else {
                if (model.getData().size() < 10){
                    adapter.setEnd(true);
                }else {
                    adapter.setEnd(false);
                }
                listData.clear();
                listData.addAll(model.getData());
                adapter.setEnd(false);
                adapter.notifyDataSetChanged();
            }
            swipeToLoadLayout.setRefreshing(false);
            if (listData.size()>0) {
                swipe_target.smoothScrollToPosition(0);
            }
        }else {
            isLoadMore = false;
            if (model.getData() == null || model.getData().size() == 0){
                ToastUtils.showSingleToast("没有更多啦");
                adapter.setEnd(true);
                adapter.notifyItemRangeChanged(listData.size()-1,adapter.getItemCount() - listData.size()-1);
            }else {
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
    public void onLoadFail(String msg, LoadEnum flag) {
        super.onFail(msg);
        if (flag == LoadEnum.REFRESH) {
            swipeToLoadLayout.setRefreshing(false);
        }else {
            isLoadMore = false;
            swipeToLoadLayout.setLoadingMore(false);
        }
    }

    @Override
    public void onReMarkSuccess(BaseData<ShareListData<List<String>>> model,int position) {
        int pos = adapter.getPosition(model.getData().getType(),model.getData().getId());
        position = pos;
        listData.set(position,model.getData());
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    /**
     * 刷新
     */
    public void refresh(){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData == null?"0":Constant.userData.getToken());
        if (Constant.userData != null){
            builder.addFormDataPart("user_id",Constant.userData.getUser_id());
        }
        mPresenter.load(builder.build().parts(), LoadEnum.REFRESH);
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
                                case "change_detail"://修改个人信息
                                case "gourmet_refresh"://刷新分享全部
                                    refresh();
                                    break;
                                case "refresh_one"://刷新点赞评价
                                    int pos = adapter.getPosition(eventSticky.type,eventSticky.id);
                                    if (pos > -1){
                                        listData.get(pos).setGood_num(eventSticky.good).setBad_num(eventSticky.bad)
                                                .setComment_num(eventSticky.comment);
                                        if (eventSticky.act == GOOD){
                                            listData.get(pos).setGood_act("1");
                                        }else if(eventSticky.act == BAD){
                                            listData.get(pos).setGood_act("0");
                                        }
                                        adapter.notifyItemChanged(pos);
                                    }
                                    break;
                                case "refresh_comment"://刷新评论
                                    int position = adapter.getPosition(eventSticky.type,eventSticky.id);
                                    if (position > -1){
                                        listData.get(position).setComment_num(eventSticky.comment);
                                        if (eventSticky.act == COMMENT){
                                            listData.get(position).setIs_comment("ok");
                                        }
                                        adapter.notifyItemChanged(position);
                                    }
                                    break;
                            }
                        }
                    });
            RxSubscriptions.add(mRxSubSticky);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRxSubSticky != null) {
            mRxSubSticky.unsubscribe();
            RxSubscriptions.remove(mRxSubSticky);
            mRxSubSticky = null;
        }
    }
}
