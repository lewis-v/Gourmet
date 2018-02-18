package com.yw.gourmet.ui.passwordChange;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.utils.ToastUtils;

import okhttp3.MultipartBody;

public class PasswordChangeActivity extends BaseActivity<PasswordChangePresenter> implements
        PasswordChangeContract.View ,View.OnClickListener{
    private EditText et_old_password,et_new_password,et_new_password2;
    private Button bt_change;
    private ImageView img_back;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_password_change;
    }

    @Override
    protected void initView() {
        et_new_password = findViewById(R.id.et_new_password);
        et_new_password2 = findViewById(R.id.et_new_password2);
        et_old_password = findViewById(R.id.et_old_password);

        bt_change = findViewById(R.id.bt_change);
        bt_change.setOnClickListener(this);

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
    }

    @Override
    public void onChangeSuccess(BaseData model) {
        ToastUtils.showSingleToast(model.getMessage());
        onReLoginFail("修改密码成功,重新登录");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_change:
                if (isEmpty()){
                    break;
                }
                if (et_new_password.getText().toString().equals(et_new_password2.getText().toString())){
                    MultipartBody.Builder builder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id", Constant.userData.getUser_id())
                            .addFormDataPart("old_password",et_old_password.getText().toString())
                            .addFormDataPart("new_password",et_new_password.getText().toString());
                    mPresenter.changeDetail(builder.build().parts());
                }else {
                    ToastUtils.showSingleToast("输入的新密码不一致");
                }
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }

    /**
     * 判断输入的是否为空
     * @return
     */
    public boolean isEmpty(){
        if (et_old_password.getText().length() == 0){
            ToastUtils.showSingleToast("请输入原密码");
            et_old_password.requestFocus();
            return true;
        }
        if (et_new_password.getText().length() == 0){
            ToastUtils.showSingleToast("请输入新密码");
            et_new_password.requestFocus();
            return true;
        }
        if (et_new_password2.getText().length() == 0){
            ToastUtils.showSingleToast("请再次输入新密码");
            et_new_password2.requestFocus();
            return true;
        }
        return false;
    }
}
