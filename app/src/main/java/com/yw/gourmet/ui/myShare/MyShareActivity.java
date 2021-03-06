package com.yw.gourmet.ui.myShare;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyFragmentStringAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.widget.MyViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyShareActivity extends BaseActivity {
    private final static String TAG = "MyShareActivity";

    private TextView tv_title;
    private TabLayout tab;
    private MyViewPager viewpager;
    private LinearLayout ll_back;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> listTitle = Arrays.asList("全部","日记","攻略","食谱","分享");
    private MyFragmentStringAdapter adapter;
    private String id;//显示者id

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_share;
    }

    @Override
    protected void initView() {
        tab = findViewById(R.id.tab);
        viewpager = findViewById(R.id.viewpager);

        id = getIntent().getStringExtra("id");
        if (id == null){
            Log.e(TAG,"id is null");
        }else{
            tv_title = findViewById(R.id.tv_title);
            if (Constant.userData == null || !id.equals(Constant.userData.getUser_id())){
                tv_title.setText(R.string.ta_share);
            }
        }

        fragmentList.add(new MyShareFragment().setId(id));
        fragmentList.add(new MyShareFragment().setType(Constant.TypeFlag.DIARY).setId(id));
        fragmentList.add(new MyShareFragment().setType(Constant.TypeFlag.RAIDERS).setId(id));
        fragmentList.add(new MyShareFragment().setType(Constant.TypeFlag.MENU).setId(id));
        fragmentList.add(new MyShareFragment().setType(Constant.TypeFlag.SHARE).setId(id));
        adapter = new MyFragmentStringAdapter(getSupportFragmentManager(),fragmentList,listTitle);
        viewpager.setPagingEnabled(true);
        viewpager.setAdapter(adapter);
        tab.setupWithViewPager(viewpager);
        viewpager.setOffscreenPageLimit(4);

        viewpager.setCurrentItem(getIntent().getIntExtra("type",-1)+1);

        ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
