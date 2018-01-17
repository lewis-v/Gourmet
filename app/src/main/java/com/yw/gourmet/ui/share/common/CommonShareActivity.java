package com.yw.gourmet.ui.share.common;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.ImgAddAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.dialog.MyDialogPhotoChooseFragment;
import com.yw.gourmet.dialog.MyDialogPhotoChooseFragment.OnChooseLister;
import com.yw.gourmet.dialog.MyDialogTipFragment;
import com.yw.gourmet.listener.OnAddListener;
import com.yw.gourmet.listener.OnDeleteListener;
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.utils.ThreadUtils;
import com.yw.gourmet.utils.ToastUtils;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import id.zelory.compressor.Compressor;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CommonShareActivity extends BaseActivity<CommonSharePresenter> implements CommonShareContract.View
        ,View.OnClickListener,OnChooseLister{
    private TextView tv_cancel,tv_send,tv_count;
    private EditText et_content;
    private RecyclerView recycler_share;
    private ImgAddAdapter addAdapter;
    private List<String> imgs = new ArrayList<>();
    private List<String> upImg = new ArrayList<>();
    private int maxSize = 9;//最大照片数量
    private int upPosition = 0;//正在上传的位置
    private int loopPosition = 0;//设置上传队列位置
    private int failNum = 0;//上传失败数量
    private boolean isUpLoad = false;//是否正在上传
    private boolean isCompress = false;//是否正在压缩
    private ExecutorService executorService,executorServiceCompress;
    private List<Future<Integer>> future = new ArrayList<>();
    private int status = 1;//权限,1为公开,0为私有,默认公开

    @Override
    protected void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_cancel = (TextView)findViewById(R.id.tv_cancel);
        tv_send = (TextView)findViewById(R.id.tv_send);
        tv_count = (TextView)findViewById(R.id.tv_count);
        tv_cancel.setOnClickListener(this);
        tv_send.setOnClickListener(this);

        et_content = (EditText)findViewById(R.id.et_content);
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_count.setText(String.valueOf(charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        recycler_share = (RecyclerView)findViewById(R.id.recycler_share);
        recycler_share.setLayoutManager(new StaggeredGridLayoutManager(3
                ,StaggeredGridLayoutManager.VERTICAL));
        recycler_share.setItemAnimator(new DefaultItemAnimator());
        addAdapter = new ImgAddAdapter(imgs,this,maxSize);
        recycler_share.setAdapter(addAdapter);
        addAdapter.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void OnDelete(View v, int position) {
            }
        });
        addAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {

            }

            @Override
            public boolean OnLongClick(View v, int position) {
                return false;
            }
        });
        addAdapter.setOnAddListener(new OnAddListener() {
            @Override
            public void OnAdd(View view, int position) {
                new MyDialogPhotoChooseFragment().setChooseNum(maxSize - imgs.size())
                        .setOnPhotoChooseListener(CommonShareActivity.this).show(getSupportFragmentManager(), "");
            }
        });

        String type = getIntent().getStringExtra("type");
        if (type != null){
            if (type.equals("photo")){//照片选择
                recycler_share.post(new Runnable() {
                    @Override
                    public void run() {
                        new MyDialogPhotoChooseFragment().setType(MyDialogPhotoChooseFragment.PHOTO)
                                .setChooseNum(maxSize - imgs.size()+1)
                                .setOnPhotoChooseListener(CommonShareActivity.this).show(getSupportFragmentManager(),"");
                    }
                });
            }else if (type.equals("take_photo")){//拍照
                new MyDialogPhotoChooseFragment().setType(MyDialogPhotoChooseFragment.TAKE_PHOTO)
                        .setChooseNum(maxSize - imgs.size()+1)
                        .setOnPhotoChooseListener(CommonShareActivity.this).show(getSupportFragmentManager(),"");
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment_share;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_send:
                if (et_content.getText().toString().trim().length() == 0 && imgs.size() == 0){
                    ToastUtils.showSingleToast("分享内容为空");
                }else {
                    new MyDialogTipFragment().setShowText("是否分享")
                            .setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                        @Override
                        public void OnEnter(String Tag) {
                            setLoadDialog(true);
                            for (int i = 0,len = imgs.size();i < len;i++) {
                                setUpImg(imgs.get(i), upPosition++);
                            }
                            if (executorService == null){
                                executorService = ThreadUtils.newCachedThreadPool();
                            }
                            executorService.submit(new Runnable() {
                                @Override
                                public void run() {
                                    while (upImg.size() != imgs.size()){
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            return;
                                        }
                                    }
                                    MultipartBody.Builder builder = new MultipartBody.Builder()
                                            .addFormDataPart("id",Constant.userData.getId())
                                            .addFormDataPart("status",String.valueOf(status));
                                    if (et_content.getText().toString().trim().length() != 0){
                                        builder.addFormDataPart("content",et_content.getText().toString());
                                    }
                                    if (upImg.size() > 0){
                                        builder.addFormDataPart("img",new JSONArray(upImg).toString());
                                    }
                                    mPresenter.shareCommon(builder.build().parts());
                                }
                            });
                        }
                    }).show(getSupportFragmentManager(),"share");
                }
                break;
        }
    }

    @Override
    public void OnChoose(final List<String> img, String tag) {
        for (String path : img) {
            addAdapter.addImg(path);

        }
    }

    /**
     * 压缩上传照片
     * @param path
     * @param position
     */
    public void setUpImg(final String path, final int position){
        if (executorServiceCompress == null){
            executorServiceCompress = ThreadUtils.newSingleThreadPool();
        }
        executorServiceCompress.submit(new Runnable() {
            @Override
            public void run() {
                while (isCompress) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                isCompress = true;
                new Compressor(CommonShareActivity.this)
                        .compressToFileAsFlowable(new File(path))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Consumer<File>() {
                            @Override
                            public void accept(File file) throws Exception {
                                while (isUpLoad){
                                    Thread.sleep(100);
                                }
                                isUpLoad = true;
                                mPresenter.upImg(new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("id", Constant.userData.getId())
                                        .addFormDataPart("path", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file))
                                        .build().parts(), position);
                                isCompress = false;
                            }
                        });

            }
        });
    }

    @Override
    public void onUpImgSuccess(final BaseData<String> model, final int position) {
        isUpLoad = false;
        if (executorService == null){
            executorService = ThreadUtils.newCachedThreadPool();
        }
        future.add(executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (position != loopPosition){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                upImg.add(model.getData());
                loopPosition ++;
            }
        },position));

    }

    @Override
    public void onUpImgFail(String msg, int position) {
        isUpLoad = false;
        imgs.remove(position-failNum);
        loopPosition++;
        failNum++;
    }

    @Override
    public void onShareSuccess(BaseData model) {
        super.onSuccess(model.getMessage());
        setLoadDialog(false);
        finish();
    }

    @Override
    public void onShareFail(String msg) {
        super.onFail(msg);
        upImg.clear();
        failNum = 0;
        upPosition = 0;
        loopPosition = 0;
        setLoadDialog(false);
    }

    @Override
    protected void onDestroy() {
        if (executorService != null){
            executorService.shutdownNow();
        }
        if (executorServiceCompress != null){
            executorServiceCompress.shutdownNow();
        }
        super.onDestroy();
    }
}
