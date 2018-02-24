package com.yw.gourmet.ui.personal;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.ShareListAdapter;
import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.data.UserData;
import com.yw.gourmet.dialog.MyDialogMoreFragment;
import com.yw.gourmet.dialog.MyDialogPhotoChooseFragment;
import com.yw.gourmet.dialog.MyDialogPhotoShowFragment;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.listener.OnMoreListener;
import com.yw.gourmet.listener.OnReMarkListener;
import com.yw.gourmet.rxbus.EventSticky;
import com.yw.gourmet.ui.changeDetail.ChangeDetailActivity;
import com.yw.gourmet.ui.chat.ChatActivity;
import com.yw.gourmet.ui.detail.common.CommonDetailActivity;
import com.yw.gourmet.ui.detail.diary.DiaryDetailActivity;
import com.yw.gourmet.ui.detail.menu.MenuDetailActivity;
import com.yw.gourmet.ui.detail.raiders.RaidersDetailActivity;
import com.yw.gourmet.ui.myShare.MyShareActivity;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.widget.GlideCircleTransform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.yw.gourmet.Constant.CommentType.BAD;
import static com.yw.gourmet.Constant.CommentType.COMMENT;
import static com.yw.gourmet.Constant.CommentType.GOOD;

public class PersonalActivity extends BaseActivity<PersonalPresenter> implements PersonalContract.View
        ,View.OnClickListener, MyDialogPhotoChooseFragment.OnCropListener {
    private CollapsingToolbarLayout toolbar_layout;
    private AppBarLayout app_bar;
    private LinearLayout ll_tool,ll_change_detail,ll_change_bottom,ll_change_top;
    private AnimatorSet animatorSetToolBarShow,animatorSetToolBarHide;//toolbar的显示隐藏动画
    private TextView tv_nickname,tv_sex,tv_address,tv_introduction,tv_change_back,tv_more,tv_nothing,tv_send;
    private FloatingActionButton float_action_header;
    private ImageView img_tool_back,img_header;
    private LinearLayout ll_nothing;
    private String id ;//此界面的用户id
    private UserData userData;
    private RecyclerView recycler_top;
    private ShareListAdapter adapter;
    private List<ShareListData<List<String>>> listTop = new ArrayList<>();

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_nickname = (TextView)findViewById(R.id.tv_nickname);
        tv_sex = (TextView)findViewById(R.id.tv_sex);
        tv_address = (TextView)findViewById(R.id.tv_address);
        tv_introduction = (TextView)findViewById(R.id.tv_introduction);
        tv_change_back = (TextView)findViewById(R.id.tv_change_back);
        tv_more = findViewById(R.id.tv_more);
        tv_nothing = findViewById(R.id.tv_nothing);
        tv_send = findViewById(R.id.tv_send);
        tv_send.setOnClickListener(this);
        tv_more.setOnClickListener(this);
        tv_change_back.setOnClickListener(this);

        img_tool_back = (ImageView)findViewById(R.id.img_tool_back);
        img_header = (ImageView)findViewById(R.id.img_header);

        img_header.setOnClickListener(this);

        float_action_header = (FloatingActionButton)findViewById(R.id.float_action_header);
        float_action_header.setOnClickListener(this);

        ll_tool = (LinearLayout) findViewById(R.id.ll_tool);
        ll_change_bottom = (LinearLayout)findViewById(R.id.ll_change_bottom);
        ll_change_top = (LinearLayout)findViewById(R.id.ll_change_top);
        ll_change_detail = (LinearLayout)findViewById(R.id.ll_change_detail);
        ll_nothing = findViewById(R.id.ll_nothing);
        ll_change_detail.setOnClickListener(this);

        ObjectAnimator animatorToolBarShow = ObjectAnimator.ofFloat(ll_tool,"alpha",0f,1f);
        ObjectAnimator animatorToolBarHide = ObjectAnimator.ofFloat(ll_tool,"alpha",1f,0f);
        animatorSetToolBarShow = new AnimatorSet();
        animatorSetToolBarHide = new AnimatorSet();
        animatorSetToolBarShow.play(animatorToolBarShow);
        animatorSetToolBarHide.play(animatorToolBarHide);
        animatorSetToolBarHide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ll_tool.setVisibility(View.GONE);//隐藏控件
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        toolbar_layout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        toolbar_layout.setTitle(" ");//显示标题内容
        toolbar_layout.setCollapsedTitleTextColor(Color.BLACK);//上滑后的文本颜色
//        toolbar_layout.setContentScrimColor(Color.BLUE);//上画后的toolbar颜色
        toolbar_layout.setExpandedTitleColor(ContextCompat.getColor(this,R.color.word_black));//下滑的文本颜色
//        toolbar_layout.setStatusBarScrimColor(Color.WHITE);

        app_bar = (AppBarLayout)findViewById(R.id.app_bar);
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {//展开
                    if (animatorSetToolBarShow.isRunning()){//显示动画运行时,将其关闭
                        animatorSetToolBarShow.cancel();
                    }
                    if (!animatorSetToolBarHide.isRunning()){//启动隐藏动画
                        animatorSetToolBarHide.start();
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {//折叠
                    ll_tool.setVisibility(View.VISIBLE);
                    if (animatorSetToolBarHide.isRunning()){//隐藏动画运行时,将其关闭
                        animatorSetToolBarHide.cancel();
                    }
                    if (!animatorSetToolBarShow.isRunning()){//启动显示动画
                        animatorSetToolBarShow.start();
                    }
                } else {//变化中
//                    ll_tool.setAlpha(Math.abs(verticalOffset)%10/10);
                    if (animatorSetToolBarShow.isRunning()){//显示动画运行时,将其关闭
                        animatorSetToolBarShow.cancel();
                    }
                    if (!animatorSetToolBarHide.isRunning() && ll_tool.getVisibility() == View.VISIBLE){//隐藏动画没运行且ll_toolbar显示的时候,运行显示动画
                        ll_tool.setVisibility(View.VISIBLE);
                        animatorSetToolBarHide.start();
                    }
                }

            }
        });
        app_bar.setOnClickListener(this);

        setData();

        if (id != null) {
            recycler_top = findViewById(R.id.recycler_top);
            recycler_top.setItemAnimator(new DefaultItemAnimator());
            recycler_top.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ShareListAdapter(this, listTop, getSupportFragmentManager());
            recycler_top.setAdapter(adapter);
            mPresenter.getTop(new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                    .addFormDataPart("id", id).build().parts());
            adapter.setListener(new OnItemClickListener() {
                @Override
                public void OnClick(View v, int position) {
                    Intent intent = null;
                    switch (listTop.get(position).getType()) {
                        case Constant.TypeFlag.SHARE:
                            intent = new Intent(PersonalActivity.this, CommonDetailActivity.class);
                            break;
                        case Constant.TypeFlag.DIARY:
                            intent = new Intent(PersonalActivity.this, DiaryDetailActivity.class);
                            break;
                        case Constant.TypeFlag.MENU:
                            intent = new Intent(PersonalActivity.this, MenuDetailActivity.class);
                            break;
                        case Constant.TypeFlag.RAIDERS:
                            intent = new Intent(PersonalActivity.this, RaidersDetailActivity.class);
                            break;
                    }
                    if (intent != null){
                        intent.putExtra("id",listTop.get(position).getId());
                        intent.putExtra("type",String.valueOf(listTop.get(position).getType()));
                        startActivity(intent);
                    }

                }

                @Override
                public boolean OnLongClick(View v, int position) {
                    return false;
                }
            });
            adapter.setOnMoreListener(new OnMoreListener() {
                @Override
                public void OnMoreClick(View view, int position) {
                    MyDialogMoreFragment myDialogMoreFragment = new MyDialogMoreFragment()
                            .setId(listTop.get(position).getId());
                    switch (listTop.get(position).getType()) {
                        case Constant.TypeFlag.SHARE://普通分享
                            myDialogMoreFragment.setShare(false).setType(listTop.get(position).getType())
                                    .show(getSupportFragmentManager(), "share");
                            break;
                        case Constant.TypeFlag.DIARY://日记分享
                        case Constant.TypeFlag.MENU://食谱分享
                        case Constant.TypeFlag.RAIDERS://攻略分享
                            myDialogMoreFragment.setShareCoverUrl(listTop.get(position).getCover())
                                    .setId(listTop.get(position).getId())
                                    .setType(listTop.get(position).getType())
                                    .setShareDescription(listTop.get(position).getContent())
                                    .setShareTitle(listTop.get(position).getTitle())
                                    .setShareUrl(Api.API_BASE_URL + "/Share/Other?id="
                                            + listTop.get(position).getId() + "&type=" + listTop.get(position).getType())
                                    .show(getSupportFragmentManager(), "share");
                            break;
                    }
                }
            });
            adapter.setOnReMarkListener(new OnReMarkListener() {
                @Override
                public void OnGoodClick(View view, int position) {
                    if (Constant.userData == null){
                        ToastUtils.showSingleToast("请登陆后再进行操作");
                    }else {
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id", Constant.userData.getUser_id())
                                .addFormDataPart("type",listTop.get(position).getType()+"")
                                .addFormDataPart("act_id",listTop.get(position).getId())
                                .addFormDataPart("act","1");
                        mPresenter.reMark(builder.build().parts(),position);
                    }
                }

                @Override
                public void OnBadClick(View view, int position) {
                    if (Constant.userData == null){
                        ToastUtils.showSingleToast("请登陆后再进行操作");
                    }else {
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id", Constant.userData.getUser_id())
                                .addFormDataPart("type",listTop.get(position).getType()+"")
                                .addFormDataPart("act_id",listTop.get(position).getId())
                                .addFormDataPart("act","0");
                        mPresenter.reMark(builder.build().parts(),position);
                    }
                }

                @Override
                public void OnCommentClick(View view, int position) {
                    Intent intent = null;
                    switch (listTop.get(position).getType()) {
                        case Constant.TypeFlag.SHARE:
                            intent = new Intent(PersonalActivity.this, CommonDetailActivity.class);
                            break;
                        case Constant.TypeFlag.DIARY:
                            intent = new Intent(PersonalActivity.this, DiaryDetailActivity.class);
                            break;
                        case Constant.TypeFlag.MENU:
                            intent = new Intent(PersonalActivity.this, MenuDetailActivity.class);
                            break;
                        case Constant.TypeFlag.RAIDERS:
                            intent = new Intent(PersonalActivity.this, RaidersDetailActivity.class);
                            break;
                    }
                    if (intent != null){
                        intent.putExtra("id",listTop.get(position).getId());
                        intent.putExtra("type",String.valueOf(listTop.get(position).getType()));
                        intent.putExtra("isComment",true);
                        startActivity(intent);
                    }
                }
            });
        }
        setRxBus();
    }

    /**
     * 设置显示的个人数据
     */
    public void setData(){
        id = getIntent().getStringExtra("id");
        if (Constant.userData == null || id != null && !id.equals(Constant.userData.getUser_id())){//查看其它用户信息
            setLoadDialog(true);
            ll_change_detail.setVisibility(View.GONE);
            ll_change_bottom.setVisibility(View.GONE);
            ll_change_top.setVisibility(View.GONE);
            tv_send.setVisibility(View.VISIBLE);
            mPresenter.getUserInfo(new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                    .addFormDataPart("id",id).build().parts());
        }else {//查看自身信息
            userData = Constant.userData;
            id = Constant.userData.getUser_id();
            tv_send.setVisibility(View.GONE);
            ll_change_detail.setVisibility(View.VISIBLE);
            ll_change_bottom.setVisibility(View.VISIBLE);
            ll_change_top.setVisibility(View.VISIBLE);
            tv_nickname.setText(Constant.userData.getNickname());
            tv_sex.setText(Constant.userData.getSex());
            tv_address.setText(Constant.userData.getAddress());
            tv_introduction.setText(Constant.userData.getIntroduction());
            GlideApp.with(this).load(Constant.userData.getImg_header()).placeholder(R.mipmap.loading).error(R.mipmap.load_fail)
                    .transform(new GlideCircleTransform(this)).into(float_action_header);
            GlideApp.with(this).load(Constant.userData.getImg_header())
                    .placeholder(R.mipmap.loading).error(R.mipmap.load_fail)
                    .transform(new GlideCircleTransform(this))
                    .into(img_header);
            GlideApp.with(this).load(Constant.userData.getPersonal_back())
                    .placeholder(R.mipmap.loading).error(R.mipmap.load_fail).into(img_tool_back);
        }
    }

    @Override
    public void onGetEvent(EventSticky eventSticky) {
        super.onGetEvent(eventSticky);
        switch (eventSticky.event){
            case "change_detail"://修改了个人信息,这里刷新置顶列表就可以了
                mPresenter.getTop(new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                        .addFormDataPart("id", id).build().parts());
                break;
            case "refresh_one"://刷新点赞评价
                int pos = adapter.getPosition(eventSticky.type,eventSticky.id);
                if (pos > -1){
                    listTop.get(pos).setGood_num(eventSticky.good).setBad_num(eventSticky.bad)
                            .setComment_num(eventSticky.comment);
                    if (eventSticky.act == GOOD){
                        listTop.get(pos).setGood_act("1");
                    }else if(eventSticky.act == BAD){
                        listTop.get(pos).setGood_act("0");
                    }
                    adapter.notifyItemChanged(pos);
                }
                break;
            case "refresh_comment"://刷新评论
                int position = adapter.getPosition(eventSticky.type,eventSticky.id);
                if (position > -1){
                    listTop.get(position).setComment_num(eventSticky.comment);
                    if (eventSticky.act == COMMENT){
                        listTop.get(position).setIs_comment("ok");
                    }
                    adapter.notifyItemChanged(position);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.app_bar:
                if (userData.getId().equals(Constant.userData.getUser_id())) {
                    setBackChangeShow();
                }
                break;
            case R.id.ll_change_detail:
                startActivity(new Intent(this, ChangeDetailActivity.class));
                break;
            case R.id.img_header:
            case R.id.float_action_header:
                new MyDialogPhotoShowFragment().addImgString(userData.getImg_header()).show(getSupportFragmentManager(),"header");
                break;
            case R.id.tv_change_back:
                new MyDialogPhotoChooseFragment().setCrop(true).setChooseNum(1).setRatio(2/3f)
                        .setOnCropListener(this).show(getSupportFragmentManager(),"changeBack");
                break;
            case R.id.tv_more:
                Intent intent = new Intent(this, MyShareActivity.class);
                intent.putExtra("id",userData.getId());
                startActivity(intent);
                break;
            case R.id.tv_send:
                if (Constant.userData == null){
                    ToastUtils.showSingleToast("请登陆后再操作");
                }else {
                    Intent intent1 = new Intent(this, ChatActivity.class);
                    String put_id = Constant.userData.getUser_id();
                    intent1.putExtra("put_id", put_id);
                    intent1.putExtra("get_id", id);
                    startActivity(intent1);
                }
                break;
        }
    }

    /**
     * 设置更改背景的点击控件的显示与隐藏
     */
    public synchronized void setBackChangeShow(){
        ObjectAnimator objectAnimator;
        final boolean isShow;
        if (tv_change_back.getVisibility() == View.VISIBLE){//原来显示,设为隐藏
            isShow = false;
            objectAnimator = ObjectAnimator.ofFloat(tv_change_back,"translationX",0,-tv_change_back.getWidth());
        }else {
            isShow = true;
            objectAnimator = ObjectAnimator.ofFloat(tv_change_back,"translationX",-tv_change_back.getWidth(),0);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(objectAnimator);
        tv_change_back.setVisibility(View.VISIBLE);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow){
                    tv_change_back.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    @Override
    public void OnCrop(String path, String tag) {
        setLoadDialog(true);
        new Compressor(this).compressToFileAsFlowable(new File(path))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id",Constant.userData.getUser_id())
                                .addFormDataPart("path",file.getName(), RequestBody.create(MediaType.parse(""),file));
                        mPresenter.upImg(builder.build().parts());
                    }
                });
    }

    @Override
    public void onUpImgSuccess(BaseData<String> model) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                .setType(MultipartBody.FORM)
                .addFormDataPart("id",Constant.userData.getUser_id())
                .addFormDataPart("personal_back",model.getData());
        mPresenter.changeBack(builder.build().parts());
    }

    @Override
    public void onChangeSuccess(BaseData<UserData> model) {
        setLoadDialog(false);
        Constant.userData = model.getData();
        setData();
        ToastUtils.showLongToast(model.getMessage());
    }

    @Override
    public void onGetTopSuccess(BaseData<List<ShareListData<List<String>>>> model) {
        if (model.getData().size()>0) {
            listTop.clear();
            listTop.addAll(model.getData());
            adapter.notifyDataSetChanged();
            ll_nothing.setVisibility(View.GONE);
        }else{
            ll_nothing.setVisibility(View.VISIBLE);
            tv_nothing.setText("没有置顶内容哟");
        }
    }

    @Override
    public void OnGetTopFail(String msg) {

    }

    @Override
    public void onReMarkSuccess(BaseData<ShareListData<List<String>>> model,int position) {
        listTop.set(position,model.getData());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onGetUserInfoSuccess(BaseData<UserData> model) {
        setLoadDialog(false);
        userData = model.getData();
        tv_nickname.setText(model.getData().getNickname());
        tv_sex.setText(model.getData().getSex());
        tv_address.setText(model.getData().getAddress());
        tv_introduction.setText(model.getData().getIntroduction());
        GlideApp.with(this).load(model.getData().getImg_header()).error(R.mipmap.load_fail)
                .placeholder(R.mipmap.loading)
                .transform(new GlideCircleTransform(this)).into(float_action_header);
        GlideApp.with(this).load(model.getData().getImg_header()).error(R.mipmap.load_fail)
                .placeholder(R.mipmap.loading)
                .transform(new GlideCircleTransform(this)).into(img_header);
        GlideApp.with(this).load(model.getData().getPersonal_back())
                .placeholder(R.mipmap.loading).error(R.mipmap.load_fail).into(img_tool_back);
    }

    @Override
    public void onGetUserInfoFail(String msg) {
        super.onFail(msg);
        finish();
    }
}
