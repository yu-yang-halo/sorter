package com.yy.sorter.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.TopManager;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.LanguageHelper;
import com.yy.sorter.utils.TextCacheUtils;
import com.yy.sorter.utils.YYToast;


import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.YYConfig;
import th.service.helper.YYCommand;

/**
 * LanAdapter
 */

public class LanAdapter extends BaseAdapter {
    Handler mainUIHandler=new Handler(Looper.getMainLooper());
    private Context ctx;
    private List<YYConfig.LanguageVersion> lanList;
    private YYConfig newConfig;
    private boolean isConnectToInternet;

    public void setConnectToInternet(boolean connectToInternet) {
        isConnectToInternet = connectToInternet;
    }

    public LanAdapter(Context ctx, List<YYConfig.LanguageVersion> lanList){
        this.ctx=ctx;
        this.lanList=lanList;
    }


    public void setNewConfig(YYConfig newConfig) {
        this.newConfig = newConfig;
        this.lanList = newConfig.getLanguage();
    }



    public void setLanList(List<YYConfig.LanguageVersion> lanList) {
        this.lanList = lanList;
    }

    @Override
    public int getCount() {
        if(lanList!=null){
            return lanList.size();
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
            convertView= LayoutInflater.from(ctx).inflate(R.layout.adapter_lans,null);
        }
        YYConfig.LanguageVersion lan=lanList.get(position);
        CheckBox ckLan= (CheckBox) convertView.findViewById(R.id.ckLan);
        TextView lanNameLabel= (TextView) convertView.findViewById(R.id.lanNameLabel);
        Button updateBtn= (Button) convertView.findViewById(R.id.updateBtn);

        if(lan.getLastVersion()!=lan.getVersion()){
            lanNameLabel.setText(lan.getName());
            updateBtn.setVisibility(View.VISIBLE);
            ckLan.setVisibility(View.GONE);

            if(!isConnectToInternet){
                /**
                 *没有连接到互联网则不提示更新
                 */
                updateBtn.setVisibility(View.GONE);
                ckLan.setVisibility(View.VISIBLE);
            }
        }else{
            lanNameLabel.setText(lan.getName());
            updateBtn.setVisibility(View.GONE);
            ckLan.setVisibility(View.VISIBLE);
        }

        int lanCountryId=TextCacheUtils.getValueInt(TextCacheUtils.KEY_LAN_COUNTRY_ID, ConstantValues.LAN_COUNTRY_EN);
        updateBtn.setText(FileManager.getInstance().getString(1018));//1018#有更新


        if(lan.getCountryId()==lanCountryId){
            ckLan.setChecked(true);
        }else{
            ckLan.setChecked(false);
        }

        lanNameLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickControl(position);
            }
        });

        ckLan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickControl(position);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickControl(position);
            }
        });



        return convertView;
    }

    private void onClickControl(final int position){
        notifyDataSetChanged();

        if(!isConnectToInternet){
            switchLan(position);
            return;
        }
        //7#确定
        //8#取消
        if(lanList.get(position).getLastVersion()!=lanList.get(position).getVersion()){
            new AlertDialog.Builder(ctx)
                    .setTitle(FileManager.getInstance().getString(6))//6#提示
                    .setMessage(lanList.get(position).getName()+" "+FileManager.getInstance().getString(1019))//1019#发现新的语言包,请及时更新
                    .setPositiveButton(FileManager.getInstance().getString(7), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    LanguageHelper.setCurrentDownloadURL(lanList.get(position).getUrl());
                    LanguageHelper.setCurrentCountryId(lanList.get(position).getCountryId());

                    AbstractDataServiceFactory.getFileDownloadService().requestDownloadWhatFile((byte) YYCommand.BUILD_VERSION, YYCommand.DOWNLOAD_FILE_TYPE_LANGUAGE,lanList.get(position).getUrl());
                }
                }).setNegativeButton(FileManager.getInstance().getString(8), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();

        }else{
            switchLan(position);
        }

    }

    private void switchLan(final int position){
        FileManager.getInstance().setCtx(ctx);
        FileManager.getInstance().switchLanguage(lanList.get(position).getUrl(), new FileManager.ILanguageHandler() {
            @Override
            public void onComplelete(final boolean success) {
                mainUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (success){
                            TextCacheUtils.loadInt(TextCacheUtils.KEY_LAN_COUNTRY_ID,lanList.get(position).getCountryId());
                            TextCacheUtils.loadString(TextCacheUtils.KEY_LAN_URL,lanList.get(position).getUrl());
                            YYToast.showToast(ctx,FileManager.getInstance().getString(1020));//1020#加载语言成功

                            TopManager.getInstance().changeTitle(FileManager.getInstance().getString(3));
                            notifyDataSetChanged();

                        }else{
                            YYToast.showToast(ctx,FileManager.getInstance().getString(1021));//1021#加载语言失败
                        }
                    }
                });
            }
        });
    }

    public void notifyDownloadSuccess(){
        int countryId=LanguageHelper.getCurrentCountryId();
        int pos=0;
        for(int i=0;i<lanList.size();i++){
            if(lanList.get(i).getCountryId()==countryId){
                pos=i;
                break;
            }
        }
        lanList.get(pos).setVersion(lanList.get(pos).getLastVersion());
        newConfig.setLanguage(lanList);
        FileManager.getInstance().saveConfig(newConfig);

        int lanCountryId=TextCacheUtils.getValueInt(TextCacheUtils.KEY_LAN_COUNTRY_ID, ConstantValues.LAN_COUNTRY_EN);
        if(lanList.get(pos).getCountryId()==lanCountryId) {
            switchLan(pos);
        }else{
            notifyDataSetChanged();
        }
    }
}
