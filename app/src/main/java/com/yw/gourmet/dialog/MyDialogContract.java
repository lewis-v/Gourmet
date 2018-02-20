package com.yw.gourmet.dialog;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/1/20.
 */

public interface MyDialogContract {
    interface View extends BaseView{

    }
    abstract class Presenter extends BasePresenter<View>{
        abstract void collection(List<MultipartBody.Part> parts);
        abstract void feedback(List<MultipartBody.Part> parts);
    }
}
