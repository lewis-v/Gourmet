package com.yw.gourmet.ui.chat;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by Lewis-v on 2017/12/28.
 */

public class ChatPresenter extends ChatContract.Presenter {
    @Override
    void sendMessage(List<MultipartBody.Part> parts, final int position) {
        mRxManager.add(Api.getInstance().SendMessage(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
            @Override
            public void onSuccess(BaseData model) {
                if (model.getStatus() == 0){
                    mView.onSendSuccess(position);
                }else {
                    mView.onSendFail(model.getMessage(),position);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onSendFail(msg,position);
            }
        }));
    }

    @Override
    void getMessageDetail(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().GetMessageDetail(parts),new RxSubscriberCallBack<BaseData<List<MessageListData>>>(new RxApiCallback<BaseData<List<MessageListData>>>() {
            @Override
            public void onSuccess(BaseData<List<MessageListData>> model) {
                if (model.getStatus() == 0){
                    mView.onGetDetailSuccess(model);
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
    void setMessageRead(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().SetMessageRead(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
            @Override
            public void onSuccess(BaseData model) {

            }

            @Override
            public void onFailure(int code, String msg) {

            }
        }));
    }

}
