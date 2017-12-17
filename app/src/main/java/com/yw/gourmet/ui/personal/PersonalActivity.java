package com.yw.gourmet.ui.personal;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.ui.changeDetail.ChangeDetailActivity;

public class PersonalActivity extends BaseActivity<PersonalPresenter> implements PersonalContract.View
        ,View.OnClickListener{
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbar_layout;
    private AppBarLayout app_bar;
    private LinearLayout ll_tool,ll_change_detail,ll_change_bottom,ll_change_top;
    private AnimatorSet animatorSetToolBarShow,animatorSetToolBarHide;//toolbar的显示隐藏动画
    private TextView tv_nickname,tv_sex,tv_address,tv_introduction;
    private FloatingActionButton float_action_header;
    private ImageView img_tool_back,img_header;
    private String id;//用户id

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_nickname = (TextView)findViewById(R.id.tv_nickname);
        tv_sex = (TextView)findViewById(R.id.tv_sex);
        tv_address = (TextView)findViewById(R.id.tv_address);
        tv_introduction = (TextView)findViewById(R.id.tv_introduction);

        img_tool_back = (ImageView)findViewById(R.id.img_tool_back);
        img_header = (ImageView)findViewById(R.id.img_header);

        float_action_header = (FloatingActionButton)findViewById(R.id.float_action_header);

        ll_tool = (LinearLayout) findViewById(R.id.ll_tool);
        ll_change_bottom = (LinearLayout)findViewById(R.id.ll_change_bottom);
        ll_change_top = (LinearLayout)findViewById(R.id.ll_change_top);
        ll_change_detail = (LinearLayout)findViewById(R.id.ll_change_detail);
        ll_change_detail.setOnClickListener(this);

        ObjectAnimator animatorToolBarShow = ObjectAnimator.ofFloat(ll_tool,"alpha",0f,1f);
        ObjectAnimator animatorToolBarHide = ObjectAnimator.ofFloat(ll_tool,"alpha",1f,0f);
        animatorSetToolBarShow = new AnimatorSet();
        animatorSetToolBarHide = new AnimatorSet();
        animatorSetToolBarShow.play(animatorToolBarShow);
        animatorSetToolBarHide.play(animatorToolBarHide);
        animatorSetToolBarHide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ll_tool.setVisibility(View.GONE);//隐藏控件
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        toolbar_layout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        toolbar_layout.setTitle(" ");//显示标题内容
        toolbar_layout.setCollapsedTitleTextColor(Color.BLACK);//上滑后的文本颜色
//        toolbar_layout.setContentScrimColor(Color.BLUE);//上画后的toolbar颜色
        toolbar_layout.setExpandedTitleColor(ContextCompat.getColor(this,R.color.word_black));//下滑的文本颜色
//        toolbar_layout.setStatusBarScrimColor(Color.WHITE);

        app_bar = (AppBarLayout)findViewById(R.id.app_bar);
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {//展开
                    if (animatorSetToolBarShow.isRunning()){//显示动画运行时,将其关闭
                        animatorSetToolBarShow.cancel();
                    }
                    if (!animatorSetToolBarHide.isRunning()){//启动隐藏动画
                        animatorSetToolBarHide.start();
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {//折叠
                    ll_tool.setVisibility(View.VISIBLE);
                    if (animatorSetToolBarHide.isRunning()){//隐藏动画运行时,将其关闭
                        animatorSetToolBarHide.cancel();
                    }
                    if (!animatorSetToolBarShow.isRunning()){//启动显示动画
                        animatorSetToolBarShow.start();
                    }
                } else {//变化中
//                    ll_tool.setAlpha(Math.abs(verticalOffset)%10/10);
                    if (animatorSetToolBarShow.isRunning()){//显示动画运行时,将其关闭
                        animatorSetToolBarShow.cancel();
                    }
                    if (!animatorSetToolBarHide.isRunning() && ll_tool.getVisibility() == View.VISIBLE){//隐藏动画没运行且ll_toolbar显示的时候,运行显示动画
                        ll_tool.setVisibility(View.VISIBLE);
                        animatorSetToolBarHide.start();
                    }
                }

            }
        });
        app_bar.setOnClickListener(this);
        setData();
    }

    /**
     * 设置显示的个人数据
     */
    public void setData(){
        id = getIntent().getStringExtra("id");
        if (id != null && id.length()>0){//查看其它用户信息
            ll_change_detail.setVisibility(View.GONE);
            ll_change_bottom.setVisibility(View.GONE);
            ll_change_top.setVisibility(View.GONE);

        }else {//查看自身信息
            if (Constant.userData != null) {
                ll_change_detail.setVisibility(View.VISIBLE);
                ll_change_bottom.setVisibility(View.VISIBLE);
                ll_change_top.setVisibility(View.VISIBLE);
                tv_nickname.setText(Constant.userData.getNickname());
                tv_sex.setText(Constant.userData.getSex());
                tv_address.setText(Constant.userData.getAddress());
                tv_introduction.setText(Constant.userData.getIntroduction());
                GlideApp.with(this).load(Constant.userData.getImg_header()).error(R.mipmap.load_fail).into(float_action_header);
                GlideApp.with(this).load(Constant.userData.getImg_header()).error(R.mipmap.load_fail).into(img_header);
                GlideApp.with(this).load(Constant.userData.getPersonal_back()).error(R.mipmap.load_fail).into(img_tool_back);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.app_bar:
                Log.i("---bar---","");
                break;
            case R.id.ll_change_detail:
                startActivity(new Intent(this, ChangeDetailActivity.class));
                break;
        }
    }
}
