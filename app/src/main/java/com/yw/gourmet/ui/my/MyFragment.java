package com.yw.gourmet.ui.my;

import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.ui.login.LoginActivity;
import com.yw.gourmet.ui.personal.PersonalActivity;

/**
 * Created by Administrator on 2017/10/22.
 */

public class MyFragment extends BaseFragment implements View.OnClickListener{
    private LinearLayout ll_set;
    private ScrollView scroll_my;
    private TextView tv_nickname;
    private Button bt_login,bt_register;
    private ConstraintLayout constraint_my;

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        ll_set = (LinearLayout)view.findViewById(R.id.ll_set);
        ll_set.setOnClickListener(this);

        constraint_my = (ConstraintLayout)view.findViewById(R.id.constraint_my);

        tv_nickname = (TextView)view.findViewById(R.id.tv_nickname);

        bt_login = (Button)view.findViewById(R.id.bt_login);
        bt_register = (Button)view.findViewById(R.id.bt_register);
        bt_login.setOnClickListener(this);
        bt_register.setOnClickListener(this);

        scroll_my = (ScrollView)view.findViewById(R.id.scroll_my);
        if (Constant.userData!=null){
            constraint_my.setVisibility(View.GONE);
            scroll_my.setVisibility(View.VISIBLE);
            setData();
        }else {
            scroll_my.setVisibility(View.GONE);
            constraint_my.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置个人信息参数显示
     */
    public void setData(){
        tv_nickname.setText(Constant.userData.getNike_name());
    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_set:
                startActivity(new Intent(getContext(), PersonalActivity.class));
                break;
            case R.id.bt_register:

                break;
            case R.id.bt_login:
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constant.userData == null){
            scroll_my.setVisibility(View.GONE);
            constraint_my.setVisibility(View.VISIBLE);
        }else if (Constant.userData != null && scroll_my.getVisibility() != View.VISIBLE){
            scroll_my.setVisibility(View.VISIBLE);
            setData();
            constraint_my.setVisibility(View.GONE);
        }
    }
}
