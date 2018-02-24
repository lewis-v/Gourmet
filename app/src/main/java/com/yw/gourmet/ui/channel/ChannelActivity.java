package com.yw.gourmet.ui.channel;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyImgViewPagerAdapter;
import com.yw.gourmet.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ChannelActivity extends BaseActivity {
    private ViewPager viewpager;
    private MyImgViewPagerAdapter<View> adapter;
    private List<View> list = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_channel;
    }

    @Override
    protected void initView() {
        viewpager = findViewById(R.id.viewpager);
        adapter = new MyImgViewPagerAdapter<>(list);
        viewpager.setAdapter(adapter);
    }
}
