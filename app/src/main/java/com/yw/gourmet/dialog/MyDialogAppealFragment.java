package com.yw.gourmet.dialog;

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
 * time: 2018/2/22.
 */

public class MyDialogAppealFragment extends BaseDialogFragment<MyDIalogPresenter> implements
        View.OnClickListener,MyDialogContract.View{
    private TextView tv_cancel,tv_enter,tv_cause;
    private EditText et_content;
    private String actId,type;
    private String rejectCause = "";

    @Override
    protected void initView() {
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_cause = view.findViewById(R.id.tv_cause);
        tv_enter = view.findViewById(R.id.tv_enter);

        tv_cause.setText("拒绝原因:"+rejectCause);

        tv_cancel.setOnClickListener(this);
        tv_enter.setOnClickListener(this);

        et_content = view.findViewById(R.id.et_content);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_appeal;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_enter:
                if (et_content.getText().toString().trim().isEmpty()){
                    ToastUtils.showSingleToast("请输入申诉原因");
                }else {
                    MultipartBody.Builder builder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("token",Constant.userData == null?"0":Constant.userData.getToken())
                            .addFormDataPart("id", Constant.userData.getUser_id())
                            .addFormDataPart("act_id",actId)
                            .addFormDataPart("type",type)
                            .addFormDataPart("cause",et_content.getText().toString());
                    mPresenter.appeal(builder.build().parts());
                    dismiss();
                }
                break;
        }
    }

    public MyDialogAppealFragment setRejectCause(String rejectCause) {
        this.rejectCause = rejectCause;
        return this;
    }

    public MyDialogAppealFragment setActId(String actId) {
        this.actId = actId;
        return this;
    }

    public MyDialogAppealFragment setType(String type) {
        this.type = type;
        return this;
    }

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
}
