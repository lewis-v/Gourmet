package com.yw.gourmet.ui.flash;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.UserData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/11/16.
 */

public interface FlashConstract {
    interface View extends BaseView{
        void onLoginSuccess(BaseData<UserData> model);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void onLogin(List<MultipartBody.Part> parts);
    }
}
