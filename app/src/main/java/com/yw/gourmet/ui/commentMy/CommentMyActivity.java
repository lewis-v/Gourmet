package com.yw.gourmet.ui.commentMy;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyFragmentStringAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.widget.MyViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommentMyActivity extends BaseActivity {
    private final static String TAG = "CommentMyActivity";

    private TabLayout tab;
    private MyViewPager viewpager;
    private LinearLayout ll_back;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> listTitle = Arrays.asList("全部","评论","赞","踩");
    private MyFragmentStringAdapter adapter;
    private String id;//打开者的id

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment_my;
    }

    @Override
    protected void initView() {
        tab = findViewById(R.id.tab);
        viewpager = findViewById(R.id.viewpager);

        id = getIntent().getStringExtra("id");
        if (id == null){
            Log.e(TAG,"id is null");
        }

        fragmentList.add(new CommentMyFragment().setType(Constant.CommentType.ALL).setId(id));
        fragmentList.add(new CommentMyFragment().setType(Constant.CommentType.COMMENT).setId(id));
        fragmentList.add(new CommentMyFragment().setType(Constant.CommentType.GOOD).setId(id));
        fragmentList.add(new CommentMyFragment().setType(Constant.CommentType.BAD).setId(id));
        adapter = new MyFragmentStringAdapter(getSupportFragmentManager(),fragmentList,listTitle);
        viewpager.setPagingEnabled(true);
        viewpager.setAdapter(adapter);
        tab.setupWithViewPager(viewpager);
        viewpager.setOffscreenPageLimit(3);

        viewpager.setCurrentItem(getIntent().getIntExtra("type",1));

        ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
