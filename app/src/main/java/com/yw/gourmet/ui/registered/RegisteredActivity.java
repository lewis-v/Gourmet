package com.yw.gourmet.ui.registered;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyFragmentAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.dialog.MyDialogTipFragment;
import com.yw.gourmet.ui.login.LoginActivity;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.widget.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

public class RegisteredActivity extends BaseActivity<RegisteredPresenter> implements RegisteredContract.View{
    private TextView tv_cancel;
    private MyViewPager viewpager_registered;
    private List<Fragment> list = new ArrayList<>();
    private MyFragmentAdapter adapter;
    private String phone;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_registered;
    }

    @Override
    protected void initView() {
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewpager_registered = findViewById(R.id.viewpager_registered);
        list.add(new RegisteredFragment());
        list.add(new SubmitCodeFragment());
        list.add(new UserInfoFragment());
        adapter = new MyFragmentAdapter(getSupportFragmentManager(),list);
        viewpager_registered.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSendSuccess(String msg) {
        if (list.size() == 3){
            viewpager_registered.setCurrentItem(1);
        }else {
            ToastUtils.showSingleToast("注册失败,请重新注册");
            finish();
        }
        super.onSuccess(msg);
    }

    @Override
    public void onSendFails(String msg) {
        super.onFail(msg);
    }

    @Override
    public void onCodeSuccess(String msg) {
        if (list.size() == 3){
            viewpager_registered.setCurrentItem(2);
        }else {
            ToastUtils.showSingleToast("注册失败,请重新注册");
            finish();
        }
        super.onSuccess(msg);
    }

    @Override
    public void onCodeFails(String msg) {
        super.onFail(msg);
    }

    @Override
    public void onCheckSuccess(BaseData model) {
        new MyDialogTipFragment().setShowText("是否使用 "+phone+" 手机号注册账号?")
                .setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
            @Override
            public void OnEnter(String Tag) {
                mPresenter.sendSMS(phone);
            }
        }).show(getSupportFragmentManager(),"phone");
    }

    @Override
    public void onCheckFails(String msg) {
        super.onFail(msg);
    }

    /**
     * 注册成功
     * @param model
     */
    @Override
    public void onRegisteredSuccess(BaseData model) {
        super.onSuccess(model.getMessage());
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("id",phone);
        startActivity(intent);
        finish();
    }

    /**
     * 发送验证码(带检测)
     * @param phone
     */
    public void sendSMS(String phone){
        this.phone = phone;
        mPresenter.Check(new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
                .addFormDataPart("token", Constant.userData == null?"0":Constant.userData.getToken())
        .addFormDataPart("accout_number",phone).build().parts());
    }

    /**
     * 发送验证码(不带检测)
     */
    public void sendSMS(){
        sendSMS(phone);
    }

    /**
     * 验证验证码
     * @param code
     */
    public void putCode(String code){
        if (phone == null || phone.length() != 11){
            ToastUtils.showSingleToast("注册失败,请重新注册");
            finish();
        }
        mPresenter.putCode(phone,code);
    }

    public void registered(String password,String sex){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                .addFormDataPart("accout_number",phone)
                .addFormDataPart("password",password)
                .addFormDataPart("sex",sex)
                .addFormDataPart("nickname",phone);
        mPresenter.Registered(builder.build().parts());
    }
}
