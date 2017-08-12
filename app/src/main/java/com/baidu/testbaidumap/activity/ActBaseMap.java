package com.baidu.testbaidumap.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.testbaidumap.R;
import com.baidu.testbaidumap.utill.LogUtils;

import java.util.List;


/**
 * @anthor ydl
 * @time 2017/8/9 13:58
 * TestBaiduMap 描述:
 */
public class ActBaseMap extends AppCompatActivity implements OnGetGeoCoderResultListener, BDLocationListener, BaiduMap.OnMapStatusChangeListener, OnGetPoiSearchResultListener, RadarSearchListener {

    private MapView mMapView;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private LocationClient mLocClient;
    private boolean isFirstLoc = true;
    private GeoCoder geoCoder;
    private BaiduMap mBaiduMap;
    private PoiSearch poiSearch;
    private TextView uptextview;


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        if (geoCoder != null) geoCoder.destroy();
        mMapView = null;
        poiSearch.destroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_basemap);
        mMapView = (MapView) findViewById(R.id.bmapView);
        uptextview = (TextView) findViewById(R.id.uptextview);
        MapView.setMapCustomEnable(true);
        mMapView.removeViewAt(2);// 移除放大缩小键
        mMapView.removeViewAt(1);// 移除百度地图标志
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setTrafficEnabled(true);//开启交通图
        //设置 地图的 缩放状态
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18).build()));//设置地图缩放级别
        mBaiduMap.setOnMapStatusChangeListener(this);//状态改变的监听
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //当前的定位模式
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        //将新定义的 定位模式 传递给  baiduMap
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, null));
        //注册定位监听
        mLocClient = new LocationClient(this);
        //定位的监听
        mLocClient.registerLocationListener(this);
        //定位选项
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        //开始定位
        mLocClient.start();

        geoCoder = GeoCoder.newInstance();
        poiSearch = PoiSearch.newInstance();
    }

    @Override
    public void onGetNearbyInfoList(RadarNearbyResult result, RadarSearchError error) {
        if (error == RadarSearchError.RADAR_NO_ERROR) {
            List<RadarNearbyInfo> infoList = result.infoList;
            for (int i=0;i<infoList.size();i++){
                LogUtils.e("nearlist "+infoList.get(i)+", position :"+i);
            }
            Toast.makeText(this, "查询周边成功", Toast.LENGTH_LONG).show();
            //获取成功，处理数据
        } else {
            //获取失败
            Toast.makeText(this, "查询周边失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {

    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {

    }



    //---------------------------------
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        //如果baLocation 为空或 View 销毁后 不在处理新的数据接受的位置
        if (bdLocation == null || mBaiduMap == null) return;
        //定位数据
        MyLocationData data = new MyLocationData.Builder()
                //定位精度
                .accuracy(bdLocation.getRadius())
                //此处设置开发者获取到的方向的信息,顺时针 0-360
                .direction(bdLocation.getDirection())
                //纬度
                .latitude(bdLocation.getLatitude())
                //经度
                .longitude(bdLocation.getLongitude())
                //构建
                .build();
        //设置定位数据
        mBaiduMap.setMyLocationData(data);

        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng point = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(point).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    //------------------------
    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
        uptextview.setVisibility(View.GONE);
    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        uptextview.setVisibility(View.VISIBLE);
        //地图操作的中心点
        mBaiduMap.clear();
         LatLng mLastLng = mapStatus.target;//重新定位后的坐标点
        //反地理 编码 得到 此坐标的地址
        someOperation(mLastLng);
    }

    private void someOperation(final LatLng mLastLng){
        new Thread(new Runnable() {
            @Override
            public void run() {
                unDiliCode(mLastLng);
                //附近搜索
                nearByAllPoiSearch(mLastLng);
            }
        }).start();
    }


    //---------------- 附近 搜索
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        List<PoiInfo> allPoi = poiResult.getAllPoi();
        if (allPoi != null && allPoi.size() != 0){
            for (int i=0;i<allPoi.size();i++){
                LogUtils.e("allpoi 里面的元素 :"+allPoi.get(i).address+", city: "+allPoi.get(i).city+", name: "+allPoi.get(i).name);
            }
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        LogUtils.e("onGetPoiDetailResult :" + poiDetailResult.toString());
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        LogUtils.e("onGetPoiIndoorResult :" + poiIndoorResult.toString());
    }

    /**
     *  地理编码
     **/
    public void diliCode(){
        //地理编码的 对象
        geoCoder = GeoCoder.newInstance();
        //设置监听
        geoCoder.setOnGetGeoCodeResultListener(this);
        //发起 地理编码 检索
        geoCoder.geocode(new GeoCodeOption().city("北京").address("海淀区上地十街10号"));
    }

    /**
     * 反地理编码 转换为地址
     */
    public void unDiliCode(LatLng  latLng){
        geoCoder = GeoCoder.newInstance();
        //设置监听
        geoCoder.setOnGetGeoCodeResultListener(this);
        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
        reverseGeoCodeOption.location(latLng);
        geoCoder.reverseGeoCode(reverseGeoCodeOption);
    }

    //附近搜索n
    public void nearByAllPoiSearch(LatLng latlng){
        //得到 poi 搜索的对象
        poiSearch = PoiSearch.newInstance();
        mBaiduMap.clear();
        poiSearch.setOnGetPoiSearchResultListener(this);
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(latlng); //设置坐标
        nearbySearchOption.keyword("房子");  //设置关键字
        nearbySearchOption.radius(2000);     //搜索范围的半径
        nearbySearchOption.pageCapacity(15); //设置最多允许加载的poi数量,默认10
        nearbySearchOption.pageNum(20);      //分页编号
        poiSearch.searchNearby(nearbySearchOption);
    }

    //------------------- setOnGetGeoCodeResultListener
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        LogUtils.e("onGetGeoCodeResult geoCodeResult: "+geoCodeResult.toString());

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        String currentCity = reverseGeoCodeResult.getAddress();

//        if (currentCity != null) uptextview.setText(currentCity);
        LogUtils.e("currentCity :" + currentCity);


    }
}