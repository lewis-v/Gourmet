package com.yw.gourmet.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by ude on 2017-08-10.
 */

public class BDUtil {
    private final static String Tag = "BDUtil";

    private LocationClient mLocationClient = null;
    private OnLocalListener localListener = null;
    private boolean isAutoStopLocal = true;//是否定位 成功后自动关闭定位
    private SuggestionSearch suggestionSearch;//地址搜索的实例
    private GeoCoder geoCoder;//坐标传地址
    private PoiSearch mPoiSearch;//POI搜索实例
    private ExecutorService executorService;

    public BDUtil(){
        //实例化单线程的线程池
        executorService = new ThreadPoolExecutor(1,1,0
                , TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    /**
     * 初始化百度定位
     */
    public BDUtil initLocation(final Activity activity){
        mLocationClient = new LocationClient(activity.getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        //获取定位的频率
        option.setScanSpan(1000);
        //返回的坐标类型
        option.setCoorType("bd09ll");
        //是否使用gps
        option.setOpenGps(true);
        //是否需要地址信息
        option.setIsNeedAddress(true);
        //位置描述
        option.setIsNeedLocationDescribe(true);
        //使用高精度定位
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setLocationNotify(true);
        option.setEnableSimulateGps(true);
        //使用设置的地图设置
        mLocationClient.setLocOption(option);
        //BD定位监听器
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                Log.e(Tag,String.valueOf(bdLocation.getLocType()));
                if (bdLocation == null) {
                    return;
                }
                if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                                != PackageManager.PERMISSION_GRANTED)) {
                    Double Lati = bdLocation.getLatitude();
                    Double Longi = bdLocation.getLongitude();
                    if ((Lati != 4.9E-324 && Longi != 4.9E-324 )) {
                        Log.i("---getmap---",Lati + ";"+Longi);
                        if (localListener != null){
                            localListener.OnLocalSuccess(bdLocation);
                        }
                        if (isAutoStopLocal) {
                            stopBDLocation();
                        }
                    } else {
                        localListener.OnLocalFail(bdLocation);
                        Log.e("----errorMap----", "过滤数值" + Lati + Longi);
                    }
                } else {
                    ToastUtils.showSingleToast("未获取定位权限");
                    stopBDLocation();
                }
                Log.e("---LocType--", bdLocation.getLocType() + "");
            }
        });
        return this;
    }

    /**
     * 定位监听器
     */
    public interface OnLocalListener{
        void OnLocalSuccess(BDLocation location);
        void OnLocalFail(BDLocation location);
    }

    /**
     * 开启百度定位
     */
    public BDUtil starBDLocation(OnLocalListener localListener,boolean isAutoStopLocal){
        this.localListener = localListener;
        this.isAutoStopLocal = isAutoStopLocal;
        mLocationClient.start();
        return this;
    }

    /**
     * 关闭百度定位
     */
    public BDUtil stopBDLocation(){
        mLocationClient.stop();
        return this;
    }

    /**
     * 百度地图初始化
     * @param mapView
     * @return
     */
    public BDUtil initBDMap(final MapView mapView){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                BaiduMap mBaiduMap = mapView.getMap();
                // 隐藏百度的LOGO
//        View child = mapView.getChildAt(1);
//        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
//            child.setVisibility(View.INVISIBLE);
//        }
                // 不显示地图上比例尺
                mapView.showScaleControl(false);
                //隐藏缩放按钮
                mapView.showZoomControls(false);
                //普通地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                //定位图层
                mBaiduMap.setMyLocationEnabled(true);
                //建筑图层3d
                mBaiduMap.setBuildingsEnabled(true);
                //室内图层
                mBaiduMap.setIndoorEnable(false);
                //交通图层
                mBaiduMap.setTrafficEnabled(false);
                //热力图
                mBaiduMap.setBaiduHeatMapEnabled(false);
                //指南针位置
//        mBaiduMap.setCompassPosition(new Point(100,100));
                mBaiduMap.getUiSettings().setOverlookingGesturesEnabled(true);
                //旋转手势

                // 将底图标注设置为隐藏
                mBaiduMap.showMapPoi(true);
            }
        });
        return this;
    }

    /**
     * 设置地图中心点(带动画)
     * @param latLng 中心坐标
     */
    public BDUtil setCenter(final BaiduMap mBaiduMap, final LatLng latLng){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                //定义Maker坐标点,39.963175, 116.400244(默认)
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(latLng)
                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                mBaiduMap.animateMapStatus(mMapStatusUpdate);
                try {
                    //此处延时是为了防止多个带动画的动作被覆盖
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return ;
                }
            }
        });
        return this;
    }

    /**
     * 设置地图缩放大小(带动画)
     * @param zoom 放大倍数
     */
    public BDUtil setZoom(final BaiduMap mBaiduMap,final int zoom){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                MapStatus mMapStatus = new MapStatus.Builder()
                        .zoom(zoom)
                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                mBaiduMap.animateMapStatus(mMapStatusUpdate);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        return this;
    }

    /**
     * 地址搜索初始化
     * @param onGetSuggestionResultListener
     */
    public BDUtil initSearch(OnGetSuggestionResultListener onGetSuggestionResultListener){
        suggestionSearch = SuggestionSearch.newInstance();
        suggestionSearch.setOnGetSuggestionResultListener(onGetSuggestionResultListener);
        return this;
    }

    /**
     * 检索地址
     * @param city 城市
     * @param key 关键字
     */
    public BDUtil search(String city,String key){
        if (suggestionSearch == null){
            throw new NullPointerException("no init , you should use initSearch to init");
        }
        suggestionSearch.requestSuggestion(new SuggestionSearchOption().city(city).keyword(key));
        return this;
    }

    /**
     * 坐标转地址初始化
     * @param onGetGeoCoderResultListener
     */
    public BDUtil initGeoCode(OnGetGeoCoderResultListener onGetGeoCoderResultListener){
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
        return this;
    }


    /**
     * 坐标转地址
     * @param latLng 需要转换的坐标
     */
    public BDUtil GeoCode(LatLng latLng){
        if (geoCoder == null){
            throw new NullPointerException("no init , you should use initGeoCode to init");
        }
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        return this;
    }

    /**
     * 设置自定义标注(带动画)
     * @param latLng 标注坐标
     * @param resourse 自定义图片
     * @return 返回Overlay供用户存储,并在对其进行操作
     */
    public Overlay setMarkerByRes(final BaiduMap mBaiduMap,LatLng latLng, int resourse){
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(resourse);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .icon(bitmap)
                .animateType(MarkerOptions.MarkerAnimateType.grow)
                .draggable(true);
        //在地图上添加Marker，并显示
        return  mBaiduMap.addOverlay(option);
    }

    /**
     * 设置自定义布局标注(带动画)
     * @param latLng 标注坐标
     * @param view 自定义布局
     * @return 返回Overlay供用户存储,并在对其进行操作
     */
    public Overlay setMarkerByView(final BaiduMap mBaiduMap, LatLng latLng, View view){
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromView(view);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .icon(bitmap)
                .animateType(MarkerOptions.MarkerAnimateType.grow)
                .draggable(true);
        //在地图上添加Marker，并显示
        return mBaiduMap.addOverlay(option);
    }

    /**
     * POI搜索初始化
     * @param onGetPoiSearchResultListener
     */
    public BDUtil initPOI(OnGetPoiSearchResultListener onGetPoiSearchResultListener) {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(onGetPoiSearchResultListener);
        return this;
    }

    /**
     * POI搜索
     * @param city 城市
     * @param key 关键字
     * @param getNum 获取数量
     */
    public BDUtil searchPOI(String city,String key,int getNum){
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(city)
                .keyword(key)
                .pageNum(getNum));
        return this;
    }


    /**
     * 释放资源
     */
    public void onDestroy(){
        if (mLocationClient!=null){
            mLocationClient.stop();
            mLocationClient = null;
            localListener = null;
        }
        if (mPoiSearch != null){
            mPoiSearch.destroy();
        }
        if (geoCoder != null){
            geoCoder.destroy();
        }
        if (suggestionSearch != null){
            suggestionSearch.destroy();
        }
        if (executorService != null){
            executorService.shutdownNow();
        }
    }
}
