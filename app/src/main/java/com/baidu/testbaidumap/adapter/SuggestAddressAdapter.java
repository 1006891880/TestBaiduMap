package com.baidu.testbaidumap.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.testbaidumap.R;

import java.util.List;

/**
 * Created by Totem on 2016/7/21.
 * @author 代码丶如风
 */
public class SuggestAddressAdapter extends android.widget.BaseAdapter {

    private Context context;
    private List<SuggestionResult.SuggestionInfo> poiData;

    public SuggestAddressAdapter(Context context, List<SuggestionResult.SuggestionInfo> poiData) {
        this.context = context;
        this.poiData = poiData;
    }

    @Override
    public int getCount() {
        return poiData.size();
    }

    @Override
    public Object getItem(int position) {
        return poiData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        SuggestionResult.SuggestionInfo poiInfo = poiData.get(position);
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.address_item, null);
            holder = new ViewHolder();
            holder.poiName = (TextView) convertView.findViewById(R.id.poiName);
            holder.poiAddress = (TextView) convertView.findViewById(R.id.poiAddress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.poiName.setText(poiInfo.key);
        holder.poiAddress.setText(poiInfo.city);
        return convertView;
    }

    static class ViewHolder {
        TextView poiName;
        TextView poiAddress;
    }
}
