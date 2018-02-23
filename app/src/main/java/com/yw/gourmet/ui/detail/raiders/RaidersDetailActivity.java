package com.yw.gourmet.ui.detail.raiders;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.CommentAdapter;
import com.yw.gourmet.adapter.IngredientAdapter;
import com.yw.gourmet.adapter.RaidersListAdapter;
import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.CommentData;
import com.yw.gourmet.data.RaidersDetailData;
import com.yw.gourmet.data.RaidersListData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.dialog.MyDialogComplaintFragment;
import com.yw.gourmet.dialog.MyDialogMoreFragment;
import com.yw.gourmet.dialog.MyDialogRaidersListFragment;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.utils.WindowUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

import static com.yw.gourmet.utils.SoftInputUtils.hideSoftInput;

public class RaidersDetailActivity extends BaseActivity<RaidersDetailPresenter> implements
        RaidersDetailContract.View , View.OnClickListener{
    private TextView tv_title,tv_introduction,tv_comment,tv_good,tv_bad,tv_dev_input;
    private ImageView img_back,img_other;
    private ImageView img_comment,img_good,img_bad;
    private RecyclerView recycler_raiders_list,recycler_tag;
    private LinearLayout ll_comment,ll_bad,ll_good,ll_input,ll_position,ll_title;
    private RecyclerView recycler_comment;
    private EditText et_input;
    private Button bt_send;
    private CommentAdapter commentAdapter;
    private List<CommentData> commentDataList = new ArrayList<>();
    private IngredientAdapter tagAdapter;
    private RaidersListAdapter raidersListAdapter;
    private NestedScrollView scroll_raiders;
    private RaidersDetailData<List<RaidersListData<List<String>>>,List<String>> data;
    private PopupWindow mPopWindow;
    private boolean isAnimShowing = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_raiders_detail;
    }

    @Override
    protected void initView() {
        setLoadDialog(true);
        toolbar = findViewById(R.id.toolbar);
        view_parent = findViewById(R.id.view_parent);

        scroll_raiders = findViewById(R.id.scroll_raiders);

        tv_comment = findViewById(R.id.tv_comment);
        tv_good = findViewById(R.id.tv_good);
        tv_bad = findViewById(R.id.tv_bad);
        tv_dev_input = findViewById(R.id.tv_dev_input);
        tv_title = findViewById(R.id.tv_title);
        tv_introduction = findViewById(R.id.tv_introduction);

        img_back = findViewById(R.id.img_back);
        img_other = findViewById(R.id.img_other);
        img_comment = findViewById(R.id.img_comment);
        img_good = findViewById(R.id.img_good);
        img_bad = findViewById(R.id.img_bad);

        img_other.setOnClickListener(this);
        img_back.setOnClickListener(this);

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

        ll_comment = findViewById(R.id.ll_comment);
        ll_good = findViewById(R.id.ll_good);
        ll_bad = findViewById(R.id.ll_bad);
        ll_input = findViewById(R.id.ll_input);
        ll_position = findViewById(R.id.ll_position);
        ll_title = findViewById(R.id.ll_title);

        ll_comment.setOnClickListener(this);
        ll_bad.setOnClickListener(this);
        ll_good.setOnClickListener(this);

        recycler_comment = findViewById(R.id.recycler_comment);
        recycler_comment.setNestedScrollingEnabled(false);
        recycler_comment.setItemAnimator(new DefaultItemAnimator());
        recycler_comment.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this,commentDataList);
        recycler_comment.setAdapter(commentAdapter);

        recycler_tag = findViewById(R.id.recycler_tag);
        recycler_tag.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.HORIZONTAL));
        recycler_tag.setItemAnimator(new DefaultItemAnimator());

        recycler_raiders_list = findViewById(R.id.recycler_raiders_list);
        recycler_raiders_list.setNestedScrollingEnabled(false);
        recycler_raiders_list.setItemAnimator(new DefaultItemAnimator());
        recycler_raiders_list.setLayoutManager(new LinearLayoutManager(this));

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                .addFormDataPart("id",getIntent().getStringExtra("id"));
        if (Constant.userData != null){
            builder.addFormDataPart("user_id",Constant.userData.getUser_id());
        }
        mPresenter.getDetail(builder.build().parts());

        recycler_comment.post(new Runnable() {
            @Override
            public void run() {
                int height = ll_position.getHeight() + toolbar.getHeight()*65/40 + ll_title.getHeight();
                ViewGroup.LayoutParams params = recycler_comment.getLayoutParams();
                params.height = WindowUtil.height - height;
                recycler_comment.setLayoutParams(params);
            }
        });

        MultipartBody.Builder builderComment = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                .addFormDataPart("id",getIntent().getStringExtra("id"))
                .addFormDataPart("type",getIntent().getStringExtra("type"));
        mPresenter.getComment(builderComment.build().parts());
    }

    @Override
    public void onGetDetailSuccess(BaseData<RaidersDetailData<List<RaidersListData<List<String>>>, List<String>>> model) {
        setLoadDialog(false);
        data = model.getData();
        tagAdapter = new IngredientAdapter(this,data.getRaiders_type(),false);
        recycler_tag.setAdapter(tagAdapter);
        tagAdapter.notifyDataSetChanged();

        raidersListAdapter = new RaidersListAdapter(this,data.getRaiders_content(),false);
        recycler_raiders_list.setAdapter(raidersListAdapter);
        raidersListAdapter.notifyDataSetChanged();
        raidersListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                new MyDialogRaidersListFragment().setRaidersData(data.getRaiders_content().get(position))
                        .setChange(false).show(getSupportFragmentManager(),"show");
            }

            @Override
            public boolean OnLongClick(View v, int position) {
                return false;
            }
        });

        tv_title.setText(data.getTitle());
        tv_introduction.setText(data.getIntroduction());

        if (data.getBad_num() > 0){
            tv_bad.setText(String.valueOf(data.getBad_num()));
        }
        if (data.getGood_num() > 0){
            tv_good.setText(String.valueOf(data.getGood_num()));
        }
        if (data.getComment_num() > 0){
            tv_comment.setText(String.valueOf(data.getComment_num()));
        }
        String isComment = data.getIs_comment();
        if (isComment != null && isComment.length()>0){
            img_comment.setImageResource(R.drawable.comment_ic);
            tv_comment.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
        }
        String goodAct = data.getGood_act();
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
        scroll_raiders.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent().getBooleanExtra("isComment",false)){
                    scroll_raiders.fullScroll(View.FOCUS_DOWN);
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
        scroll_raiders.fullScroll(View.FOCUS_DOWN);
        recycler_comment.smoothScrollToPosition(commentDataList.size());
        et_input.setText("");
        img_comment.setImageResource(R.drawable.comment_ic);
        tv_comment.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
    }

    @Override
    public void onSendCommentFail(String msg) {
        super.onFail(msg);
    }

    @Override
    public void onRemarkSuccess(BaseData<ShareListData> model) {
        data.setGood_num(model.getData().getGood_num());
        data.setBad_num(model.getData().getBad_num());
        data.setComment_num(model.getData().getComment_num());
        if (data.getBad_num() > 0){
            tv_bad.setText(String.valueOf(data.getBad_num()));
        }
        if (data.getGood_num() > 0){
            tv_good.setText(String.valueOf(data.getGood_num()));
        }
        if (data.getComment_num() > 0){
            tv_comment.setText(String.valueOf(data.getComment_num()));
        }
        String goodAct = model.getData().getGood_act();
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
    public void onClick(View v) {
        hideSoftInput(et_input);
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_other:
                showMoreMenu();
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
                            .addFormDataPart("type",String.valueOf(data.getType()))
                            .addFormDataPart("act_id",data.getId())
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
                            .addFormDataPart("type",String.valueOf(data.getType()))
                            .addFormDataPart("act_id",data.getId())
                            .addFormDataPart("act","0");
                    mPresenter.reMark(builderGood.build().parts());
                }else {
                    ToastUtils.showSingleToast("请登陆后再进行操作");
                }
                break;

            case R.id.tv_collect:
                new MyDialogMoreFragment()
                        .setId(data.getId()).setType(data.getType()).setShare(false)
                        .show(getSupportFragmentManager(),"collect");
                break;
            case R.id.tv_share:
                new MyDialogMoreFragment().setShowCollect(false).setType(data.getType())
                        .setId(data.getId())
                        .setShareTitle(data.getTitle()).setShareCoverUrl(data.getCover())
                        .setShareDescription(data.getRaiders_content().toString()).setShareUrl(Api.API_BASE_URL + "/Share/Other?id="
                        + data.getId() + "&type=" + data.getType())
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
                                    .addFormDataPart("act_id",data.getId())
                                    .addFormDataPart("type", String.valueOf(data.getType()))
                                    .addFormDataPart("content",edit)
                                    .build().parts());
                        }
                    }).show(getSupportFragmentManager(),"complaint");
                }
                break;
        }
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
                .addFormDataPart("act_id",data.getId())
                .addFormDataPart("type",getIntent().getStringExtra("type"))
                .addFormDataPart("content",et_input.getText().toString());
        mPresenter.sendComment(builder.build().parts());
        setLoadDialog(true);
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
}
