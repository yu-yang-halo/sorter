package com.yy.sorter.version;


import com.yy.sorter.ui.AddTextUi;
import com.yy.sorter.ui.BackgroundUi;
import com.yy.sorter.ui.CameraAdjustUi;
import com.yy.sorter.ui.DeviceListUi;
import com.yy.sorter.ui.FeederUi;
import com.yy.sorter.ui.HomeUi;
import com.yy.sorter.ui.LanUi;
import com.yy.sorter.ui.LoginUi;
import com.yy.sorter.ui.ModeListUi;
import com.yy.sorter.ui.MoreUi;
import com.yy.sorter.ui.RegisterUi;
import com.yy.sorter.ui.RemoteLoginUi;
import com.yy.sorter.ui.SenseUi;
import com.yy.sorter.ui.ValveRateUi;
import com.yy.sorter.ui.VersionUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.ui.page.HsvPage;
import com.yy.sorter.ui.page.RgbIrPage;
import com.yy.sorter.ui.page.ShapePage;
import com.yy.sorter.ui.page.SvmPage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/6.
 * 配置界面版本对应表
 *
 */

public abstract class BasePageV {
    protected   Map<Integer,Class> basePages;
    private static final Map<Integer, Class<? extends BasePageV>> map = new HashMap();
    private static BasePageV currentPageV;
    static class VersionNumber{
        /**
         *   版本编号标识：取决于 类型（特殊机型）    大版本        小版本
         *   类型    ： 0（一般机型） 1（大米机型-特殊系列）
         *   大版本  ： 0-255
         *   小版本  ： 0-255
         */
        public static final int VN_0_NEW=0x000100;///默认最新版本
        public static final int VN_0_1_0=0x000100;


        public static final int VN_1_NEW=0x010000;///默认最新版本

        public static final int VN_2_NEW=0x020000;///默认最新版本

    }
    static {

        map.put(VersionNumber.VN_0_1_0, PageV010.class);

    }

    public static BasePageV createPageV(int version){

        Class<? extends BasePageV> clazz=map.get(version);
        if(clazz==null){
            int type = (version>>16)&0xff;
            switch (type)
            {
                case 0:
                    clazz = map.get(VersionNumber.VN_0_NEW);
                    break;
                case 1:
                    clazz = map.get(VersionNumber.VN_1_NEW);
                    break;
                case 2:
                    clazz = map.get(VersionNumber.VN_2_NEW);
                    break;
                default:
                    clazz = PageVNotFound.class;
                    break;
            }
        }

        if(clazz == null)
        {
            clazz = PageVNotFound.class;
        }
        if(currentPageV==null||currentPageV.getClass()!=clazz){
            try {
                currentPageV=clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return currentPageV;
    }

    public  Map<Integer,Class> getBasePages(){
        return basePages;
    }

    /**
     * 子类可以重写该方法，来定制特定版本的UI
     */
    protected void initPages(){
        basePages.put(ConstantValues.VIEW_LOGIN, LoginUi.class);//登录
        basePages.put(ConstantValues.VIEW_LOGIN_REMOTE, RemoteLoginUi.class);//远程登录
        basePages.put(ConstantValues.VIEW_REGISTER, RegisterUi.class);//注册（授权）
        basePages.put(ConstantValues.VIEW_LAN, LanUi.class);//语言设置界面
        basePages.put(ConstantValues.VIEW_HOME, HomeUi.class);//主界面
        basePages.put(ConstantValues.VIEW_DEVICE_LIST, DeviceListUi.class);//设备列表
        basePages.put(ConstantValues.VIEW_MODE_LIST, ModeListUi.class);//方案
        basePages.put(ConstantValues.VIEW_SENSE, SenseUi.class);//灵敏度主界面
        basePages.put(ConstantValues.VIEW_MORE, MoreUi.class);//
        basePages.put(ConstantValues.VIEW_FEEDER, FeederUi.class);//给料量
        basePages.put(ConstantValues.VIEW_CAMERAADJUST, CameraAdjustUi.class);//相机校准
        basePages.put(ConstantValues.VIEW_VERSION, VersionUi.class);//相机校准
        basePages.put(ConstantValues.VIEW_BACKGROUND, BackgroundUi.class);//背景灯光
        basePages.put(ConstantValues.VIEW_VALVE_RATE, ValveRateUi.class);//背景灯光
        basePages.put(ConstantValues.VIEW_ADD_TEXT, AddTextUi.class);//添加文本


        /**
         * child page
         */
        basePages.put(ConstantValues.VIEW_PAGE_RGB_IR, RgbIrPage.class);//色选
        basePages.put(ConstantValues.VIEW_PAGE_SVM, SvmPage.class);//SVM
        basePages.put(ConstantValues.VIEW_PAGE_HSV, HsvPage.class);//hsv
        basePages.put(ConstantValues.VIEW_PAGE_SHAPE, ShapePage.class);//shape

    }


}
