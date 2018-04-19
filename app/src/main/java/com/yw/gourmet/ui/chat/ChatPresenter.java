package com.yw.gourmet.ui.chat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.audio.recoder.AudioRecoderData;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.center.MessageCenter;
import com.yw.gourmet.center.event.IMessageSendEvent;
import com.yw.gourmet.dao.data.messageData.MessageDataUtil;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;
import com.yw.gourmet.data.UserData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MultipartBody;

/**
 * Created by Lewis-v on 2017/12/28.
 */

public class ChatPresenter extends ChatContract.Presenter {
    private ExecutorService executors;
    private IMessageSendEvent iMessageSendEvent;

    public ChatPresenter(){
        super();
        executors = Executors.newSingleThreadExecutor();
        iMessageSendEvent = new IMessageSendEvent() {
            @Override
            public boolean onSendMessageResult(final BaseData<MessageListData> message, MessageListData MessageListData, final int position) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (message.getStatus() == 0) {
                            mView.onSendSuccess(message.getData(),position);
                        }else {
                            mView.onSendFail(message.getMessage(),position);
                        }
                    }
                });

                return true;
            }

            @Override
            public boolean onSendMessageFail(MessageListData message, final int position, final String msg) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mView.onSendFail(msg,position);
                    }
                });
                return true;
            }
        };
    }

    @Override
    void sendMessage(List<MultipartBody.Part> parts,MessageListData messageListData, final int position) {
        MessageCenter.getInstance().sendMessage(parts,messageListData,position);
//
//        mRxManager.add(Api.getInstance().SendMessage(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
//            @Override
//            public void onSuccess(BaseData model) {
//                if (model.getStatus() == 0){
//                    mView.onSendSuccess(position);
//                }else {
//                    mView.onSendFail(model.getMessage(),position);
//                }
//            }
//
//            @Override
//            public void onFailure(int code, String msg) {
//                mView.onSendFail(msg,position);
//            }
//        }));
    }

    @Override
    void getMessageDetail(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().GetMessageDetail(parts),new RxSubscriberCallBack<BaseData<List<MessageListData>>>(new RxApiCallback<BaseData<List<MessageListData>>>() {
            @Override
            public void onSuccess(BaseData<List<MessageListData>> model) {
                if (isReLoginFail(model)){
                    return;
                }
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
                if (isReLoginFail(model)){
                    return;
                }

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
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 0){
                    mView.onUpImgSuccess(model,position);
                }else if (model.getStatus() == 1){
                    mView.onUpImgFail(model.getMessage(),position);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onUpImgFail(msg,position);
            }
        }));
    }

    @Override
    void upAudio(List<MultipartBody.Part> parts, final int position, final AudioRecoderData data) {
        mRxManager.add(Api.getInstance().UpAudio(parts),new RxSubscriberCallBack<BaseData<String>>(new RxApiCallback<BaseData<String>>() {
            @Override
            public void onSuccess(BaseData<String> model) {
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 0){
                    mView.onUpAudioSuccess(model,position,data);
                }else if (model.getStatus() == 1){
                    mView.onUpAudioFail(model.getMessage(),position);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onUpAudioFail(msg,position);
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
    void getUserInfo(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().GetUserInfo(parts),new RxSubscriberCallBack<BaseData<UserData>>(new RxApiCallback<BaseData<UserData>>() {
            @Override
            public void onSuccess(BaseData<UserData> model) {
                if (isReLoginFail(model)){
                    return;
                }
                if (model.getStatus() == 0){
                    mView.onGetUserInfoSuccess(model.getData());
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
    void onPause() {
        if (iMessageSendEvent != null){
            MessageCenter.getInstance().removeMessageHandle(iMessageSendEvent);
        }
    }

    @Override
    void onResume() {
        MessageCenter.getInstance().addMessageHandleTop(iMessageSendEvent);
    }


    @Override
    void updataDB(final MessageListData messageListData){
        executors.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    MessageDataUtil.updata(messageListData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executors != null) {
            executors.shutdownNow();
        }
    }
}
