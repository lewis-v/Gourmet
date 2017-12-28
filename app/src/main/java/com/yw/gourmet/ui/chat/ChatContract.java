package com.yw.gourmet.ui.chat;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by Lewis-v on 2017/12/28.
 */

public interface ChatContract {
    interface View extends BaseView{
        void onSendSuccess();
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void sendMessage(List<MultipartBody.Part> parts);
    }
}
