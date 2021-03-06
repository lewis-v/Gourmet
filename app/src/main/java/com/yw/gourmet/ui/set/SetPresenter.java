package com.yw.gourmet.ui.set;

import com.yw.gourmet.base.rx.RxApiCallback;
import com.yw.gourmet.base.rx.RxSubscriberCallBack;
import com.yw.gourmet.dao.data.audioCacheData.AudioCacheUtil;
import com.yw.gourmet.utils.FileUtils;
import com.yw.gourmet.utils.ThreadUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Lewis-v on 2017/12/21.
 */

public class SetPresenter extends SetContract.Presenter{
    @Override
    int clearFile(final List<File> path) {
        mRxManager.add(Observable.from(ThreadUtils.newSingleThreadPool().submit(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        long size = 0;
                        for (File file : path){
                            size += FileUtils.deleteFile(file);
                        }
                        AudioCacheUtil.clearAll();
                        return size;
                    }
                })).observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                ,new RxSubscriberCallBack<Long>(new RxApiCallback<Long>() {
                    @Override
                    public void onSuccess(Long model) {
                        mView.onClearSuccess(model);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        mView.onFail("清理失败");
                    }
                }));
        return 0;
    }
}
