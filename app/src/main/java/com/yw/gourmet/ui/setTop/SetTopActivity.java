package com.yw.gourmet.ui.setTop;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.gourmet.Constant;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MySetTopAdapter;
import com.yw.gourmet.base.BaseActivity;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.ShareListData;
import com.yw.gourmet.myenum.LoadEnum;
import com.yw.gourmet.ui.main.FunctionFragment;
import com.yw.gourmet.utils.WindowUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

public class SetTopActivity extends BaseActivity<SetTopPresenter> implements SetTopContract.TopView
        ,View.OnClickListener{
    private ImageView img_back;
    private RecyclerView recycler_top;
    private MySetTopAdapter adapter;
    private LinearLayout ll_share;
    private List<ShareListData<List<String>>> data = new ArrayList<>();
    private FrameLayout fl_share;
    private TextView tv_move;
    private MyShareFragment shareFragment;
    private ImageView img_top;
    private float downY;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_top;
    }

    @Override
    protected void initView() {
        img_back = findViewById(R.id.img_back);
        img_top = findViewById(R.id.img_top);
        img_top.setOnClickListener(this);
        img_back.setOnClickListener(this);

        ll_share = findViewById(R.id.ll_share);

        fl_share = findViewById(R.id.fl_share);
        tv_move = findViewById(R.id.tv_move);
        tv_move.setOnClickListener(this);
        img_top.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            if (event.getRawY() > WindowUtil.height/8) {
                                ViewGroup.LayoutParams params = ll_share.getLayoutParams();
                                params.height = (int) (WindowUtil.height - event.getRawY());
                                ll_share.setLayoutParams(params);
                            }
                            return true;
                        case MotionEvent.ACTION_UP:
                            if (downY > event.getRawY()){//上滑
                                if (downY - event.getRawY() > WindowUtil.height/8){
                                    perforAnimate(ll_share,ll_share.getHeight(),WindowUtil.height*7/8);
                                }else{
                                    perforAnimate(ll_share,ll_share.getHeight(),WindowUtil.height/8);
                                }
                            }else{//下滑
                                if (event.getRawY() - downY > WindowUtil.height/8){
                                    perforAnimate(ll_share,ll_share.getHeight(),WindowUtil.height/8);
                                }else{
                                    perforAnimate(ll_share,ll_share.getHeight(),WindowUtil.height*7/8);
                                }
                            }
                            return true;
                    }
                return false;
            }
        });

        recycler_top = findViewById(R.id.recycler_top);
        recycler_top.setItemAnimator(new DefaultItemAnimator());
        recycler_top.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MySetTopAdapter(this,data);
        recycler_top.setAdapter(adapter);

        refresh();
        addFragmentFunction();
    }

    public void perforAnimate(final View view, final int starHeight, final int endHeight){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1,100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private IntEvaluator mEvaluator = new IntEvaluator();
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();

                float fraction = animation.getAnimatedFraction();
                view.getLayoutParams().height = mEvaluator.evaluate(fraction,starHeight,endHeight);
                view.requestLayout();
            }
        });
        valueAnimator.setDuration(300).start();
    }

    /**
     * 显示我的分享fragment
     */
    public void addFragmentFunction() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        shareFragment = new MyShareFragment().setTopList(data).setTopAdapter(adapter);
        fragmentTransaction.add(R.id.fl_share, shareFragment);
        fragmentTransaction.commitNow();
    }

    @Override
    public void onSetTopSuccess(String msg, int position) {

    }

    @Override
    public void onGetTopSuccess(BaseData<List<ShareListData<List<String>>>> model) {
        data.clear();
        data.addAll(model.getData());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void OnGetTopFail(String msg) {

    }

    /**
     * 刷新数据
     */
    public void refresh(){
        mPresenter.getTop(new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("id", Constant.userData.getId()).build().parts());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
        }
    }
}
