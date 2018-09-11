package com.yy.sorter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.yy.sorter.activity.BuildConfig;
import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.ui.base.BaseUI;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.AuthUtils;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.LanguageHelper;
import com.yy.sorter.utils.StringUtils;
import com.yy.sorter.utils.TextCacheUtils;
import com.yy.sorter.utils.ThToast;
import com.yy.sorter.view.PageSwitchView;
import com.yy.sorter.view.ThSegmentView;

import java.util.ArrayList;
import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.ThConfig;
import th.service.data.ThDevice;
import th.service.helper.IPUtils;
import th.service.helper.ThCommand;
import th.service.helper.ThLogger;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

/**
 * Created by Administrator on 2017/3/17.
 * 登录页面
 */

public class LoginUI extends BaseUI {
    private static final String TAG="LoginUI";
    public LoginUI(Context ctx) {
        super(ctx);
    }
    private Button localLogin;
    private Button remoteLogin;
    private Button lanBtn;
    private TextView appversionLabel;
    private String deviceSN;
    private boolean isLastestNew=false;

    private Button btn_share_qr;

    @Override
    public View getChild() {
        if(view==null){
            view=LayoutInflater.from(ctx).inflate(R.layout.ui_login,null);

            localLogin= (Button) view.findViewById(R.id.localLogin);
            remoteLogin= (Button) view.findViewById(R.id.remoteLogin);
            lanBtn= (Button) view.findViewById(R.id.button9);

            appversionLabel= (TextView) view.findViewById(R.id.appversionLabel);


            btn_share_qr= (Button) view.findViewById(R.id.btn_share_qr);


            setListenser();



        }
        /**
         * 初始化宽度和高度
         */
        initViewWidthHeight();

        initUsernameAndPassword();
        initAppVersion(isLastestNew);


        if(BuildConfig.SOFT_VERSION_TYPE==0x01
                || BuildConfig.SOFT_VERSION_TYPE==0x02){

            btn_share_qr.setVisibility(View.VISIBLE);
        }else{
            btn_share_qr.setVisibility(View.GONE);
        }

        return view;
    }


    private void initAppVersion(boolean isLastestNew){
        this.isLastestNew=isLastestNew;
        String isNewText="--"+FileManager.getInstance().getString(260);//260#最新版
        if(!isLastestNew){
            isNewText="";
        }

        if(!AuthUtils.isEngineerVersion()){
            appversionLabel.setText(String.format("v%s(%s)%s",
                    StringUtils.getAppVersionName(ctx),
                    FileManager.getInstance().getString(258),isNewText));//258#普通用户版

        }else{
            appversionLabel.setText(
                    String.format("v%s(%s)%s",
                            StringUtils.getAppVersionName(ctx),
                            FileManager.getInstance().getString(259),isNewText));//  259#工程师版
        }
    }




    private void initAutoCompleteDeviceNumber(AutoCompleteTextView auto){
        String[] hisArrays= StringUtils.getLastThreeHistorys(ctx,2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
                R.layout.item_dropdown, hisArrays);
        auto.setAdapter(adapter);
        auto.setDropDownHeight(250);
        auto.setThreshold(1);
        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
    }

    private void initAutoComplete(AutoCompleteTextView auto) {
        String[] hisArrays= StringUtils.getLastThreeHistorys(ctx,1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
                R.layout.item_dropdown, hisArrays);
        auto.setAdapter(adapter);
        auto.setDropDownHeight(250);
        auto.setThreshold(1);
        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
    }
    private void  initUsernameAndPassword(){

        String username=TextCacheUtils.getValueString(TextCacheUtils.KEY_USERNAME,null);
        String password=TextCacheUtils.getValueString(TextCacheUtils.KEY_PASSWORD,null);
        String deviceNumber=TextCacheUtils.getValueString(TextCacheUtils.KEY_DEVICE_NUMBER,null);

    }

    private void cacheUsernameAndPassword(String username,String password){
       TextCacheUtils.loadString(TextCacheUtils.KEY_USERNAME,username);
       TextCacheUtils.loadString(TextCacheUtils.KEY_PASSWORD,password);
    }

    private void cacheDeviceNumber(String deviceNumber){
        TextCacheUtils.loadString(TextCacheUtils.KEY_DEVICE_NUMBER,deviceNumber);
    }

    private void cacheVCode(int vcode){
        TextCacheUtils.loadInt(TextCacheUtils.KEY_VCODE,vcode);
    }


    @Override
    public void onViewStart() {
        super.onViewStart();
        AbstractDataServiceFactory.getInstance().emptyDeviceSets();
        AbstractDataServiceFactory.getInstance().reSetReconnectFlag();
        AbstractDataServiceFactory.getInstance().closeConnect();





    }

    @Override
    public void onViewStop() {
        super.onViewStop();
    }

    @Override
    public void initViewContent() {
        super.initViewContent();
        localLogin.setText(FileManager.getInstance().getString(4));//4#本地登录
        remoteLogin.setText(FileManager.getInstance().getString(5));//5#远端登录
        lanBtn.setText(FileManager.getInstance().getString(3));
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_LOGIN;
    }

    private void setListenser(){
        remoteLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });
        localLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(hud!=null){
                    hud.dismiss();
                }

                AbstractDataServiceFactory.initService(AbstractDataServiceFactory.SERVICE_TYPE_UDP);


                if(!AuthUtils.isEngineerVersion()){
                    /**
                     * 用户版不验证注册信息
                     */
                    localLoginReqest("test","123456");
                }else{
                    String[] arrs=AuthUtils.getLocalCertificationFileContents();
                    if(arrs==null || arrs.length!=3){
                        ThToast.showToast(ctx, FileManager.getInstance().getString(239)); //239#您还没有注册，请先注册
                        return;
                    }
                    localLoginReqest("test","123456");
                }

            }
        });

        lanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MiddleManger.getInstance().changeUI(ConstantValues.VIEW_LAN,
                        FileManager.getInstance().getString(3));
            }
        });


        btn_share_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiddleManger.getInstance().changeUI(ConstantValues.VIEW_SHARE_QR,
                        FileManager.getInstance().getString(385));//385#APP二维码
            }
        });
    }

    private void localLoginReqest(final String username, final String password){
        StringUtils.cacheLastThreeHistory(ctx,username,1);
        hud=KProgressHUD.create(ctx).setLabel(FileManager.getInstance().getString(8)).show(); //8#搜索中...

        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                reqDeviceList(username,password);
                try {
                    new Thread().sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mainUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(hud!=null){
                            hud.dismiss();
                        }
                        if(AbstractDataServiceFactory.getInstance().getDevices().size()<=0){
                            ThToast.showToast(ctx, FileManager.getInstance().getString(9)); //9#请确保手机与设备处于同一局域网内
                        }else{
                            cacheUsernameAndPassword(username,password);
                            MiddleManger.getInstance().changeUI(ConstantValues.VIEW_DEVICE_LIST,
                                    FileManager.getInstance().getString(15));
                            TextCacheUtils.loadInt(TextCacheUtils.KEY_LOGIN_TYPE,0);
                        }
                    }
                });

            }
        });
    }


    private void reqDeviceList(String username,String password){
         AbstractDataServiceFactory.getInstance().requestDeviceList(IPUtils.getBroadCastAddress(ctx),username,password,0);
        try {
            new Thread().sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AbstractDataServiceFactory.getInstance().requestDeviceList(IPUtils.getBroadCastAddress(ctx),username,password,0);
    }
    @Override
    public int getLeaver() {
        return ConstantValues.LEAVER_DEFAULT;
    }

    @Override
    public void update(Object var1, final Object var2) {
        super.update(var1,var2);
        ThLogger.debug(TAG,var1+""+var2);
        mainUIHandler.post(new Runnable(){
            @Override
            public void run() {
                if(var2.getClass()== ThPackage.class){
                    ThPackage thPackage= (ThPackage) var2;
                    /**
                     * 网络下载逻辑处理
                     */
                    LanguageHelper.onCallbackFileHandler(ctx, thPackage, LoginUI.this, new LanguageHelper.IProgressListenser() {
                        @Override
                        public void onFinished(byte fileType, boolean success,ThConfig config) {
                            if(config!=null){
                               boolean lanHasUpdate=false;
                               List<ThConfig.LanguageVersion> languageVersions= config.getLanguage();
                               if(languageVersions!=null){
                                   for (ThConfig.LanguageVersion languageVersion:languageVersions){
                                       if(languageVersion.getVersion()!=languageVersion.getLastVersion()){
                                           int currentCountryId=TextCacheUtils.getValueInt(TextCacheUtils.KEY_LAN_COUNTRY_ID,0);
                                           if(currentCountryId == languageVersion.getCountryId()){
                                               //当前语言有更新才会提示
                                               lanHasUpdate=true;
                                               break;
                                           }
                                       }
                                   }
                               }
                                final boolean finalLanHasUpdate = lanHasUpdate;
                                mainUIHandler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       if(finalLanHasUpdate){
                                           showLongToast(FileManager.getInstance().getString(392,"语言包有更新啦，快去更新一下吧！"));//392#语言包有更新啦，快去更新一下吧！
                                       }
                                   }
                               });

                            }
                        }
                        @Override
                        public void onVersionUpdate(boolean isLastestNew) {
                            initAppVersion(isLastestNew);
                        }
                    });

                    if(AbstractDataServiceFactory.isTcp()){
                        if(hud!=null){
                            hud.dismiss();
                        }
                        if(thPackage.getType()==0x51){

                            if(thPackage.getExtendType()==0x01){
                                ThDevice currentDevice=new ThDevice("","",deviceSN);
                                AbstractDataServiceFactory.getInstance().setCurrentDevice(currentDevice);
                                int lanCountryId=TextCacheUtils.getValueInt(TextCacheUtils.KEY_LAN_COUNTRY_ID,ConstantValues.LAN_COUNTRY_EN);
                                AbstractDataServiceFactory.getInstance().login(null,(byte) lanCountryId);
                            }else if(thPackage.getExtendType()==0x02){
                                if(thPackage.getData1()[0]==2){
                                    showToast(FileManager.getInstance().getString(261)); //261#您还没有注册
                                }else  if(thPackage.getData1()[0]==3){
                                    showToast(FileManager.getInstance().getString(262)); //262#用户数据错误
                                }
                            }
                        }else if(thPackage.getType()== ThCommand.LOGIN_CMD) {

                            if (thPackage.getExtendType() == 0x03) {
                                AbstractDataServiceFactory.getInstance().closeConnect();
                                if (MiddleManger.getInstance().isCurrentUI(LoginUI.this)) {
                                    ThToast.showToast(ctx, FileManager.getInstance().getString(12));  //12#该屏幕已被锁定
                                }
                            } else if (thPackage.getExtendType() == 0x04) {
                                AbstractDataServiceFactory.getInstance().closeConnect();
                                if (MiddleManger.getInstance().isCurrentUI(LoginUI.this)) {
                                    ThToast.showToast(ctx, FileManager.getInstance().getString(242)); //242#设备不在线
                                }
                            } else if (thPackage.getExtendType() == 0x05) {
                                AbstractDataServiceFactory.getInstance().closeConnect();
                                if (MiddleManger.getInstance().isCurrentUI(LoginUI.this)) {
                                    ThToast.showToast(ctx, FileManager.getInstance().getString(316)); //316#授权码错误
                                }
                            }else if (thPackage.getExtendType() == 0x01) {
                                if (MiddleManger.getInstance().isCurrentUI(LoginUI.this)) {
                                    MachineData machineData = ThPackageHelper.parseMachineData(thPackage);
                                    ThDevice currentDevice = AbstractDataServiceFactory.getInstance().getCurrentDevice();
                                    if (currentDevice != null) {
                                        currentDevice.setMachineData(machineData);
                                    }
                                    MiddleManger.getInstance().changeUI(ConstantValues.VIEW_HOME, FileManager.getInstance().getString(13));

                                    TextCacheUtils.loadInt(TextCacheUtils.KEY_LOGIN_TYPE,1);


                                }
                            }
                        }
                    }else if(thPackage.getType()==0x02){
//                        ThDevice device=ThPackageHelper.parseMyDevice(thPackage);
//                        AbstractDataServiceFactory.getInstance().addDevice(device);

                    }


                }
            }
        });

    }
}
