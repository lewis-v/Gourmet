package com.yw.gourmet.base;

/**
 * Created by LYW on 2017/11/15.
 */

public interface BaseView {
    void onFail(String msg);
    void onSuccess(String msg);
    void onReLoginFail(String msg);
    void setLoadDialog(boolean isLoadDialog);
}
