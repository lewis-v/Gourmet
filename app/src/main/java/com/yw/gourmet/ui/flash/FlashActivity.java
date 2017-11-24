package com.yw.gourmet.ui.flash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.UserData;
import com.yw.gourmet.ui.main.MainActivity;
import com.yw.gourmet.utils.SPUtils;

import okhttp3.MultipartBody;

public class FlashActivity extends BaseActivity<FlashPresenter> implements FlashConstract.View {

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        String token = null;
        token = SPUtils.getSharedStringData(getApplicationContext(),"token");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            return;
        }
        if (token == null || token.length() == 0) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .addFormDataPart("token", token);
            mPresenter.onLogin(builder.build().parts());
        }
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
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onFail(String msg) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


}