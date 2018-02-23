package com.yw.gourmet.ui.search.keySearch;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/2/2.
 */

public class KeySearchPresenter extends KeySearchContract.Presenter {
    @Override
    void search(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().Search(parts),new RxSubscriberCallBack<BaseData<List<ShareListData<List<String>>>>>(new RxApiCallback<BaseData<List<ShareListData<List<String>>>>>() {
            @Override
            public void onSuccess(BaseData<List<ShareListData<List<String>>>> model) {
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 0){
                    mView.onSearchSuccess(model);
                }else {
                    mView.onSearchFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onSearchFail(msg);
            }
        }));
    }

    @Override
    void searchUser(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().Search(parts),new RxSubscriberCallBack<BaseData<List<ShareListData<List<String>>>>>(new RxApiCallback<BaseData<List<ShareListData<List<String>>>>>() {
            @Override
            public void onSuccess(BaseData<List<ShareListData<List<String>>>> model) {
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 0){
                    mView.onSearchUserSuccess(model);
                }else {
                    mView.onSearchUserFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onSearchUserFail(msg);
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
