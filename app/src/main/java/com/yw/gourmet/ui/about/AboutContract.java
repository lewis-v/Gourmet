package com.yw.gourmet.ui.about;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.InitData;

/**
 * auth: lewis-v
 * time: 2018/1/21.
 */

public interface AboutContract {
    interface View extends BaseView{
        void onGetVersionSuccess(BaseData<InitData> model);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void getVersion();
    }
}
