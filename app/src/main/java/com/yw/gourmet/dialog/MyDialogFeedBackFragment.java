package com.yw.gourmet.dialog;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.utils.ToastUtils;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/2/19.
 */

public class MyDialogFeedBackFragment extends BaseDialogFragment<MyDIalogPresenter> implements
        MyDialogContract.View,View.OnClickListener{
    private EditText et;
    private TextView tv_cancel,tv_enter;
    private static boolean isShow = false;//是否显示

    @Override
    public void onFail(String msg) {
        ToastUtils.showSingleToast(msg);
    }

    @Override
    public void onSuccess(String msg) {
        ToastUtils.showSingleToast(msg);
    }

    @Override
    public void onReLoginFail(String msg) {

    }

    @Override
    public void setLoadDialog(boolean isLoadDialog) {

    }

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

        getDialog().setCanceledOnTouchOutside(false);
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
                if (et != null){
                    et.setText("");
                }
                dismiss();
                break;
            case R.id.tv_enter:
                if (et.getText().toString().trim().length() == 0){
                    ToastUtils.showSingleToast("请输入反馈信息");
                }else {
                    MultipartBody.Builder builder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                            .addFormDataPart("id", Constant.userData.getUser_id())
                            .addFormDataPart("content",et.getText().toString());
                    mPresenter.feedback(builder.build().parts());
                    isShow = false;
                    if (et != null){
                        et.setText("");
                    }
                    dismiss();
                }
                break;
        }
    }

    @Override
    public void onBack() {
        isShow = false;
        if (et != null){
            et.setText("");
        }
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
