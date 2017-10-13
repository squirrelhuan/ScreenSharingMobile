package com.huan.administrator.screensharingmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.huan.administrator.screensharingmobile.R;
import com.huan.administrator.screensharingmobile.bean.AreaDeviceBean;

import java.util.List;


/**
 * Created by user on 2017/5/9.
 */

public class AreaDeviceAdapter extends BaseAdapter {
    private Context mContext;
    private List<AreaDeviceBean> mData;

    public AreaDeviceAdapter(Context context, List<AreaDeviceBean> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AreaDeviceBean bean = (AreaDeviceBean) getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_area_device, null);
            holder.resIdIv = (ImageView) convertView.findViewById(R.id.item_area_device_iv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.item_area_device_name);
            holder.ipTv = (TextView) convertView.findViewById(R.id.item_area_device_ip);
            holder.macTv = (TextView) convertView.findViewById(R.id.item_area_device_mac);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.resIdIv.setImageResource(bean.getResId());
        holder.nameTv.setText(bean.getName());
        holder.ipTv.setText(bean.getIp());
        holder.macTv.setText(bean.getMac());
        return convertView;
    }

    private class ViewHolder {
        ImageView resIdIv;
        TextView nameTv;
        TextView ipTv;
        TextView macTv;
    }
}
