package com.yw.gourmet.ui.changeDetail;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.UserData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/11/29.
 */

public class ChangeDetailPresenter extends ChangeDetailContract.Presenter{
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

    @Override
    void upImg(List<MultipartBody.Part> parts) {
        mView.setLoadDialog(true);
        mRxManager.add(Api.getInstance().UpImg(parts),new RxSubscriberCallBack<BaseData<String>>(new RxApiCallback<BaseData<String>>() {
            @Override
            public void onSuccess(BaseData<String> model) {
                mView.setLoadDialog(false);
                if (model.getStatus() == 0){
                    mView.onUpSuccess(model);
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
