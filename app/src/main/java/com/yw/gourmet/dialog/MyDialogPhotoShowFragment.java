package com.yw.gourmet.dialog;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyImgViewPagerAdapter;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.utils.WindowUtil;
import com.yw.gourmet.widget.MyViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by LYW on 2017/12/8.
 */

public class MyDialogPhotoShowFragment extends BaseDialogFragment implements View.OnClickListener{
    private MyViewPager viewpager_photo;
    private MyImgViewPagerAdapter<PhotoView> adapter;
    private LinearLayout ll_dialog;
    private List<String> imgString = new ArrayList<>(2);
    private List<PhotoView> list = new ArrayList<>(2);
    private int position = 0;//默认显示位置,0

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout((int) (WindowUtil.width), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.ImgDialog;
        return dialog;
    }

    @Override
    protected void initView() {
        viewpager_photo = (MyViewPager)view.findViewById(R.id.viewpager_photo);
        adapter = new MyImgViewPagerAdapter<PhotoView>(list);
        viewpager_photo.setAdapter(adapter);
        viewpager_photo.setPagingEnabled(true);
        setData();
        if (position < list.size()){
            viewpager_photo.setCurrentItem(position);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_photo_show;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    public void setData(){
        if (imgString != null && imgString.size()>0){
            for (String path : imgString){
                PhotoView photoView = new PhotoView(getContext());
                GlideApp.with(this).load(path).error(R.mipmap.load_fail)
                        .placeholder(R.mipmap.loading).into(photoView);
                photoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                list.add(photoView);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public MyDialogPhotoShowFragment setImgString(List<String> imgString) {
        this.imgString = imgString;
        return this;
    }

    public MyDialogPhotoShowFragment setPosition(int position) {
        this.position = position;
        return this;
    }

    public MyDialogPhotoShowFragment addImgString(String path){
        this.imgString.add(path);
        return this;
    }

    @Override
    public void onClick(View v) {

    }
}
