package com.yw.gourmet.ui.set;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.audio.play.AudioPlayMode;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.dialog.MyDialogFeedBackFragment;
import com.yw.gourmet.dialog.MyDialogTipFragment;
import com.yw.gourmet.ui.about.AboutActivity;
import com.yw.gourmet.ui.chat.ChatActivity;
import com.yw.gourmet.ui.passwordChange.PasswordChangeActivity;
import com.yw.gourmet.utils.SPUtils;
import com.yw.gourmet.utils.SizeChangeUtils;
import com.yw.gourmet.utils.ToastUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SetActivity extends BaseActivity<SetPresenter> implements View.OnClickListener,SetContract.View{
    private LinearLayout ll_back,ll_clear,ll_about,ll_change_password,ll_customer_service,ll_feedback;
    private TextView tv_out;
    private SwitchCompat switch_audio;
    private final static String path = Environment.getExternalStorageDirectory().getPath() + "/data/gourmet/Img/";//图片存储目录
    private List<File> clearFiles = Arrays.asList(new File(path));//可以清理的文件目录

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ll_about = findViewById(R.id.ll_about);
        ll_back = (LinearLayout)findViewById(R.id.ll_back);
        ll_clear = (LinearLayout)findViewById(R.id.ll_clear);
        ll_change_password = findViewById(R.id.ll_change_password);
        ll_customer_service = findViewById(R.id.ll_customer_service);
        ll_feedback = findViewById(R.id.ll_feedback);
        ll_feedback.setOnClickListener(this);
        ll_customer_service.setOnClickListener(this);
        ll_change_password.setOnClickListener(this);
        ll_clear.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        ll_about.setOnClickListener(this);

        tv_out = (TextView)findViewById(R.id.tv_out);
        tv_out.setOnClickListener(this);

        switch_audio = findViewById(R.id.switch_audio);
        AudioPlayMode audioMode = AudioPlayMode.getByTypeName(SPUtils.getSharedIntData(this,"audio_play_mode"));
        if (audioMode == AudioPlayMode.RECEIVER){
            switch_audio.setChecked(true);
        }else {
            switch_audio.setChecked(false);
        }
        switch_audio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SPUtils.setSharedIntData(SetActivity.this,"audio_play_mode",AudioPlayMode.RECEIVER.getTypeName());
                }else {
                    SPUtils.setSharedIntData(SetActivity.this,"audio_play_mode",AudioPlayMode.MEGAPHONE.getTypeName());
                }
            }
        });
    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_set;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_out:
                new MyDialogTipFragment().setShowText("是否退出当前账号").setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                    @Override
                    public void OnEnter(String Tag) {
                        onReLoginFail("退出账号");
                    }
                }).show(getSupportFragmentManager(),"out");
                break;
            case R.id.ll_clear:
                setLoadDialog(true);
                mPresenter.clearFile(clearFiles);
                break;
            case R.id.ll_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.ll_change_password:
                if (Constant.userData == null){
                    ToastUtils.showSingleToast("请登录后在操作");
                }else {
                    startActivity(new Intent(this, PasswordChangeActivity.class));
                }
                break;
            case R.id.ll_customer_service:
                if (Constant.userData == null){
                    ToastUtils.showSingleToast("请登陆后再操作");
                }else {
                    Intent intent1 = new Intent(this, ChatActivity.class);
                    String put_id = Constant.userData.getUser_id();
                    intent1.putExtra("put_id", put_id);
                    intent1.putExtra("get_id", "0");
                    startActivity(intent1);
                }
                break;
            case R.id.ll_feedback:
                if (Constant.userData == null){
                    ToastUtils.showSingleToast("请登陆后再操作");
                }else {
                    MyDialogFeedBackFragment.getInstance().show(getSupportFragmentManager(), "");
                }
                break;
        }
    }

    @Override
    public void onClearSuccess(Long clearSize) {
        setLoadDialog(false);
        ToastUtils.showLongToast("清理成功:"+ SizeChangeUtils.getSizeByBytes(clearSize));
        File file = new File(path);
        if (!file.exists()){//不存在目录则创建目录
            file.mkdirs();
        }
    }
}
