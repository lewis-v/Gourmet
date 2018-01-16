package com.yw.gourmet.ui.detail.raiders;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.CommentData;
import com.yw.gourmet.data.RaidersDetailData;
import com.yw.gourmet.data.RaidersListData;
import com.yw.gourmet.data.ShareListData;

import java.util.List;

public class RaidersDetailActivity extends BaseActivity<RaidersDetailPresenter> implements RaidersDetailContract.View{


    @Override
    protected int getLayoutId() {
        return R.layout.activity_raiders_detail;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onGetDetailSuccess(BaseData<RaidersDetailData<List<RaidersListData<List<String>>>, List<String>>> model) {

    }

    @Override
    public void onGetDetailFail(String msg) {

    }

    @Override
    public void onGetCommentSuccess(BaseData<List<CommentData>> model) {

    }

    @Override
    public void onGetCommentFail(String msg) {

    }

    @Override
    public void onSendCommentSuccess(BaseData<List<CommentData>> model) {

    }

    @Override
    public void onSendCommentFail(String msg) {

    }

    @Override
    public void onRemarkSuccess(BaseData<ShareListData> model) {

    }
}
