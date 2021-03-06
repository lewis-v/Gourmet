package com.yw.gourmet.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyFragmentAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.InitData;
import com.yw.gourmet.dialog.MyDialogTipFragment;
import com.yw.gourmet.listener.MyAction;
import com.yw.gourmet.push.PushReceiver;
import com.yw.gourmet.service.UpdateService;
import com.yw.gourmet.ui.gourmet.GourmetFragment;
import com.yw.gourmet.ui.message.MessageFragment;
import com.yw.gourmet.ui.my.MyFragment;
import com.yw.gourmet.ui.search.SearchFragment;
import com.yw.gourmet.utils.DisplayUtils;
import com.yw.gourmet.utils.WindowUtil;
import com.yw.gourmet.widget.DepthPageTransformer;
import com.yw.gourmet.widget.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View, View.OnClickListener {
    private LinearLayout ll_menu, ll_home, ll_message, ll_search, ll_my, ll_add;
    private ImageView img_home, img_message, img_search, img_my;
    private FrameLayout fl_function;
    private MyViewPager viewpager;
    private MyFragmentAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private int position = 0;//目前选择的功能位置
    private boolean isFunction = false;//是否打开功能fragment
    private FunctionFragment functionFragment;
    private boolean funtionShowing = false;//功能fragment动画是否在展示中

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtil.initWindowUtil(getWindowManager());
        DisplayUtils.init(this);
    }

    @Override
    protected void initView() {
        fl_function = (FrameLayout) findViewById(R.id.fl_function);

        ll_menu = (LinearLayout) findViewById(R.id.ll_menu);
        ll_home = (LinearLayout) findViewById(R.id.ll_home);
        ll_message = (LinearLayout) findViewById(R.id.ll_message);
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        ll_my = (LinearLayout) findViewById(R.id.ll_my);
        ll_add = (LinearLayout) findViewById(R.id.ll_add);

        ll_home.setOnClickListener(this);
        ll_message.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        ll_my.setOnClickListener(this);
        ll_add.setOnClickListener(this);

        img_home = (ImageView) findViewById(R.id.img_home);
        img_message = (ImageView) findViewById(R.id.img_message);
        img_my = (ImageView) findViewById(R.id.img_my);
        img_search = (ImageView) findViewById(R.id.img_search);


        fragmentList.add(new GourmetFragment());
        fragmentList.add(new MessageFragment());
        fragmentList.add(new SearchFragment());
        fragmentList.add(new MyFragment());
        viewpager = (MyViewPager) findViewById(R.id.viewpager);
        viewpager.setOffscreenPageLimit(3);
        adapter = new MyFragmentAdapter(getSupportFragmentManager(), fragmentList);
        viewpager.setAdapter(adapter);
        position = getIntent().getIntExtra("position", 0);
        viewpager.setCurrentItem(position);
        selectItem(position);
        viewpager.setPageTransformer(true, new DepthPageTransformer());
        initPermission();
        mPresenter.getVersion();
    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        == PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                            , Manifest.permission.READ_PHONE_STATE
                            , Manifest.permission.RECORD_AUDIO}, 0);

        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_home:
                selectItem(0);
                break;
            case R.id.ll_message:
                selectItem(1);
                break;
            case R.id.ll_search:
                selectItem(2);
                break;
            case R.id.ll_my:
                selectItem(3);
                break;
            case R.id.ll_add:
                addFragmentFunction(true);
                break;
        }
    }

    /**
     * 选择tab
     *
     * @param position
     */
    public void selectItem(int position) {
        this.position = position;
        changeIcon();
        chooseItem(position);
        viewpager.setCurrentItem(position);
    }

    /**
     * 设为已选中的图标
     *
     * @param position
     */
    public void chooseItem(int position) {
        switch (position) {
            case 0:
                img_home.setImageResource(R.drawable.home_choose);
                break;
            case 1:
                img_message.setImageResource(R.drawable.message_choose);
                break;
            case 2:
                img_search.setImageResource(R.drawable.search_choose);
                break;
            case 3:
                img_my.setImageResource(R.drawable.my_choose);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (isFunction) {
            addFragmentFunction(false);
            return;
        } else {
            moveTaskToBack(true);
        }
    }

    /**
     * 改变对应位置功能的图标为未选中状态
     */
    public void changeIcon() {
        if (position != 0) {
            img_home.setImageResource(R.drawable.home);
        }
        if (position != 1) {
            img_message.setImageResource(R.drawable.message);
        }
        if (position != 2) {
            img_search.setImageResource(R.drawable.search);
        }
        if (position != 3) {
            img_my.setImageResource(R.drawable.my);
        }
    }


    /**
     * 打开功能fragment
     *
     * @param isShow 是否显示
     */
    @Override
    public synchronized void addFragmentFunction(final boolean isShow) {
        if (funtionShowing) {
            return;
        }
        funtionShowing = true;
        fl_function.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (isShow) {
            if (functionFragment == null) {
                functionFragment = new FunctionFragment();
                fragmentTransaction.add(R.id.fl_function, functionFragment);
            }
        } else {
            if (functionFragment != null) {
                fragmentTransaction.remove(functionFragment);
                functionFragment = null;
            }
        }
        fragmentTransaction.runOnCommit(new Runnable() {
            @Override
            public void run() {
                funtionShowing = false;//fragment是否显示的标记
                isFunction = isShow;
            }
        }).commit();
    }

    public void addFragmentFunction(final boolean isShow, final MyAction action) {
        if (funtionShowing) {
            return;
        }
        funtionShowing = true;
        fl_function.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (isShow) {
            if (functionFragment == null) {
                functionFragment = new FunctionFragment();
            }
            fragmentTransaction.add(R.id.fl_function, functionFragment);
        } else {
            if (functionFragment != null) {
                fragmentTransaction.remove(functionFragment);
            }
        }
        fragmentTransaction.runOnCommit(new Runnable() {
            @Override
            public void run() {
                if (action != null) {
                    action.Action0();
                }
                isFunction = isShow;
                funtionShowing = false;
            }
        }).commit();

    }

    @Override
    public void onGetVersionSuccess(BaseData<InitData> model) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            String localVersion = packageInfo.versionName;
            if (!localVersion.equals(model.getData().getAndroid_version())) {
                new MyDialogTipFragment().setOnEnterListener(new MyDialogTipFragment.OnEnterListener() {
                    @Override
                    public void OnEnter(String Tag) {
                        Intent intent = new Intent(MainActivity.this, UpdateService.class);
                        startService(intent);
                    }
                }).setShowText("有新版本 " + model.getData().getAndroid_version() + " \n更新内容:\n" + model.getData().getUpdate_content()).setTextEnter("更新").show(getSupportFragmentManager(), "downloadUpdate");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isFunction) {
            addFragmentFunction(false);
        }
        PushReceiver.isInit = true;
        Log.i("Main", String.valueOf(PushReceiver.isInit));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
