package com.yw.gourmet.ui.login;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.UserData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/11/5.
 */

public class LoginPresenter extends LoginContract.Presenter{
    public void login(List<MultipartBody.Part> parts){
        mRxManager.add(Api.getInstance().Login(parts),new RxSubscriberCallBack<BaseData<UserData>>(new RxApiCallback<BaseData<UserData>>() {
            @Override
            public void onSuccess(BaseData<UserData> model) {
                if (model.getStatus() == 1){
                    mView.onFail(model.getMessage());
                }else if (model.getStatus() == 0){
                    mView.onLoginSuccess(model);
                }
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        }));
    }
}
