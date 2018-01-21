package com.yw.gourmet.ui.about;

import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseActivity;

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

            }
        });
    }
}
