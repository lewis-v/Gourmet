package com.yw.gourmet.ui.detail.menu;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.yw.gourmet.adapter.IngredientAdapter;
import com.yw.gourmet.adapter.PracticeAdapter;
import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.CommentData;
import com.yw.gourmet.data.MenuDetailData;
import com.yw.gourmet.data.MenuPracticeData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.dialog.MyDialogComplaintFragment;
import com.yw.gourmet.dialog.MyDialogMoreFragment;
import com.yw.gourmet.dialog.MyDialogPhotoShowFragment;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.rxbus.RxBus;
import com.yw.gourmet.ui.detail.common.CommonDetailActivity;
import com.yw.gourmet.ui.imgShow.ImgShowActivity;
import com.yw.gourmet.utils.ShareTransitionUtil;
import com.yw.gourmet.utils.StringHandleUtils;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.utils.WindowUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;

import static com.yw.gourmet.ui.share.menu.MenuActivity.levelText;
import static com.yw.gourmet.utils.SoftInputUtils.hideSoftInput;

public class MenuDetailActivity extends BaseActivity<MenuDetailPresenter> implements
        MenuDetailContract.View,View.OnClickListener{
    private ImageView img_back,img_other,img_no_comment,img_up,img_cover;
    private ImageView img_comment,img_good,img_bad;
    private List<ImageView> difficultList = new ArrayList<>();//难度条
    private TextView tv_title,tv_comment,tv_good,tv_bad,tv_dev_input,tv_introduction,tv_tip
            ,tv_time_hour,tv_time_min,tv_difficult;
    private LinearLayout ll_comment,ll_bad,ll_good,ll_input,ll_position,ll_title;
    private RelativeLayout rl_layout;
    private RecyclerView recycler_comment;
    private EditText et_input;
    private Button bt_send;
    private ConstraintLayout coordinator_menu,constraint_tip;
    private NestedScrollView scroll_menu;
    private RecyclerView recycler_ingredient,recycler_practice;
    private IngredientAdapter adapterIngredient;//用料适配器
    private PracticeAdapter adapterPractice;//步骤适配器
    private MenuDetailData<List<MenuPracticeData<List<String>>>,List<String>> shareListData;
    private CommentAdapter commentAdapter;
    private List<CommentData> commentDataList = new ArrayList<>();
    private boolean isAnimShowing = false;//动画是否在显示
    private PopupWindow mPopWindow;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_menu_detail;
    }

    @Override
    protected void initView() {
        setLoadDialog(true);
        toolbar = findViewById(R.id.toolbar);
        view_parent = findViewById(R.id.view_parent);

        coordinator_menu = findViewById(R.id.coordinator_menu);
        constraint_tip = findViewById(R.id.constraint_tip);

        scroll_menu = findViewById(R.id.scroll_menu);

        scroll_menu.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY + scroll_menu.getHeight() == coordinator_menu.getHeight()){
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

        img_back = findViewById(R.id.img_back);
        img_other = findViewById(R.id.img_other);
        img_no_comment = findViewById(R.id.img_no_comment);
        img_up = findViewById(R.id.img_up);
        img_cover = findViewById(R.id.img_cover);
        img_comment = findViewById(R.id.img_comment);
        img_good = findViewById(R.id.img_good);
        img_bad = findViewById(R.id.img_bad);

        ImageView imageView = (ImageView)findViewById(R.id.img_difficult1);
        imageView.setOnClickListener(this);
        difficultList.add(imageView);
        imageView = (ImageView)findViewById(R.id.img_difficult2);
        imageView.setOnClickListener(this);
        difficultList.add(imageView);
        imageView = (ImageView)findViewById(R.id.img_difficult3);
        imageView.setOnClickListener(this);
        difficultList.add(imageView);
        imageView = (ImageView)findViewById(R.id.img_difficult4);
        imageView.setOnClickListener(this);
        difficultList.add(imageView);
        imageView = (ImageView)findViewById(R.id.img_difficult5);
        imageView.setOnClickListener(this);
        difficultList.add(imageView);

        img_cover.setOnClickListener(this);
        img_back.setOnClickListener(this);
        img_other.setOnClickListener(this);
        img_up.setOnClickListener(this);

        tv_comment = findViewById(R.id.tv_comment);
        tv_good = findViewById(R.id.tv_good);
        tv_bad = findViewById(R.id.tv_bad);
        tv_dev_input = findViewById(R.id.tv_dev_input);
        tv_title = findViewById(R.id.tv_title);
        tv_introduction = findViewById(R.id.tv_introduction);
        tv_tip = findViewById(R.id.tv_tip);
        tv_time_hour = findViewById(R.id.tv_time_hour);
        tv_time_min = findViewById(R.id.tv_time_min);
        tv_difficult = findViewById(R.id.tv_difficult);

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

        recycler_ingredient = (RecyclerView)findViewById(R.id.recycler_ingredient);
        recycler_ingredient.setItemAnimator(new DefaultItemAnimator());
        recycler_ingredient.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.HORIZONTAL));
        recycler_ingredient.setNestedScrollingEnabled(false);//禁止滑动

        recycler_practice = (RecyclerView)findViewById(R.id.recycler_practice);
        recycler_practice.setItemAnimator(new DefaultItemAnimator());
        recycler_practice.setLayoutManager(new LinearLayoutManager(this));
        recycler_practice.setNestedScrollingEnabled(false);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                .addFormDataPart("id",getIntent().getStringExtra("id"));
        if (Constant.userData != null){
            builder.addFormDataPart("user_id",Constant.userData.getUser_id());
        }
        mPresenter.getDetail(builder.build().parts());

        recycler_comment = findViewById(R.id.recycler_comment);
        recycler_comment.setNestedScrollingEnabled(false);
        recycler_comment.setItemAnimator(new DefaultItemAnimator());
        recycler_comment.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this,commentDataList);
        recycler_comment.setAdapter(commentAdapter);

        MultipartBody.Builder builderComment = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
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
    public void onClick(View v) {
        hideSoftInput(et_input);
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_other:
                showMoreMenu();
                break;
            case R.id.img_up:

                break;
            case R.id.img_cover:
                final String shareFlag = "tran"+(int)(Math.random()*1000);
                Intent intent = new Intent(MenuDetailActivity.this, ImgShowActivity.class);
                ArrayList<String> list = new ArrayList<String>();
                list.add(shareListData.getCover());
                intent.putStringArrayListExtra("img",list);
                intent.putExtra("position",0);
                intent.putExtra("shareFlag",shareFlag);

                if (android.os.Build.VERSION.SDK_INT > 20) {
                    setExitSharedElementCallback(new SharedElementCallback() {
                        @Override
                        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                            super.onMapSharedElements(names, sharedElements);
                        }
                    });
                    ShareTransitionUtil.position = -1;
                    img_cover.setTransitionName(shareFlag);
                    startActivity(intent
                            , ActivityOptions.makeSceneTransitionAnimation(MenuDetailActivity.this
                                    , img_cover, shareFlag).toBundle());
                } else {
                    startActivity(intent);
                }
//                new MyDialogPhotoShowFragment().addImgString(shareListData.getCover())
//                        .show(getSupportFragmentManager(),"cover");
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
                            .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                            .addFormDataPart("id", Constant.userData.getUser_id())
                            .addFormDataPart("type",String.valueOf(shareListData.getType()))
                            .addFormDataPart("act_id",shareListData.getId())
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
                            .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                            .addFormDataPart("id", Constant.userData.getUser_id())
                            .addFormDataPart("type",String.valueOf(shareListData.getType()))
                            .addFormDataPart("act_id",shareListData.getId())
                            .addFormDataPart("act","0");
                    mPresenter.reMark(builderGood.build().parts());
                }else {
                    ToastUtils.showSingleToast("请登陆后再进行操作");
                }
                break;

            case R.id.tv_collect:
                new MyDialogMoreFragment()
                        .setId(shareListData.getId()).setType(shareListData.getType()).setShare(false)
                        .show(getSupportFragmentManager(),"collect");
                break;
            case R.id.tv_share:
                new MyDialogMoreFragment().setShowCollect(false).setType(shareListData.getType())
                        .setId(shareListData.getId())
                        .setShareTitle(shareListData.getTitle()).setShareCoverUrl(shareListData.getCover())
                        .setShareDescription(shareListData.getContent()).setShareUrl(Api.API_BASE_URL + "/Share/Other?id="
                        + shareListData.getId() + "&type=" + shareListData.getType())
                        .show(getSupportFragmentManager(),"share");
                break;
            case R.id.tv_complaint:
                if (Constant.userData == null){
                    ToastUtils.showSingleToast("请登陆后在进行操作");
                }else {
                    new MyDialogComplaintFragment().setOnEnterListener(new MyDialogComplaintFragment.OnEnterListener() {
                        @Override
                        public void onEnter(String edit, String Tag) {
                            mPresenter.complaint(new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                    .addFormDataPart("user_id",Constant.userData.getUser_id())
                                    .addFormDataPart("act_id",shareListData.getId())
                                    .addFormDataPart("type", String.valueOf(shareListData.getType()))
                                    .addFormDataPart("content", StringHandleUtils.deleteEnter(edit.trim()))
                                    .build().parts());
                        }
                    }).show(getSupportFragmentManager(),"complaint");
                }
                break;
        }
    }

    @Override
    public void onGetDetailSuccess(BaseData<MenuDetailData<List<MenuPracticeData<List<String>>>,List<String>>> model) {
        setLoadDialog(false);
        shareListData = model.getData();
        tv_title.setText(shareListData.getTitle());
        GlideApp.with(this).load(shareListData.getCover()).error(R.mipmap.load_fail)
                .placeholder(R.mipmap.loading).into(img_cover);
        setDifficultLevel(shareListData.getDifficult_level());
        String[] time = shareListData.getPlay_time().split(",");
        if (time != null && time.length > 1) {
            tv_time_hour.setText(time[0]);
            tv_time_min.setText(time[1]);
        }
        tv_introduction.setText(shareListData.getIntroduction());
        if (shareListData.getTip() != null && shareListData.getTip().length() > 0){
            tv_tip.setText(shareListData.getTip());
        }else {
            constraint_tip.setVisibility(View.GONE);
        }
        if (shareListData.getIngredient() != null && shareListData.getIngredient().size()>0) {
            adapterIngredient = new IngredientAdapter(this, shareListData.getIngredient(), false);
            recycler_ingredient.setAdapter(adapterIngredient);
            adapterIngredient.notifyDataSetChanged();
        }
        if (shareListData.getPractice() != null && shareListData.getPractice().size()>0) {
            adapterPractice = new PracticeAdapter(this, shareListData.getPractice(), getSupportFragmentManager(), false);
            recycler_practice.setAdapter(adapterPractice);
            adapterPractice.notifyDataSetChanged();
            adapterPractice.setShowImg(true);
        }
        if (shareListData.getBad_num() > 0){
            tv_bad.setText(String.valueOf(shareListData.getBad_num()));
        }
        if (shareListData.getGood_num() > 0){
            tv_good.setText(String.valueOf(shareListData.getGood_num()));
        }
        if (shareListData.getComment_num() > 0){
            tv_comment.setText(String.valueOf(shareListData.getComment_num()));
        }
        String isComment = shareListData.getIs_comment();
        if (isComment != null && isComment.length()>0){
            img_comment.setImageResource(R.drawable.comment_ic);
            tv_comment.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
        }
        String goodAct = shareListData.getGood_act();
        if (goodAct != null){
            if (goodAct.equals("0")){
                img_bad.setImageResource(R.drawable.bad_ic);
                tv_bad.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
            }else if (goodAct.equals("1")){
                img_good.setImageResource(R.drawable.good_ic);
                tv_good.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
            }
        }
    }

    @Override
    public void onGetDetailFail(String msg) {
        super.onFail(msg);
        finish();
    }

    @Override
    public void onGetCommentSuccess(BaseData<List<CommentData>> model) {
        setLoadDialog(false);
        commentDataList.clear();
        commentDataList.addAll(model.getData());
        commentAdapter.notifyDataSetChanged();
        scroll_menu.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent().getBooleanExtra("isComment",false)){
                    scroll_menu.fullScroll(View.FOCUS_DOWN);
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
        commentDataList.clear();
        commentDataList.addAll(model.getData());
        commentAdapter.notifyDataSetChanged();
        tv_comment.setText(String.valueOf(commentDataList.size()));
        scroll_menu.fullScroll(View.FOCUS_DOWN);
        recycler_comment.smoothScrollToPosition(commentDataList.size());
        et_input.setText("");
        img_comment.setImageResource(R.drawable.comment_ic);
        tv_comment.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
        EventSticky eventSticky = new EventSticky("refresh_comment",shareListData.getType()
                ,shareListData.getId(),commentDataList.size(),Constant.CommentType.COMMENT);
        RxBus.getDefault().postSticky(eventSticky);
    }

    @Override
    public void onSendCommentFail(String msg) {
        super.onFail(msg);
    }

    @Override
    public void onRemarkSuccess(BaseData<ShareListData> model) {
        shareListData.setGood_num(model.getData().getGood_num());
        shareListData.setBad_num(model.getData().getBad_num());
        shareListData.setComment_num(model.getData().getComment_num());
        if (shareListData.getBad_num() > 0){
            tv_bad.setText(String.valueOf(shareListData.getBad_num()));
        }
        if (shareListData.getGood_num() > 0){
            tv_good.setText(String.valueOf(shareListData.getGood_num()));
        }
        if (shareListData.getComment_num() > 0){
            tv_comment.setText(String.valueOf(shareListData.getComment_num()));
        }
        EventSticky eventSticky = new EventSticky("refresh_one",model.getData().getType()
                ,model.getData().getId(),model.getData().getGood_num(),model.getData().getBad_num()
                ,model.getData().getComment_num());
        String goodAct = model.getData().getGood_act();
        if (goodAct != null){
            if (goodAct.equals("0")){
                eventSticky.setAct(Constant.CommentType.BAD);
                img_bad.setImageResource(R.drawable.bad_ic);
                tv_bad.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
            }else if (goodAct.equals("1")){
                img_good.setImageResource(R.drawable.good_ic);
                tv_good.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
                eventSticky.setAct(Constant.CommentType.GOOD);
            }
        }
        if (model.getData().getComment_num() != commentDataList.size()){//评论数量与服务器不一致,进行刷新
            MultipartBody.Builder builderComment = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                    .addFormDataPart("id",getIntent().getStringExtra("id"))
                    .addFormDataPart("type",getIntent().getStringExtra("type"));
            mPresenter.getComment(builderComment.build().parts());
        }
        RxBus.getDefault().postSticky(eventSticky);
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
                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                .addFormDataPart("user_id",Constant.userData.getUser_id())
                .addFormDataPart("act_id",shareListData.getId())
                .addFormDataPart("type",getIntent().getStringExtra("type"))
                .addFormDataPart("content",et_input.getText().toString());
        mPresenter.sendComment(builder.build().parts());
        setLoadDialog(true);
    }




    /**
     * 设置难度等级的显示
     * @param level
     */
    public synchronized void setDifficultLevel(int level) {
        if (level >= 0 && level <= 4){
            tv_difficult.setText(levelText[level]);
        }
        for (int i = 0 ,len = difficultList.size(); i<len ; i++ ){
            if (i <= level) {
                difficultList.get(i).setImageResource(R.drawable.difficult_back_accent);
            }else {
                difficultList.get(i).setImageResource(R.drawable.ingredient_back);
            }
        }
    }


    /**
     * 更多弹窗
     */
    public void showMoreMenu(){
        //设置contentView
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_more_menu, null);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setFocusable(true);
        //设置各个控件的点击响应
        TextView tv_collect = (TextView)contentView.findViewById(R.id.tv_collect);
        TextView tv_share = (TextView)contentView.findViewById(R.id.tv_share);
        contentView.findViewById(R.id.tv_complaint).setOnClickListener(this);
        tv_collect.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        mPopWindow.showAsDropDown(img_other,-55,0);
    }
}
