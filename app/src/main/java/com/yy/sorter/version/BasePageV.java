package com.yy.sorter.version;


import com.yy.sorter.ui.LanUI;
import com.yy.sorter.ui.LoginUI;
import com.yy.sorter.ui.RegisterUI;
import com.yy.sorter.ui.ShareQRUI;
import com.yy.sorter.ui.base.ConstantValues;

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
        public static final int VN_0_NEW=0x000000;///默认最新版本
        public static final int VN_0_2_0=0x000200;


        public static final int VN_1_NEW=0x010000;///默认最新版本

        public static final int VN_2_NEW=0x020000;///默认最新版本

    }
    static {

        map.put(VersionNumber.VN_0_2_0, PageV020.class);

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
        basePages.put(ConstantValues.VIEW_LOGIN, LoginUI.class);//登录
        basePages.put(ConstantValues.VIEW_REGISTER, RegisterUI.class);//注册（授权）
        basePages.put(ConstantValues.VIEW_LAN, LanUI.class);//语言设置界面
        basePages.put(ConstantValues.VIEW_SHARE_QR, ShareQRUI.class);//给料器设置


    }


}