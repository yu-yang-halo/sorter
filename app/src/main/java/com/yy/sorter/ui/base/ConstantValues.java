package com.yy.sorter.ui.base;


/**
 * Created by Administrator on 2017/3/17.
 */

public class ConstantValues {
    /**
     * 定义UI view的唯一标示
     */
    public static final int VIEW_LOGIN=1;              //登录
    public static final int VIEW_LOGIN_REMOTE = 2;     //远程登录界面
    public static final int VIEW_DEVICE_LIST = 3;
    public static final int VIEW_HOME=4;               //主界面
    public static final int VIEW_SENSE=5;          //灵敏度
    public static final int VIEW_MODE_LIST = 6; //方案列表
    public static final int VIEW_MORE = 7;      //更多
    public static final int VIEW_FEEDER=8; //给料器设置

    public static final int VIEW_LAN=11;    //语言设置界面
    public static final int VIEW_CAMERAADJUST=12; //相机校准
    public static final int VIEW_VERSION=13;  //版本信息
    public static final int VIEW_BACKGROUND=14;//背景灯光
    public static final int VIEW_VALVE_RATE=15;//阀指示





    public static final int  VIEW_WHEEL_SETTINGS= 17; //履带设置
    public static final int  VIEW_OPTICS_ADJUST = 18; //光学校准
    public static final int  VIEW_REGISTER  = 20;     //注册（授权）

    public static final int  VIEW_SHARE_QR=24; //分享APP二维码
    public static final int  VIEW_NOT_FOUND=404;//404页面无法找到



    public static final int VIEW_PAGE_RGB_IR = 0x100;
    public static final int VIEW_PAGE_SVM = 0x101;
    public static final int VIEW_PAGE_HSV=0x102;
    public static final int VIEW_PAGE_SHAPE=0x102;






    /**
     * 定义UI view 级别
     *
     */
    public static final int LEAVER_DEFAULT=-1;//默认级别 不判断
    public static final int LEAVER_TAB=0x8888;  //Tab 级别


    /**
     * 定义语言
     */

    public static final int LAN_COUNTRY_CN=1;//中文
    public static final int LAN_COUNTRY_EN=2;//英文


    /**
     * View_TYPE
     */
    public static final int VIEW_TYPE_ITEM = 0x100;
    public static final int VIEW_TYPE_NONE = 0x101;
    public static final int VIEW_TYPE_FRONT = 0x102;
    public static final int VIEW_TYPE_REAR = 0x103;


    /**
     * USER LEVEL 权限定义
     * 权限 0:user 1:工程师 2:厂家
     */

    public static final byte LEVEL_NOMAL = 0;
    public static final byte LEVEL_ENGINNER = 1;
    public static final byte LEVEL_PRODUCT = 2;






}
