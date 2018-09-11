package com.yy.sorter.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.ui.LoginUI;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import th.service.core.AbstractDataServiceFactory;
import th.service.core.ThObserver;
import th.service.data.ThDevice;
import th.service.helper.ThCommand;
import th.service.helper.ThLogger;
import th.service.helper.ThPackage;

/**
 * Created by Administrator on 2017/3/17.
 * 所有 UI 界面的基类
 * 后期修复：baseUI界面之间传递数据，后一个页面给前一个页面回传数据
 *           TopManager中的事件通知给baseUI
 *
 *           onViewCreate();
 *
 *
 *
 */

public abstract class BaseUI implements ThObserver{
    private static final String TAG="BaseUI";
    protected ExecutorService singleThreadExecutor= Executors.newSingleThreadExecutor();
    protected Context ctx;
    protected Handler mainUIHandler=new Handler(Looper.getMainLooper());
    /**
     * 子类需要使用该对象以便父类可见
     */
    protected KProgressHUD hud;
    private String title;
    private Bundle bundle;

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 已经定义好需要显示的view
     */
    protected View    view;
    public BaseUI(Context ctx){
        this.ctx=ctx;
    }
    protected void initViewWidthHeight(){
        if(view==null){
            return;
        }
        ViewGroup.LayoutParams params=view.getLayoutParams();
        if(params==null){
            params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(params);
        }
    }

    /**
     * 类似于onCreateView
     * 建议只做一些页面View初始化工作
     * @return
     */
    public abstract View getChild();

    /**
     * UI界面的唯一标识
     * @return
     */
    public abstract int  getID();
    /**
     * UI界面的级别,判断是否属于同一级使用
     * @return
     */
    public  int  getLeaver(){
        return ConstantValues.LEAVER_DEFAULT;
    }

    /**
     * 加入BaseUI 生命周期
     * onViewStart 界面加入时
     * onViewStop  界面移除时
     */

    public  void initViewContent(){

    }

    /**
     * 建议做数据加载工作
     */
    public  void onViewStart(){
        initViewContent();

    }

    /**
     * 刷新当前页面数据
     */
    protected void refreshPageData(){

    }


    public  void onViewStop(){
        if(hud!=null){
            hud.dismiss();
        }
    }
    public void onActivityStart(){

    }
    public void onActivityStop(){

    }

    /**
     * 横竖屏切换通知
     * @param newConfig
     */
    public void onConfigurationChanged(Configuration newConfig){

    }

    /**
     * Topmanager 左侧按钮点击事件触发
     */
    public void onLeftClick(){
        onViewBackPress();
    }
    /**
     * Topmanager 右侧按钮点击事件触发
     */
    public void onRightClick(){
    }

    /**
     * Topmanager 标题点击事件触发
     */
    public void onTitleClick()
    {

    }

    /**
     * 重新请求层视数据
     */
    public void onReloadLayerView(){

    }
    long lastTime=0;
    private void goBack(){


        if(MiddleManger.getInstance().getCurrentUI().getID()==ConstantValues.VIEW_HOME
                ||MiddleManger.getInstance().getCurrentUI().getID()==ConstantValues.VIEW_SENSITIVE
                ||MiddleManger.getInstance().getCurrentUI().getID()==ConstantValues.VIEW_FEEDER_SETTINGS
                ||MiddleManger.getInstance().getCurrentUI().getID()==ConstantValues.VIEW_VALVE_RATE){

            if(System.currentTimeMillis()-lastTime>3000){
                lastTime=System.currentTimeMillis();
                Toast.makeText(ctx, FileManager.getInstance().getString(330),Toast.LENGTH_SHORT).show();  //330#再按一次退出界面
            }else{
                MiddleManger.getInstance().goBack();
                /**
                 *
                 * 返回到设备列表界面时退出
                 */
                ThDevice currentDevice= AbstractDataServiceFactory.getInstance().getCurrentDevice();
                if(currentDevice!=null){
                    AbstractDataServiceFactory.getInstance().logout();
                }
            }

        }else{
            if(!MiddleManger.getInstance().goBack()){

                if(System.currentTimeMillis()-lastTime>3000){
                    lastTime=System.currentTimeMillis();
                    Toast.makeText(ctx, FileManager.getInstance().getString(222),Toast.LENGTH_SHORT).show();  //222#再按一次退出
                }else{
                    ((Activity)ctx).finish();
                }
            }
        }

    }

    public void onViewBackPress(){
        goBack();
    }

    public void onDeviceOffline(){

    }

    protected void showToast(Context ctx,String message){
        Toast.makeText(ctx,message,Toast.LENGTH_SHORT).show();
    }
    protected void showToast(String message){
        Toast.makeText(ctx,message,Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(String message){
        Toast.makeText(ctx,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void update(Object var1, final Object var2) {
        mainUIHandler.post(new Runnable() {
            @Override
            public void run() {

                if(MiddleManger.getInstance().isCurrentUI(BaseUI.this)){

                    if(var2.getClass()== ThPackage.class){
                        ThPackage thPackage= (ThPackage) var2;
                        if(thPackage.getType()==(byte)0xb0){
                            /**
                             * 刷新当前页面
                             */
                            ThLogger.addLog("发现重复包，刷新当前页面 包号:"+ ConvertUtils.unsignByteToInt(thPackage.getPacketNumber()));
                            refreshPageData();

                            return;
                        }

                        if (thPackage.getType()==ThCommand.TCP_DEVICE_OFFLINE_CMD){
                            if(hud!=null){
                                hud.dismiss();
                            }
                            onDeviceOffline();
                            if(thPackage.getExtendType()==0x01){
                                showToast(FileManager.getInstance().getString(213)+ FileManager.getInstance().getString(214)); //213#设备 214#已经下线
                                MiddleManger.getInstance().goBack(ConstantValues.VIEW_DEVICE_LIST);
                                ThLogger.addLog("UDP--设备已经下线");
                            }else{
                                String devSN= StringUtils.convertByteArrayToString(thPackage.getContents());
                                showToast(FileManager.getInstance().getString(213)+"["+devSN+"]"+ FileManager.getInstance().getString(214)); //213#设备 214#已经下线
                                MiddleManger.getInstance().goBack(ConstantValues.VIEW_LOGIN);
                                ThLogger.addLog("TCP--设备已经下线");
                            }

                        }


                    }else if(var2.getClass()==Integer.class){
                        if(hud!=null){
                            hud.dismiss();
                        }
                        int errorCode=(int)var2;
                        switch (errorCode){
                            case ThCommand.NETWORK_TIMEOUT_RECONNECT:
                                showToast(FileManager.getInstance().getString(319)); //319#网络不稳定，重连中...
                                break;
                            case ThCommand.NETWORK_LOGGIN_SUCCESS:
                                showToast(FileManager.getInstance().getString(329)); //329#连接成功
                                break;
                            case ThCommand.UDP_NETWORK_UNREACHABLE:
                                showToast(FileManager.getInstance().getString(9)); //9#请确保手机与设备处于同一局域网内
                                BaseUI currentUI= MiddleManger.getInstance().getCurrentUI();
                                if(!(currentUI instanceof LoginUI)){
                                    MiddleManger.getInstance().goBack(ConstantValues.VIEW_DEVICE_LIST);
                                }
                                break;
                            case ThCommand.UDP_HEART_CMD_TIMEOUT_TIPS:
                                showToast(FileManager.getInstance().getString(215)); //215#信号不稳定或者设备已经下线
                                break;
                            case ThCommand.UDP_HEART_CMD_TIMEOUT_TIPS_RETURN: //216#抱歉,设备不稳定,退出当前设备
                                showToast(FileManager.getInstance().getString(216));
                                MiddleManger.getInstance().goBack(ConstantValues.VIEW_DEVICE_LIST);
                                break;
                            case ThCommand.TCP_CONNECT_CLOSE:
                                //217#连接已断开,需要重新登录
                                toLoginUI(217);
                                break;
                            case ThCommand.TCP_CONNECT_TIMEOUT:
                                //218#连接超时,请重试
                                toLoginUI(218);
                                break;
                            case ThCommand.TCP_RECEIVE_TIMEOUT:
                                //219#网络不稳定，请重新登录
                                toLoginUI(219);
                                break;
                            case ThCommand.TCP_CONNECT_CLOSE_NOMESSAGE:
                                toLoginUI(-1);
                                break;
                            case ThCommand.TCP_CONNECT_OFFLINE:
                                //328#请检查手机网络是否可用
                                toLoginUI(328);
                                break;
                        }

                    }

                }


            }
        });

    }

    private void toLoginUI(int lanId){

        BaseUI currentUI=MiddleManger.getInstance().getCurrentUI();

        if(currentUI==null){
            return;
        }
        if(currentUI.getID()==ConstantValues.VIEW_REGISTER
                ||currentUI.getID()==ConstantValues.VIEW_LAN){
            //不提示
        }else{
            if(lanId>0){
                showToast(FileManager.getInstance().getString(lanId));
            }
            MiddleManger.getInstance().goBack(ConstantValues.VIEW_LOGIN);
        }

    }
}
