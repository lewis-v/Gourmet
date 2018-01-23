package com.yw.gourmet.dialog;

import android.view.View;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.listener.OnCancelClickListener;

/**
 * auth: lewis-v
 * time: 2018/1/23.
 */

public class MyDialogDraftFragment extends BaseDialogFragment implements View.OnClickListener{
    private TextView tv_delete,tv_delete_all,tv_cancel;
    private OnDeleteListener onDeleteListener;
    private OnCancelClickListener onCancelClickListener;

    @Override
    protected void initView() {
        tv_delete = view.findViewById(R.id.tv_delete);
        tv_delete_all = view.findViewById(R.id.tv_delete_all);
        tv_cancel = view.findViewById(R.id.tv_cancel);

        tv_cancel.setOnClickListener(this);
        tv_delete_all.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_draft;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_delete:
                if (onDeleteListener != null){
                    onDeleteListener.onDelete(getTag());
                }
                break;
            case R.id.tv_delete_all:
                if (onDeleteListener != null){
                    onDeleteListener.onDeleteAll(getTag());
                }
                break;
            case R.id.tv_cancel:
                if (onCancelClickListener != null){
                    onCancelClickListener.OnCancel(getTag());
                }
                break;
        }
        dismiss();
    }

    public interface OnDeleteListener{
        void onDelete(String Tag);
        void onDeleteAll(String Tag);
    }

    public MyDialogDraftFragment setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
        return this;
    }

    public MyDialogDraftFragment setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
        return this;
    }
}
