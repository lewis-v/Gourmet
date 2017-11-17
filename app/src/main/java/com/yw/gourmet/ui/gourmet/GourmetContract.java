package com.yw.gourmet.ui.gourmet;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/11/17.
 */

public interface GourmetContract {
    interface View extends BaseView{
        void onLoadSuccess(BaseData<List<ShareListData<String>>> model,int flag);
        void onLoadFail(String msg,int flag);
    }
    abstract class Presenter extends BasePresenter<View>{
        abstract void load(List<MultipartBody.Part> parts,int flag);
    }
}
