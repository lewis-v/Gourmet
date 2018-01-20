package com.yw.gourmet.ui.commentMy;

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

public interface CommentMyContract {
    interface View extends BaseView{
        void onGetListSuccess(BaseData<List<ShareListData<List<String>>>> model, final LoadEnum flag);
        void onGetListFail(String msg, final LoadEnum flag);
        void onReMarkSuccess(BaseData<ShareListData<List<String>>> model,int position);
    }
    abstract class Presener extends BasePresenter<View>{
        abstract void getShareList(List<MultipartBody.Part> parts, final LoadEnum flag);
        abstract void reMark(List<MultipartBody.Part> parts, final int position);
    }
}
