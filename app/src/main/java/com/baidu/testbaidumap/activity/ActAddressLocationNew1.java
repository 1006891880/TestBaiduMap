package com.baidu.testbaidumap.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.testbaidumap.R;


/**
 * @anthor ydl
 * @time 2017/8/10 11:04
 * TestBaiduMap 描述:
 */

public class ActAddressLocationNew1 extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_addresslocationnew1);
        mMapView = (MapView) findViewById(R.id.cmapView);
        mBaiduMap = mMapView.getMap();

        //设置地图中心点位置
        MapStatus mapStatus = new MapStatus.Builder().zoom(18).build();
        // 设置地图的缩放级别
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
        mLocationClient = new LocationClient(this);     //声明LocationClient类
        //定位的监听
        locationListener();
        //定位选项
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        //设置是否需要地址信息,默认无地址
        option.setIsNeedAddress(false);
        //设置事都需要返回位置语义化信息,可以在BDLocation.getLocationDescribe()中得到数据
        //可用做地址信息的补充
        option.setIsNeedLocationDescribe(true);
        //设置是否需要返回 位置POI信息
        option.setIsNeedLocationPoiList(true);
        //设置定位模式
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        //设置扫描 的间隔时间 最低1000ms ,低于其值 则无效
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        //开始定位
        mLocationClient.start();

    }
    //定位的监听
    private void locationListener() {
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                //如果baLocation 为空或 View 销毁后 不在处理新的数据接受的位置
                if (bdLocation == null || mBaiduMap == null)return;
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
                //是否是第一次定位
//                if (isFirstLoc){
//                    isFirstLoc=false;
//                    LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
//                    MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng,18);
//                    mBaiduMap.animateMapStatus(msu);
//                }
//                //包含经纬的信息
//                locationLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
//                //获取城市,用于POISsarch
//                city = bdLocation.getCity();
//
//                ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
//                reverseGeoCodeOption.location(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
//                //发起反地理编码请求  ,把坐标点转化为  地址信息
//                geoCoder.reverseGeoCode(reverseGeoCodeOption);
//                //设置查询结果的监听者
//                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
//                    @Override
//                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
//
//                    }
//
//                    @Override
//                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//                        //拿到变化地点后的 附近地址
//                        List<PoiInfo> poiList = reverseGeoCodeResult.getPoiList();
//                        for (int i=0;i<poiList.size();i++){
//                            LogUtils.e("地点变化后 地址: "+poiList.get(i));
//                        }
//
//
//                    }
//                });
            }


            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        });
    }
}
