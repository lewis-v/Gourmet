package com.yw.gourmet.ui.registered;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    }
}
