package com.yw.gourmet.ui.draft;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.dao.data.saveData.SaveData;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/23.
 */

public interface DraftContract {
    interface View extends BaseView{
        void onGetSuccess(List<SaveData> model);
        void onGetFail(String msg);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void getAllSaveData();
    }
}
