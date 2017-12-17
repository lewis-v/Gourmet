package com.yw.gourmet.dialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yalantis.ucrop.UCrop;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.listener.OnCancelClickListener;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.utils.UriToFileUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;

import static android.app.Activity.RESULT_OK;


/**
 * Created by LYW on 2017/11/30.
 */

public class MyDialogPhotoChooseFragment extends BaseDialogFragment implements View.OnClickListener{
    public final static int COMMON = 0;//普通
    public final static int PHOTO = 1;//照片选择
    public final static int TAKE_PHOTO = 2;//拍照

    private final static int REQUEST_CODE_CAPTURE_CAMEIA = 888;
    private final static String path = Environment.getExternalStorageDirectory().getPath() + "/data/gourmet/";//存储目录

    private TextView tv_take,tv_choose,tv_cancel;
    private OnCancelClickListener onCancelClickListener;
    private OnChooseLister onPhotoChooseListener;
    private OnCropListener onCropListener;
    private int chooseNum = 1;//选择数量,默认1张,最大9张
    private boolean isCrop = false;//是否剪裁,默认不剪裁
    private List<String> list = new ArrayList<>();//选择结果
    private int type = COMMON;//类型,默认为普通
    private float ratio = 0;//裁剪比例

    @Override
    protected void initView() {
        tv_cancel = (TextView)view.findViewById(R.id.tv_cancel);
        tv_choose = (TextView)view.findViewById(R.id.tv_choose);
        tv_take = (TextView)view.findViewById(R.id.tv_take);

        tv_take.setOnClickListener(this);
        tv_choose.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

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
            case R.id.tv_choose://照片选择
                PhotoPicker.builder()
                        .setPhotoCount(chooseNum)
                        .setShowCamera(false)
                        .setShowGif(false)
                        .setPreviewEnabled(false)
                        .start(getContext(),this, PhotoPicker.REQUEST_CODE);
                break;
            case R.id.tv_take:
                //手机拍照
                getImageFromCamera();
                break;
        }
    }

    /**
     * 拍照并获取照片
     */
    protected void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
//            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);//存入指定路径
            startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
        }
        else {
            ToastUtils.showSingleToast("请确认已经插入SD卡");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("---result---",requestCode+";"+resultCode+";"+data.toString());
        if (resultCode == RESULT_OK) {
            if (requestCode == PhotoPicker.REQUEST_CODE) {
                if (data != null) {
                    list = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    if (chooseNum == 1 && isCrop && list.size() > 0){
                        File file = new File(path);
                        if (!file.exists()){
                            file.mkdirs();
                        }
                        String cropPath = path+new Date()+".jpg";
                        UCrop uCrop = UCrop.of(Uri.fromFile(new File(list.get(0))),Uri.fromFile(new File(cropPath)));
                        if (ratio != 0){
                            uCrop.withAspectRatio(1,ratio);
                        }
                        uCrop.start(getContext(),this);
                    }else {
                        if (onPhotoChooseListener != null) {
                            onPhotoChooseListener.OnChoose(list, getTag());
                        }
                        dismiss();
                    }
                }
            }else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
                Uri uri = data.getData();
                if (uri == null) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");

                    try {
                        File dirFile = new File(path);
                        if (!dirFile.exists()) {
                            dirFile.mkdir();
                        }
                        File myCaptureFile = new File(path +new Date().getTime()+ "photo.jpeg");
                        if (!myCaptureFile.exists()){
                            myCaptureFile.createNewFile();
                        }
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                        bos.flush();
                        bos.close();
                        uri = Uri.fromFile(myCaptureFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                if (isCrop){
                    String cropPath = Environment.getExternalStorageDirectory().getPath() + "/data/gourmet/"+new Date()+".jpg";
                    UCrop uCrop = UCrop.of(Uri.fromFile(new File(list.get(0))),Uri.fromFile(new File(cropPath)));
                    if (ratio != 0){
                        uCrop.withAspectRatio(1,ratio);
                    }
                    uCrop.start(getContext(),this);
                }else {
                    list.add(UriToFileUtil.getPath(getContext(), uri));
                    if (onPhotoChooseListener != null) {
                        onPhotoChooseListener.OnChoose(list, getTag());
                    }
                    dismiss();
                }
            }else if (requestCode == UCrop.REQUEST_CROP){
                Uri resultUri = UCrop.getOutput(data);//裁剪的结果
                if (onCropListener != null){
                    onCropListener.OnCrop(UriToFileUtil.getPath(getContext(),resultUri),getTag());
                }
                dismiss();
            }
        }else if (resultCode == UCrop.RESULT_ERROR){
            ToastUtils.showLongToast("剪裁出错");
        }
    }

    /**
     * 剪裁监听器
     */
    public interface OnCropListener{
        void OnCrop(String path,String tag);
    }

    /**
     * 选择照片监听器
     */
    public interface OnChooseLister{
        void OnChoose(List<String> imgs,String tag);
    }

    public MyDialogPhotoChooseFragment setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
        return this;
    }

    public MyDialogPhotoChooseFragment setOnPhotoChooseListener(OnChooseLister onPhotoChooseListener) {
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

    public MyDialogPhotoChooseFragment setRatio(float ratio) {
        this.ratio = ratio;
        return this;
    }

    public MyDialogPhotoChooseFragment setOnCropListener(OnCropListener onCropListener) {
        this.onCropListener = onCropListener;
        return this;
    }
}
