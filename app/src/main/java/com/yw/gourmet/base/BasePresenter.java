package com.yw.gourmet.base;

import android.content.Context;

import com.yw.gourmet.base.rx.RxManager;
import com.yw.gourmet.data.BaseData;


/**
 * Created by yw on 2017-08-07.
 */

public class BasePresenter<V> {
    protected V mView;
    protected Context context;
    public RxManager mRxManager=new RxManager();


    public BasePresenter(){
    }

    public void setmView(V mView) {
        this.mView = mView;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void onDestroy(){
        mRxManager.clear();
    }

    public boolean isReLoginFail(BaseData model){
        if (model.getStatus() == -2){//重新登录
            if (mView instanceof BaseView){
                ((BaseView)mView).onReLoginFail(model.getMessage());
                return true;
            }
        }
        return false;
    }
}
