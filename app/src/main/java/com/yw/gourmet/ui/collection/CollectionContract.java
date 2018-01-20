package com.yw.gourmet.ui.collection;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.myenum.LoadEnum;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/1/20.
 */

public interface CollectionContract {
    interface View extends BaseView{
        void onGetCollectionSuccess(BaseData<List<ShareListData<List<String>>>> model, LoadEnum flag);
        void onGetCollectionFail(String msg, LoadEnum flag);
        void onReMarkSuccess(BaseData<ShareListData<List<String>>> model,int position);
    }
    abstract class Presenter extends BasePresenter<View>{
        abstract void getCollection(List<MultipartBody.Part> parts, LoadEnum flag);
        abstract void reMark(List<MultipartBody.Part> parts, final int position);
    }
}
