package com.yw.gourmet.ui.myShare;

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
 * time: 2018/1/19.
 */

public class MySharePresenter extends MyShareConstract.Presenter{
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

    @Override
    void reMark(List<MultipartBody.Part> parts, final int position) {
        mRxManager.add(Api.getInstance().PutReMark(parts),new RxSubscriberCallBack<BaseData<ShareListData<List<String>>>>(new RxApiCallback<BaseData<ShareListData<List<String>>>>() {
            @Override
            public void onSuccess(BaseData<ShareListData<List<String>>> model) {
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 0){
                    mView.onReMarkSuccess(model,position);
                }else if (model.getStatus() == 1){
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
