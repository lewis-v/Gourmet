package com.yw.gourmet.ui.reject;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.myenum.LoadEnum;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/2/22.
 */

public interface RejectContract {
    interface View extends BaseView{
        void onGetRejectSuccess(BaseData<List<ShareListData<List<String>>>> model,LoadEnum type);
        void onGetListFail(String msg, LoadEnum flag);
        void onRejectDeleteSuccess(BaseData model,int position);
    }
    abstract class Presenter extends BasePresenter<View>{
        abstract void getRejectList(List<MultipartBody.Part> parts,LoadEnum type);
        abstract void rejectDelete(List<MultipartBody.Part> parts,int position);
    }
}
