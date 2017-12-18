package com.yw.gourmet.ui.share.diary;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by Lewis-v on 2017/12/19.
 */

public interface DiaryContract {
    interface View extends BaseView{
        void onUpImgSuccess(BaseData<String> model);
        void onPutSuccess(BaseData model);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void upImg(List<MultipartBody.Part> parts);
        abstract void putDiary(List<MultipartBody.Part> parts);
    }
}
