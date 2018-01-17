package com.yw.gourmet.dialog;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.listener.OnCancelClickListener;
import com.yw.gourmet.listener.OnEditDialogEnterClickListener;
import com.yw.gourmet.utils.ToastUtils;

/**
 * Created by LYW on 2017/11/29.
 */

public class MyDialogEditFragment extends BaseDialogFragment implements View.OnClickListener{
    private EditText et;
    private TextView tv_canael,tv_enter;
    private String etHint = "";//提示语
    private String text = "";//默认显示字符串
    private OnCancelClickListener onCancelListener;
    private OnEditDialogEnterClickListener onEditDialogEnterClickListener;

    @Override
    protected void initView() {
        et = (EditText)view.findViewById(R.id.et);
        et.setHint(etHint);
        et.setText(text);

        tv_canael = (TextView)view.findViewById(R.id.tv_cancel);
        tv_enter = (TextView)view.findViewById(R.id.tv_enter);
        tv_canael.setOnClickListener(this);
        tv_enter.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_edit;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                if (onCancelListener != null){
                    onCancelListener.OnCancel(getTag());
                }
                dismiss();
                break;
            case R.id.tv_enter:
                if (et.getText().toString().trim().length() == 0){
                    ToastUtils.showSingleToast(etHint);
                }else {
                    if (onEditDialogEnterClickListener != null){
                        onEditDialogEnterClickListener.OnClick(et.getText().toString(),getTag());
                    }
                    dismiss();
                }
                break;
        }
    }

    public MyDialogEditFragment setEtHint(String etHint) {
        this.etHint = etHint;
        return this;
    }

    public MyDialogEditFragment setOnCancelListener(OnCancelClickListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    public MyDialogEditFragment setOnEditDialogEnterClickListener(OnEditDialogEnterClickListener onEditDialogEnterClickListener) {
        this.onEditDialogEnterClickListener = onEditDialogEnterClickListener;
        return this;
    }

    public MyDialogEditFragment setText(String text) {
        this.text = text;
        return this;
    }
}
