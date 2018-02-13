package com.yw.gourmet.ui.chat;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by Lewis-v on 2017/12/28.
 */

public interface ChatContract {
    interface View extends BaseView{
        void onSendSuccess(int position);
        void onSendFail(String msg,int position);
        void onGetDetailSuccess(BaseData<List<MessageListData>> model);
        void onUpImgSuccess(BaseData<String> model,int position);
        void onUpImgFail(String msg,int position);
        void onGetHistorySuccess(List<MessageListData> model);
        void onGetHistoryFail(String msg);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void sendMessage(List<MultipartBody.Part> parts,MessageListData messageListData,int position);
        abstract void getMessageDetail(List<MultipartBody.Part> parts);
        abstract void setMessageRead(List<MultipartBody.Part> parts);
        abstract void upImg(List<MultipartBody.Part> parts,int position);
        abstract void getHistory(String put_id,String get_id,int startId);
        abstract void insertDB(MessageListData messageListData);
        abstract void updataDB(MessageListData messageListData);
    }
}
