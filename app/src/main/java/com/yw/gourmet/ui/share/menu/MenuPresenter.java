package com.yw.gourmet.ui.share.menu;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by Lewis-v on 2017/12/21.
 */

public class MenuPresenter extends MenuContract.Presenter{
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
}
