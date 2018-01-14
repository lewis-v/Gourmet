package com.yw.gourmet.ui.flash;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.InitData;
import com.yw.gourmet.data.UserData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/11/16.
 */

public class FlashPresenter extends FlashConstract.Presenter {

    @Override
    void onLogin(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().Login(parts),new RxSubscriberCallBack<BaseData<UserData>>(new RxApiCallback<BaseData<UserData>>() {
            @Override
            public void onSuccess(BaseData<UserData> model) {
                if (model.getStatus() == 0){
                    mView.onLoginSuccess(model);
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
    void Init() {
        mRxManager.add(Api.getInstance().Init(),new RxSubscriberCallBack<BaseData<InitData>>(new RxApiCallback<BaseData<InitData>>() {
            @Override
            public void onSuccess(BaseData<InitData> model) {
                if (model.getStatus() == 0){
                    mView.onInitSuccess(model);
                }else {
                    mView.onInitFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg)  {
                mView.onInitFail(msg);
            }
        }));
    }

    @Override
    void getAreaDetail() {
        mRxManager.add(Api.getInstance().GetAreaDetail(),new RxSubscriberCallBack<BaseData<List<String>>>(new RxApiCallback<BaseData<List<String>>>() {
            @Override
            public void onSuccess(BaseData<List<String>> model) {
                if (model.getStatus() == 0){
                    mView.onGetAreaSuccess(model);
                }else {
                    mView.onInitFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg)  {
                mView.onInitFail(msg);
            }
        }));
    }


}
