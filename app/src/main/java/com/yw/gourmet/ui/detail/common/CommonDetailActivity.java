package com.yw.gourmet.ui.detail.common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.CommentAdapter;
import com.yw.gourmet.adapter.ImgAddAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.CommentData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.dialog.MyDialogPhotoShowFragment;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

public class CommonDetailActivity extends BaseActivity<CommonDetailPresenter> implements
        CommonDetailContract.View,View.OnClickListener{
    private ImageView img_header,img_back,img_other,img_share,img_no_comment,img_up;
    private TextView tv_nickname,tv_time,tv_content,tv_comment,tv_good,tv_bad;
    private LinearLayout ll_img,ll_comment,ll_bad,ll_good;
    private RelativeLayout rl_layout;
    private RecyclerView recycler_share,recycler_comment;
    private ImgAddAdapter imgAdapter;
    private ShareListData<List<String>> listShareListData;
    private CommentAdapter commentAdapter;
    private List<CommentData> commentDataList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_detail;
    }

    @Override
    protected void initView() {
        toolbar = findViewById(R.id.toolbar);

        img_header = findViewById(R.id.img_header);
        img_back = findViewById(R.id.img_back);
        img_other = findViewById(R.id.img_other);
        img_share = findViewById(R.id.img_share);
        img_no_comment = findViewById(R.id.img_no_comment);
        img_up = findViewById(R.id.img_up);

        img_back.setOnClickListener(this);
        img_other.setOnClickListener(this);
        img_share.setOnClickListener(this);
        img_up.setOnClickListener(this);

        tv_nickname = findViewById(R.id.tv_nickname);
        tv_time = findViewById(R.id.tv_time);
        tv_content = findViewById(R.id.tv_content);
        tv_comment = findViewById(R.id.tv_comment);
        tv_good = findViewById(R.id.tv_good);
        tv_bad = findViewById(R.id.tv_bad);

        ll_img = findViewById(R.id.ll_img);
        ll_comment = findViewById(R.id.ll_comment);
        ll_good = findViewById(R.id.ll_good);
        ll_bad = findViewById(R.id.ll_bad);

        ll_comment.setOnClickListener(this);
        ll_bad.setOnClickListener(this);
        ll_good.setOnClickListener(this);

        rl_layout = findViewById(R.id.rl_layout);

        recycler_share = findViewById(R.id.recycler_share);
        recycler_share.setItemAnimator(new DefaultItemAnimator());
        recycler_share.setLayoutManager(new StaggeredGridLayoutManager(3
                , StaggeredGridLayoutManager.VERTICAL));

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id",getIntent().getStringExtra("id"));
        mPresenter.getDetail(builder.build().parts());

        recycler_comment = findViewById(R.id.recycler_comment);
        recycler_comment.setItemAnimator(new DefaultItemAnimator());
        recycler_comment.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this,commentDataList);
        recycler_comment.setAdapter(commentAdapter);

        MultipartBody.Builder builderComment = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id",getIntent().getStringExtra("id"))
                .addFormDataPart("type",getIntent().getStringExtra("type"));
        mPresenter.getComment(builderComment.build().parts());

    }

    @Override
    public void onGetDetailSuccess(BaseData<ShareListData<List<String>>> model) {
        listShareListData = model.getData();
        GlideApp.with(this).load(listShareListData.getImg_header()).error(R.mipmap.load_fail).into(img_header);
        tv_nickname.setText(listShareListData.getNickname());
        tv_time.setText(listShareListData.getPut_time());
        if (listShareListData.getContent() != null && listShareListData.getContent().length() > 0){
            tv_content.setVisibility(View.VISIBLE);
            tv_content.setText(listShareListData.getContent());
        }
        if (listShareListData.getImg() != null ){
            if (listShareListData.getImg().size() > 1) {//大于1张照片
                recycler_share.setVisibility(View.VISIBLE);
                imgAdapter = new ImgAddAdapter(listShareListData.getImg(), this);
                imgAdapter.setChange(false);
                recycler_share.setAdapter(imgAdapter);
                imgAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void OnClick(View v, int position) {
                        new MyDialogPhotoShowFragment().setImgString(listShareListData.getImg()).setPosition(position).show(getSupportFragmentManager(),"imgs");
                    }

                    @Override
                    public boolean OnLongClick(View v, int position) {
                        return false;
                    }
                });
            }else if (listShareListData.getImg().size() == 1){//1张照片
                ll_img.setVisibility(View.VISIBLE);
                GlideApp.with(this).load(listShareListData.getImg().get(0))
                        .error(R.mipmap.load_fail).into(img_share);
            }
        }

        if (listShareListData.getBad_num() > 0){
            tv_bad.setText(String.valueOf(listShareListData.getBad_num()));
        }
        if (listShareListData.getGood_num() > 0){
            tv_good.setText(String.valueOf(listShareListData.getGood_num()));
        }
        if (listShareListData.getComment_num() > 0){
            tv_comment.setText(String.valueOf(listShareListData.getComment_num()));
        }
    }

    @Override
    public void onGetDetailFail(String msg) {
        super.onFail(msg);
        finish();
    }

    @Override
    public void onGetCommentSuccess(BaseData<List<CommentData>> model) {
        commentDataList.clear();
        commentDataList.addAll(model.getData());
        commentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetCommentFail(String msg) {

    }

    @Override
    public void onSendCommentSuccess(BaseData model, int position) {

    }

    @Override
    public void onSendCommentFail(String msg, int position) {

    }

    @Override
    public void onRemarkSuccess(BaseData<ShareListData<List<String>>> model) {
        listShareListData.setGood_num(model.getData().getGood_num());
        listShareListData.setBad_num(model.getData().getBad_num());
        listShareListData.setComment_num(model.getData().getComment_num());
        if (listShareListData.getBad_num() > 0){
            tv_bad.setText(String.valueOf(listShareListData.getBad_num()));
        }
        if (listShareListData.getGood_num() > 0){
            tv_good.setText(String.valueOf(listShareListData.getGood_num()));
        }
        if (listShareListData.getComment_num() > 0){
            tv_comment.setText(String.valueOf(listShareListData.getComment_num()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_other:

                break;

            case R.id.img_share:
                new MyDialogPhotoShowFragment().setImgString(listShareListData.getImg()).show(getSupportFragmentManager(),"img");
                break;
            case R.id.img_up:

                break;
            case R.id.ll_comment:

                break;
            case R.id.ll_good:
                if (Constant.userData != null) {
                    MultipartBody.Builder builderGood = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id", Constant.userData.getId())
                            .addFormDataPart("type",String.valueOf(listShareListData.getType()))
                            .addFormDataPart("act_id",listShareListData.getId())
                            .addFormDataPart("act","1");
                    mPresenter.reMark(builderGood.build().parts());
                }else {
                    ToastUtils.showSingleToast("请登陆后再进行操作");
                }
                break;
            case R.id.ll_bad:
                if (Constant.userData != null) {
                    MultipartBody.Builder builderGood = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id", Constant.userData.getId())
                            .addFormDataPart("type",String.valueOf(listShareListData.getType()))
                            .addFormDataPart("act_id",listShareListData.getId())
                            .addFormDataPart("act","0");
                    mPresenter.reMark(builderGood.build().parts());
                }else {
                    ToastUtils.showSingleToast("请登陆后再进行操作");
                }
                break;
        }
    }
}
