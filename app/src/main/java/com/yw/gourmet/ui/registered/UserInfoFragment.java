package com.yw.gourmet.ui.registered;

import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.utils.ToastUtils;

/**
 * auth: lewis-v
 * time: 2018/2/5.
 */

public class UserInfoFragment extends BaseFragment {
    private EditText et_password,et_password2;
    private Button bt_enter;
    private AppCompatRadioButton radio_boy;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void initView() {
        et_password = view.findViewById(R.id.et_password);
        et_password2 = view.findViewById(R.id.et_password2);

        radio_boy = view.findViewById(R.id.radio_boy);

        bt_enter = view.findViewById(R.id.bt_enter);
        bt_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrue()){
                    ((RegisteredActivity)getActivity()).registered(et_password.getText().toString()
                            ,radio_boy.isChecked()?"男":"女");
                }
            }
        });
    }

    /**
     * 输入的是否正确
     * @return
     */
    public boolean isTrue(){
        if (et_password.getText().length() == 0 || et_password2.getText().length() == 0){
            ToastUtils.showSingleToast("请输入密码");
            return false;
        }else
        if (!et_password.getText().toString().equals(et_password2.getText().toString())){
            ToastUtils.showSingleToast("输入的密码不一致");
            return false;
        }
        return true;
    }
}
