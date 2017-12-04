package com.yw.gourmet.ui.changeDetail;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.UserData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/11/29.
 */

public interface ChangeDetailContract {
    interface View extends BaseView{
        void onChangeSuccess(BaseData<UserData> model);
        void onUpSuccess(BaseData<String> model);
    }
    abstract class Presenter extends BasePresenter<View>{
        abstract void changeDetail(List<MultipartBody.Part> parts);
        abstract void upImg(List<MultipartBody.Part> parts);
    }
}
