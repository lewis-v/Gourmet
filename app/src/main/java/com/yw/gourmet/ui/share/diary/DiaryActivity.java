package com.yw.gourmet.ui.share.diary;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.dao.data.SaveData;
import com.yw.gourmet.dao.data.SaveDataUtil;
import com.yw.gourmet.dao.gen.SaveDataDao;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.dialog.MyDialogPhotoChooseFragment;
import com.yw.gourmet.dialog.MyDialogTipFragment;
import com.yw.gourmet.listener.MyAction;
import com.yw.gourmet.listener.OnCancelClickListener;
import com.yw.gourmet.listener.OnToolClickListener;
import com.yw.gourmet.ui.share.ToolFragment;
import com.yw.gourmet.ui.share.ToolType;
import com.yw.gourmet.utils.ToastUtils;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.richeditor.RichEditor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class DiaryActivity extends BaseActivity<DiaryPresenter> implements View.OnClickListener,OnToolClickListener
        , MyDialogPhotoChooseFragment.OnCropListener,DiaryContract.View {
    private EditText et_title;
    private TextView tv_address,tv_time,tv_auth,tv_cancel,tv_send,tv_power;
    private RichEditor richeditor_diary;
    private ImageView img_tool;
    private FrameLayout fl_tool;
    private String content = "";
    private boolean isShowTool = false;//工具栏是否显示
    private ToolFragment toolFragment;
    private ToolType type;
    private boolean isTouch = false;//是否触发一个点击功能
    private long create_time;//创建时间
    private int status = 1;//权限,公开或私有,1公开,0私有,默认公开
    private boolean toolShowing = false;//工具栏动画是否在展示中
    private SaveData saveData,saveDataCache;
    private double lat,lng;//坐标

    @Override
    protected void initView() {
        view_parent = findViewById(R.id.view_parent);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        create_time = System.currentTimeMillis();
        et_title = (EditText)findViewById(R.id.et_title);

        fl_tool = (FrameLayout)findViewById(R.id.fl_tool);

        tv_address = (TextView)findViewById(R.id.tv_address);
        tv_time = (TextView)findViewById(R.id.tv_time);
        tv_auth = (TextView)findViewById(R.id.tv_auth);
        tv_cancel = (TextView)findViewById(R.id.tv_cancel);
        tv_send = (TextView)findViewById(R.id.tv_send);
        tv_power = (TextView)findViewById(R.id.tv_power);

        tv_auth.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        tv_auth.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        tv_power.setOnClickListener(this);

        img_tool = (ImageView)findViewById(R.id.img_tool);
        img_tool.setOnClickListener(this);

        if (Constant.userData != null){
            tv_auth.setText("作者:"+Constant.userData.getNickname());
        }

        richeditor_diary = (RichEditor)findViewById(R.id.richeditor_diary);
        richeditor_diary.focusEditor();
        richeditor_diary.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                content = text;
            }
        });
        richeditor_diary.setPlaceholder("请输入日记内容");
        richeditor_diary.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                content = text;
            }
        });

        type = new ToolType();

        String mType = getIntent().getStringExtra("type");
        if (mType != null){
            List<SaveData> data ;
            switch (mType){
                case "new":
                    data = SaveDataUtil
                            .querydataById(SaveDataDao.Properties.Type.eq(Constant.TypeFlag.DIARY)
                                    , SaveDataDao.Properties.User_id.eq(Constant.userData.getUser_id()));
                    if (data != null && data.size()>0) {
                        saveDataCache = data.get(0);
                        new MyDialogTipFragment().setTextEnter("是").setTextCancel("否")
                                .setShowText("草稿箱中存在未完成日记,是否继续上次的编辑?")
                                .setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                                    @Override
                                    public void OnEnter(String Tag) {
                                        initSaveData(saveDataCache);
                                    }
                                }).show(getSupportFragmentManager(), "tip");
                    }
                    break;
                case "change":
                    data = SaveDataUtil
                            .querydataById(SaveDataDao.Properties.Type.eq(Constant.TypeFlag.DIARY)
                            ,SaveDataDao.Properties._id.eq(getIntent().getLongExtra("_id",0)));
                    if (data != null && data.size()>0) {
                        saveDataCache = data.get(0);
                        initSaveData(saveDataCache);
                    }
                    break;
            }
        }
    }

    public void initSaveData(SaveData saveData){
        if (saveData != null){
            this.saveData = saveDataCache;
            et_title.setText(saveData.getTitle());
            tv_address.setText(saveData.getAddress());
            lat = saveData.getLat();
            lng = saveData.getLng();
            tv_time.setText(saveData.getTime());
            status = saveData.getStatus();
            if (status == 1){
                tv_power.setText("公开");
            }else if (status == 0){
                tv_power.setText("私有");
            }
            content = saveData.getContent();
            richeditor_diary.setHtml(saveData.getContent());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_diary;
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_address:

                break;
            case R.id.tv_time:
                setTime();
                break;
            case R.id.img_tool:
                addFragmentFunction(!isShowTool,null);
                break;
            case R.id.tv_cancel:
                back();
                break;
            case R.id.tv_power:
                if (status == 1){
                    status = 0;
                    tv_power.setText("私有");
                }else {
                    status = 1;
                    tv_power.setText("公开");
                }
                break;
            case R.id.tv_send:
                if (isEmpty()){
                    break;
                }
                new MyDialogTipFragment().setShowText("是否分享您的日记")
                        .setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                    @Override
                    public void OnEnter(String Tag) {
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id",Constant.userData.getUser_id())
                                .addFormDataPart("time",tv_time.getText().toString())
                                .addFormDataPart("title",et_title.getText().toString())
                                .addFormDataPart("content",content)
                                .addFormDataPart("create_time",Long.toString(create_time))
                                .addFormDataPart("status",Integer.toString(status));
                        mPresenter.putDiary(builder.build().parts());
                    }
                }).show(getSupportFragmentManager(),"share");
                break;
        }
    }

    /**
     * 对图片进行重置大小，宽度就是手机屏幕宽度，高度根据宽度比便自动缩放
     **/
    private void imgReset() {
        richeditor_diary.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "var img = objs[i];   " +
                "    img.style.maxWidth = '80%'; img.style.height = 'auto';  " +
                "}" +
                "})()");
    }

    public void addFragmentFunction(final boolean isShow, final MyAction action){
        if (toolShowing){
            return;
        }
        toolShowing = true;
        fl_tool.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ObjectAnimator objectAnimator = null;
        if (isShow){
            if (toolFragment == null){
                toolFragment = new ToolFragment().setOnToolClickListener(this).setType(type);
            }
            fragmentTransaction.add(R.id.fl_tool,toolFragment);
            objectAnimator = ObjectAnimator.ofFloat(img_tool,"rotation",0,-45);//加号的旋转动画
        }else {
            if (toolFragment != null) {
                fragmentTransaction.remove(toolFragment);
                objectAnimator = ObjectAnimator.ofFloat(img_tool,"rotation",-45,0);//加号的旋转动画
            }
        }
        fragmentTransaction.runOnCommit(new Runnable() {
            @Override
            public void run() {
                if (action != null) {
                    action.Action0();
                }
                isShowTool = isShow;
                toolShowing = false;
            }
        });
        fragmentTransaction.commit();
        if (objectAnimator != null) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimator);
            animatorSet.start();
        }
    }

    @Override
    public void OnImgChoose() {
        new MyDialogPhotoChooseFragment().setChooseNum(1).setCrop(true)
                .setOnCropListener(this).show(getSupportFragmentManager(),"diary");
    }

    @Override
    public void OnBold() {
        richeditor_diary.setBold();
    }

    @Override
    public void OnItalic() {
        richeditor_diary.setItalic();
    }

    @Override
    public void OnTextLeft() {
        richeditor_diary.setAlignLeft();
    }

    @Override
    public void OnTextCenter() {
        richeditor_diary.setAlignCenter();
    }

    @Override
    public void OnTextRight() {
        richeditor_diary.setAlignRight();
    }

    @Override
    public void OnCrop(String path, String tag) {
        setLoadDialog(true);
        new Compressor(this)
                .compressToFileAsFlowable(new File(path))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        MultipartBody.Builder builder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("id",Constant.userData.getUser_id())
                                .addFormDataPart("path",file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),file));
                        mPresenter.upImg(builder.build().parts());
                    }
                });
    }

    @Override
    public void onUpImgSuccess(BaseData<String> model) {
        setLoadDialog(false);
        richeditor_diary.insertImage(model.getData(),"img");
        imgReset();
    }

    @Override
    public void onPutSuccess(BaseData model) {
        ToastUtils.showLongToast(model.getMessage());
        try {
            if (saveData != null) {
                SaveDataUtil.delete(saveData.get_id());
            }
        }catch (Exception e){}
        finish();
    }

    /**
     * 调用系统时间选择器,设置时间
     */
    public void setTime(){
        if (isTouch){
            return;
        }
        isTouch = true;
        Calendar c1 = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set( year,monthOfYear,dayOfMonth);
                tv_time.setText(DateFormat.format("yyyy-MM-dd",calendar));
                isTouch = false;
            }
        }, c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH));
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isTouch = false;
            }
        });
        dialog.show();
    }

    /**
     * 判断填写的必填项是否为空
     */
    public boolean isEmpty(){
        if (et_title.getText().toString().trim().isEmpty()){
            ToastUtils.showLongToast("请填写标题");
            return true;
        }else if (tv_time.getText().toString().trim().isEmpty()){
            ToastUtils.showLongToast("请填写日记时间");
            return true;
        }else if (content.isEmpty()){
            ToastUtils.showLongToast("请填写日记内容");
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        richeditor_diary.destroy();
        super.onDestroy();
    }

    /**
     * 判断是否需要存储
     * @return
     */
    public boolean isNeedSave(){
        if (!et_title.getText().toString().trim().isEmpty()){
            return true;
        }else if (!tv_time.getText().toString().trim().isEmpty()){
            return true;
        }else if (!tv_address.getText().toString().trim().isEmpty()){
            return true;
        }else if (!content.trim().isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * 退出操作
     */
    public void back(){
        if (isNeedSave()){
            new MyDialogTipFragment().setShowText("是否将此次编辑内容保存到草稿箱?")
                    .setTextCancel("否")
                    .setTextEnter("是")
                    .setOnCancelClickListener(new OnCancelClickListener() {
                        @Override
                        public void OnCancel(String tag) {
                            finish();
                        }
                    })
                    .setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                        @Override
                        public void OnEnter(String Tag) {
                            SaveData saveData = new SaveData();
                            saveData.setUser_id(Constant.userData.getUser_id())
                                    .setChange_time(System.currentTimeMillis())
                                    .setTitle(et_title.getText().toString())
                                    .setContent(content)
                                    .setTime(tv_time.getText().toString())
                                    .setStatus(status)
                                    .setAddress(tv_address.getText().toString())
                                    .setLat(lat).setLng(lng)
                                    .setType(Constant.TypeFlag.DIARY);
                            if (DiaryActivity.this.saveData == null) {
                                saveData.set_id(System.currentTimeMillis());
                                SaveDataUtil.insert(saveData);
                            }else {
                                saveData.set_id(DiaryActivity.this.saveData.get_id());
                                SaveDataUtil.updata(saveData);
                            }
                            finish();
                        }
                    })
                    .show(getSupportFragmentManager(),"isSave");
        }else {
            finish();
        }
    }
}
