package com.yw.gourmet.ui.share.raiders;

import android.support.v7.widget.RecyclerView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;

/**
 * auth: lewis-v
 * time: 2018/1/8.
 */

public class RaidersListShowFragment extends BaseFragment {
    private RecyclerView recycler_raiders;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_raiders_list;
    }

    @Override
    protected void initView() {
        recycler_raiders = view.findViewById(R.id.recycler_raiders);
    }
}
