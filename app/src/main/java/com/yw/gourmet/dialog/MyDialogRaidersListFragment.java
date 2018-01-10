package com.yw.gourmet.dialog;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.IngredientAdapter;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.data.RaidersListData;
import com.yw.gourmet.listener.OnAddListener;
import com.yw.gourmet.listener.OnDeleteListener;
import com.yw.gourmet.listener.OnEditDialogEnterClickListener;
import com.yw.gourmet.utils.WindowUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/10.
 */

public class MyDialogRaidersListFragment extends BaseDialogFragment implements View.OnClickListener{
    private EditText et_title,et_address;
    private TextView tv_img_tip,tv_enter,tv_cancel;
    private RecyclerView recycler_type;
    private IngredientAdapter adapter;
    private ImageView img_cover;
    private ImageView img_address_search;
    private RaidersListData<List<String>> raidersData;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout((int) (WindowUtil.width * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void initView() {
        getDialog().setCanceledOnTouchOutside(false);
        et_title = view.findViewById(R.id.et_title);
        et_address = view.findViewById(R.id.et_address);

        tv_img_tip = view.findViewById(R.id.tv_img_tip);
        tv_enter = view.findViewById(R.id.tv_enter);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_enter.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        img_address_search = view.findViewById(R.id.img_address_search);
        img_address_search.setOnClickListener(this);

        img_cover = view.findViewById(R.id.img_cover);
        img_cover.setOnClickListener(this);

        if (raidersData == null){
            raidersData = new RaidersListData<List<String>>();
            raidersData.setType(new ArrayList<String>());
        }
        recycler_type = view.findViewById(R.id.recycler_type);
        recycler_type.setItemAnimator(new DefaultItemAnimator());
        recycler_type.setLayoutManager(new LinearLayoutManager(getContext()
                , LinearLayoutManager.HORIZONTAL, false));
        adapter = new IngredientAdapter(getContext(), raidersData.getType(), true);
        recycler_type.setAdapter(adapter);
        adapter.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void OnDelete(View v, int position) {
                raidersData.getType().remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
        adapter.setOnAddListener(new OnAddListener() {
            @Override
            public void OnAdd(View view, final int position) {
                new MyDialogEditFragment().setEtHint("请输入标签、类型").setOnEditDialogEnterClickListener(new OnEditDialogEnterClickListener() {
                    @Override
                    public void OnClick(String edit, String tag) {
                        raidersData.getType().add(edit);
                        adapter.notifyItemInserted(position);
                    }
                }).show(getFragmentManager(), "type");
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_raiders_list;
    }

    public MyDialogRaidersListFragment setRaidersData(RaidersListData<List<String>> raidersData) {
        this.raidersData = raidersData;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cover:
                new MyDialogPhotoChooseFragment().setRatio(1).setCrop(true).setChooseNum(1)
                        .setOnCropListener(new MyDialogPhotoChooseFragment.OnCropListener() {
                    @Override
                    public void OnCrop(String path, String tag) {
                        GlideApp.with(getContext()).load(path).error(R.mipmap.load_fail)
                                .into(img_cover);
                        if (tv_img_tip.getVisibility() == View.VISIBLE) {
                            tv_img_tip.setVisibility(View.GONE);
                        }
                    }
                }).show(getFragmentManager(),"cover");
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_enter:

                break;
            case R.id.img_address_search:

                break;
        }
    }
}
