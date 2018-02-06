package com.yw.gourmet.ui.flash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mob.pushsdk.MobPush;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.InitData;
import com.yw.gourmet.data.UserData;
import com.yw.gourmet.push.PushManager;
import com.yw.gourmet.ui.main.MainActivity;
import com.yw.gourmet.utils.SPUtils;

import java.util.List;

import okhttp3.MultipartBody;

public class FlashActivity extends BaseActivity<FlashPresenter> implements FlashConstract.View {

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        try {//todo 模拟停留
            Thread.sleep(200);
        } catch (InterruptedException e) {
            return;
        }
        mPresenter.Init();
    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_flash;
    }

    @Override
    public void onLoginSuccess(BaseData<UserData> model) {
        SPUtils.setSharedStringData(getApplicationContext(),"token",model.getData().getToken());
        Constant.userData = model.getData();
        PushManager.getInstance().setTag(Constant.userData.getUser_id(),PushManager.NOMAL_TAG);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onInitSuccess(BaseData<InitData> model) {
        Constant.serviceTime = model.getData().getTime() - System.currentTimeMillis();
        mPresenter.getAreaDetail();
    }

    @Override
    public void onGetAreaSuccess(BaseData<List<String>> model) {
        Constant.areaList = model.getData();
        String token = null;
        token = SPUtils.getSharedStringData(getApplicationContext(),"token");
        if (token == null || token.length() == 0) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("token", token);
            mPresenter.onLogin(builder.build().parts());
        }
    }

    @Override
    public void onInitFail(String msg) {
        super.onFail(msg);
    }

    @Override
    public void onFail(String msg) {
        PushManager.getInstance().setTag(PushManager.NOMAL_ALIAS,PushManager.NOMAL_TAG);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


}
