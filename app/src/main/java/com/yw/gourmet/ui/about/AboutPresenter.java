package com.yw.gourmet.ui.about;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.InitData;

/**
 * auth: lewis-v
 * time: 2018/1/21.
 */

public class AboutPresenter extends AboutContract.Presenter {
    @Override
    void getVersion() {
        mRxManager.add(Api.getInstance().Init(),new RxSubscriberCallBack<BaseData<InitData>>(new RxApiCallback<BaseData<InitData>>() {
            @Override
            public void onSuccess(BaseData<InitData> model) {
                if (model.getStatus() == 0){
                    mView.onGetVersionSuccess(model);
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
}
