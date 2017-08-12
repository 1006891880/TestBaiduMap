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
public class BuildingFragment extends Fragment {

    private List<PoiInfo> buildingData = new ArrayList<>();
    private AddressAdapter adapter;
    private ListView buildListView;
    private LatLng point;
    private PoiSearch buildPoiSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_building,null);
        buildPoiSearch = PoiSearch.newInstance();
        buildingData.clear();
        buildingData = getArguments().getParcelableArrayList("buildPoiData");
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.map.status.changed");
        getActivity().registerReceiver(mBuildingBroad,intentFilter);
    }

    private BroadcastReceiver mBuildingBroad = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            buildingData.clear();
            point = intent.getParcelableExtra("Position");
            nearByBuildPoiSearch();
        }
    };

    private void nearByBuildPoiSearch() {
        buildingData.clear();
        buildPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.getAllPoi() == null) {
                    Toast.makeText(getActivity(),"定位失败,暂无数据信息", Toast.LENGTH_LONG).show();
                } else {
                    buildingData.addAll(poiResult.getAllPoi());
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
        nearbySearchOption.keyword("写字楼");
        nearbySearchOption.radius(2000);
        nearbySearchOption.pageCapacity(15);
        buildPoiSearch.searchNearby(nearbySearchOption);
    }

    private void initView(View view){
        buildListView = (ListView) view.findViewById(R.id.building_list);
        adapter = new AddressAdapter(getActivity(),buildingData);
        buildListView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBuildingBroad);
    }
}
