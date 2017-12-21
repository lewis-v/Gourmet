package com.yw.gourmet.ui.share.menu;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by Lewis-v on 2017/12/21.
 */

public interface MenuContract {
    interface View extends BaseView{
        void onUpImgSuccess(BaseData<String> model);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void upImg(List<MultipartBody.Part> parts);
    }
}
