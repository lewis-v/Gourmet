package com.yw.gourmet.ui.search;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.FlowData;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/28.
 */

public class SearchPresenter extends SearchContract.Presenter {
    @Override
    void getFlow() {
        mRxManager.add(Api.getInstance().GetFlow(),new RxSubscriberCallBack<BaseData<List<FlowData>>>(new RxApiCallback<BaseData<List<FlowData>>>() {
            @Override
            public void onSuccess(BaseData<List<FlowData>> model) {
                if (model.getStatus() == 0){
                    mView.onGetFlowSuccess(model);
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
