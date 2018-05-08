package com.yw.gourmet.ui.chat.voiceChat;

import com.yw.gourmet.base.BasePresenter;
import com.yw.gourmet.base.BaseView;

/**
 * auth: lewis-v
 * time: 2018/5/4.
 */

public interface VoiceChatContract {
    interface View extends BaseView{
        void onApplySuccess();
        void onRejectSuccess();
        void onStopSuccess();
        void onCancel(String[] msg);
    }

    abstract class Presenter extends BasePresenter<View>{
        abstract void apply();
        abstract void reject();
        abstract void stop(String msg);
        abstract long getTime();
    }
}
