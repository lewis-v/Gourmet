package com.yw.gourmet.dialog;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.utils.SoftInputUtils;
import com.yw.gourmet.utils.ToastUtils;

/**
 * auth: lewis-v
 * time: 2018/2/14.
 */

public class MyDialogComplaintFragment extends BaseDialogFragment implements View.OnClickListener{
    private TextView tv_cancel,tv_enter;
    private EditText et;
    private OnEnterListener onEnterListener;
    private String cancelText = "算了吧";
    private String enterText = "投诉TA";
    private String tipText = "请输入投诉内容或原因";


    @Override
    protected void initView() {
        et = view.findViewById(R.id.et);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_enter = view.findViewById(R.id.tv_enter);

        et.setHint(tipText);
        tv_cancel.setText(cancelText);
        tv_enter.setText(enterText);

        tv_enter.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_complaint;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_enter:
                if (et.getText().toString().trim().isEmpty()){
                    ToastUtils.showSingleToast(tipText);
                    break;
                }
                if (onEnterListener != null){
                    onEnterListener.onEnter(et.getText().toString().trim(),getTag());
                }
                SoftInputUtils.hideSoftInput(et);
                dismiss();
                break;
            case R.id.tv_cancel:
                SoftInputUtils.hideSoftInput(et);
                dismiss();
                break;
        }
    }

    public MyDialogComplaintFragment setOnEnterListener(OnEnterListener onEnterListener) {
        this.onEnterListener = onEnterListener;
        return this;
    }

    public MyDialogComplaintFragment setCancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    public MyDialogComplaintFragment setEnterText(String enterText) {
        this.enterText = enterText;
        return this;
    }

    public MyDialogComplaintFragment setTipText(String tipText) {
        this.tipText = tipText;
        return this;
    }

    public interface OnEnterListener{
        void onEnter(String edit,String Tag);
    }
}
