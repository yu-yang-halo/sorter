package com.yy.sorter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.utils.ConvertUtils;

import java.util.List;
import th.service.data.ThSVersion;

/**
 * Created by Administrator on 2017/4/10.
 */

public class ColorVersionAdapter extends BaseAdapter {
    private Context ctx;
    private List<ThSVersion.ColorVersion> thColorRet;


    public ColorVersionAdapter(Context ctx, List<ThSVersion.ColorVersion> thValveRet){
        this.ctx=ctx;
        this.thColorRet=thValveRet;
    }

    public void setRet(List<ThSVersion.ColorVersion> ret) {
        this.thColorRet = ret;
    }

    public void setThValveRet(List<ThSVersion.ColorVersion> thColorRet) {
        this.thColorRet = thColorRet;
    }

    @Override

    public int getCount() {
        if(thColorRet!=null){
            return thColorRet.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=LayoutInflater.from(ctx).inflate(R.layout.item_colorversion,null);
        }
        TextView numTxt= (TextView) convertView.findViewById(R.id.tv_chute);
        TextView tvARM = (TextView)convertView.findViewById(R.id.tv_arm_adapter);
        TextView tvFPGA = (TextView)convertView.findViewById(R.id.tv_fpga_adapter);
        TextView tvHardware = (TextView)convertView.findViewById(R.id.tv_hardware_adapter);

        numTxt.setText(Integer.toString(position+1));
        ThSVersion.ColorVersion ver = thColorRet.get(position);
        tvARM.setText(Float.toString(ConvertUtils.bytes2ToFloat(ver.getArm())));
        tvFPGA.setText(Float.toString(ConvertUtils.bytes2ToFloat(ver.getFpga())));
        tvHardware.setText(ver.getHardwareStr());


        return convertView;
    }
}
