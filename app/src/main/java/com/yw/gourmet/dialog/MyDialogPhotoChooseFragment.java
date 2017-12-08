package com.yw.gourmet.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.listener.OnCancelClickListener;
import com.yw.gourmet.listener.OnPhotoChooseListener;
import com.yw.gourmet.utils.UriToFileUtil;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;
import cn.finalteam.rxgalleryfinal.bean.ImageCropBean;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.ui.RxGalleryListener;
import cn.finalteam.rxgalleryfinal.ui.base.IRadioImageCheckedListener;

/**
 * Created by LYW on 2017/11/30.
 */

public class MyDialogPhotoChooseFragment extends BaseDialogFragment implements View.OnClickListener{
    public final static int COMMON = 0;//普通
    public final static int PHOTO = 1;//照片选择
    public final static int TAKE_PHOTO = 2;//拍照

    private TextView tv_take,tv_choose,tv_cancel;
    private OnCancelClickListener onCancelClickListener;
    private OnPhotoChooseListener onPhotoChooseListener;
    private int chooseNum = 1;//选择数量,默认1张,最大9张
    private boolean isCrop = false;//是否剪裁,默认不剪裁
    private List<String> list = new ArrayList<>();//选择结果
    private int type = COMMON;//类型,默认为普通

    @Override
    protected void initView() {
        tv_cancel = (TextView)view.findViewById(R.id.tv_cancel);
        tv_choose = (TextView)view.findViewById(R.id.tv_choose);
        tv_take = (TextView)view.findViewById(R.id.tv_take);

        tv_take.setOnClickListener(this);
        tv_choose.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        //裁剪图片的回调
        RxGalleryListener
                .getInstance()
                .setRadioImageCheckedListener(
                        new IRadioImageCheckedListener() {
                            @Override
                            public void cropAfter(Object t) {
                                list.add(t.toString());
                                if (onPhotoChooseListener != null) {
                                    onPhotoChooseListener.OnChoose(list, getTag());
                                }
                            }

                            @Override
                            public boolean isActivityFinish() {
                                return true;
                            }
                        });
        tv_cancel.post(new Runnable() {
            @Override
            public void run() {
                if (type == PHOTO){
                    onClick(tv_choose);
                }else if (type == TAKE_PHOTO){
                    onClick(tv_take);
                }
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_photo_choose;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                if (onCancelClickListener != null){
                    onCancelClickListener.OnClick(getTag());
                }
                dismiss();
                break;
            case R.id.tv_choose:
                dismiss();
                if (chooseNum == 1) {
                    //自定义方法的单选
                    if (isCrop){
                        RxGalleryFinal.with(getContext())
                                .image()
                                .radio()
                                .hideCamera()
                                .cropWithAspectRatio(1,1)
                                .crop()
                                .imageLoader(ImageLoaderType.PICASSO)
                                .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                                    @Override
                                    protected void onEvent(ImageRadioResultEvent imageMultipleResultEvent) throws Exception {

                                    }

                                }).openGallery();
                    }else {
                        RxGalleryFinal
                                .with(getContext())
                                .image()
                                .radio()
                                .hideCamera()
                                .imageLoader(ImageLoaderType.PICASSO)
                                .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                                    @Override
                                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                                        list.add(imageRadioResultEvent.getResult().getOriginalPath());
                                        if (onPhotoChooseListener != null) {
                                            onPhotoChooseListener.OnChoose(list, getTag());
                                        }
                                    }
                                }).openGallery();
                    }
                }else {
                    //自定义方法的多选
                    RxGalleryFinal.with(getContext())
                            .image()
                            .multiple()
                            .maxSize(chooseNum)
                            .hideCamera()
                            .imageLoader(ImageLoaderType.PICASSO)
                            .subscribe(new RxBusResultDisposable<ImageMultipleResultEvent>() {
                                @Override
                                protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                                   for (MediaBean mediaBean : imageMultipleResultEvent.getResult()) {
                                       list.add(mediaBean.getOriginalPath());
                                   }
                                   if (onPhotoChooseListener != null){
                                       onPhotoChooseListener.OnChoose(list,getTag());
                                   }
                                }

                            }).openGallery();
                }
                break;
            case R.id.tv_take:
                dismiss();
                //手机拍照
                RxGalleryFinalApi.openZKCamera(this);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (requestCode == RxGalleryFinalApi.TAKE_IMAGE_REQUEST_CODE){
            if (onPhotoChooseListener != null && intent.getExtras().get(MediaStore.EXTRA_OUTPUT)!=null){
                list.add(UriToFileUtil.getPath(getContext()
                        ,Uri.parse(intent.getExtras().get(MediaStore.EXTRA_OUTPUT).toString())));
                Log.i("---intent---",list.toString());
                onPhotoChooseListener.OnChoose(list,getTag());
            }
        }
        super.startActivityForResult(intent, requestCode);
    }

    public MyDialogPhotoChooseFragment setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
        return this;
    }

    public MyDialogPhotoChooseFragment setOnPhotoChooseListener(OnPhotoChooseListener onPhotoChooseListener) {
        this.onPhotoChooseListener = onPhotoChooseListener;
        return this;
    }

    public MyDialogPhotoChooseFragment setChooseNum(int chooseNum) {
        this.chooseNum = chooseNum;
        return this;
    }

    public MyDialogPhotoChooseFragment setCrop(boolean crop) {
        isCrop = crop;
        return this;
    }

    public MyDialogPhotoChooseFragment setType(int type) {
        this.type = type;
        return this;
    }
}
