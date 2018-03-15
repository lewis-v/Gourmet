package com.yw.gourmet.ui.imgShow;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyImgViewPagerAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.utils.ShareTransitionUtil;

import java.util.ArrayList;
import java.util.List;

public class ImgShowActivity extends BaseActivity {
    private ViewPager viewpager;
    private List<String> imgList;
    private MyImgViewPagerAdapter<PhotoView> adapter;
    private List<PhotoView> list = new ArrayList<>(2);
    private int position = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_img_show;
    }

    @Override
    protected void initView() {
        view_parent = findViewById(R.id.view_parent);
        animBack(view_parent);
        viewpager = findViewById(R.id.viewpager);
        final Intent intent = getIntent();
        if (intent == null ){
            finish();
            return;
        }
        imgList = intent.getStringArrayListExtra("img");
        position = intent.getIntExtra("position",0);
        if (imgList == null){
            finish();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            viewpager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startPostponedEnterTransition();
                }
            },500);
        }
        for (int len = imgList.size(),num = 0;num<len;num++){
            final PhotoView img = new PhotoView(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && num == position) {
                img.setTransitionName(getIntent().getStringExtra("shareFlag"));
                GlideApp.with(this).asBitmap().load(imgList.get(num))
                        .placeholder(R.mipmap.loading).error(R.mipmap.load_fail)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                img.setImageBitmap(resource);
                                startPostponedEnterTransition();
                            }
                        });
            }else{
                GlideApp.with(this).load(imgList.get(num))
                        .placeholder(R.mipmap.loading).error(R.mipmap.load_fail)
                        .into(img);
            }
            list.add(img);
        }

        adapter = new MyImgViewPagerAdapter<PhotoView>(list);
        viewpager.setAdapter(adapter);
        if (position < list.size()){
            viewpager.setCurrentItem(position);
        }
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ShareTransitionUtil.position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            for (int num =0,len = list.size();num < len ; num ++){
                if (num == ShareTransitionUtil.position){
                    list.get(num).setTransitionName(getIntent().getStringExtra("shareFlag"));
                }else {
                    list.get(num).setTransitionName("");
                }
            }
        }
        super.onBackPressed();
    }

    /**
     * 启动时的背景渐变动画
     * @param view
     */
    public void animBack(View view){

        ValueAnimator colorAnim = ObjectAnimator.ofInt(view,
                "backgroundColor", Color.WHITE, Color.BLACK);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.start();
    }
}
