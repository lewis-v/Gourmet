package com.yw.gourmet.ui.registered;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.yw.gourmet.Constant;
import com.yw.gourmet.api.Api;
import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.data.BaseData;


import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.MultipartBody;

/**
 * auth: lewis-v
 * time: 2018/2/5.
 */

public class RegisteredPresenter extends RegisteredContract.Presenter{
    private Handler handler;
    public RegisteredPresenter(){
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    void sendSMS(String phone) {
        SMSSDK.unregisterAllEventHandler();
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, final int result, final Object data) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            mView.onSendSuccess("验证码发送成功");
                        } else{
                            mView.onSendFails("验证码发送失败,请重试");
                            Log.e("SMS",data.toString());
                        }
                    }
                });

            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(Constant.COUNTRY_CODE, phone);
    }

    @Override
    void putCode(String phone, String code) {
        SMSSDK.unregisterAllEventHandler();
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, final int result, final Object data) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            mView.onCodeSuccess("验证成功");
                        } else{
                            mView.onCodeFails("验证码错误");
                            Log.e("SMS",data.toString());
                        }
                    }
                });
            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(Constant.COUNTRY_CODE, phone, code);
    }

    @Override
    void Check(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().Check(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
            @Override
            public void onSuccess(BaseData model) {
                if (model.getStatus() == 0){
                    mView.onCheckSuccess(model);
                }else {
                    mView.onCodeFails(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onCheckFails(msg);
            }
        }));
    }

    @Override
    void Registered(List<MultipartBody.Part> parts) {
        mRxManager.add(Api.getInstance().Registered(parts),new RxSubscriberCallBack<BaseData>(new RxApiCallback<BaseData>() {
            @Override
            public void onSuccess(BaseData model) {
                if (model.getStatus() == 0){
                    mView.onRegisteredSuccess(model);
                }else {
                    mView.onFail(model.getMessage());
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mView.onFail(msg);
            }
        }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}
