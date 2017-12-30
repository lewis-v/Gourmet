package com.yw.gourmet.ui.detail.common;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.CommentData;
import com.yw.gourmet.data.ShareListData;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by Lewis-v on 2017/12/30.
 */

public class CommonDetailPresenter extends CommonDetailContract.Presenter{
    @Override
    void getDetail(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().GetCommonDetail(parts),new RxSubscriberCallBack<BaseData<ShareListData<List<String>>>>(new RxApiCallback<BaseData<ShareListData<List<String>>>>() {
            @Override
            public void onSuccess(BaseData<ShareListData<List<String>>> model) {
                if (model.getStatus() == 0){
                    mView.onGetDetailSuccess(model);
                }else {
                    mView.onGetDetailFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onGetDetailFail(msg);
            }
        }));
    }

    @Override
    void getComment(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().GetComment(parts),new RxSubscriberCallBack<BaseData<List<CommentData>>>(new RxApiCallback<BaseData<List<CommentData>>>() {
            @Override
            public void onSuccess(BaseData<List<CommentData>> model) {
                if (model.getStatus() == 0){
                    mView.onGetCommentSuccess(model);
                }else {
                    mView.onGetCommentFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onGetCommentFail(msg);
            }
        }));
    }

    @Override
    void sendComment(List<MultipartBody.Part> parts, final int position) {
        mRxManager.add(Api.getInstance().PutComment(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
            @Override
            public void onSuccess(BaseData model) {
                if (model.getStatus() == 0){
                    mView.onSendCommentSuccess(model,position);
                }else {
                    mView.onSendCommentFail(model.getMessage(),position);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onSendCommentFail(msg,position);
            }
        }));
    }

    @Override
    void reMark(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().PutReMark(parts),new RxSubscriberCallBack<BaseData<ShareListData<List<String>>>>(new RxApiCallback<BaseData<ShareListData<List<String>>>>() {
            @Override
            public void onSuccess(BaseData<ShareListData<List<String>>> model) {
                if (model.getStatus() == 0){
                    mView.onRemarkSuccess(model);
                }else if (model.getStatus() == 1){
                    mView.onFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onFail(msg);
            }
        }));
    }
}
