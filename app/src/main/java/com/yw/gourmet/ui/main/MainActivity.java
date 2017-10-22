package com.yw.gourmet.ui.main;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyFragmentAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.ui.gourmet.GourmetFragment;
import com.yw.gourmet.ui.message.MessageFragment;
import com.yw.gourmet.ui.my.MyFragment;
import com.yw.gourmet.ui.search.SearchFragment;
import com.yw.gourmet.widget.DepthPageTransformer;
import com.yw.gourmet.widget.MyViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View,View.OnClickListener{
    private LinearLayout ll_menu,ll_home,ll_message,ll_search,ll_my,ll_add;
    private ImageView img_home,img_message,img_search,img_my;
    private MyViewPager viewpager;
    private MyFragmentAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private int position = 0;//目前选择的功能位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        ll_menu = (LinearLayout)findViewById(R.id.ll_menu);
        ll_home = (LinearLayout)findViewById(R.id.ll_home);
        ll_message = (LinearLayout)findViewById(R.id.ll_message);
        ll_search = (LinearLayout)findViewById(R.id.ll_search);
        ll_my = (LinearLayout)findViewById(R.id.ll_my);
        ll_add = (LinearLayout)findViewById(R.id.ll_add);

        ll_home.setOnClickListener(this);
        ll_message.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        ll_my.setOnClickListener(this);
        ll_add.setOnClickListener(this);

        img_home = (ImageView)findViewById(R.id.img_home);
        img_message = (ImageView)findViewById(R.id.img_message);
        img_my = (ImageView)findViewById(R.id.img_my);
        img_search = (ImageView)findViewById(R.id.img_search);


        fragmentList.add(new GourmetFragment());
        fragmentList.add(new MessageFragment());
        fragmentList.add(new SearchFragment());
        fragmentList.add(new MyFragment());
        viewpager = (MyViewPager)findViewById(R.id.viewpager);
        adapter = new MyFragmentAdapter(getSupportFragmentManager(),fragmentList);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);
        viewpager.setPageTransformer(true,new DepthPageTransformer());

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_home:
                if (position != 0) {
                    changeIcon(position);
                    viewpager.setCurrentItem(0);
                    img_home.setImageResource(R.drawable.home_choose);
                    position = 0;
                }
                break;
            case R.id.ll_message:
                if (position != 1) {
                    changeIcon(position);
                    viewpager.setCurrentItem(1);
                    img_message.setImageResource(R.drawable.message_choose);
                    position = 1;
                }
                break;
            case R.id.ll_search:
                if (position != 2) {
                    changeIcon(position);
                    viewpager.setCurrentItem(2);
                    img_search.setImageResource(R.drawable.search_choose);
                    position = 2;
                }
                break;
            case R.id.ll_my:
                if (position != 3) {
                    changeIcon(position);
                    viewpager.setCurrentItem(3);
                    img_my.setImageResource(R.drawable.my_choose);
                    position = 3;
                }
                break;
            case R.id.ll_add:

                break;
        }
    }

    /**
     * 改变对应位置功能的图标为未选中状态
     * @param position
     */
    public void changeIcon(int position){
        switch (position){
            case 0:
                img_home.setImageResource(R.drawable.home);
                break;
            case 1:
                img_message.setImageResource(R.drawable.message);
                break;
            case 2:
                img_search.setImageResource(R.drawable.search);
                break;
            case 3:
                img_my.setImageResource(R.drawable.my);
                break;
        }
    }
}
