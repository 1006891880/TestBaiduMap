package com.baidu.testbaidumap.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.testbaidumap.R;
import com.baidu.testbaidumap.adapter.AddressAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Totem on 2016/7/21.
 * @author 代码丶如风
 */
public class AllPoiFragment extends Fragment {

    private List<PoiInfo> allData = new ArrayList<>();
    private ListView allPoiList;
    private AddressAdapter adapter;
    private PoiSearch allPoiSearch;
    private LatLng point;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.map.status.changed");
        getActivity().registerReceiver(mAllBroad, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all, null);
        allPoiSearch = PoiSearch.newInstance();
        allData.clear();
        allData = getArguments().getParcelableArrayList("allPoiData");
        initView(view);
        return view;
    }

    private BroadcastReceiver mAllBroad = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            /**
             * 当地图位置状态改变的时候,接收Activity传递过来的坐标,重新搜索Poi
             * */
            point = intent.getParcelableExtra("Position");
            nearByAllPoiSearch();
        }
    };

    private void initView(View view) {
        allPoiList = (ListView) view.findViewById(R.id.all_list);
        adapter = new AddressAdapter(getActivity(), allData);
        allPoiList.setAdapter(adapter);
    }

    private void nearByAllPoiSearch() {
        allData.clear();
        allPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                /**
                 * 更新数据
                 * */
                if(poiResult.getAllPoi() == null){
                    Toast.makeText(getActivity(),"定位失败,暂无数据信息", Toast.LENGTH_LONG).show();
                }else{
                    allData.clear();
                    allData.addAll(poiResult.getAllPoi());
                    adapter.notifyDataSetChanged();
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
        nearbySearchOption.keyword("房子");
        nearbySearchOption.radius(2000);
        nearbySearchOption.pageCapacity(15);
        allPoiSearch.searchNearby(nearbySearchOption);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mAllBroad);
    }
}
