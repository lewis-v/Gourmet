package com.yw.gourmet.ui.personal;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.yw.gourmet.ui.changeDetail.ChangeDetailActivity;
import com.yw.gourmet.ui.detail.common.CommonDetailActivity;
import com.yw.gourmet.ui.detail.diary.DiaryDetailActivity;
import com.yw.gourmet.ui.detail.menu.MenuDetailActivity;
import com.yw.gourmet.ui.detail.raiders.RaidersDetailActivity;
import com.yw.gourmet.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PersonalActivity extends BaseActivity<PersonalPresenter> implements PersonalContract.View
        ,View.OnClickListener, MyDialogPhotoChooseFragment.OnCropListener {
    private CollapsingToolbarLayout toolbar_layout;
    private AppBarLayout app_bar;
    private LinearLayout ll_tool,ll_change_detail,ll_change_bottom,ll_change_top;
    private AnimatorSet animatorSetToolBarShow,animatorSetToolBarHide;//toolbar的显示隐藏动画
    private TextView tv_nickname,tv_sex,tv_address,tv_introduction,tv_change_back;
    private FloatingActionButton float_action_header;
    private ImageView img_tool_back,img_header;
    private String id ;//打开此界面的用户id
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
                    .addFormDataPart("id", Constant.userData.getId()).build().parts());
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
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id", Constant.userData.getId())
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
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id", Constant.userData.getId())
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
    }

    /**
     * 设置显示的个人数据
     */
    public void setData(){
        id = getIntent().getStringExtra("id");
        if (id != null && !id.equals(Constant.userData.getId())){//查看其它用户信息
            ll_change_detail.setVisibility(View.GONE);
            ll_change_bottom.setVisibility(View.GONE);
            ll_change_top.setVisibility(View.GONE);

        }else {//查看自身信息
            if (Constant.userData != null) {
                id = Constant.userData.getId();
                ll_change_detail.setVisibility(View.VISIBLE);
                ll_change_bottom.setVisibility(View.VISIBLE);
                ll_change_top.setVisibility(View.VISIBLE);
                tv_nickname.setText(Constant.userData.getNickname());
                tv_sex.setText(Constant.userData.getSex());
                tv_address.setText(Constant.userData.getAddress());
                tv_introduction.setText(Constant.userData.getIntroduction());
                GlideApp.with(this).load(Constant.userData.getImg_header()).error(R.mipmap.load_fail).into(float_action_header);
                GlideApp.with(this).load(Constant.userData.getImg_header()).error(R.mipmap.load_fail).into(img_header);
                GlideApp.with(this).load(Constant.userData.getPersonal_back()).error(R.mipmap.load_fail).into(img_tool_back);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.app_bar:
                setBackChangeShow();
                break;
            case R.id.ll_change_detail:
                startActivity(new Intent(this, ChangeDetailActivity.class));
                break;
            case R.id.img_header:
            case R.id.float_action_header:
                new MyDialogPhotoShowFragment().addImgString(Constant.userData.getImg_header()).show(getSupportFragmentManager(),"header");
                break;
            case R.id.tv_change_back:
                new MyDialogPhotoChooseFragment().setCrop(true).setChooseNum(1).setRatio(2/3f)
                        .setOnCropListener(this).show(getSupportFragmentManager(),"changeBack");
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
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id",Constant.userData.getId())
                                .addFormDataPart("path",file.getName(), RequestBody.create(MediaType.parse(""),file));
                        mPresenter.upImg(builder.build().parts());
                    }
                });
    }

    @Override
    public void onUpImgSuccess(BaseData<String> model) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id",Constant.userData.getId())
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
        listTop.clear();
        listTop.addAll(model.getData());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void OnGetTopFail(String msg) {

    }

    @Override
    public void onReMarkSuccess(BaseData<ShareListData<List<String>>> model,int position) {
        listTop.set(position,model.getData());
        adapter.notifyDataSetChanged();
    }
}
