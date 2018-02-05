package com.yw.gourmet.ui.registered;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.utils.SoftInputUtils;
import com.yw.gourmet.utils.ToastUtils;

/**
 * auth: lewis-v
 * time: 2018/2/5.
 */

public class RegisteredFragment extends BaseFragment {
    private EditText et_phone;
    private Button bt_register;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_registered;
    }

    @Override
    protected void initView() {
        bt_register = view.findViewById(R.id.bt_register);
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPhone()){
                    ((RegisteredActivity)getActivity()).sendSMS(et_phone.getText().toString().trim());
                }else {
                    ToastUtils.showSingleToast("请输入正确的手机号码");
                }
                SoftInputUtils.hideSoftInput(et_phone);
            }
        });
        et_phone = view.findViewById(R.id.et_phone);
    }

    /**
     * 判断输入的手机是否正确
     * @return
     */
    public boolean isPhone(){
        if (et_phone.getText().toString().trim().length() == 11){
            return true;
        }else {
            return false;
        }
    }
}
