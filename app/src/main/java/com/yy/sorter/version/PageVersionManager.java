package com.yy.sorter.version;


import com.yy.sorter.utils.ConvertUtils;

import java.util.Map;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.ThDevice;

/**
 * Created by YUYANG on 2017/11/6.
 * 页面版本管理
 * 负责统一管理各个版本的页面
 *
 *
 */

public class PageVersionManager {
    private static PageVersionManager instance;
    public static PageVersionManager getInstance(){
        synchronized (PageVersionManager.class){
         if(instance==null){
             instance=new PageVersionManager();
         }
        }
        return instance;
    }

    public Map<Integer,Class> getPagesMap(){
        ThDevice device = AbstractDataServiceFactory.getInstance().getCurrentDevice();
        int screenVersion=-1;
        if(!device.isDeviceIsNull()){
            MachineData machineData = device.getMachineData();
            screenVersion = ConvertUtils.getScreenProtocolVersion(machineData);

        }
        BasePageV basePageV=BasePageV.createPageV(screenVersion);
        return basePageV.getBasePages();
    }



}
