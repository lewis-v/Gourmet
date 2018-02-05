package com.yw.gourmet.ui.registered;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/2/5.
 */

public interface RegisteredContract {
    interface View extends BaseView{
        void onSendSuccess(String msg);
        void onSendFails(String msg);
        void onCodeSuccess(String msg);
        void onCodeFails(String msg);
        void onCheckSuccess(BaseData model);
        void onCheckFails(String msg);
        void onRegisteredSuccess(BaseData model);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void sendSMS(String phone);
        abstract void putCode(String phone,String code);
        abstract void Check(List<MultipartBody.Part> parts);
        abstract void Registered(List<MultipartBody.Part> parts);
    }
}
