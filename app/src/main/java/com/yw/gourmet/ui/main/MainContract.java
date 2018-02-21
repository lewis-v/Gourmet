package com.yw.gourmet.ui.main;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.InitData;
import com.yw.gourmet.listener.MyAction;

/**
 * Created by yw on 2017/10/22.
 */

public interface MainContract {
    interface View extends BaseView {
        void addFragmentFunction(boolean isShow);
        void addFragmentFunction(final boolean isShow, final MyAction action);
        void onGetVersionSuccess(BaseData<InitData> model);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void getVersion();
    }
}
