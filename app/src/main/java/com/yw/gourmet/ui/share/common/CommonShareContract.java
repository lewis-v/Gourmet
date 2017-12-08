package com.yw.gourmet.ui.share.common;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/12/6.
 */

public interface CommonShareContract {
    interface View extends BaseView{
        void onUpImgSuccess(BaseData<String> model,int position);
        void onUpImgFail(String msg,int position);
        void onShareSuccess(BaseData model);
        void onShareFail(String msg);
    }
    abstract class Presenter extends BasePresenter<View>{
        abstract void upImg(List<MultipartBody.Part> parts,int position);
        abstract void shareCommon(List<MultipartBody.Part> parts);
    }
}
