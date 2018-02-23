package com.yw.gourmet.ui.setTop;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.myenum.LoadEnum;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/1/26.
 */

public class MySharePresenter extends SetTopContract.Presenter{
    @Override
    void setTop(List<MultipartBody.Part> parts, final int position) {
        mRxManager.add(Api.getInstance().PutTop(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
            @Override
            public void onSuccess(BaseData model) {
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 0) {
                    mView.onSetTopSuccess(model.getMessage(), position);
                }else {
                    mView.onFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onFail(msg);
            }
        }));
    }

    @Override
    void getShareList(List<MultipartBody.Part> parts, final LoadEnum flag) {
        mRxManager.add(Api.getInstance().LoadShareList(parts),new RxSubscriberCallBack<BaseData<List<ShareListData<List<String>>>>>(new RxApiCallback<BaseData<List<ShareListData<List<String>>>>>() {
            @Override
            public void onSuccess(BaseData<List<ShareListData<List<String>>>> model) {
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 0){
                    mView.onGetListSuccess(model,flag);
                }else if (model.getStatus() == 1){
                    mView.onGetListFail(model.getMessage(),flag);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onGetListFail(msg,flag);
            }
        }));
    }
}
