package com.yw.gourmet.dialog;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.listener.OnCancelClickListener;
import com.yw.gourmet.utils.SoftInputUtils;
import com.yw.gourmet.utils.ToastUtils;

/**
 * Created by Lewis-v on 2017/12/21.
 */

public class MyDialogIngredientFragment extends BaseDialogFragment implements View.OnClickListener{
    private EditText et_left,et_right;
    private TextView tv_cancel,tv_enter;
    private OnCancelClickListener onCancelClickListener;
    private OnEnterListener onEnterListener;
    private String leftHint = "",rightHint = "";//文字的提示
    private String leftText = "",rightText = "";//显示的文字
    private boolean isChange = true;//是否可以修改,默认可以
    private int position;//触发的位置

    @Override
    protected void initView() {
        et_left = (EditText)view.findViewById(R.id.et_left);
        et_right = (EditText)view.findViewById(R.id.et_right);

        et_left.setHint(leftHint);
        et_right.setHint(rightHint);
        et_left.setText(leftText);
        et_right.setText(rightText);

        tv_cancel = (TextView)view.findViewById(R.id.tv_cancel);
        tv_enter = (TextView)view.findViewById(R.id.tv_enter);
        tv_cancel.setOnClickListener(this);
        tv_enter.setOnClickListener(this);

        if (!isChange) {
            et_left.setEnabled(false);
            et_left.setFocusable(false);
            et_left.clearFocus();
            et_right.setEnabled(false);
            et_right.setFocusable(false);
            et_right.clearFocus();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_ingredient;
    }

    public MyDialogIngredientFragment setChange(boolean change) {
        isChange = change;
        return this;
    }

    public MyDialogIngredientFragment setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
        return this;
    }

    public MyDialogIngredientFragment setOnEnterListener(OnEnterListener onEnterListener) {
        this.onEnterListener = onEnterListener;
        return this;
    }

    public MyDialogIngredientFragment setLeftHint(String leftHint) {
        this.leftHint = leftHint;
        return this;
    }

    public MyDialogIngredientFragment setRightHint(String rightHint) {
        this.rightHint = rightHint;
        return this;
    }

    public MyDialogIngredientFragment setLeftText(String leftText) {
        this.leftText = leftText;
        return this;
    }

    public MyDialogIngredientFragment setRightText(String rightText) {
        this.rightText = rightText;
        return this;
    }

    public MyDialogIngredientFragment setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                if (onCancelClickListener != null){
                    onCancelClickListener.OnCancel(getTag());
                }
                SoftInputUtils.hideSoftInput(et_left);
                SoftInputUtils.hideSoftInput(et_right);
                dismiss();
                break;
            case R.id.tv_enter:
                if (isChange) {
                    if (et_left.getText().toString().trim().isEmpty()) {
                        ToastUtils.showLongToast("请输入" + leftHint);
                        break;
                    }
                    if (et_right.getText().toString().trim().isEmpty()) {
                        ToastUtils.showLongToast("请输入" + rightHint);
                        break;
                    }
                    if (et_left.getText().toString().contains(",")
                            || et_right.getText().toString().contains(",")){
                        ToastUtils.showSingleToast("不可输入\"&&\"符号");
                        break;
                    }
                    if (onEnterListener != null) {
                        onEnterListener.OnEnter(et_left.getText().toString() + "&&" + et_right.getText().toString(), position, getTag());
                    }
                    SoftInputUtils.hideSoftInput(et_left);
                    SoftInputUtils.hideSoftInput(et_right);
                }
                dismiss();
                break;
        }
    }

    public interface OnEnterListener{
        void OnEnter(String content,int position ,String Tag);
    }
}
