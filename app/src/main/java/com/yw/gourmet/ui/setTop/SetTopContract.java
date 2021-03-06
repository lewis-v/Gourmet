package com.yw.gourmet.ui.setTop;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.myenum.LoadEnum;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/1/26.
 */

public interface SetTopContract {
    interface View extends BaseView{
        void onSetTopSuccess(String msg,int position);
        void onGetListSuccess(BaseData<List<ShareListData<List<String>>>> model, final LoadEnum flag);
        void onGetListFail(String msg, final LoadEnum flag);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void setTop(List<MultipartBody.Part> parts,int position);
        abstract void getShareList(List<MultipartBody.Part> parts, final LoadEnum flag);
    }

    interface TopView extends BaseView{
        void onSetTopSuccess(String msg,int position,int endPosition);
        void onGetTopSuccess(BaseData<List<ShareListData<List<String>>>> model);
        void OnGetTopFail(String msg);
        void addTop(ShareListData<List<String>> data,int position);
        List<ShareListData<List<String>>> getTop();
    }

    abstract class TopPresenter extends BasePresenter<TopView>{
        abstract void setTop(List<MultipartBody.Part> parts,int position,int endPosition);
        abstract void getTop(List<MultipartBody.Part> parts);
    }
}
