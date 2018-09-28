package com.yy.sorter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


import com.yy.sorter.manager.FileManager;

import th.service.helper.IPUtils;
import th.service.helper.ThLogger;

/**
 * Created by Administrator on 2017/4/7.
 * 监听网络变化
 */

public class NetworkReceiver extends BroadcastReceiver {
    private  NetworkChangeListenser listenser;
    public void setListenser(NetworkChangeListenser listenser) {
        this.listenser = listenser;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       boolean enable;
       if(IPUtils.isConnected(context)){
           Toast.makeText(context, FileManager.getInstance().getString(128),Toast.LENGTH_SHORT).show(); //128#网络已连接
           enable=true;
           ThLogger.addLog("网络改变---网络已连接 ip:"+IPUtils.localIpAddress(context));
       }else {
           Toast.makeText(context, FileManager.getInstance().getString(129),Toast.LENGTH_SHORT).show(); //129#没有网络
           enable=false;
           ThLogger.addLog("网络改变---没有网络 ip:"+IPUtils.localIpAddress(context));
       }
        if(listenser!=null){
            listenser.onNetworkChange(enable);
        }
    }



}
