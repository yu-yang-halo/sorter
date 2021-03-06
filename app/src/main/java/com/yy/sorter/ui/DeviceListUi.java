package com.yy.sorter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yy.sorter.activity.R;
import com.yy.sorter.adapter.DevicesAdapter;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.TextCacheUtils;
import com.yy.sorter.utils.YYToast;

import java.util.Set;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.YYDevice;
import th.service.helper.IPUtils;
import th.service.helper.YYCommand;
import th.service.helper.YYPackage;
import th.service.helper.YYPackageHelper;

public class DeviceListUi extends BaseUi {
    private PullRefreshLayout refreshLayout;
    private ListView  listView;
    private DevicesAdapter devicesAdapter;
    public DeviceListUi(Context ctx) {
        super(ctx);
    }
    @Override
    protected View onInitView() {
        if(view==null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_devicelist, null);
            listView= (ListView) view.findViewById(R.id.listview);
            refreshLayout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

            refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshDeviceList();
                }
            });

            Set<YYDevice> devices= AbstractDataServiceFactory.getInstance().getDevices();

            devicesAdapter=new DevicesAdapter(ctx,devices);
            listView.setAdapter(devicesAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    YYDevice device= devicesAdapter.getDevices().get(position);
                    AbstractDataServiceFactory.getInstance().setCurrentDevice(device);

                    int lanCountryId=TextCacheUtils.getValueInt(TextCacheUtils.KEY_LAN_COUNTRY_ID,ConstantValues.LAN_COUNTRY_EN);

                    AbstractDataServiceFactory.getInstance().login(null,(byte) lanCountryId);
                    hud= KProgressHUD.create(ctx).setLabel( FileManager.getInstance().getString(13)).show(); //13#本地登录中...
                    mainUIHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(hud!=null){
                                hud.dismiss();
                                hud=null;
                                showToast(FileManager.getInstance().getString(1000)); //1000#信号不稳定或者设备已经下线
                            }
                        }
                    },3000);



                }
            });
        }
        return view;
    }

    @Override
    public void onViewStart() {
        refreshDeviceList();
    }

    @Override
    public void onViewStop() {
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
    private void refreshDeviceList(){
        final String username= "test";
        final String password= "1234";
        devicesAdapter.setDevices(null);
        devicesAdapter.notifyDataSetChanged();

        if(AbstractDataServiceFactory.isTcp()){
            refreshLayout.setRefreshing(false);
        }else{
            if(username!=null&&password!=null){
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

                                if(AbstractDataServiceFactory.getInstance().getDevices().size()<=0
                                        && MiddleManger.getInstance().isCurrentUI(DeviceListUi.this)){
                                    YYToast.showToast(ctx, FileManager.getInstance().getString(9));
                                    Set<YYDevice> devices=AbstractDataServiceFactory.getInstance().getDevices();

                                    devicesAdapter.setDevices(devices);
                                    devicesAdapter.notifyDataSetChanged();
                                }
                                refreshLayout.setRefreshing(false);

                            }
                        });
                    }
                });


            }else {
                refreshLayout.setRefreshing(false);
            }

        }
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_DEVICE_LIST;
    }

    @Override
    public void receivePacketData(YYPackage packet) {
        if(packet.getType()== YYCommand.LOGIN_CMD){
            if(hud!=null){
                hud.dismiss();
                hud=null;
            }
            if(packet.getExtendType()==0x03){
                if(MiddleManger.getInstance().isCurrentUI(DeviceListUi.this)){
                    YYToast.showToast(ctx, FileManager.getInstance().getString(1001));  //1001#该屏幕已被锁定
                }
            }else if(packet.getExtendType()==0x01) {
                if(MiddleManger.getInstance().isCurrentUI(DeviceListUi.this)){
                    MachineData machineData= YYPackageHelper.parseMachineData(packet);
                    YYDevice currentDevice=AbstractDataServiceFactory.getInstance().getCurrentDevice();
                    if(currentDevice!=null){
                        currentDevice.setMachineData(machineData);
                    }
                    MiddleManger.getInstance().changeUI(ConstantValues.VIEW_HOME, FileManager.getInstance().getString(32));
                }
            }
        }else if(packet.getType()== YYCommand.BROADCAST_DEV_CMD){

            final YYDevice device= YYPackageHelper.parseMyDevice(packet);
            if (device!=null){
                AbstractDataServiceFactory.getInstance().addDevice(device);
            }
            final Set<YYDevice> devices=AbstractDataServiceFactory.getInstance().getDevices();


            mainUIHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    devicesAdapter.setDevices(devices);
                    devicesAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }
            },100);

        }
    }
}
