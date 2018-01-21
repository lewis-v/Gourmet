package com.yw.gourmet.dialog;

import android.view.View;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.listener.OnCancelClickListener;

/**
 * auth: lewis-v
 * time: 2018/1/17.
 */

public class MyDialogTipFragment extends BaseDialogFragment implements View.OnClickListener{
    private TextView tv,tv_enter,tv_cancel;
    private String textEnter = "确定",textCancel = "取消",showText = "";
    private boolean isShowEnter = true,isShowCancel = true;//是否显示确定及取消按钮,默认显示
    private OnCancelClickListener onCancelClickListener;
    private OnEnterListener onEnterListener;


    @Override
    protected void initView() {
        tv = view.findViewById(R.id.tv);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_enter = view.findViewById(R.id.tv_enter);

        tv.setOnClickListener(this);
        tv_enter.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        if (!isShowCancel){
            tv_cancel.setVisibility(View.GONE);
        }
        if (!isShowEnter){
            tv_enter.setVisibility(View.GONE);
        }
        tv_enter.setText(textEnter);
        tv_cancel.setText(textCancel);
        tv.setText(showText);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_tip;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_enter:
                if (onEnterListener != null){
                    onEnterListener.OnEnter(getTag());
                }
                dismiss();
                break;
            case R.id.tv_cancel:
                if (onCancelClickListener != null){
                    onCancelClickListener.OnCancel(getTag());
                }
                dismiss();
                break;
        }
    }

    public interface OnEnterListener{
        void OnEnter(String Tag);
    }

    public MyDialogTipFragment setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
        return this;
    }

    public MyDialogTipFragment setOnEnterListener(OnEnterListener onEnterListener) {
        this.onEnterListener = onEnterListener;
        return this;
    }

    public MyDialogTipFragment setTextEnter(String textEnter) {
        this.textEnter = textEnter;
        return this;
    }

    public MyDialogTipFragment setTextCancel(String textCancel) {
        this.textCancel = textCancel;
        return this;
    }

    public MyDialogTipFragment setShowText(String showText) {
        this.showText = showText;
        return this;
    }

    public MyDialogTipFragment setShowEnter(boolean showEnter) {
        isShowEnter = showEnter;
        return this;
    }

    public MyDialogTipFragment setShowCancel(boolean showCancel) {
        isShowCancel = showCancel;
        return this;
    }
}
