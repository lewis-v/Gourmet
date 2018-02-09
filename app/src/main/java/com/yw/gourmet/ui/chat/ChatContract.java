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
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void sendMessage(List<MultipartBody.Part> parts,int position);
        abstract void getMessageDetail(List<MultipartBody.Part> parts);
        abstract void setMessageRead(List<MultipartBody.Part> parts);
    }
}
