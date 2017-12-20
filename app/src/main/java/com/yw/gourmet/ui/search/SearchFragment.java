package com.yw.gourmet.ui.search;

import android.support.v7.widget.Toolbar;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;

/**
 * Created by Administrator on 2017/10/22.
 */

public class SearchFragment extends BaseFragment
{
    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }
}
