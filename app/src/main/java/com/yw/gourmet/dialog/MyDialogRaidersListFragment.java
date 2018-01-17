package com.yw.gourmet.dialog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.yw.gourmet.Constant;
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
    private AppCompatSpinner spinner_address;
    private LinearLayout ll_introduction;
    private IngredientAdapter adapter;
    private ImageView img_cover,img_introduction;
    private ImageView img_address_search;
    private RaidersListData<List<String>> raidersData;
    private MapView mapview;
    private BaiduMap baiduMap;
    private BDUtil bdUtil;
    private LatLng latLng;//选择的位置
    private boolean isVisi = true;//简介是否显示
    private OnEnterListener onEnterListener;
    private String POICache = "";//POI搜索地名的缓存
    private List<String> list = new ArrayList<>();
    private boolean isChange = true;//是否可以编辑,默认可以

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout((int) (WindowUtil.width * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void initView() {
        et_title = view.findViewById(R.id.et_title);
        et_address = view.findViewById(R.id.et_address);
        et_introduction = view.findViewById(R.id.et_introduction);

        ll_introduction = view.findViewById(R.id.ll_introduction);

        spinner_address = view.findViewById(R.id.spinner_address);

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
        }else {
            et_title.setText(raidersData.getTitle());
            et_address.setText(raidersData.getAddress());
            et_introduction.setText(raidersData.getIntroduction());
            GlideApp.with(this).load(raidersData.getImg_cover()).error(R.mipmap.load_fail)
                    .into(img_cover);
            latLng = new LatLng(raidersData.getLat(),raidersData.getLng());
        }
        recycler_type = view.findViewById(R.id.recycler_type);
        recycler_type.setItemAnimator(new DefaultItemAnimator());
        recycler_type.setLayoutManager(new LinearLayoutManager(getContext()
                , LinearLayoutManager.HORIZONTAL, false));


        if (isChange) {
            getDialog().setCanceledOnTouchOutside(false);
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
            list = Constant.areaList;
            spinner_address.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,list));
        }else {
            tv_cancel.setVisibility(View.GONE);
            tv_enter.setVisibility(View.GONE);
            et_title.setEnabled(false);
            et_title.setFocusable(false);
            et_introduction.setEnabled(false);
            et_introduction.setFocusable(false);
            et_address.setEnabled(false);
            et_address.setFocusable(false);
            adapter = new IngredientAdapter(getContext(), raidersData.getType(), false);
            recycler_type.setAdapter(adapter);
            spinner_address.setVisibility(View.GONE);
            img_address_search.setVisibility(View.GONE);
        }

        mapview = view.findViewById(R.id.mapview);
        initMap(mapview);
    }

    public void initMap(final MapView mapview){
        baiduMap = mapview.getMap();
        bdUtil = new BDUtil().initBDMap(mapview);
        if (latLng == null) {
            bdUtil.initLocation(getActivity()).starBDLocation(new BDUtil.OnLocalListener() {
                @Override
                public void OnLocalSuccess(BDLocation location) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    bdUtil.setCenter(latLng)
                            .setZoom(10);
                }

                @Override
                public void OnLocalFail(BDLocation location) {

                }
            }, true);
        }else {
            bdUtil.setCenter(latLng).setZoom(10).setMarkerByRes(latLng,R.mipmap.icon_gcoding);
        }
        bdUtil.initGeoCode(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                et_address.setText(reverseGeoCodeResult.getAddress()+POICache);
            }
        });
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MyDialogRaidersListFragment.this.latLng = latLng;
                POICache = "";
                bdUtil.GeoCode(latLng).setCenter(latLng).setMarkerByRes(latLng,R.mipmap.icon_gcoding);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                MyDialogRaidersListFragment.this.latLng = mapPoi.getPosition();
                POICache = mapPoi.getName();
                bdUtil.GeoCode(mapPoi.getPosition()).setCenter(mapPoi.getPosition())
                        .setMarkerByRes(mapPoi.getPosition(),R.mipmap.icon_gcoding);
                return true;
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
                if (isChange) {
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
                            }).show(getFragmentManager(), "cover");
                }else {
                    new MyDialogPhotoShowFragment().addImgString(raidersData.getImg_cover())
                            .show(getFragmentManager(),"cover");
                }
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_enter:
                if (isEmpty()){
                    break;
                }
                raidersData.setAddress(et_address.getText().toString());
                raidersData.setTitle(et_title.getText().toString());
                raidersData.setIntroduction(et_introduction.getText().toString());
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
                }).search(spinner_address.getSelectedItem().toString(),et_address.getText().toString());
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

    public MyDialogRaidersListFragment setChange(boolean change) {
        isChange = change;
        return this;
    }

    /**
     * 必须输入的是否为空
     * @return
     */
    public boolean isEmpty(){
        if (et_title.getText().toString().trim().isEmpty()){
            ToastUtils.showSingleToast("请输入攻略标题");
            return true;
        }
        if (raidersData.getType().size() == 0){
            ToastUtils.showSingleToast("请输入攻略类型");
            return true;
        }
        if (raidersData.getImg_cover() == null || raidersData.getImg_cover().isEmpty()){
            ToastUtils.showSingleToast("请选择封面");
            return true;
        }
        if (et_address.getText().toString().trim().isEmpty() || latLng == null){
            ToastUtils.showSingleToast("请输入地址并定位具体地点");
            return true;
        }
        if (et_introduction.getText().toString().trim().isEmpty()){
            ToastUtils.showSingleToast("请输入攻略简介");
            return true;
        }
        return false;
    }
}
