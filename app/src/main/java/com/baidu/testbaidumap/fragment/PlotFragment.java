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
public class PlotFragment extends Fragment {

    private List<PoiInfo> plotData = new ArrayList<>();
    private ListView plotListView;
    private AddressAdapter adapter;
    private PoiSearch plotPoiSearch;
    private LatLng point;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.map.status.changed");
        getActivity().registerReceiver(mPlotBroad,intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plot,null);
        plotPoiSearch = PoiSearch.newInstance();
        plotData.clear();
        plotData = getArguments().getParcelableArrayList("plotPoiData");
        initView(view);
        return view;
    }

    private void initView(View view){
        plotListView = (ListView) view.findViewById(R.id.plot_list);
        adapter = new AddressAdapter(getActivity(), plotData);
        plotListView.setAdapter(adapter);
        plotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiInfo poiInfo = (PoiInfo) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra("Address",poiInfo);
                getActivity().setResult(Activity.RESULT_OK,intent);
                getActivity().finish();
            }
        });
    }

    private void nearByPlotPoiSearch() {
        plotData.clear();
        plotPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.getAllPoi() == null) {
                    Toast.makeText(getActivity(),"定位失败,暂无数据信息", Toast.LENGTH_LONG).show();
                } else {
                    plotData.addAll(poiResult.getAllPoi());
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
        nearbySearchOption.keyword("小区");
        nearbySearchOption.radius(2000);
        nearbySearchOption.pageCapacity(15);
        plotPoiSearch.searchNearby(nearbySearchOption);
    }

    private BroadcastReceiver mPlotBroad = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            plotData.clear();
            point = intent.getParcelableExtra("Position");
            nearByPlotPoiSearch();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mPlotBroad);
    }
}
