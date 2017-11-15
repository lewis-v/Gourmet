package com.yw.gourmet.ui.main;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;

/**
 * Created by yw on 2017/10/22.
 */

public interface MainContract {
    interface View extends BaseView {
        void addFragmentFunction(boolean isShow);
    }

    abstract class Presenter extends BasePresenter<View>{

    }
}
