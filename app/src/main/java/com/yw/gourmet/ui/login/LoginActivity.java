package com.yw.gourmet.ui.login;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.UserData;

import okhttp3.MultipartBody;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.View,View.OnClickListener{
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
                if (mPresenter == null){
                    Log.i("---null---","1");
                }else {
                    mPresenter.login(builder.build().parts());
                }
                break;
        }
    }

    /**
     * 登录成功
     *
     * @param model
     */
    @Override
    public void onLoginSuccess(BaseData<UserData> model) {
        onSuccess(model.getMessage());
        Constant.userData = model.getData();
        finish();
    }
}
