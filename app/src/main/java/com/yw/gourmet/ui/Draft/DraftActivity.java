package com.yw.gourmet.ui.Draft;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.DraftAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.dao.data.SaveData;
import com.yw.gourmet.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class DraftActivity extends BaseActivity<DraftPresenter> implements DraftContract.View
,OnRefreshListener {
    private List<SaveData> data = new ArrayList<>();
    private RecyclerView swipe_target;
    private SwipeToLoadLayout swipeToLoadLayout;
    private LinearLayout ll_back;
    private DraftAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_draft;
    }

    @Override
    protected void initView() {
        setLoadDialog(true);
        ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeToLoadLayout = findViewById(R.id.swipeToLoadLayout);
        swipe_target = findViewById(R.id.swipe_target);
        swipe_target.setLayoutManager(new LinearLayoutManager(this));
        swipe_target.setItemAnimator(new DefaultItemAnimator());
        adapter = new DraftAdapter(this,data);
        swipe_target.setAdapter(adapter);
        swipeToLoadLayout.setOnRefreshListener(this);
        mPresenter.getAllSaveData();
    }

    @Override
    public void onGetSuccess(List<SaveData> model) {
        data.clear();
        data.addAll(model);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                setLoadDialog(false);
                swipeToLoadLayout.setRefreshing(false);
                if (data.size() == 0){
                    ToastUtils.showSingleToast("草稿箱里什么都没有哟");
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
}
