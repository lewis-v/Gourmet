package com.yw.gourmet.ui.changeDetail;


import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.UserData;
import com.yw.gourmet.dialog.MyDialogChooseListFragment;
import com.yw.gourmet.dialog.MyDialogEditFragment;
import com.yw.gourmet.dialog.MyDialogPhotoChooseFragment;
import com.yw.gourmet.listener.OnEditDialogEnterClickListener;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
import com.yw.gourmet.utils.SPUtils;
import com.yw.gourmet.utils.StringHandleUtils;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.widget.GlideCircleTransform;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.yw.gourmet.dialog.MyDialogPhotoChooseFragment.*;

public class ChangeDetailActivity extends BaseActivity<ChangeDetailPresenter> implements ChangeDetailContract.View
        ,View.OnClickListener,OnEditDialogEnterClickListener,OnCropListener {
    private LinearLayout ll_nickname,ll_sex,ll_address,ll_introduction,ll_header;
    private ImageView img_back,img_header;
    private TextView tv_nickname,tv_sex,tv_address,tv_introduction;
    private List<String> listSex = Arrays.asList("男","女");

    @Override
    protected void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ll_nickname = (LinearLayout)findViewById(R.id.ll_nickname);
        ll_sex = (LinearLayout)findViewById(R.id.ll_sex);
        ll_address = (LinearLayout)findViewById(R.id.ll_address);
        ll_introduction = (LinearLayout)findViewById(R.id.ll_introduction);
        ll_header = (LinearLayout)findViewById(R.id.ll_header);

        ll_nickname.setOnClickListener(this);
        ll_sex.setOnClickListener(this);
        ll_address.setOnClickListener(this);
        ll_introduction.setOnClickListener(this);
        ll_header.setOnClickListener(this);

        img_header = (ImageView)findViewById(R.id.img_header);
        img_back = (ImageView)findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        tv_nickname = (TextView)findViewById(R.id.tv_nickname);
        tv_sex = (TextView)findViewById(R.id.tv_sex);
        tv_address = (TextView)findViewById(R.id.tv_address);
        tv_introduction = (TextView)findViewById(R.id.tv_introduction);
        setData();
    }

    /**
     * 设置显示的数据
     */
    public void setData(){
        if (Constant.userData != null){
            GlideApp.with(this).load(Constant.userData.getImg_header()).placeholder(R.mipmap.loading)
                    .error(R.mipmap.load_fail)
                    .transform(new GlideCircleTransform(this)).into(img_header);
            tv_address.setText(Constant.userData.getAddress());
            tv_introduction.setText(Constant.userData.getIntroduction());
            tv_nickname.setText(Constant.userData.getNickname());
            tv_sex.setText(Constant.userData.getSex());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_detail;
    }

    @Override
    public void onChangeSuccess(BaseData<UserData> model) {
        super.onSuccess(model.getMessage());
        Constant.userData = model.getData();
        SPUtils.setSharedStringData(getApplicationContext(),"img_header",model.getData().getImg_header());
        setData();
        RxBus.getDefault().postSticky(new EventSticky("change_detail"));
    }

    @Override
    public void onUpSuccess(BaseData<String> model) {
        mPresenter.changeDetail(new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                .addFormDataPart("id",Constant.userData.getUser_id())
                .addFormDataPart("img_header",model.getData()).build().parts());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.ll_header:
                new MyDialogPhotoChooseFragment().setChooseNum(1).setCrop(true).setRatio(1)
                        .setOnCropListener(this).show(getSupportFragmentManager(),"header");
                break;
            case R.id.ll_nickname:
                if (Constant.userData != null) {
                    new MyDialogEditFragment().setText(Constant.userData.getNickname())
                            .setEtHint("请输入您的昵称").setOnEditDialogEnterClickListener(this)
                            .show(getSupportFragmentManager(), "nickname");
                }else {
                    ToastUtils.showSingleToast("请登陆后再在进行操作");
                }
                break;
            case R.id.ll_sex:
                int sexPosition =  listSex.indexOf(tv_sex.getText().toString());
                new MyDialogChooseListFragment().setTip("请选择性别:").setData(listSex)
                        .setPosition(sexPosition==-1?0:sexPosition)
                        .setOnChooseListener(new MyDialogChooseListFragment.OnChooseListener() {
                    @Override
                    public void onChoose(int position, String tag) {
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                .addFormDataPart("id",Constant.userData.getUser_id())
                                .addFormDataPart("sex",listSex.get(position));
                        mPresenter.changeDetail(builder.build().parts());
                    }
                }).show(getSupportFragmentManager(),"sex");
                break;
            case R.id.ll_address:
                int addressPosition =  Constant.areaList.indexOf(tv_address.getText().toString());
                new MyDialogChooseListFragment().setTip("请选择地址:").setData(Constant.areaList)
                        .setPosition(addressPosition == -1?0:addressPosition)
                        .setOnChooseListener(new MyDialogChooseListFragment.OnChooseListener() {
                            @Override
                            public void onChoose(int position, String tag) {
                                MultipartBody.Builder builder = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                        .addFormDataPart("id",Constant.userData.getUser_id())
                                        .addFormDataPart("address",Constant.areaList.get(position));
                                mPresenter.changeDetail(builder.build().parts());
                            }
                        }).show(getSupportFragmentManager(),"address");
                break;
            case R.id.ll_introduction:
                if (Constant.userData != null) {
                    new MyDialogEditFragment().setText(Constant.userData.getIntroduction())
                            .setEtHint("请输入您的简介").setOnEditDialogEnterClickListener(this)
                            .show(getSupportFragmentManager(), "introduction");
                }else {
                    ToastUtils.showSingleToast("请登陆后再在进行操作");
                }
                break;
        }
    }

    @Override
    public void OnClick(String edit, String tag) {
        switch (tag){
            case "nickname":
                mPresenter.changeDetail(new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                        .addFormDataPart("id",Constant.userData.getUser_id())
                        .addFormDataPart("nickname", StringHandleUtils.deleteEnter(edit.trim())).build().parts());
                break;
            case "introduction":
                mPresenter.changeDetail(new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                        .addFormDataPart("id",Constant.userData.getUser_id())
                        .addFormDataPart("introduction",StringHandleUtils.deleteEnter(edit.trim())).build().parts());
                break;
        }
    }

    @Override
    public void OnCrop(String path, String tag) {
        new Compressor(this)
                .compressToFileAsFlowable(new File(path))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                .addFormDataPart("id",Constant.userData.getUser_id())
                                .addFormDataPart("path",file.getName(),imageBody);
                        mPresenter.upImg(builder.build().parts());
                    }
                });


//        Luban.with(this).load(new File(img.get(0)))
//                .setCompressListener(new OnCompressListener() {
//                    @Override
//                    public void onStart() {
//
//                    }
//
//                    @Override
//                    public void onSuccess(File file) {
//                        Log.i("---length---",file.length()+"");
//                        MultipartBody.Builder builder = new MultipartBody.Builder()
//                                .setType(MultipartBody.FORM)
//                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
//                                .addFormDataPart("id",Constant.userData.getUser_id())
//                                .addFormDataPart("path",file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
//                        mPresenter.upImg(builder.build().parts());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//                }).launch();
    }
}
