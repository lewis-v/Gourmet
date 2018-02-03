package com.yw.gourmet.ui.search.keySearch;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.data.UserData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/2/2.
 */

public interface KeySearchContract {
    interface View extends BaseView{
        void onSearchSuccess(BaseData<List<ShareListData<List<String>>>> model);
        void onSearchUserSuccess(BaseData<List<ShareListData<List<String>>>> model);
        void onSearchFail(String msg);
        void onSearchUserFail(String msg);
        void onReMarkSuccess(BaseData<ShareListData<List<String>>> model,int position);
    }
    abstract class Presenter extends BasePresenter<View>{
        abstract void search(List<MultipartBody.Part> parts);
        abstract void searchUser(List<MultipartBody.Part> parts);
        abstract void reMark(List<MultipartBody.Part> parts,int position);
    }
}
