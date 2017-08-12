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
import android.widget.AdapterView;
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
public class SchoolFragment extends Fragment {

    private List<PoiInfo> schoolData = new ArrayList<>();
    private ListView schoolListView;
    private AddressAdapter adapter;
    private PoiSearch schoolPoiSearch;
    private LatLng point;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.map.status.changed");
        getActivity().registerReceiver(mSchoolBroad, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school, null);
        schoolPoiSearch = PoiSearch.newInstance();
        schoolData.clear();
        schoolData = getArguments().getParcelableArrayList("schoolPoiData");
        initView(view);
        return view;
    }

    private void nearBySchoolPoiSearch() {
        schoolData.clear();
        schoolPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.getAllPoi() == null) {
                    Toast.makeText(getActivity(), "定位失败,暂无数据信息", Toast.LENGTH_LONG).show();
                } else {
                    schoolData.addAll(poiResult.getAllPoi());
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
        nearbySearchOption.keyword("学校");
        nearbySearchOption.radius(2000);
        nearbySearchOption.pageCapacity(15);
        schoolPoiSearch.searchNearby(nearbySearchOption);
    }

    private BroadcastReceiver mSchoolBroad = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            point = intent.getParcelableExtra("Position");
            nearBySchoolPoiSearch();
        }
    };

    private void initView(View view) {
        schoolListView = (ListView) view.findViewById(R.id.school_list);
        adapter = new AddressAdapter(getActivity(), schoolData);
        schoolListView.setAdapter(adapter);
        schoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiInfo poiInfo = (PoiInfo) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra("Address", poiInfo);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mSchoolBroad);
    }
}
