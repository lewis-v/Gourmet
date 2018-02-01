package com.yw.gourmet.ui.search;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.MyImgViewPagerAdapter;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.utils.BDUtil;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.widget.MyViewPager;
import com.yw.gourmet.widget.YWFlowViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/22.
 */

public class SearchFragment extends BaseFragment<SearchPresenter> implements SearchContract.View
    ,View.OnClickListener {
    private LinearLayout ll_search,ll_diary,ll_menu,ll_raiders,ll_local;
    private TextView tv_search,tv_local_text,tv_local;
    private YWFlowViewPager<ImageView> viewpager;
    private List<ImageView> imageViews = new ArrayList<>();
    private BDUtil bdUtil;

    /**
     * 初始化UI
     */
    @Override
    protected void initView() {
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);

        tv_search = view.findViewById(R.id.tv_search);
        tv_local_text = view.findViewById(R.id.tv_local_text);
        tv_local = view.findViewById(R.id.tv_local);
        ll_search = view.findViewById(R.id.ll_search);
        ll_diary = view.findViewById(R.id.ll_diary);
        ll_menu = view.findViewById(R.id.ll_menu);
        ll_raiders = view.findViewById(R.id.ll_raiders);
        ll_local = view.findViewById(R.id.ll_local);

        ll_search.setOnClickListener(this);
        ll_diary.setOnClickListener(this);
        ll_menu.setOnClickListener(this);
        ll_raiders.setOnClickListener(this);
        ll_local.setOnClickListener(this);

        bdUtil = new BDUtil();
        bdUtil.initLocation(getActivity());
        bdUtil.starBDLocation(new BDUtil.OnLocalListener() {
            @Override
            public void OnLocalSuccess(BDLocation location) {
                tv_local_text.setText(location.getCity());
                tv_local.setText(location.getCity().substring(0,1));
            }

            @Override
            public void OnLocalFail(BDLocation location) {

            }
        },true);

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.comment_ic);
        ImageView imageView2 = new ImageView(getContext());
        imageView2.setImageResource(R.drawable.good_ic);
        ImageView imageView3 = new ImageView(getContext());
        imageView3.setImageResource(R.drawable.bad_ic);

        ImageView imageView4 = new ImageView(getContext());
        imageView4.setImageResource(R.drawable.add_circle_outline);
        ImageView imageView5 = new ImageView(getContext());
        imageView5.setImageResource(R.drawable.add_circle);
        ImageView imageView6 = new ImageView(getContext());
        imageView6.setImageResource(R.drawable.refresh);
        imageViews.add(imageView);
        imageViews.add(imageView2);
        imageViews.add(imageView3);
//        imageViews.add(imageView4);
//        imageViews.add(imageView5);
//        imageViews.add(imageView6);
        viewpager = view.findViewById(R.id.viewpager);
        viewpager.setFlowView(imageViews);
        viewpager.start(true);
        viewpager.setOnPageClickListener(new YWFlowViewPager.OnPageClickListener() {
            @Override
            public void onPageClick(View view, int position) {
                ToastUtils.showSingleToast(view.toString()+position);
            }
        });
    }

    /**
     * 设置布局文件
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_search:

                break;
            case R.id.ll_diary:

                break;
            case R.id.ll_menu:

                break;
            case R.id.ll_raiders:

                break;
            case R.id.ll_local:

                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewpager.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewpager.pause();
    }
}
