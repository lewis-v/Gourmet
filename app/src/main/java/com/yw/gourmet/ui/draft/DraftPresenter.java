package com.yw.gourmet.ui.draft;

import com.yw.gourmet.dao.data.saveData.SaveDataUtil;
import com.yw.gourmet.utils.ThreadUtils;

import java.util.concurrent.ExecutorService;

/**
 * auth: lewis-v
 * time: 2018/1/23.
 */

public class DraftPresenter extends DraftContract.Presenter {
    private ExecutorService executorService;

    public DraftPresenter() {
        super();
        executorService = ThreadUtils.newSingleThreadPool();
    }

    @Override
    void getAllSaveData() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mView.onGetSuccess(SaveDataUtil.querydataBy());
                }catch (Exception e){
                    mView.onGetFail(e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService !=null){
            executorService.shutdownNow();
        }
    }
}
