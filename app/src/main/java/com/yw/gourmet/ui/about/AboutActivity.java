package com.yw.gourmet.ui.about;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.InitData;
import com.yw.gourmet.dialog.MyDialogTipFragment;
import com.yw.gourmet.service.UpdateService;
import com.yw.gourmet.utils.ToastUtils;

public class AboutActivity extends BaseActivity<AboutPresenter> implements AboutContract.View {
    private TextView tv_version,tv_check;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        tv_version = findViewById(R.id.tv_version);
        try {
            tv_version.setText(getPackageManager().getPackageInfo(getPackageName(),0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tv_check = findViewById(R.id.tv_check);
        tv_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getVersion();
            }
        });
        mPresenter.getVersion();
    }

    @Override
    public void onGetVersionSuccess(BaseData<InitData> model) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            String localVersion = packageInfo.versionName;
            if (localVersion.equals(model.getData().getAndroid_version())){
                ToastUtils.showSingleToast("已经是最新版咯");
            }else {
                new MyDialogTipFragment().setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                    @Override
                    public void OnEnter(String Tag) {
                        Intent intent = new Intent(AboutActivity.this, UpdateService.class);
                        startService(intent);
                    }
                }).setShowText("有新版本是否更新?").setTextEnter("更新").show(getSupportFragmentManager(),"downloadUpdate");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
