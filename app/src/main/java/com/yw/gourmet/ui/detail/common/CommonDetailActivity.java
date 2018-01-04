package com.yw.gourmet.ui.detail.common;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.yw.gourmet.utils.WindowUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

public class CommonDetailActivity extends BaseActivity<CommonDetailPresenter> implements
        CommonDetailContract.View,View.OnClickListener{
    private ImageView img_header,img_back,img_other,img_share,img_no_comment,img_up;
    private TextView tv_nickname,tv_time,tv_content,tv_comment,tv_good,tv_bad,tv_dev_input;
    private LinearLayout ll_img,ll_comment,ll_bad,ll_good,ll_input,ll_position,ll_title;
    private RelativeLayout rl_layout;
    private RecyclerView recycler_share,recycler_comment;
    private EditText et_input;
    private Button bt_send;
    private ConstraintLayout constraint_comment;
    private NestedScrollView scroll_comment;
    private ImgAddAdapter imgAdapter;
    private ShareListData<List<String>> listShareListData;
    private CommentAdapter commentAdapter;
    private List<CommentData> commentDataList = new ArrayList<>();
    private boolean isAnimShowing = false;//动画是否在显示
    private PopupWindow mPopWindow;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_detail;
    }

    @Override
    protected void initView() {
        setLoadDialog(true);
        toolbar = findViewById(R.id.toolbar);
        view_parent = findViewById(R.id.view_parent);

        constraint_comment = findViewById(R.id.constraint_comment);

        scroll_comment = findViewById(R.id.scroll_comment);

        scroll_comment.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY + scroll_comment.getHeight() == constraint_comment.getHeight()){
                    recycler_comment.setNestedScrollingEnabled(true);
                }else {
                    recycler_comment.setNestedScrollingEnabled(false);
                }
            }
        });

        et_input = findViewById(R.id.et_input);

        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendComment();
                    return true;
                }
                return false;
            }
        });

        bt_send = findViewById(R.id.bt_send);

        bt_send.setOnClickListener(this);

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
        tv_dev_input = findViewById(R.id.tv_dev_input);

        ll_img = findViewById(R.id.ll_img);
        ll_comment = findViewById(R.id.ll_comment);
        ll_good = findViewById(R.id.ll_good);
        ll_bad = findViewById(R.id.ll_bad);
        ll_input = findViewById(R.id.ll_input);
        ll_position = findViewById(R.id.ll_position);
        ll_title = findViewById(R.id.ll_title);

        ll_comment.setOnClickListener(this);
        ll_bad.setOnClickListener(this);
        ll_good.setOnClickListener(this);

        rl_layout = findViewById(R.id.rl_layout);

        recycler_share = findViewById(R.id.recycler_share);
        recycler_share.setNestedScrollingEnabled(false);
        recycler_share.setItemAnimator(new DefaultItemAnimator());
        recycler_share.setLayoutManager(new StaggeredGridLayoutManager(3
                , StaggeredGridLayoutManager.VERTICAL));

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id",getIntent().getStringExtra("id"));
        mPresenter.getDetail(builder.build().parts());

        recycler_comment = findViewById(R.id.recycler_comment);
        recycler_comment.setNestedScrollingEnabled(false);
        recycler_comment.setItemAnimator(new DefaultItemAnimator());
        recycler_comment.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this,commentDataList);
        recycler_comment.setAdapter(commentAdapter);

        MultipartBody.Builder builderComment = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id",getIntent().getStringExtra("id"))
                .addFormDataPart("type",getIntent().getStringExtra("type"));
        mPresenter.getComment(builderComment.build().parts());

        recycler_comment.post(new Runnable() {
            @Override
            public void run() {
                int height = ll_position.getHeight() + toolbar.getHeight()*65/40 + ll_title.getHeight();
                ViewGroup.LayoutParams params = recycler_comment.getLayoutParams();
                params.height = WindowUtil.height - height;
                recycler_comment.setLayoutParams(params);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (ll_input.getVisibility() == View.VISIBLE){
            setInput(false);
            return;
        }
        super.onBackPressed();
        finish();
    }

    @Override
    public void onGetDetailSuccess(BaseData<ShareListData<List<String>>> model) {
        setLoadDialog(false);
        listShareListData = model.getData();
        GlideApp.with(this).load(listShareListData.getImg_header()).error(R.mipmap.load_fail).into(img_header);
        tv_nickname.setText(listShareListData.getNickname());
        tv_time.setText(listShareListData.getPut_time());
        if (listShareListData.getContent() != null && listShareListData.getContent().length() > 0){
            shoViewTop(tv_content,true);
            tv_content.setText(listShareListData.getContent());
        }
        if (listShareListData.getImg() != null ){
            if (listShareListData.getImg().size() > 1) {//大于1张照片
                shoViewTop(recycler_share,true);
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
                shoViewTop(ll_img,true);
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
        scroll_comment.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent().getBooleanExtra("isComment",false)){
                    scroll_comment.fullScroll(View.FOCUS_DOWN);
                }
            }
        });

    }

    @Override
    public void onGetCommentFail(String msg) {
    }

    @Override
    public void onSendCommentSuccess(BaseData<List<CommentData>> model) {
        setLoadDialog(false);
        et_input.setText("");
        commentDataList.clear();
        commentDataList.addAll(model.getData());
        commentAdapter.notifyDataSetChanged();
        tv_comment.setText(String.valueOf(commentDataList.size()));
        scroll_comment.fullScroll(View.FOCUS_DOWN);
        recycler_comment.smoothScrollToPosition(commentDataList.size());
    }

    @Override
    public void onSendCommentFail(String msg) {
        super.onFail(msg);
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
        hideSoftInput(et_input);
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_other:
                showMoreMenu();
                break;
            case R.id.img_share:
                new MyDialogPhotoShowFragment().setImgString(listShareListData.getImg()).show(getSupportFragmentManager(),"img");
                break;
            case R.id.img_up:

                break;
            case R.id.ll_comment:
                if (ll_input.getVisibility() == View.VISIBLE){
                    setInput(false);
                }else {
                    setInput(true);
                }
                break;
            case R.id.bt_send:
                sendComment();
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
            case R.id.tv_collect:

                break;
            case R.id.tv_share:

                break;
        }
    }

    /**
     * 设置UI的显示与隐藏,带从下往上的动画
     * @param isInput
     */
    public void setInput(final boolean isInput){
        if (isAnimShowing){
            return;
        }
        isAnimShowing = true;
        Animation animationBottom ;
        if (isInput){
            animationBottom = AnimationUtils.loadAnimation(this, R.anim.anim_view_enter_bottom);
        }else {
            animationBottom = AnimationUtils.loadAnimation(this, R.anim.anim_view_exit_bottom);
        }
        tv_dev_input.setVisibility(View.VISIBLE);
        ll_input.setVisibility(View.VISIBLE);
        ll_input.startAnimation(animationBottom);
        animationBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!isInput){
                    tv_dev_input.setVisibility(View.GONE);
                    ll_input.setVisibility(View.GONE);
                }
                isAnimShowing = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 发送评论
     */
    public void sendComment(){
        if (Constant.userData == null){
            ToastUtils.showSingleToast("请登录后在评论");
            return;
        }
        if (et_input.getText().toString().trim().isEmpty()){
            ToastUtils.showSingleLongToast("请输入评论信息");
            return;
        }
        hideSoftInput(et_input);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token",Constant.userData.getToken())
                .addFormDataPart("user_id",Constant.userData.getId())
                .addFormDataPart("act_id",listShareListData.getId())
                .addFormDataPart("type",getIntent().getStringExtra("type"))
                .addFormDataPart("content",et_input.getText().toString());
        mPresenter.sendComment(builder.build().parts());
        setLoadDialog(true);
    }

    /**
     * 隐藏对应控件的软键盘
     * @param view
     */
    public void hideSoftInput(View view){
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }catch (Exception e){}
    }

    /**
     * UI的显示隐藏,带从上往下的动画
     * @param view
     * @param isShow
     */
    public void shoViewTop(final View view, final boolean isShow){
        isAnimShowing = true;
        Animation animationBottom ;
        if (isShow){
            animationBottom = AnimationUtils.loadAnimation(this, R.anim.anim_view_enter_bottom);
        }else {
            animationBottom = AnimationUtils.loadAnimation(this, R.anim.anim_view_exit_bottom);
        }
        view.setVisibility(View.VISIBLE);
        ll_input.startAnimation(animationBottom);
        animationBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!isShow){
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void showMoreMenu(){
        //设置contentView
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_more_menu, null);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        //设置各个控件的点击响应
        TextView tv_collect = (TextView)contentView.findViewById(R.id.tv_collect);
        TextView tv_share = (TextView)contentView.findViewById(R.id.tv_share);
        tv_share.setVisibility(View.GONE);
        tv_collect.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        mPopWindow.showAsDropDown(img_other,-100,0);
    }
}
