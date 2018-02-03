package com.yw.gourmet.ui.search;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.FlowData;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/28.
 */

public interface SearchContract {
    interface View extends BaseView{
        void onGetFlowSuccess(BaseData<List<FlowData>> model);
    }
    abstract class Presenter extends BasePresenter<View>{
        abstract void getFlow();
    }
}
