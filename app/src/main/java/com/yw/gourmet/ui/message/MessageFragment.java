package com.yw.gourmet.ui.message;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;

/**
 * Created by Administrator on 2017/10/22.
 */

public class MessageFragment extends BaseFragment<MessagePresenter> implements MessageContract.View {

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {

    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }
}
