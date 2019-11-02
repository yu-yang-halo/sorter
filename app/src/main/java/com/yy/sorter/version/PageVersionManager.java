package com.yy.sorter.version;


import android.content.Context;
import android.widget.Toast;

import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.page.PageBaseUi;
import com.yy.sorter.utils.ConvertUtils;

import java.util.Map;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.YYDevice;

/**
 * Created by YUYANG on 2018/11/6.
 * PageVersionManager
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
        YYDevice device = AbstractDataServiceFactory.getInstance().getCurrentDevice();
        int screenVersion=-1;
        if(!device.isDeviceIsNull()){
            MachineData machineData = device.getMachineData();
            screenVersion = ConvertUtils.getScreenProtocolVersion(machineData);

        }
        BasePageV basePageV=BasePageV.createPageV(screenVersion);
        return basePageV.getBasePages();
    }
    public Class<? extends BaseUi>  getPageClass(int pageId)
    {
        Map<Integer, Class> pagesMap = getPagesMap();
        Class<? extends PageBaseUi> targetClazz = pagesMap.get(pageId);

        return targetClazz;
    }

    public PageBaseUi createPage(int pageId, Context ctx)
    {
        Map<Integer, Class> pagesMap = getPagesMap();
        Class<? extends PageBaseUi> targetClazz = pagesMap.get(pageId);
        PageBaseUi pageUi=null;

        if (targetClazz == null) {
            Toast.makeText(ctx,"不支持的界面Create",Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            pageUi = targetClazz.getConstructor(Context.class).newInstance(ctx);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return pageUi;
    }


}
