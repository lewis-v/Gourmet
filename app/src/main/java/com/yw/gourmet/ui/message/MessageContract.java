package com.yw.gourmet.ui.message;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;

/**
 * Created by LYW on 2017/11/24.
 */

public interface MessageContract {
    interface View extends BaseView{

    }

    abstract class Presenter extends BasePresenter<View>{

    }
}
