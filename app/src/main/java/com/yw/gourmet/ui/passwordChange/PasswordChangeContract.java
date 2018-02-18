package com.yw.gourmet.ui.passwordChange;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/2/18.
 */

public interface PasswordChangeContract {
    interface View extends BaseView{
        void onChangeSuccess(BaseData model);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void changeDetail(List<MultipartBody.Part> parts);
    }
}
