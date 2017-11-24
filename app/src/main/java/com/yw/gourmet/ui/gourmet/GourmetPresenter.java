package com.yw.gourmet.ui.gourmet;

import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.myenum.LoadEnum;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by LYW on 2017/11/17.
 */

public class GourmetPresenter extends GourmetContract.Presenter {
    @Override
    void load(List<MultipartBody.Part> parts, final LoadEnum flag) {
        mRxManager.add(Api.getInstance().LoadShareList(parts),new RxSubscriberCallBack<BaseData<List<ShareListData<List<String>>>>>(new RxApiCallback<BaseData<List<ShareListData<List<String>>>>>() {
            @Override
            public void onSuccess(BaseData<List<ShareListData<List<String>>>> model) {
                if (model.getStatus() == 0){
                    mView.onLoadSuccess(model,flag);
                }else if (model.getStatus() == 1){
                    mView.onLoadFail(model.getMessage(),flag);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onLoadFail(msg,flag);
            }
        }));
    }
}
