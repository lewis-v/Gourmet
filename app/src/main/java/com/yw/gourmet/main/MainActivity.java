package com.yw.gourmet.main;

import android.os.Bundle;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


}
