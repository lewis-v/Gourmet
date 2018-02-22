package com.yw.gourmet.ui.reject;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

public class RejectActivity extends BaseActivity<RejectPresenter> implements RejectContract.View,View.OnClickListener{
    private RecyclerView swipe_target;
    private SwipeToLoadLayout swipeToLoadLayout;
    private ImageView img_back;
    private TextView tv_clear;
    private List<ShareListData<List<String>>> listData = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reject;
    }

    @Override
    protected void initView() {
        img_back = findViewById(R.id.img_back);
        tv_clear = findViewById(R.id.tv_clear);

        img_back.setOnClickListener(this);
        tv_clear.setOnClickListener(this);

        swipe_target = findViewById(R.id.swipe_target);
        swipeToLoadLayout = findViewById(R.id.swipeToLoadLayout);



        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", Constant.userData.getUser_id());
        mPresenter.getRejectList(builder.build().parts());
    }

    @Override
    public void onGetRejectSuccess(BaseData<List<ShareListData<List<String>>>> model) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_clear:

                break;
        }
    }
}
