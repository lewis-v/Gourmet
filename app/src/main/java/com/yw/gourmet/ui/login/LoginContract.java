package com.yw.gourmet.ui.login;

import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.data.BaseData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/11/13.
 */

public interface LoginContract {
    abstract class View extends BaseActivity<LoginPresenter>{
        /**
         * 登录成功
         * @param model
         */
        abstract void onLoginSuccess(BaseData model);
    }

    abstract class Presenter extends BasePresenter<LoginActivity>{
        abstract void login(List<MultipartBody.Part> parts);
    }
}
