package com.baidu.testbaidumap.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.testbaidumap.R;
import com.baidu.testbaidumap.utill.LogUtils;

import java.util.List;


/**
 * @anthor ydl
 * @time 2017/8/10 11:04
 * TestBaiduMap 描述:
 */

public class ActAddressLocationNew extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private GeoCoder geoCoder;
    private LocationClient mLocationClient;
    private BDLocation lastLocation;
    private Marker mCurrentMarker;
    private double mCurrentLongitude;
    private double mCurrentLantitude;
    private PoiSearch mPoiSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_addresslocationnew);
        mMapView = (MapView) findViewById(R.id.cmapView);
        mBaiduMap = mMapView.getMap();

        //设置地图中心点位置
        MapStatus mapStatus = new MapStatus.Builder().zoom(18).build();
        // 设置地图的缩放级别
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
        geoCoder = GeoCoder.newInstance();
        setMapStatusChange();
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        //监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    return;
                }
                Log.d("map", "On location change received:" + location);
                Log.d("map", "addr:" + location.getAddrStr());
               /* if (progressDialog != null) {
                    progressDialog.dismiss();
                }
*/
//                if (lastLocation != null) {
//                    if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
//                        Log.d("map", "same location, skip refresh");
//                        // mMapView.refresh(); //need this refresh?
//                        return;
//                    }
//                }
//                lastLocation = location;
//                mBaiduMap.clear();
//                mCurrentLantitude = lastLocation.getLatitude();
//                mCurrentLongitude = lastLocation.getLongitude();
//                Log.e(">>>>>>>", mCurrentLantitude + "," + mCurrentLongitude);
//                LatLng llA = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
//                CoordinateConverter converter = new CoordinateConverter();
//                converter.coord(llA);
//                converter.from(CoordinateConverter.CoordType.COMMON);
//                LatLng convertLatLng = converter.convert();
//                OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
//                        .fromResource(R.drawable.icon_marka))
//                        .zIndex(4).draggable(true);
//                mCurrentMarker = (Marker) mBaiduMap.addOverlay(ooA);
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 16.0f);
//                mBaiduMap.animateMapStatus(u);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        searchNeayBy();
//                    }
//                }).start();
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        });

        mPoiSearch = PoiSearch.newInstance();
        searchNeayBy();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult != null) {
                    List<PoiInfo> poiList = poiResult.getAllPoi();
                    for (int i=0;i<poiList.size();i++){
                        LogUtils.e("地点变化后 地址: "+poiList.get(i));
                    }
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }
    private void searchNeayBy() {
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.mRadius=1000;
        option.keyword("写字楼");
        option.sortType(PoiSortType.distance_from_near_to_far);
        option.location(new LatLng(mCurrentLantitude, mCurrentLongitude));
        option.pageCapacity(20);
        mPoiSearch.searchNearby(option);

    }

    //地图状态改变的监听
    private void setMapStatusChange() {
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                //地图操作的中心点
                LatLng cenpt = mapStatus.target;
                LogUtils.e("setMapStatusChange : cenpt "+cenpt);
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(cenpt));
            }
        });
    }
}
