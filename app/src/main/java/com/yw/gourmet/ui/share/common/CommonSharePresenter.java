package com.yw.gourmet.ui.share.common;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/12/6.
 */

public class CommonSharePresenter extends CommonShareContract.Presenter{
    @Override
    void upImg(List<MultipartBody.Part> parts, final int position) {
        mRxManager.add(Api.getInstance().UpImg(parts),new RxSubscriberCallBack<BaseData<String>>(new RxApiCallback<BaseData<String>>() {
            @Override
            public void onSuccess(BaseData<String> model) {
                if (model.getStatus() == 0){
                    mView.onUpImgSuccess(model,position);
                }else if (model.getStatus() == 1){
                    mView.onFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onUpImgFail(msg,position);
            }
        }));
    }

    @Override
    void shareCommon(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().ShareCommon(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
            @Override
            public void onSuccess(BaseData model) {
                if (model.getStatus() == 0) {
                    mView.onShareSuccess(model);
                }else if (model.getStatus() == 1){
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
