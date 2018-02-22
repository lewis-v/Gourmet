package com.yw.gourmet.ui.reject;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/2/22.
 */

public interface RejectContract {
    interface View extends BaseView{
        void onGetRejectSuccess(BaseData<List<ShareListData<List<String>>>> model);
    }
    abstract class Presenter extends BasePresenter<View>{
        abstract void getRejectList(List<MultipartBody.Part> parts);
    }
}
