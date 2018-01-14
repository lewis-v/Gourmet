package com.yw.gourmet.dialog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.yw.gourmet.GlideApp;
import com.yw.gourmet.R;
import com.yw.gourmet.adapter.IngredientAdapter;
import com.yw.gourmet.base.BaseDialogFragment;
import com.yw.gourmet.data.RaidersListData;
import com.yw.gourmet.listener.OnAddListener;
import com.yw.gourmet.listener.OnDeleteListener;
import com.yw.gourmet.listener.OnEditDialogEnterClickListener;
import com.yw.gourmet.utils.BDUtil;
import com.yw.gourmet.utils.ToastUtils;
import com.yw.gourmet.utils.WindowUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/10.
 */

public class MyDialogRaidersListFragment extends BaseDialogFragment implements View.OnClickListener{
    private EditText et_title,et_address,et_introduction;
    private TextView tv_img_tip,tv_enter,tv_cancel;
    private RecyclerView recycler_type;
    private LinearLayout ll_introduction;
    private IngredientAdapter adapter;
    private ImageView img_cover,img_introduction;
    private ImageView img_address_search;
    private RaidersListData<List<String>> raidersData;
    private MapView mapview;
    private BaiduMap baiduMap;
    private BDUtil bdUtil;
    private LatLng latLng;//选择的位置
    private boolean isVisi = true;
    private OnEnterListener onEnterListener;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout((int) (WindowUtil.width * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void initView() {
        getDialog().setCanceledOnTouchOutside(false);
        et_title = view.findViewById(R.id.et_title);
        et_address = view.findViewById(R.id.et_address);
        et_introduction = view.findViewById(R.id.et_introduction);

        ll_introduction = view.findViewById(R.id.ll_introduction);

        tv_img_tip = view.findViewById(R.id.tv_img_tip);
        tv_enter = view.findViewById(R.id.tv_enter);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_enter.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        img_address_search = view.findViewById(R.id.img_address_search);
        img_address_search.setOnClickListener(this);
        img_introduction = view.findViewById(R.id.img_introduction);
        img_introduction.setOnClickListener(this);
        img_cover = view.findViewById(R.id.img_cover);
        img_cover.setOnClickListener(this);

        if (raidersData == null){
            raidersData = new RaidersListData<List<String>>();
            raidersData.setType(new ArrayList<String>());
        }
        recycler_type = view.findViewById(R.id.recycler_type);
        recycler_type.setItemAnimator(new DefaultItemAnimator());
        recycler_type.setLayoutManager(new LinearLayoutManager(getContext()
                , LinearLayoutManager.HORIZONTAL, false));
        adapter = new IngredientAdapter(getContext(), raidersData.getType(), true);
        recycler_type.setAdapter(adapter);
        adapter.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void OnDelete(View v, int position) {
                raidersData.getType().remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
        adapter.setOnAddListener(new OnAddListener() {
            @Override
            public void OnAdd(View view, final int position) {
                new MyDialogEditFragment().setEtHint("请输入标签、类型").setOnEditDialogEnterClickListener(new OnEditDialogEnterClickListener() {
                    @Override
                    public void OnClick(String edit, String tag) {
                        raidersData.getType().add(edit);
                        adapter.notifyItemInserted(position);
                    }
                }).show(getFragmentManager(), "type");
            }
        });

        mapview = view.findViewById(R.id.mapview);
        initMap(mapview);
    }

    public void initMap(final MapView mapview){
        baiduMap = mapview.getMap();
        bdUtil = new BDUtil().initBDMap(mapview).initLocation(getActivity()).starBDLocation(new BDUtil.OnLocalListener() {
                    @Override
                    public void OnLocalSuccess(BDLocation location) {
                        latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        bdUtil.setCenter(latLng)
                                .setZoom(10);
                    }

                    @Override
                    public void OnLocalFail(BDLocation location) {

                    }
                },true);
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MyDialogRaidersListFragment.this.latLng = latLng;
                bdUtil.setCenter(latLng).setMarkerByRes(latLng,R.mipmap.icon_gcoding);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        mapview.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapview.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        bdUtil.onDestroy();
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_dialog_raiders_list;
    }

    public MyDialogRaidersListFragment setRaidersData(RaidersListData<List<String>> raidersData) {
        this.raidersData = raidersData;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cover:
                new MyDialogPhotoChooseFragment().setRatio(1).setCrop(true).setChooseNum(1)
                        .setOnCropListener(new MyDialogPhotoChooseFragment.OnCropListener() {
                    @Override
                    public void OnCrop(String path, String tag) {
                        GlideApp.with(getContext()).load(path).error(R.mipmap.load_fail)
                                .into(img_cover);
                        raidersData.setImg_cover(path);
                        if (tv_img_tip.getVisibility() == View.VISIBLE) {
                            tv_img_tip.setVisibility(View.GONE);
                        }
                    }
                }).show(getFragmentManager(),"cover");
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_enter:
                raidersData.setAddress(et_address.getText().toString());
                raidersData.setTitle(et_title.getText().toString());
                raidersData.setLat(latLng.latitude);
                raidersData.setLng(latLng.longitude);
                if (onEnterListener != null){
                    onEnterListener.onEnter(raidersData,getTag());
                }
                dismiss();
                break;
            case R.id.img_address_search:
                bdUtil.initSearch(new OnGetSuggestionResultListener() {
                    @Override
                    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                        if (suggestionResult != null && suggestionResult.getAllSuggestions()!=null
                                &&suggestionResult.getAllSuggestions().size()>0) {
                            latLng = suggestionResult.getAllSuggestions().get(0).pt;
                            baiduMap.clear();
                            bdUtil.setCenter(latLng);
                            bdUtil.setMarkerByRes(latLng,R.mipmap.icon_gcoding);
                        }else {
                            ToastUtils.showSingleToast("无搜索结果");
                        }
                    }
                }).search("广东",et_address.getText().toString());
                break;
            case R.id.img_introduction:
                showIntroduction();
                break;
        }
    }

    /**
     * 控制简介栏的显示与隐藏(带动画)
     */
    public void showIntroduction(){
        ObjectAnimator objectAnimatorImg;
        ObjectAnimator objectAnimatorET;
        ObjectAnimator objectAnimatorLL;
        if (isVisi){
            isVisi = false;
            objectAnimatorImg = ObjectAnimator.ofFloat(img_introduction,"rotation",0,180);
            objectAnimatorET = ObjectAnimator.ofFloat(et_introduction,"alpha",1f,0.1f);
            objectAnimatorLL = ObjectAnimator.ofFloat(ll_introduction,"translationY"
                    ,0,et_introduction.getHeight());
            Log.e("visi",ll_introduction.getTranslationY()+";"+et_introduction.getHeight());
        }else {
            et_introduction.setVisibility(View.VISIBLE);
            isVisi = true;
            objectAnimatorImg = ObjectAnimator.ofFloat(img_introduction,"rotation",180,360);
            objectAnimatorET = ObjectAnimator.ofFloat(et_introduction,"alpha",0.1f,1f);
            objectAnimatorLL = ObjectAnimator.ofFloat(ll_introduction,"translationY"
                    ,et_introduction.getHeight(),0);
            Log.e("visi",ll_introduction.getPivotY()+";"+img_introduction.getHeight()+";"+et_introduction.getHeight());
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimatorImg,objectAnimatorET,objectAnimatorLL);
        animatorSet.start();
    }

    public interface OnEnterListener{
        void onEnter(RaidersListData<List<String>> raidersListData,String Tag);
    }

    public MyDialogRaidersListFragment setOnEnterListener(OnEnterListener onEnterListener) {
        this.onEnterListener = onEnterListener;
        return this;
    }


}
