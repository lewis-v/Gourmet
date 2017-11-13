package com.yw.gourmet.ui.main;

import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.base.BasePresenter;

/**
 * Created by yw on 2017/10/22.
 */

public interface MainContract {
    abstract class View extends BaseActivity<MainPresenter> {
        public abstract void addFragmentFunction(boolean isShow);
    }

    abstract class Presenter extends BasePresenter<MainActivity>{

    }
}
