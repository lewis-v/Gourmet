package com.yw.gourmet.ui.share.raiders;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/1/6.
 */
public interface RaidersContract {
    interface View extends BaseView{
        void onUpImgSuccess(BaseData<String> model, int position);
        void onUpImgFail(String msg,int position);
        void onShareSuccess(String msg);
        void onShareFail(String msg);
    }
    abstract class Presenter extends BasePresenter<View>{
        abstract void upImg(List<MultipartBody.Part> parts, int position);
        abstract void shareRaiders(List<MultipartBody.Part> parts);
    }
}
