package com.yw.gourmet.ui.chat;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.dao.data.messageData.MessageDataUtil;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MultipartBody;

/**
 * Created by Lewis-v on 2017/12/28.
 */

public class ChatPresenter extends ChatContract.Presenter {
    private ExecutorService executors;

    public ChatPresenter(){
        super();
        executors = Executors.newSingleThreadExecutor();
    }

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
    void getHistory(final String put_id, final String get_id, final int startId) {
        executors.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mView.onGetHistorySuccess(MessageDataUtil.getHistory(put_id, get_id, startId));
                }catch (Exception e){
                    e.printStackTrace();
                    mView.onGetHistoryFail("加载历史记录失败");
                }
            }
        });
    }

    @Override
    void insertDB(final MessageListData messageListData) {
        executors.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageDataUtil.insert(messageListData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executors.shutdownNow();
    }
}
