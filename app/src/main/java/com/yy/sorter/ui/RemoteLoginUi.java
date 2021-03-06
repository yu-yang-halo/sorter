package com.yy.sorter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.StringUtils;
import com.yy.sorter.utils.TextCacheUtils;
import com.yy.sorter.utils.YYToast;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.YYDevice;
import th.service.helper.YYCommand;
import th.service.helper.YYPackage;
import th.service.helper.YYPackageHelper;

public class RemoteLoginUi extends BaseUi {
    private Button remoteLogin;
    private AutoCompleteTextView deviceNumberEdit;
    private EditText vcodeEdit;
    private Button registerBtn;

    private String deviceSN;

    public RemoteLoginUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_login_remote,null);
            remoteLogin = (Button) view.findViewById(R.id.remoteLogin);
            deviceNumberEdit = (AutoCompleteTextView) view.findViewById(R.id.deviceNumberEdit);
            vcodeEdit = (EditText) view.findViewById(R.id.vcodeEdit);
            registerBtn = (Button) view.findViewById(R.id.button10);


            setListenser();
        }
        return view;
    }

    private void setLanguage()
    {
        remoteLogin.setText(FileManager.getInstance().getString(87));//87#登录
        deviceNumberEdit.setHint(FileManager.getInstance().getString(88));//88#设备编号
        vcodeEdit.setHint(FileManager.getInstance().getString(89));//89#登录口令
    }

    private void  initDeviceNumber(){

        String deviceNumber=TextCacheUtils.getValueString(TextCacheUtils.KEY_DEVICE_NUMBER,null);
        if(deviceNumber!=null){
            deviceNumberEdit.setText(deviceNumber);
        }
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_LOGIN_REMOTE;
    }

    @Override
    public void onViewStart() {
        super.onViewStart();
        AbstractDataServiceFactory.getInstance().emptyDeviceSets();
        AbstractDataServiceFactory.getInstance().reSetReconnectFlag();
        AbstractDataServiceFactory.getInstance().closeConnect();

        initAutoCompleteDeviceNumber(deviceNumberEdit);
        initDeviceNumber();

        setLanguage();
    }

    private void setListenser(){
        remoteLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hud!=null){
                    hud.dismiss();
                }

                final String deviceNumber=deviceNumberEdit.getText().toString();
                final String vcodeStr=vcodeEdit.getText().toString();
                final int vcode= ConvertUtils.toInt(vcodeStr);
                deviceSN = deviceNumber;

                if(deviceNumber.trim().isEmpty()){
                    YYToast.showToast(ctx, FileManager.getInstance().getString(16));//16#设备编号不能为空
                }else if(!StringUtils.isVaildDeviceSN(deviceNumber)){
                    YYToast.showToast(ctx, FileManager.getInstance().getString(17));//17#请输入正确格式的设备编号
                } else if(vcodeStr.trim().isEmpty()){
                    YYToast.showToast(ctx, FileManager.getInstance().getString(18));//18#授权码不能为空
                }else{
                    AbstractDataServiceFactory.initService(AbstractDataServiceFactory.SERVICE_TYPE_TCP);

                    cacheDeviceNumber(deviceNumber);
                    cacheVCode(vcode);

                    StringUtils.cacheLastThreeHistory(ctx,deviceNumber,2);
                    hud= KProgressHUD.create(ctx).setLabel( FileManager.getInstance().getString(19)).show(); //19#远程登录中...

                    AbstractDataServiceFactory.getInstance().requestDeviceList(deviceNumber,null,null,vcode);

                }




            }
        });




        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextCacheUtils.loadInt(TextCacheUtils.KEY_LOGIN_TYPE,1);
                MiddleManger.getInstance().changeUI(ConstantValues.VIEW_REGISTER,
                        FileManager.getInstance().getString(27));
            }
        });

    }
    private void cacheDeviceNumber(String deviceNumber){
        TextCacheUtils.loadString(TextCacheUtils.KEY_DEVICE_NUMBER,deviceNumber);
    }

    private void cacheVCode(int vcode){
        TextCacheUtils.loadInt(TextCacheUtils.KEY_VCODE,vcode);
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


    @Override
    public void receivePacketData(YYPackage packet) {
        if(hud!=null){
            hud.dismiss();
        }
        if(packet.getType()==0x51){

            if(packet.getExtendType()==0x01){
                YYDevice currentDevice=new YYDevice("","",deviceSN);
                AbstractDataServiceFactory.getInstance().setCurrentDevice(currentDevice);
                int lanCountryId=TextCacheUtils.getValueInt(TextCacheUtils.KEY_LAN_COUNTRY_ID,ConstantValues.LAN_COUNTRY_EN);
                AbstractDataServiceFactory.getInstance().login(null,(byte) lanCountryId);
            }else if(packet.getExtendType()==0x02){
                if(packet.getData1()[0]==2){
                    showToast(FileManager.getInstance().getString(1023)); //1023#您还没有注册
                }else  if(packet.getData1()[0]==3){
                    showToast(FileManager.getInstance().getString(1024)); //1024#用户数据错误
                }
            }
        }else if(packet.getType()== YYCommand.LOGIN_CMD) {

            if (packet.getExtendType() == 0x03) {
                AbstractDataServiceFactory.getInstance().closeConnect();
                if (MiddleManger.getInstance().isCurrentUI(RemoteLoginUi.this)) {
                    YYToast.showToast(ctx, FileManager.getInstance().getString(1001));  //1001#该屏幕已被锁定
                }
            } else if (packet.getExtendType() == 0x04) {
                AbstractDataServiceFactory.getInstance().closeConnect();
                if (MiddleManger.getInstance().isCurrentUI(RemoteLoginUi.this)) {
                    YYToast.showToast(ctx, FileManager.getInstance().getString(1025)); //1025#设备不在线
                }
            } else if (packet.getExtendType() == 0x05) {
                AbstractDataServiceFactory.getInstance().closeConnect();
                if (MiddleManger.getInstance().isCurrentUI(RemoteLoginUi.this)) {
                    YYToast.showToast(ctx, FileManager.getInstance().getString(1026)); //1026#授权码错误
                }
            }else if (packet.getExtendType() == 0x01) {
                if (MiddleManger.getInstance().isCurrentUI(RemoteLoginUi.this)) {
                    MachineData machineData = YYPackageHelper.parseMachineData(packet);
                    YYDevice currentDevice = AbstractDataServiceFactory.getInstance().getCurrentDevice();
                    if (currentDevice != null) {
                        currentDevice.setMachineData(machineData);
                    }
                    MiddleManger.getInstance().changeUI(ConstantValues.VIEW_HOME, FileManager.getInstance().getString(32));//32#主页

                    TextCacheUtils.loadInt(TextCacheUtils.KEY_LOGIN_TYPE,1);
                }
            }
        }
    }

}
