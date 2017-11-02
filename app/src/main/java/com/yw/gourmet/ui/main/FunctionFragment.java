package com.yw.gourmet.ui.main;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.utils.WindowUtil;

/**
 * Created by Administrator on 2017/10/23.
 */

public class FunctionFragment extends BaseFragment implements View.OnClickListener{
    private LinearLayout ll_text,ll_photo,ll_take_photo,ll_diary,ll_menu,ll_raiders,ll_close,ll_choose;

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        ll_choose = (LinearLayout) view.findViewById(R.id.ll_choose);
        ll_text = (LinearLayout) view.findViewById(R.id.ll_text);
        ll_photo = (LinearLayout) view.findViewById(R.id.ll_photo);
        ll_take_photo = (LinearLayout) view.findViewById(R.id.ll_take_photo);
        ll_diary = (LinearLayout) view.findViewById(R.id.ll_diary);
        ll_menu = (LinearLayout) view.findViewById(R.id.ll_menu);
        ll_raiders = (LinearLayout) view.findViewById(R.id.ll_raiders);
        ll_close = (LinearLayout) view.findViewById(R.id.ll_close);

        ll_text.setOnClickListener(this);
        ll_photo.setOnClickListener(this);
        ll_take_photo.setOnClickListener(this);
        ll_diary.setOnClickListener(this);
        ll_menu.setOnClickListener(this);
        ll_raiders.setOnClickListener(this);
        ll_close.setOnClickListener(this);
    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_function;
    }


    @Override
    public void onClick(View view) {
        ((MainContract.View) getActivity()).addFragmentFunction(false);
        switch (view.getId()) {
            case R.id.ll_text:

                break;
            case R.id.ll_photo:

                break;
            case R.id.ll_take_photo:

                break;
            case R.id.ll_diary:

                break;
            case R.id.ll_menu:

                break;
            case R.id.ll_raiders:

                break;
            case R.id.ll_close:
                break;
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        Animation anim ;
        if (enter) {
            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_view_enter_alpha);
        }else {
            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_view_exit_alpha);
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_view_exit_right);
            ll_diary.startAnimation(animation);
            ll_menu.startAnimation(animation);
            ll_raiders.startAnimation(animation);

            Animation animationLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_view_exit_left);
            ll_text.startAnimation(animationLeft);
            ll_photo.startAnimation(animationLeft);
            ll_take_photo.startAnimation(animationLeft);

            Animation animationBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_view_exit_bottom);
            ll_close.startAnimation(animationBottom);
        }
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ll_choose.setVisibility(View.VISIBLE);
                if (enter) {
                    Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_view_enter_right);
                    ll_diary.startAnimation(anim);
                    ll_menu.startAnimation(anim);
                    ll_raiders.startAnimation(anim);

                    Animation animationLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_view_enter_left);
                    ll_text.startAnimation(animationLeft);
                    ll_photo.startAnimation(animationLeft);
                    ll_take_photo.startAnimation(animationLeft);

                    Animation animationBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_view_enter_bottom);
                    ll_close.startAnimation(animationBottom);


//                    AnimatorSet animatorSet = new AnimatorSet();
//                    animatorSet.playTogether(getViewAnim(ll_text),getViewAnim(ll_diary)
//                            ,getViewAnim(ll_photo),getViewAnim(ll_menu)
//                            ,getViewAnim(ll_take_photo),getViewAnim(ll_raiders)
//                            ,getViewAnim(ll_close));
//                    animatorSet.start();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return anim;
    }

    /**
     * 获取进入时的动画
     * @param view 动画控件
     * @return
     */
    public ObjectAnimator getViewAnim(View view){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,"translationY", WindowUtil.height,view.getY());
        objectAnimator.setDuration(300);
        return objectAnimator;
    }
}
