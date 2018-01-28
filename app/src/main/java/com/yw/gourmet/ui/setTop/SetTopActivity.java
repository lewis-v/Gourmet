package com.yw.gourmet.ui.setTop;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
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
import com.yw.gourmet.listener.OnItemClickListener;
import com.yw.gourmet.listener.OnMoveListener;
import com.yw.gourmet.ui.detail.common.CommonDetailActivity;
import com.yw.gourmet.ui.detail.diary.DiaryDetailActivity;
import com.yw.gourmet.ui.detail.menu.MenuDetailActivity;
import com.yw.gourmet.ui.detail.raiders.RaidersDetailActivity;
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
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                Intent intent = null;
                switch (data.get(position).getType()) {
                    case Constant.TypeFlag.SHARE:
                        intent = new Intent(SetTopActivity.this, CommonDetailActivity.class);
                        break;
                    case Constant.TypeFlag.DIARY:
                        intent = new Intent(SetTopActivity.this, DiaryDetailActivity.class);
                        break;
                    case Constant.TypeFlag.MENU:
                        intent = new Intent(SetTopActivity.this, MenuDetailActivity.class);
                        break;
                    case Constant.TypeFlag.RAIDERS:
                        intent = new Intent(SetTopActivity.this, RaidersDetailActivity.class);
                        break;
                }
                if (intent != null){
                    intent.putExtra("id",data.get(position).getId());
                    intent.putExtra("type",String.valueOf(data.get(position).getType()));
                    startActivity(intent);
                }

            }

            @Override
            public boolean OnLongClick(View v, int position) {
                return false;
            }
        });
        adapter.setOnTopClickListener(new MySetTopAdapter.OnTopClickListener() {
            @Override
            public void onSetTop(View view, int position) {
                adapter.setMove(position);
            }
        });

        adapter.setOnMoveListener(new OnMoveListener() {
            @Override
            public void onUp(View vIew, int position) {
                setLoadDialog(true);
                mPresenter.setTop(new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id",Constant.userData.getId())
                .addFormDataPart("type", String.valueOf(data.get(position).getType()))
                .addFormDataPart("act_id",data.get(position).getId())
                .addFormDataPart("top_num", String.valueOf(position+1))
                .addFormDataPart("top_num2", String.valueOf(position)).build().parts(),position,position-1);
            }

            @Override
            public void onDelete(View vIew, int position) {
                setLoadDialog(true);
                mPresenter.setTop(new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id",Constant.userData.getId())
                        .addFormDataPart("type", String.valueOf(data.get(position).getType()))
                        .addFormDataPart("act_id",data.get(position).getId())
                        .addFormDataPart("top_num", String.valueOf(position+1))
                        .addFormDataPart("top_num2", String.valueOf(position+1)).build().parts(),position,position);
            }

            @Override
            public void onDown(View vIew, int position) {
                setLoadDialog(true);
                mPresenter.setTop(new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id",Constant.userData.getId())
                        .addFormDataPart("type", String.valueOf(data.get(position).getType()))
                        .addFormDataPart("act_id",data.get(position).getId())
                        .addFormDataPart("top_num", String.valueOf(position+1))
                        .addFormDataPart("top_num2", String.valueOf(position+2)).build().parts(),position,position+1);
            }
        });

        refresh();
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
    public void onSetTopSuccess(String msg, int position,int endPosition) {
        setLoadDialog(false);
        if (position == endPosition){//删除
            data.remove(position);
            adapter.notifyItemRemoved(position);
        }else {//换位置
            ShareListData<List<String>> cache = data.get(position);
            data.set(position,data.get(endPosition));
            data.set(endPosition,cache);
            adapter.notifyItemMoved(position,endPosition);
        }
        adapter.setMove(endPosition);
    }

    @Override
    public void onGetTopSuccess(BaseData<List<ShareListData<List<String>>>> model) {
        data.clear();
        data.addAll(model.getData());
        adapter.notifyDataSetChanged();
        addFragmentFunction();
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
