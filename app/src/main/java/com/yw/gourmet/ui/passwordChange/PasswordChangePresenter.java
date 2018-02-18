package com.yw.gourmet.ui.passwordChange;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.UserData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/2/18.
 */

public class PasswordChangePresenter extends PasswordChangeContract.Presenter {
    @Override
    void changeDetail(List<MultipartBody.Part> parts) {
        mView.setLoadDialog(true);
        mRxManager.add(Api.getInstance().ChangeUserDetail(parts),new RxSubscriberCallBack<BaseData<UserData>>(new RxApiCallback<BaseData<UserData>>() {
            @Override
            public void onSuccess(BaseData<UserData> model) {
                mView.setLoadDialog(false);
                if (model.getStatus() == 0){
                    mView.onChangeSuccess(model);
                }else if (model.getStatus() == 1){
                    mView.onFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.setLoadDialog(false);
                mView.onFail(msg);
            }
        }));
    }


}
