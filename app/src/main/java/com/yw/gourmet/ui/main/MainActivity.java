package com.yw.gourmet.ui.main;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyFragmentAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.listener.MyAction;
import com.yw.gourmet.rxbus.RxBus;
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

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View, View.OnClickListener{
    private LinearLayout ll_menu,ll_home,ll_message,ll_search,ll_my,ll_add;
    private ImageView img_home,img_message,img_search,img_my;
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
        fl_function = (FrameLayout)findViewById(R.id.fl_function);

        ll_menu = (LinearLayout)findViewById(R.id.ll_menu);
        ll_home = (LinearLayout)findViewById(R.id.ll_home);
        ll_message = (LinearLayout)findViewById(R.id.ll_message);
        ll_search = (LinearLayout)findViewById(R.id.ll_search);
        ll_my = (LinearLayout)findViewById(R.id.ll_my);
        ll_add = (LinearLayout)findViewById(R.id.ll_add);

        ll_home.setOnClickListener(this);
        ll_message.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        ll_my.setOnClickListener(this);
        ll_add.setOnClickListener(this);

        img_home = (ImageView)findViewById(R.id.img_home);
        img_message = (ImageView)findViewById(R.id.img_message);
        img_my = (ImageView)findViewById(R.id.img_my);
        img_search = (ImageView)findViewById(R.id.img_search);


        fragmentList.add(new GourmetFragment());
        fragmentList.add(new MessageFragment());
        fragmentList.add(new SearchFragment());
        fragmentList.add(new MyFragment());
        viewpager = (MyViewPager)findViewById(R.id.viewpager);
        viewpager.setOffscreenPageLimit(3);
        adapter = new MyFragmentAdapter(getSupportFragmentManager(),fragmentList);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);
        viewpager.setPageTransformer(true,new DepthPageTransformer());
        initPermission();
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
                        == PERMISSION_DENIED ) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                            ,Manifest.permission.READ_PHONE_STATE}, 0);

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
                if (position != 0) {
                    changeIcon(position);
                    viewpager.setCurrentItem(0);
                    img_home.setImageResource(R.drawable.home_choose);
                    position = 0;
                }
                break;
            case R.id.ll_message:
                if (position != 1) {
                    changeIcon(position);
                    viewpager.setCurrentItem(1);
                    img_message.setImageResource(R.drawable.message_choose);
                    position = 1;
                }
                break;
            case R.id.ll_search:
                if (position != 2) {
                    changeIcon(position);
                    viewpager.setCurrentItem(2);
                    img_search.setImageResource(R.drawable.search_choose);
                    position = 2;
                }
                break;
            case R.id.ll_my:
                if (position != 3) {
                    changeIcon(position);
                    viewpager.setCurrentItem(3);
                    img_my.setImageResource(R.drawable.my_choose);
                    position = 3;
                }
                break;
            case R.id.ll_add:
                addFragmentFunction(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isFunction){
            addFragmentFunction(false);
            return;
        }else {
            moveTaskToBack(true);
        }
    }

    /**
     * 改变对应位置功能的图标为未选中状态
     * @param position
     */
    public void changeIcon(int position){
        switch (position){
            case 0:
                img_home.setImageResource(R.drawable.home);
                break;
            case 1:
                img_message.setImageResource(R.drawable.message);
                break;
            case 2:
                img_search.setImageResource(R.drawable.search);
                break;
            case 3:
                img_my.setImageResource(R.drawable.my);
                break;
        }
    }

    /**
     * 打开功能fragment
     * @param isShow 是否显示
     */
    @Override
    public synchronized void addFragmentFunction(final boolean isShow){
        if (funtionShowing){
            return;
        }
        funtionShowing = true;
        fl_function.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (isShow){
            if (functionFragment == null){
                functionFragment = new FunctionFragment();
                fragmentTransaction.add(R.id.fl_function,functionFragment);
            }
        }else {
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

    public void addFragmentFunction(final boolean isShow, final MyAction action){
        if (funtionShowing){
            return;
        }
        funtionShowing = true;
        fl_function.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (isShow){
            if (functionFragment == null){
                functionFragment = new FunctionFragment();
            }
            fragmentTransaction.add(R.id.fl_function,functionFragment);
        }else {
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
    protected void onResume() {
        super.onResume();
        if (isFunction){
            addFragmentFunction(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除观察者
        RxBus.getDefault().removeAllStickyEvents();
    }
}
