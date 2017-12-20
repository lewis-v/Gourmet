package com.yw.gourmet.ui.set;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;

public class SetActivity extends BaseActivity implements View.OnClickListener{
    private LinearLayout ll_back;
    private TextView tv_out;

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ll_back = (LinearLayout)findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);

        tv_out = (TextView)findViewById(R.id.tv_out);
        tv_out.setOnClickListener(this);
    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_set;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_out:
                onReLoginFail("退出账号");
                break;
        }
    }
}
