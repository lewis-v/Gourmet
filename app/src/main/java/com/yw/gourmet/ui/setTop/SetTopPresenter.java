package com.yw.gourmet.ui.setTop;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/1/26.
 */

public class SetTopPresenter extends SetTopContract.TopPresenter {
    @Override
    void setTop(List<MultipartBody.Part> parts, final int position, final int endPosition) {
        mRxManager.add(Api.getInstance().PutTop(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
            @Override
            public void onSuccess(BaseData model) {
                if (model.getStatus() == 0) {
                    mView.onSetTopSuccess(model.getMessage(), position,endPosition);
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
}
