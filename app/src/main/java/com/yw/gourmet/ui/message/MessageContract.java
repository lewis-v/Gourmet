package com.yw.gourmet.ui.message;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.MessageListData;

import java.util.List;

/**
 * Created by LYW on 2017/11/24.
 */

public interface MessageContract {
    interface View extends BaseView{
        void onLoadMessageListSuccess(BaseData<List<MessageListData>> model);
    }

    abstract class Presenter extends BasePresenter<View>{

    }
}
