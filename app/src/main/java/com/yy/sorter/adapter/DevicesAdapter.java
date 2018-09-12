package com.yy.sorter.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yy.sorter.activity.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import th.service.data.ThDevice;

/**
 * Created by Administrator on 2017/3/18.
 */

public class DevicesAdapter extends BaseAdapter {
    private List<ThDevice> devices;
    private Context ctx;
    public DevicesAdapter(Context ctx, Set<ThDevice> devices){
       setDevices(devices);
       this.ctx=ctx;
    }

    public List<ThDevice> getDevices() {
        return devices;
    }

    public void setDevices(Set<ThDevice> devices) {

        if(devices==null){
            return;
        }
        this.devices=new ArrayList<>();
        for (ThDevice d:devices){
            if(d!=null){
                this.devices.add(d);
            }
        }
        Collections.sort(this.devices, new Comparator<ThDevice>() {
            @Override
            public int compare(ThDevice o1, ThDevice o2) {
                return o1.hashCode()-o2.hashCode();
            }
        });

    }

    @Override
    public int getCount() {
        if(devices==null){
            return 0;
        }
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        if(devices==null){
            return null;
        }
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(ctx).inflate(R.layout.adapter_devices,null);
        }
        TextView deviceNameLabel= (TextView) convertView.findViewById(R.id.deviceNameLabel);
        TextView deviceSNLabel= (TextView) convertView.findViewById(R.id.deviceSNLabel);

        ThDevice device=devices.get(position);
        deviceNameLabel.setText(device.getDeviceName());

        StringBuilder builder=new StringBuilder();
        builder.append(device.getDeviceSN());
        deviceSNLabel.setText(builder.toString());

        deviceNameLabel.setTextColor(Color.parseColor("#00989d"));
        deviceSNLabel.setTextColor(Color.parseColor("#80888888"));
        return convertView;
    }
}
