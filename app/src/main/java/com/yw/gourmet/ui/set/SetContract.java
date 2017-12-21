package com.yw.gourmet.ui.set;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;

import java.io.File;

/**
 * Created by Lewis-v on 2017/12/21.
 */

public interface SetContract {
    interface View extends BaseView{
        void onClearSuccess(Long clearSize);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract int clearFile(File file);
    }
}
