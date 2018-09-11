package th.service.core;

import com.yy.sorter.utils.AuthUtils;

import java.util.HashSet;
import java.util.Set;

import th.service.data.ThDevice;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;

/**
 * 抽象通讯服务类：数据存储部分+公共协议部分
 *
 * Created by Administrator on 2017/3/24.
 */

public abstract class AbstractIDataService implements IDataService{
    /**
     * 当前选中的设备
     */

    private ThDevice currentDevice;

    public ThDevice getCurrentDevice() {

        if(currentDevice==null){
            ThDevice device=new ThDevice("","","");
            device.setDeviceIsNull(true);
            return device;
        }
        currentDevice.setDeviceIsNull(false);
        return currentDevice;
    }

    public void setCurrentDevice(ThDevice currentDevice) {
        this.currentDevice = currentDevice;
    }

    private  Set<ThDevice> devices=new HashSet<>();

    /**
     * 清空设备集合
     */
    public synchronized void emptyDeviceSets(){
        devices.clear();
    }

    public synchronized Set<ThDevice> getDevices() {
        return devices;
    }
    public synchronized void addDevice(ThDevice device){
        if(devices.contains(device)){
            devices.remove(device);
        }
        devices.add(device);
    }



    /**
     * 公共协议部分
     */

    @Override
    public void requestDownloadWhatFile(byte softVersion, byte whatTypeFile, String fileName) {
    }

    @Override
    public void requestDownloadFile() {
    }

    @Override
    public void closeFileSocket() {

    }


    @Override
    public void requestDeviceList(String broadcastAddress,String username, String password,int code) {
        // TODO Auto-generated method stub
        String sendData=username+"#"+password;
        byte[] data1=new byte[]{(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
        ThPackage packet= new ThPackage(ThCommand.BROADCAST_DEV_CMD,(byte)0, data1,(byte)0, (byte)0, (byte)0,sendData.getBytes());

        sendPacketData(packet,broadcastAddress);
    }

    @Override
    public void login(String devSn,byte lanCountryId){
        /**
         * loginType 针对大米机 用户和工程师版
         */
        byte loginType=(byte)1;
        if(AuthUtils.isEngineerVersion()){
            //工程师版
            loginType=(byte)2;
        }

        String currentIp=this.getCurrentDevice().getLocalIp();
        byte[] data1=new byte[]{lanCountryId,loginType,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
        ThPackage packet= new ThPackage(ThCommand.LOGIN_CMD,ThCommand.EXTEND_LOGIN_CMD, data1,(byte)0, (byte)0, (byte)0,null);

        sendPacketData(packet,currentIp);

    }


    @Override
    public void logout() {
        String currentIp=this.getCurrentDevice().getLocalIp();
        byte[] data1=new byte[]{(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
        ThPackage packet= new ThPackage(ThCommand.LOGIN_CMD,(byte)ThCommand.EXTEND_LOGOUT_CMD, data1,(byte)0, (byte)0, (byte)0,null);

        sendPacketData(packet,currentIp);
    }

    @Override
    public void controlDevice(byte extendType, boolean isOn) {
        String currentIp=this.getCurrentDevice().getLocalIp();
        byte[] data1=new byte[]{isOn?ThCommand.RESERVED_SEND_ON:ThCommand.RESERVED_SEND_OFF,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
        ThPackage packet= new ThPackage(ThCommand.CONTROL_CMD,extendType, data1,(byte)0, (byte)0, (byte)0,null);


        sendPacketData(packet,currentIp);
    }




}
