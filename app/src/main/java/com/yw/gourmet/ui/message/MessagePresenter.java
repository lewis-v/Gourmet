package com.yw.gourmet.ui.message;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/11/24.
 */

public class MessagePresenter extends MessageContract.Presenter {
    public void LoadMessageList(List<MultipartBody.Part> parts){
        mRxManager.add(Api.getInstance().LoadMessageList(parts),new RxSubscriberCallBack<BaseData<List<MessageListData>>>(new RxApiCallback<BaseData<List<MessageListData>>>() {
            @Override
            public void onSuccess(BaseData<List<MessageListData>> model) {
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 1){
                    mView.onFail(model.getMessage());
                }else if (model.getStatus() == 0){
                    mView.onLoadMessageListSuccess(model);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onFail(msg);
            }
        }));
    }
}
