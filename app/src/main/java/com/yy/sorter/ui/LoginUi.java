package com.yy.sorter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.LanguageHelper;
import com.yy.sorter.utils.StringUtils;
import com.yy.sorter.utils.TextCacheUtils;
import com.yy.sorter.utils.YYToast;

import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.YYConfig;
import th.service.data.YYDevice;
import th.service.helper.IPUtils;
import th.service.helper.YYCommand;
import th.service.helper.YYPackage;
import th.service.helper.YYPackageHelper;

/**
 * LoginUi
 * 登录页面
 */

public class LoginUi extends BaseUi {
    private static final String TAG="LoginUi";
    public LoginUi(Context ctx) {
        super(ctx);
    }
    private Button localLogin;
    private Button remoteLogin;
    private Button lanBtn;
    private TextView appversionLabel;
    private boolean isLastestNew=false;

    @Override
    protected View onInitView() {
        if(view==null){
            view=LayoutInflater.from(ctx).inflate(R.layout.ui_login,null);

            localLogin= (Button) view.findViewById(R.id.localLogin);
            remoteLogin= (Button) view.findViewById(R.id.remoteLogin);
            lanBtn= (Button) view.findViewById(R.id.button9);

            appversionLabel= (TextView) view.findViewById(R.id.appversionLabel);

            setListenser();

        }

        initAppVersion(isLastestNew);


        return view;
    }


    private void initAppVersion(boolean isLastestNew){
        this.isLastestNew=isLastestNew;
        String isNewText="--"+FileManager.getInstance().getString(12);//12#最新版
        if(!isLastestNew){
            isNewText="";
        }

        appversionLabel.setText(
                String.format("v%s%s",
                        StringUtils.getAppVersionName(ctx),isNewText));
    }



    @Override
    public void onViewStart() {
        super.onViewStart();

        localLogin.setText(FileManager.getInstance().getString(4));//4#本地登录
        remoteLogin.setText(FileManager.getInstance().getString(5));//5#远端登录
        lanBtn.setText(FileManager.getInstance().getString(3));//3#语言

        AbstractDataServiceFactory.getInstance().emptyDeviceSets();
        AbstractDataServiceFactory.getInstance().reSetReconnectFlag();
        AbstractDataServiceFactory.getInstance().closeConnect();


    }

    @Override
    public void onViewStop() {
        super.onViewStop();
    }



    @Override
    public int getID() {
        return ConstantValues.VIEW_LOGIN;
    }

    private void setListenser(){
        remoteLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            MiddleManger.getInstance().changeUI(ConstantValues.VIEW_LOGIN_REMOTE,21);//21#远程登录

            }
        });
        localLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(hud!=null){
                    hud.dismiss();
                }

                AbstractDataServiceFactory.initService(AbstractDataServiceFactory.SERVICE_TYPE_UDP);
                localLoginReqest("test","1234");

            }
        });

        lanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MiddleManger.getInstance().changeUI(ConstantValues.VIEW_LAN,
                        FileManager.getInstance().getString(3));
            }
        });

    }

    private void localLoginReqest(final String username, final String password){
        StringUtils.cacheLastThreeHistory(ctx,username,1);
        hud=KProgressHUD.create(ctx).setLabel(FileManager.getInstance().getString(13)).show(); //13#本地登录中...

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
                            YYToast.showToast(ctx, FileManager.getInstance().getString(14)); //14#本地无法找到设备
                        }else{

                            MiddleManger.getInstance().changeUI(ConstantValues.VIEW_DEVICE_LIST, 31);//31#设备列表
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
    public void receivePacketData(YYPackage packet) {
        /**
         * 网络下载逻辑处理
         */
        LanguageHelper.onCallbackFileHandler(ctx, packet, LoginUi.this, new LanguageHelper.IProgressListenser() {
            @Override
            public void onFinished(byte fileType, boolean success, YYConfig config) {
                if(config!=null){
                    boolean lanHasUpdate=false;
                    List<YYConfig.LanguageVersion> languageVersions= config.getLanguage();
                    if(languageVersions!=null){
                        for (YYConfig.LanguageVersion languageVersion:languageVersions){
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
                                showLongToast(FileManager.getInstance().getString(15,"语言包有更新啦，快去更新一下吧！"));//15#语言包有更新啦，快去更新一下吧！
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

        if(packet.getType()== YYCommand.BROADCAST_DEV_CMD){
            YYDevice device= YYPackageHelper.parseMyDevice(packet);
            AbstractDataServiceFactory.getInstance().addDevice(device);
        }

    }

}
