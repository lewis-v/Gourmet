package com.yw.gourmet.ui.login;

import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.UserData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/11/13.
 */

public interface LoginContract {
     interface View extends BaseView{
        /**
         * 登录成功
         * @param model
         */
        void onLoginSuccess(BaseData<UserData> model);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void login(List<MultipartBody.Part> parts);
    }
}
