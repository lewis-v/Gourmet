package com.yw.gourmet.ui.set;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.ActivityStack;
import com.yw.gourmet.ui.main.MainActivity;
import com.yw.gourmet.utils.WindowUtil;

/**
 * auth: lewis-v
 * time: 2018/4/24.
 */

public class NightChangeActivity extends AppCompatActivity {
    private ImageView img;
    private FrameLayout view_parent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
    }

    protected int getLayoutId() {
        return R.layout.activity_night_change;
    }

    protected void initView() {
        view_parent = findViewById(R.id.view_parent);

        img = findViewById(R.id.img);
        ActivityStack.getScreenManager().clearAllActivity();
        if (getIntent().getBooleanExtra("night",false)){//转换为夜间模式
            changeAnim(Color.WHITE,Color.BLACK,R.mipmap.sun,R.mipmap.month);
        }else {//转换为日间模式
            changeAnim(Color.BLACK,Color.WHITE,R.mipmap.month,R.mipmap.sun);
        }
    }

    /**
     * 转换动画
     * @param startBack
     * @param endBack
     * @param startImg
     * @param endImg
     */
    public void changeAnim(final int startBack, final int endBack, int startImg, final int endImg){
        view_parent.setBackgroundColor(startBack);
        img.setImageResource(startImg);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator imgStartAnimator = ObjectAnimator.ofFloat(img,"Y",img.getY(), WindowUtil.height);
                final ObjectAnimator imgEndAnimator = ObjectAnimator.ofFloat(img,"Y", WindowUtil.height,img.getY());
                imgStartAnimator.setDuration(1000);
                imgEndAnimator.setDuration(1300);
                imgStartAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        img.setImageResource(endImg);
                        imgEndAnimator.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                imgStartAnimator.start();


                ValueAnimator backAnimator = ObjectAnimator.ofInt(view_parent,"backgroundColor",startBack,endBack);
                backAnimator.setDuration(3000);
                backAnimator.setEvaluator(new ArgbEvaluator());
                backAnimator.start();
                backAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = new Intent(NightChangeActivity.this, MainActivity.class);
                        intent.putExtra("position",3);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_view_enter_alpha,0);
                        startActivity(new Intent(NightChangeActivity.this,SetActivity.class));
                        overridePendingTransition(R.anim.anim_view_enter_alpha,0);
                        finish();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            }
        },500);
    }

    @Override
    public void onBackPressed() {

    }
}
