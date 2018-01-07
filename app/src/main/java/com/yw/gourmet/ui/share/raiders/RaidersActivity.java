package com.yw.gourmet.ui.share.raiders;

import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.EditText;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.adapter.IngredientAdapter;
import com.yw.gourmet.adapter.MyFragmentAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.widget.CustomLayoutManager;
import com.yw.gourmet.widget.MyViewPager;

import java.util.ArrayList;
import java.util.List;

public class RaidersActivity extends BaseActivity<RaidersPresenter> {
    private EditText et_title,et_introduction;
    private TextView tv_cancel,tv_send,tv_power;
    private RecyclerView recycler_tag;
    private MyViewPager viewpager_raiders;
    private MyFragmentAdapter fragmentAdapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private IngredientAdapter tagAdapter;
    private List<String> tagList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_raiders;
    }

    @Override
    protected void initView() {
        et_title = findViewById(R.id.et_title);
        et_introduction = findViewById(R.id.et_introduction);

        tv_cancel = findViewById(R.id.tv_cancel);
        tv_send = findViewById(R.id.tv_send);
        tv_power = findViewById(R.id.tv_power);

        recycler_tag = findViewById(R.id.recycler_tag);
        recycler_tag.setLayoutManager(new CustomLayoutManager());
        recycler_tag.setItemAnimator(new DefaultItemAnimator());
        tagAdapter = new IngredientAdapter(this,tagList,true);
        recycler_tag.setAdapter(tagAdapter);
        tagList.add("11,111");
        tagList.add("11,111");
        tagList.add("11,111");
        tagList.add("11,111");
        tagList.add("11,111");
        tagList.add("11,111");
        tagList.add("11,111");
        tagAdapter.notifyDataSetChanged();

        viewpager_raiders = findViewById(R.id.viewpager_raiders);
        fragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(),fragmentList);
        viewpager_raiders.setAdapter(fragmentAdapter);
        fragmentAdapter.notifyDataSetChanged();
    }
}
