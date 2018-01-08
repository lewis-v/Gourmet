package com.yw.gourmet.ui.share.raiders;

import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.adapter.IngredientAdapter;
import com.yw.gourmet.adapter.MyFragmentAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.dialog.MyDialogEditFragment;
import com.yw.gourmet.listener.OnAddListener;
import com.yw.gourmet.listener.OnDeleteListener;
import com.yw.gourmet.listener.OnEditDialogEnterClickListener;
import com.yw.gourmet.widget.MyViewPager;

import java.util.ArrayList;
import java.util.List;

public class RaidersActivity extends BaseActivity<RaidersPresenter> implements View.OnClickListener{
    private EditText et_title,et_introduction;
    private TextView tv_cancel,tv_send,tv_power;
    private RecyclerView recycler_tag;
    private MyViewPager viewpager_raiders;
    private MyFragmentAdapter fragmentAdapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private IngredientAdapter tagAdapter;
    private List<String> tagList = new ArrayList<>();
    private int status = 1;//权限,公开或私有,1公开,0私有,默认公开

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

        tv_cancel.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        tv_power.setOnClickListener(this);

        recycler_tag = findViewById(R.id.recycler_tag);
        recycler_tag.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.HORIZONTAL));
        recycler_tag.setItemAnimator(new DefaultItemAnimator());
        tagAdapter = new IngredientAdapter(this,tagList,true);
        tagAdapter.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void OnDelete(View v, int position) {
                tagList.remove(position);
                tagAdapter.notifyItemRemoved(position);
            }
        });
        tagAdapter.setOnAddListener(new OnAddListener() {
            @Override
            public void OnAdd(View view, final int position) {
                new MyDialogEditFragment().setEtHint("请输入标签、类型").setOnEditDialogEnterClickListener(new OnEditDialogEnterClickListener() {
                    @Override
                    public void OnClick(String edit, String tag) {
                        tagList.add(edit);
                        tagAdapter.notifyItemInserted(position);
                    }
                }).show(getSupportFragmentManager(),"type");
            }
        });
        recycler_tag.setAdapter(tagAdapter);
        tagAdapter.notifyDataSetChanged();

        viewpager_raiders = findViewById(R.id.viewpager_raiders);
        fragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(),fragmentList);
        viewpager_raiders.setAdapter(fragmentAdapter);
        fragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_send:

                break;
            case R.id.tv_power:
                if (status == 1){
                    tv_power.setText("私有");
                    status = 0;
                }else {
                    tv_power.setText("公开");
                    status = 1;
                }
                break;
        }
    }
}
