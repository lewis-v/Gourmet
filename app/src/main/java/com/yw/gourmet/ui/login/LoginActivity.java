package com.yw.gourmet.ui.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;

public class LoginActivity extends LoginContract.View implements View.OnClickListener{
    private EditText et_id,et_password;
    private Button bt_login;

    @Override
    protected void initView() {
        et_id = (EditText)findViewById(R.id.et_id);
        et_password = (EditText)findViewById(R.id.et_password);
        bt_login = (Button)findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_login:
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .addFormDataPart("id",et_id.getText().toString())
                        .addFormDataPart("password",et_password.getText().toString());
                mPresenter.login(builder.build().parts());
                break;
        }
    }

    /**
     * 登录成功
     *
     * @param model
     */
    @Override
    void onLoginSuccess(BaseData model) {
        onSuccess(model.getMessage());
        finish();
    }
}
