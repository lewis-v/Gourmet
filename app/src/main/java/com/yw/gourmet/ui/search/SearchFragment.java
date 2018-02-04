package com.yw.gourmet.ui.search;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.base.BaseFragment;
import com.yw.gourmet.data.BaseData;
import com.yw.gourmet.data.FlowData;
import com.yw.gourmet.ui.search.keySearch.KeySearchActivity;
import com.yw.gourmet.utils.BDUtil;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.widget.YWFlowView;
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
    private YWFlowView flow_view;
    private ArrayList<View> imageViews = new ArrayList<>();
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
        flow_view = view.findViewById(R.id.flow_view);
        mPresenter.getFlow();

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
                startActivity(new Intent(getContext(), KeySearchActivity.class));
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
        flow_view.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        flow_view.pause();
    }

    @Override
    public void onGetFlowSuccess(BaseData<List<FlowData>> model) {
        if (model != null){
            for (FlowData data : model.getData()){
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                GlideApp.with(this).load(data.getImg()).error(R.mipmap.load_fail)
                        .placeholder(R.mipmap.loading).into(imageView);
                imageViews.add(imageView);
            }
            flow_view.setFlowViewList(imageViews).setPointLayoutBackground(R.color.transparent)
            .setOnPageClickListener(new YWFlowViewPager.OnPageClickListener() {
                @Override
                public void onPageClick(View view, int position) {
                    ToastUtils.showSingleToast("广告栏招商中");
                }
            }).start(getContext(),true);
        }
    }
}
