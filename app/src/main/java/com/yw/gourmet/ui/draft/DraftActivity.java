package com.yw.gourmet.ui.draft;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.DraftAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.dao.data.saveData.SaveData;
import com.yw.gourmet.dao.data.saveData.SaveDataUtil;
import com.yw.gourmet.dialog.MyDialogDraftFragment;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.listener.OnOtherClickListener;
import com.yw.gourmet.ui.share.common.CommonShareActivity;
import com.yw.gourmet.ui.share.diary.DiaryActivity;
import com.yw.gourmet.ui.share.menu.MenuActivity;
import com.yw.gourmet.ui.share.raiders.RaidersActivity;
import com.yw.gourmet.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class DraftActivity extends BaseActivity<DraftPresenter> implements DraftContract.View
        ,OnRefreshListener {
    private List<SaveData> data = new ArrayList<>();
    private RecyclerView swipe_target;
    private SwipeToLoadLayout swipeToLoadLayout;
    private LinearLayout ll_back,ll_nothing;
    private TextView tv_nothing;
    private DraftAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_draft;
    }

    @Override
    protected void initView() {
        setLoadDialog(true);
        ll_nothing = findViewById(R.id.ll_nothing);
        ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_nothing = findViewById(R.id.tv_nothing);

        swipeToLoadLayout = findViewById(R.id.swipeToLoadLayout);
        swipe_target = findViewById(R.id.swipe_target);
        swipe_target.setLayoutManager(new LinearLayoutManager(this));
        swipe_target.setItemAnimator(new DefaultItemAnimator());
        adapter = new DraftAdapter(this,data);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                Intent intent = null;
                switch (data.get(position).getType()){
                    case Constant.TypeFlag.DIARY:
                        intent = new Intent(DraftActivity.this, DiaryActivity.class);
                        break;
                    case Constant.TypeFlag.MENU:
                        intent = new Intent(DraftActivity.this, MenuActivity.class);
                        break;
                    case Constant.TypeFlag.RAIDERS:
                        intent = new Intent(DraftActivity.this, RaidersActivity.class);
                        break;
                    case Constant.TypeFlag.SHARE:
                        intent = new Intent(DraftActivity.this, CommonShareActivity.class);
                        break;
                }
                if (intent != null){
                    intent.putExtra("type","change");
                    intent.putExtra("_id",data.get(position).get_id());
                    startActivity(intent);
                }
            }

            @Override
            public boolean OnLongClick(View v, int position) {
                return false;
            }
        });
        adapter.setOnOtherClickListener(new OnOtherClickListener() {
            @Override
            public void onOther(View view, final int position) {
                new MyDialogDraftFragment().setOnDeleteListener(new MyDialogDraftFragment.OnDeleteListener() {
                    @Override
                    public void onDelete(String Tag) {
                        SaveDataUtil.delete(data.get(position).get_id());
                        data.remove(position);
                        adapter.notifyItemRemoved(position);
                    }

                    @Override
                    public void onDeleteAll(String Tag) {
                        SaveDataUtil.clearAll();
                        while (data.size()>0){
                            data.remove(0);
                            adapter.notifyItemRemoved(0);
                        }
                    }
                }).show(getSupportFragmentManager(),"delete");
            }
        });
        swipe_target.setAdapter(adapter);
        swipeToLoadLayout.setOnRefreshListener(this);
    }

    @Override
    public void onGetSuccess(final List<SaveData> model) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoadDialog(false);
                swipeToLoadLayout.setRefreshing(false);
                if (model == null || model.size()==0){
                    ll_nothing.setVisibility(View.VISIBLE);
                    tv_nothing.setText("草稿箱里什么都没有哟");
                }else {
                    ll_nothing.setVisibility(View.GONE);
                    data.clear();
                    data.addAll(model);
                    adapter.notifyDataSetChanged();
                    if (data.size() == 0) {
                        ToastUtils.showSingleToast("草稿箱里什么都没有哟");
                    }
                }
            }
        });
    }

    @Override
    public void onGetFail(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoadDialog(false);
                ToastUtils.showSingleToast("加载失败");
                swipeToLoadLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        mPresenter.getAllSaveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getAllSaveData();
    }
}
