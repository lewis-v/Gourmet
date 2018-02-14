package com.yw.gourmet.ui.detail.diary;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.CommentData;
import com.yw.gourmet.data.ShareListData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by Lewis-v on 2017/12/31.
 */

public interface DiaryDetailContract {
    interface View extends BaseView{
        void onGetDetailSuccess(BaseData<ShareListData> model);
        void onGetDetailFail(String msg);
        void onGetCommentSuccess(BaseData<List<CommentData>> model);
        void onGetCommentFail(String msg);
        void onSendCommentSuccess(BaseData<List<CommentData>> model);
        void onSendCommentFail(String msg);
        void onRemarkSuccess(BaseData<ShareListData> model);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void getDetail(List<MultipartBody.Part> parts);
        abstract void getComment(List<MultipartBody.Part> parts);
        abstract void sendComment(List<MultipartBody.Part> parts);
        abstract void reMark(List<MultipartBody.Part> parts);
        abstract void complaint(List<MultipartBody.Part> parts);
    }
}
