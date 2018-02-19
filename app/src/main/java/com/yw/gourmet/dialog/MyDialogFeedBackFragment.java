package com.yw.gourmet.dialog;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseDialogFragment;

/**
 * auth: lewis-v
 * time: 2018/2/19.
 */

public class MyDialogFeedBackFragment extends BaseDialogFragment implements View.OnClickListener{
    private EditText et;
    private TextView tv_cancel,tv_enter;
    private static boolean isShow = false;//是否显示

    private static class Instance{
        private static MyDialogFeedBackFragment instance = new MyDialogFeedBackFragment();
    }

    public static MyDialogFeedBackFragment getInstance(){
        return Instance.instance;
    }

    @Override
    protected void initView() {
        et = view.findViewById(R.id.et);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_enter = view.findViewById(R.id.tv_enter);

        tv_enter.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_feedback;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                isShow = false;
                dismiss();
                break;
            case R.id.tv_enter:
                isShow = false;
                dismiss();
                break;
        }
    }

    @Override
    public void onBack() {
        isShow = false;
        super.onBack();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (isShow){
            dismiss();
        }
        super.show(manager, tag);
        isShow = true;
    }

    public boolean isShow(){
        return isShow;
    }
}
