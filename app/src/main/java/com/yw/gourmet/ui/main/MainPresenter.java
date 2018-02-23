package com.yw.gourmet.ui.main;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.InitData;

/**
 * Created by yw on 2017/10/22.
 */

public class MainPresenter extends MainContract.Presenter{
    @Override
    void getVersion() {
        mRxManager.add(Api.getInstance().Init(),new RxSubscriberCallBack<BaseData<InitData>>(new RxApiCallback<BaseData<InitData>>() {
            @Override
            public void onSuccess(BaseData<InitData> model) {
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 0){
                    mView.onGetVersionSuccess(model);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onFail(msg);
            }
        }));
    }
}
