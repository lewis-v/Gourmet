package com.yw.gourmet.ui.personal;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;

public class PersonalActivity extends BaseActivity {
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbar_layout;
    private AppBarLayout app_bar;
    private LinearLayout ll_tool;
    private AnimatorSet animatorSetToolBarShow,animatorSetToolBarHide;//toolbar的显示隐藏动画

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ll_tool = (LinearLayout) findViewById(R.id.ll_tool);

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
        toolbar_layout.setTitle("测试文字阿双方尽快收到回复is倒海翻江可视电话");//
//        toolbar_layout.setCollapsedTitleTextColor(Color.RED);//上滑后的文本颜色
//        toolbar_layout.setContentScrimColor(Color.BLUE);//上画后的toolbar颜色
        toolbar_layout.setExpandedTitleColor(Color.BLACK);//下滑的文本颜色
        toolbar_layout.setStatusBarScrimColor(Color.WHITE);

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

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal;
    }
}
