package com.yw.gourmet.ui.my;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.ui.personal.PersonalActivity;

/**
 * Created by Administrator on 2017/10/22.
 */

public class MyFragment extends BaseFragment implements View.OnClickListener{
    private LinearLayout ll_set;

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        ll_set = (LinearLayout)view.findViewById(R.id.ll_set);
        ll_set.setOnClickListener(this);
    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_set:
                startActivity(new Intent(getContext(), PersonalActivity.class));
                break;
        }
    }
}
