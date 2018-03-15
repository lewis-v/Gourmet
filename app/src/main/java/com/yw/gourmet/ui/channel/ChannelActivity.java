package com.yw.gourmet.ui.channel;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyImgViewPagerAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.ui.main.MainActivity;
import com.yw.gourmet.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class ChannelActivity extends BaseActivity {
    private final String TAG = "ChannelActivity";
    private ViewPager viewpager;
    private MyImgViewPagerAdapter<View> adapter;
    private List<View> list = new ArrayList<>();
    private TextView tv_go;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_channel;
    }

    @Override
    protected void initView() {
        tv_go = findViewById(R.id.tv_go);

        viewpager = findViewById(R.id.viewpager);
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        GlideApp.with(this).load(R.mipmap.channel0).error(R.mipmap.load_fail).into(imageView);
        list.add(imageView);
        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        GlideApp.with(this).load(R.mipmap.channel1).error(R.mipmap.load_fail).into(imageView);
        list.add(imageView);
        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        GlideApp.with(this).load(R.mipmap.channel2).error(R.mipmap.load_fail).into(imageView);
        list.add(imageView);
        adapter = new MyImgViewPagerAdapter<>(list);
        viewpager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        viewpager.setCurrentItem(0);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 1){
                    if (tv_go.getVisibility() != View.VISIBLE) {
                        tv_go.setVisibility(View.VISIBLE);
                    }
                    try {
                        tv_go.setAlpha(positionOffset);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    tv_go.setOnClickListener(null);
                }else if (position == 0){
                    if (tv_go.getVisibility() == View.VISIBLE) {
                        tv_go.setVisibility(View.GONE);
                        tv_go.setOnClickListener(null);
                    }
                }else if (position == 2){
                    tv_go.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(ChannelActivity.this, MainActivity.class));
                            try {
                                SPUtils.setSharedBooleanData(ChannelActivity.this
                                ,getPackageManager().getPackageInfo(getPackageName(),0).versionName,true);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean isFeedBack() {
        return false;
    }
}
