package com.baidu.testbaidumap.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
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
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.testbaidumap.R;
import com.baidu.testbaidumap.adapter.SuggestAddressAdapter;
import com.baidu.testbaidumap.fragment.AllPoiFragment;
import com.baidu.testbaidumap.fragment.BuildingFragment;
import com.baidu.testbaidumap.fragment.PlotFragment;
import com.baidu.testbaidumap.fragment.SchoolFragment;
import com.baidu.testbaidumap.utill.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Totem on 2016/7/21.
 * @author 代码丶如风
 */
public class LocationAndPoiSearchActivity extends FragmentActivity implements OnGetGeoCoderResultListener,View.OnClickListener{

    /**
     * MapView地图视图
     * */
    private MapView mapView;

    /**
     * 地理编码对象
     * */
    private GeoCoder geoCoder;

    /**
     * 百度地图
     * */
    private BaiduMap baiduMap;

    /**
     * 定位核心类
     * */
    private LocationClient mLocClient;

    /**
     * 判断是否是第一次定位
     * */
    private boolean IsFirstLoc = true;

    /**
     * 坐标对象(经度+纬度形式)
     * */
    private LatLng point;

    /**
     * PoiSearch对象
     * */
    private PoiSearch allpoiSearch;
    private PoiSearch buildPoiSearch;
    private PoiSearch plotPoiSearch;
    private PoiSearch schoolPoiSearch;

    /**
     * 在线建议查询
     * */
    private SuggestionSearch keyWordsPoiSearch;

    /**
     * Poi搜索结果集
     * */
    private List<PoiInfo> allPoiData;
    private List<PoiInfo> buildPoiData;
    private List<PoiInfo> plotPoiData;
    private List<PoiInfo> schoolPoiData;

    /**
     * 在线建议查询结果集
     * */
    private List<SuggestionResult.SuggestionInfo> keyWordPoiData;

    /**
     * 用于判断EditText是否获取了焦点
     * */
    private boolean isFocus;

    private ImageView mPoiIndicator;
    private ViewPager viewPager;
    private ImageView back;
    private EditText location_name;
    private LinearLayout inputPoiSearchLayout;
    private LinearLayout layout;
    private ListView inputPoiListView;
    private TextView textView[] = new TextView[4];
    private int currentPosition;
    private MyOrderInformationPagerAdapter adapter;
    private SuggestAddressAdapter suggestAdapter;
    private String currentCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_location_and_poi_search);
        init();
        initView();
    }

    private void init() {
        allPoiData = new ArrayList<>();
        buildPoiData = new ArrayList<>();
        plotPoiData = new ArrayList<>();
        schoolPoiData = new ArrayList<>();
        keyWordPoiData = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.address_viewpager);
        mapView = (MapView) findViewById(R.id.address_MapView);
        baiduMap = mapView.getMap();

        /**
         * 1.地理编码对象实例化
         * 2.设置地理编码和反地理编码的监听事件
         * @地理编码：   将当前位置转化成坐标的形式
         * @反地理编码： 将坐标点转化成当前的位置信息
         * */
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);

        baiduMap.setMyLocationEnabled(true);  //开启定位图层
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(new MyLocationListener());

        /**
         * 设置定位
         * */
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);            // 打开gps
        option.setCoorType("bd09ll");       // 设置坐标类型  bd09ll:百度经纬度坐标系
        option.setScanSpan(1000);           // 设置查询范围,默认500
        mLocClient.setLocOption(option);    // 设置option
        mLocClient.start();


        /**
         * 设置地图状态改变时的监听
         * */
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                LatLng ptCenter = baiduMap.getMapStatus().target;  //获取中心点坐标
                point = ptCenter;
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter)); //重新进行地理编码

                /**
                 * 当地图状态改变的时候需要发送广播,告知Fragment根据当前坐标重新搜索Poi
                 * */
                Intent intent = new Intent("com.map.status.changed");
                intent.putExtra("Position",point);
                getApplicationContext().sendBroadcast(intent);
            }
        });
    }

    private void initView() {
        layout = (LinearLayout) findViewById(R.id.address_title_layout);
        back = (ImageView) findViewById(R.id.back);
        location_name = (EditText) findViewById(R.id.location_name);
        mPoiIndicator = (ImageView) findViewById(R.id.poi_indicator);
        inputPoiSearchLayout = (LinearLayout) findViewById(R.id.edit_search_poi);
        inputPoiListView = (ListView) findViewById(R.id.edit_search_poi_list);
        ViewGroup.LayoutParams params = mPoiIndicator.getLayoutParams();
        Point size = ScreenUtils.getScreenSize(getApplicationContext());
        params.width = size.x / 2 - 65;
        mPoiIndicator.setLayoutParams(params);
        textView[0] = (TextView) findViewById(R.id.all_poi);
        textView[1] = (TextView) findViewById(R.id.build_poi);
        textView[2] = (TextView) findViewById(R.id.plot_poi);
        textView[3] = (TextView) findViewById(R.id.school_poi);
        textView[0].setOnClickListener(this);
        textView[1].setOnClickListener(this);
        textView[2].setOnClickListener(this);
        textView[3].setOnClickListener(this);
        layout.setFocusable(true);
        layout.setFocusableInTouchMode(true);
        layout.requestFocus();
        isFocus = false;
        back.setOnClickListener(this);
        location_name.addTextChangedListener(watcher);

        /**
         * 为EditText设置焦点事件
         * */
        location_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inputPoiSearchLayout.setVisibility(View.VISIBLE);
                } else {
                    inputPoiSearchLayout.setVisibility(View.GONE);
                }
                isFocus = hasFocus;
            }
        });
    }

    /**
     * 监听onKeyDown事件
     * 目的是判断当前页面是地图显示页面还是在线建议查询页面
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isFocus) {
                inputPoiSearchLayout.setVisibility(View.GONE);
                location_name.setText("");
                location_name.clearFocus();
                keyWordPoiData.clear();
                layout.setFocusable(true);
                layout.setFocusableInTouchMode(true);
                layout.requestFocus();
                isFocus = false;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void changeTextColor(int position) {
        setPageIndicator(position);
        viewPager.setCurrentItem(position);
        textView[currentPosition].setTextColor(getResources().getColor(R.color.poi_normal_color));
        textView[position].setTextColor(getResources().getColor(R.color.poi_blue_color));
        currentPosition = position;
    }

    private void setPageIndicator(int index) {
        Point size = ScreenUtils.getScreenSize(getApplicationContext());
        mPoiIndicator.setTranslationX((size.x / 4 - 3) * index);
    }

    /**
     * 房子Poi数据搜索
     * @注意： 所有的Poi搜索都是异步完成的
     * @这里先nearByAllPoiSearch()方法执行完毕后再执行nearByBuildPoiSearch()..
     * @如果同时开启四个Poi让其分别进行查询,会出现很多bug
     * */
    private void nearByAllPoiSearch() {
        allpoiSearch = PoiSearch.newInstance();
        allPoiData.clear();
        allpoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.getAllPoi() == null) {
                    Toast.makeText(getApplicationContext(),"定位失败,暂无数据信息", Toast.LENGTH_LONG).show();
                } else {
                    allPoiData.addAll(poiResult.getAllPoi());
                }
                nearByBuildPoiSearch();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });

        /**
         * 设置Poi Option
         * 当前搜索为附近搜索：以圆形的状态进行搜索
         * 还有两种其他的搜素方式：范围搜素和城市内搜索
         * */
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(new LatLng(point.latitude, point.longitude)); //设置坐标
        nearbySearchOption.keyword("房子");                                       //设置关键字
        nearbySearchOption.radius(2000);                                          //搜索范围的半径
        nearbySearchOption.pageCapacity(15);                                      //设置最多允许加载的poi数量,默认10
        allpoiSearch.searchNearby(nearbySearchOption);
    }

    private void nearByBuildPoiSearch() {
        buildPoiSearch = PoiSearch.newInstance();
        buildPoiData.clear();
        buildPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.getAllPoi() == null) {
                    Toast.makeText(getApplicationContext(),"定位失败,暂无数据信息", Toast.LENGTH_LONG).show();
                } else {
                    buildPoiData.addAll(poiResult.getAllPoi());
                }
                nearByPlotPoiSearch();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(new LatLng(point.latitude, point.longitude));
        nearbySearchOption.keyword("写字楼");
        nearbySearchOption.radius(2000);
        nearbySearchOption.pageCapacity(15);
        buildPoiSearch.searchNearby(nearbySearchOption);
    }

    private void nearByPlotPoiSearch() {
        plotPoiSearch = PoiSearch.newInstance();
        plotPoiData.clear();
        plotPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.getAllPoi() == null) {
                    Toast.makeText(getApplicationContext(),"定位失败,暂无数据信息", Toast.LENGTH_LONG).show();
                } else {
                    plotPoiData.addAll(poiResult.getAllPoi());
                }
                nearBySchoolPoiSearch();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(new LatLng(point.latitude, point.longitude));
        nearbySearchOption.keyword("小区");
        nearbySearchOption.radius(2000);
        nearbySearchOption.pageCapacity(15);
        plotPoiSearch.searchNearby(nearbySearchOption);
    }

    private void nearBySchoolPoiSearch() {
        schoolPoiSearch = PoiSearch.newInstance();
        schoolPoiData.clear();
        schoolPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.getAllPoi() == null) {
                    Toast.makeText(getApplicationContext(),"定位失败,暂无数据信息", Toast.LENGTH_LONG).show();
                } else {
                    schoolPoiData.addAll(poiResult.getAllPoi());
                }
                if (adapter == null) {
                    adapter = new MyOrderInformationPagerAdapter(getSupportFragmentManager());
                    viewPager.setAdapter(adapter);
                    viewPager.setOnPageChangeListener(onPageChangeListener);
                    /**
                     * 这里修改了Fragment的预加载数量,一次加载三页数据,默认是一页
                     * */
                    viewPager.setOffscreenPageLimit(3);
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(new LatLng(point.latitude, point.longitude));
        nearbySearchOption.keyword("学校");
        nearbySearchOption.radius(2000);
        nearbySearchOption.pageCapacity(15);
        schoolPoiSearch.searchNearby(nearbySearchOption);
    }

    @Override
    public void onClick(View view) {

    }

    private class MyOrderInformationPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> list;
        private AllPoiFragment allPoiFragment = new AllPoiFragment();
        private BuildingFragment buildingFragment = new BuildingFragment();
        private PlotFragment plotFragment = new PlotFragment();
        private SchoolFragment schoolFragment = new SchoolFragment();

        public MyOrderInformationPagerAdapter(FragmentManager fm) {
            super(fm);

            /**
             * 第一次加载的时候由Activity搜索,将数据传递给Fragment,后续由Fragment来完成搜索功能
             * */

            Bundle allPoiBundle = new Bundle();
            allPoiBundle.putParcelableArrayList("allPoiData", (ArrayList<? extends Parcelable>) allPoiData);
            allPoiFragment.setArguments(allPoiBundle);

            Bundle buildPoiBundle = new Bundle();
            buildPoiBundle.putParcelableArrayList("buildPoiData", (ArrayList<? extends Parcelable>) buildPoiData);
            buildingFragment.setArguments(buildPoiBundle);

            Bundle plotPoiBundle = new Bundle();
            plotPoiBundle.putParcelableArrayList("plotPoiData", (ArrayList<? extends Parcelable>) plotPoiData);
            plotFragment.setArguments(plotPoiBundle);

            Bundle schoolPoiBundle = new Bundle();
            schoolPoiBundle.putParcelableArrayList("schoolPoiData", (ArrayList<? extends Parcelable>) schoolPoiData);
            schoolFragment.setArguments(schoolPoiBundle);

            list = new ArrayList<>();
            list.add(allPoiFragment);
            list.add(buildingFragment);
            list.add(plotFragment);
            list.add(schoolFragment);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setPageIndicator(position);
            changeTextColor(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (location_name.getText().toString().equals("")) {
                return;
            }

            suggestAdapter = null;

            /**
             * 在线建议查询对象实例化+设置监听
             * @在线建议查询： 根据城市和关键字搜索出相应的位置信息(模糊查询)
             * */
            keyWordsPoiSearch = SuggestionSearch.newInstance();
            keyWordsPoiSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
                @Override
                public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                    keyWordPoiData.clear();
                    if (suggestionResult.getAllSuggestions() == null) {
                        Toast.makeText(getApplicationContext(),"暂无数据信息", Toast.LENGTH_LONG).show();
                    } else {
                        keyWordPoiData = suggestionResult.getAllSuggestions();
                        //设置Adapter结束
                        suggestAdapter = new SuggestAddressAdapter(getApplicationContext(), keyWordPoiData);
                        inputPoiListView.setAdapter(suggestAdapter);
                    }
                }
            });
            keyWordsPoiSearch.requestSuggestion((new SuggestionSearchOption()).keyword(location_name.getText().toString()).city(currentCity));
        }
    };

    /**
     * LBS定位监听
     * */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置

            if (IsFirstLoc) {
                IsFirstLoc = false;
                point = new LatLng(location.getLatitude(), location.getLongitude());
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));

                /**
                 * 设置当前位置为定位到的地理位置
                 * */
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(point).zoom(20.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                /**
                 * 执行Poi搜索,Activity在被创建时,只执行一次
                 * */
                nearByAllPoiSearch();
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        /**
         * 获取反地理编码后的城市信息
         * */
        currentCity = reverseGeoCodeResult.getAddress();
    }

    @Override
    protected void onDestroy() {
        mLocClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
