package com.yw.gourmet.dialog;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.TransitionInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyImgViewPagerAdapter;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.utils.ImageUtil;
import com.yw.gourmet.utils.ToastUtils;
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
    private TextView tv_save_img;
    private List<String> imgString = new ArrayList<>(6);
    private List<PhotoView> list = new ArrayList<>(6);
    private int position = 0;//默认显示位置,0
    private String shareFlag;

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
        tv_save_img = view.findViewById(R.id.tv_save_img);
        tv_save_img.setOnClickListener(this);
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
            for (int len = imgString.size(),num = 0;num<len;num++){
                final PhotoView photoView = new PhotoView(getContext());
                GlideApp.with(this).asBitmap().load(imgString.get(num)).error(R.mipmap.load_fail)
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

    public MyDialogPhotoShowFragment setShareFlag(String shareFlag) {
        this.shareFlag = shareFlag;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_save_img:
                GlideApp.with(this).asBitmap().load(imgString.get(viewpager_photo.getCurrentItem()))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                String path = ImageUtil.saveImageToGallery(getContext(),resource);
                                ToastUtils.showSingleToast("保存成功:"+path);
                            }
                        });
                break;
        }
    }
}
