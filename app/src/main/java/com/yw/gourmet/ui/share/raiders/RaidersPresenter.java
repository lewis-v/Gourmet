package com.yw.gourmet.ui.share.raiders;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.RaidersDetailData;
import com.yw.gourmet.data.RaidersListData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/1/6.
 */

public class RaidersPresenter extends RaidersContract.Presenter{

    @Override
    void upImg(List<MultipartBody.Part> parts, final int position) {
        mRxManager.add(Api.getInstance().UpImg(parts),new RxSubscriberCallBack<BaseData<String>>(new RxApiCallback<BaseData<String>>() {
            @Override
            public void onSuccess(BaseData<String> model) {
                if (model.getStatus() == 0) {
                    mView.onUpImgSuccess(model,position);
                }else{
                    mView.onUpImgFail(model.getMessage(),position);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onUpImgFail(msg,position);
            }
        }));
    }

    @Override
    void shareRaiders(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().ShareRaiders(parts),new RxSubscriberCallBack<BaseData<RaidersDetailData<List<RaidersListData<List<String>>>,List<String>>>>
                (new RxApiCallback<BaseData<RaidersDetailData<List<RaidersListData<List<String>>>, List<String>>>>() {
            @Override
            public void onSuccess(BaseData<RaidersDetailData<List<RaidersListData<List<String>>>, List<String>>> model) {
                if (model.getStatus() == 0) {
                    mView.onShareSuccess(model.getMessage());
                }else{
                    mView.onShareFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onShareFail(msg);
            }
        }));
    }
}
