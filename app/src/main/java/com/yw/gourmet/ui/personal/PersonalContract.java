package com.yw.gourmet.ui.personal;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.data.UserData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/11/29.
 */

public interface PersonalContract {
    interface View extends BaseView{
        void onUpImgSuccess(BaseData<String> model);
        void onChangeSuccess(BaseData<UserData> model);
        void onGetTopSuccess(BaseData<List<ShareListData<List<String>>>> model);
        void OnGetTopFail(String msg);
        void onReMarkSuccess(BaseData<ShareListData<List<String>>> model,int position);
        void onGetUserInfoSuccess(BaseData<UserData> model);
        void onGetUserInfoFail(String msg);
    }
    abstract class Presenter extends BasePresenter<View>{
        abstract void upImg(List<MultipartBody.Part> parts);
        abstract void changeBack(List<MultipartBody.Part> parts);
        abstract void getTop(List<MultipartBody.Part> parts);
        abstract void reMark(List<MultipartBody.Part> parts,int position);
        abstract void getUserInfo(List<MultipartBody.Part> parts);
    }
}
