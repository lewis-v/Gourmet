package com.yw.gourmet.ui.registered;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.utils.SoftInputUtils;

/**
 * auth: lewis-v
 * time: 2018/2/5.
 */

public class SubmitCodeFragment extends BaseFragment {
    private EditText et_code;
    private Button bt_register;
    private TextView tv_re_get;
    private int waitTime = 60;//等待剩余秒数

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_submit_code;
    }

    @Override
    protected void initView() {
        et_code = view.findViewById(R.id.et_code);
        bt_register = view.findViewById(R.id.bt_register);
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisteredActivity)getActivity()).putCode(et_code.getText().toString().trim());
                SoftInputUtils.hideSoftInput(et_code);
            }
        });
        tv_re_get = view.findViewById(R.id.tv_re_get);
        tv_re_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waitTime <= 0) {
                    ((RegisteredActivity) getActivity()).sendSMS();
                    waitTime = 60;
                    setWaitTime();
                }
            }
        });
        setWaitTime();
    }

    /**
     * 获取验证码倒计时
     */
    public void setWaitTime(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                waitTime--;
                if (waitTime == 0){
                    tv_re_get.setText("重新获取验证码");
                    tv_re_get.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
                }else {
                    tv_re_get.setText("重新获取验证码(" + (waitTime) + "s)");
                    tv_re_get.setTextColor(ContextCompat.getColor(getContext(),R.color.close));
                    setWaitTime();
                }
            }
        },1000);
    }
}
