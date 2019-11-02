package com.yy.sorter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import java.util.List;
import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.YYSVersion;


/**
 * CameraVersionAdapter
 */

public class CameraVersionAdapter extends BaseAdapter {
    private Context ctx;
    private List<YYSVersion.CameraVersion> thCameraRet;


    public CameraVersionAdapter(Context ctx, List<YYSVersion.CameraVersion> thCameraRet){
        this.ctx=ctx;
        this.thCameraRet=thCameraRet;
    }

    public void setRet(List<YYSVersion.CameraVersion> ret) {
        this.thCameraRet = ret;
    }

    public void setThValveRet(List<YYSVersion.CameraVersion> thCameraRet) {
        this.thCameraRet = thCameraRet;
    }

    @Override

    public int getCount() {
        if(thCameraRet!=null){
            return thCameraRet.size();
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
            convertView=LayoutInflater.from(ctx).inflate(R.layout.item_cameraversion,null);
        }

        YYSVersion.CameraVersion  cameraVersion=thCameraRet.get(position);



        TextView numTxt= (TextView) convertView.findViewById(R.id.tv_chute);
        TextView tvSoftFront = (TextView)convertView.findViewById(R.id.tv_soft_adapter1);
        TextView tvSoftRear = (TextView)convertView.findViewById(R.id.tv_soft_adapter2);
        TextView tvHardFront = (TextView)convertView.findViewById(R.id.tv_hard_adapter1);
        TextView tvHardRear = (TextView)convertView.findViewById(R.id.tv_hard_adapter2);

        numTxt.setText(Integer.toString(position+1));

        YYSVersion.CameraVersion ver = thCameraRet.get(position);
        tvSoftFront.setText(String.valueOf(ver.getFront_software()[1]));
        tvSoftRear.setText(String.valueOf(ver.getRear_software()[1]));
        tvHardFront.setText(ver.getFrontHardwareStr());
        tvHardRear.setText(ver.getRearHardwareStr());

        MachineData machineData= AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();
        int layer= AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer()-1;


        if(cameraVersion.getCameraType()==2||cameraVersion.getCameraType()==3){
            tvSoftFront.setVisibility(View.VISIBLE);
            tvHardFront.setVisibility(View.VISIBLE);
            tvSoftRear.setVisibility(View.VISIBLE);
            tvHardRear.setVisibility(View.VISIBLE);
        }else {
            tvSoftFront.setVisibility(View.VISIBLE);
            tvHardFront.setVisibility(View.VISIBLE);
            tvSoftRear.setVisibility(View.VISIBLE);
            tvHardRear.setVisibility(View.VISIBLE);


        }

        return convertView;
    }
}
