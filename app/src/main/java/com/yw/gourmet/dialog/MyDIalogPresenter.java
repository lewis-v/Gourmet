package com.yw.gourmet.dialog;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/1/20.
 */

public class MyDIalogPresenter extends MyDialogContract.Presenter {
    @Override
    void collection(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().Collection(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
            @Override
            public void onSuccess(BaseData model) {
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 0){
                    mView.onSuccess(model.getMessage());
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
    void feedback(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().Feedback(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
            @Override
            public void onSuccess(BaseData model) {
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 0){
                    mView.onSuccess(model.getMessage());
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
    void appeal(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().Appeal(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
            @Override
            public void onSuccess(BaseData model) {
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 0){
                    mView.onSuccess(model.getMessage());
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
}
