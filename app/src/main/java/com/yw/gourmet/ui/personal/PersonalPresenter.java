package com.yw.gourmet.ui.personal;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.data.UserData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/11/29.
 */

public class PersonalPresenter extends PersonalContract.Presenter{
    @Override
    void upImg(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().UpImg(parts),new RxSubscriberCallBack<BaseData<String>>(new RxApiCallback<BaseData<String>>() {
            @Override
            public void onSuccess(BaseData<String> model) {
                if (model.getStatus() == 0) {
                    mView.onUpImgSuccess(model);
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

    @Override
    void changeBack(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().ChangeUserDetail(parts),new RxSubscriberCallBack<BaseData<UserData>>(new RxApiCallback<BaseData<UserData>>() {
            @Override
            public void onSuccess(BaseData<UserData> model) {
                if (model.getStatus() == 0){
                    mView.onChangeSuccess(model);
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

    @Override
    void getTop(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().GetTop(parts),new RxSubscriberCallBack<BaseData<List<ShareListData<List<String>>>>>(new RxApiCallback<BaseData<List<ShareListData<List<String>>>>>() {
            @Override
            public void onSuccess(BaseData<List<ShareListData<List<String>>>> model) {
                if (model.getStatus() == 0){
                    mView.onGetTopSuccess(model);
                }else{
                    mView.OnGetTopFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.OnGetTopFail(msg);
            }
        }));
    }

    @Override
    void reMark(List<MultipartBody.Part> parts, final int position) {
        mRxManager.add(Api.getInstance().PutReMark(parts),new RxSubscriberCallBack<BaseData<ShareListData<List<String>>>>(new RxApiCallback<BaseData<ShareListData<List<String>>>>() {
            @Override
            public void onSuccess(BaseData<ShareListData<List<String>>> model) {
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

    @Override
    void getUserInfo(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().GetUserInfo(parts),new RxSubscriberCallBack<BaseData<UserData>>(new RxApiCallback<BaseData<UserData>>() {
            @Override
            public void onSuccess(BaseData<UserData> model) {
                if (model.getStatus() == 0){
                    mView.onGetUserInfoSuccess(model);
                }else {
                    mView.onGetUserInfoFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onGetUserInfoFail(msg);
            }
        }));
    }


}
