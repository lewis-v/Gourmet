package com.yw.gourmet.dialog;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.adapter.ChooseListAdapter;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.listener.OnCancelClickListener;
import com.yw.gourmet.utils.WindowUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/24.
 */

public class MyDialogChooseListFragment extends BaseDialogFragment implements View.OnClickListener{
    private RecyclerView recycler_choose;
    private ChooseListAdapter adapter;
    private List<String> data;
    private OnChooseListener onChooseListener;
    private OnCancelClickListener onCancelClickListener;
    private TextView tv_tip,tv_cancel,tv_enter;
    private String tip;
    private int position = 0;

    @Override
    public void onStart() {
        super.onStart();
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void initView() {
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_tip = view.findViewById(R.id.tv_tip);
        tv_enter = view.findViewById(R.id.tv_enter);
        tv_enter.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        tv_tip.setText(tip);

        recycler_choose = view.findViewById(R.id.recycler_choose);
        recycler_choose.setItemAnimator(new DefaultItemAnimator());
        recycler_choose.setLayoutManager(new LinearLayoutManager(getContext()));
        if (data != null) {
            adapter = new ChooseListAdapter(getContext(), data);
            adapter.setChoosePosition(position);
            recycler_choose.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }
        recycler_choose.post(new Runnable() {
            @Override
            public void run() {
                if (recycler_choose.getHeight()> WindowUtil.height*2/3){
                    ViewGroup.LayoutParams layoutParams = recycler_choose.getLayoutParams();
                    layoutParams.height = WindowUtil.height*2/3;
                    recycler_choose.setLayoutParams(layoutParams);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_choose_list;
    }

    public MyDialogChooseListFragment setData(List<String> data) {
        this.data = data;
        return this;
    }

    public MyDialogChooseListFragment setOnChooseListener(OnChooseListener onChooseListener) {
        this.onChooseListener = onChooseListener;
        return this;
    }

    public MyDialogChooseListFragment setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
        return this;
    }

    public MyDialogChooseListFragment setTip(String tip) {
        this.tip = tip;
        return this;
    }

    public MyDialogChooseListFragment setPosition(int position) {
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
                dismiss();
                break;
            case R.id.tv_enter:
                if (onChooseListener != null){
                    onChooseListener.onChoose(adapter.getChoosePosition(),getTag());
                }
                dismiss();
                break;
        }
    }

    public interface OnChooseListener{
        void onChoose(int position,String tag);
    }
}
