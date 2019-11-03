package th.service.core;

import com.yy.sorter.utils.ConvertUtils;

import java.util.HashSet;
import java.util.Set;

import th.service.data.YYDevice;
import th.service.helper.YYCommand;
import th.service.helper.YYPackage;

/**
 * 抽象通讯服务类：数据存储部分+公共协议部分
 *
 * Created by YUYANG on 2018/11/6.
 */

public abstract class AbstractIDataService implements IDataService{
    /**
     * 当前选中的设备
     */

    private YYDevice currentDevice;

    public YYDevice getCurrentDevice() {

        if(currentDevice==null){
            YYDevice device=new YYDevice("","","");
            device.setDeviceIsNull(true);
            return device;
        }
        currentDevice.setDeviceIsNull(false);
        return currentDevice;
    }

    public void setCurrentDevice(YYDevice currentDevice) {
        this.currentDevice = currentDevice;
    }

    private  Set<YYDevice> devices=new HashSet<>();

    /**
     * 清空设备集合
     */
    public synchronized void emptyDeviceSets(){
        devices.clear();
    }

    public synchronized Set<YYDevice> getDevices() {
        return devices;
    }
    public synchronized void addDevice(YYDevice device){
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
        YYPackage packet= new YYPackage(YYCommand.BROADCAST_DEV_CMD,(byte)0, null,(byte)0, (byte)0, (byte)0,sendData.getBytes());

        sendPacketData(packet,broadcastAddress);
    }

    @Override
    public void login(String devSn,byte lanCountryId){
        /**
         * loginType 针对大米机 用户和工程师版
         */
        byte loginType=(byte)1;
        byte[] data1=new byte[]{lanCountryId,loginType};
        YYPackage packet= new YYPackage(YYCommand.LOGIN_CMD, YYCommand.EXTEND_LOGIN_CMD, data1,(byte)0, (byte)0, (byte)0,null);

        sendPacketData(packet);

    }


    @Override
    public void logout() {
        YYPackage packet= new YYPackage(YYCommand.LOGIN_CMD, YYCommand.EXTEND_LOGOUT_CMD, null,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    @Override
    public void controlDevice(byte extendType, boolean isOn) {
        YYPackage packet= new YYPackage(YYCommand.CONTROL_CMD,extendType, null,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }


    @Override
    public void requestModeList(byte bigModeIndex) {
        byte[] data1=new byte[]{bigModeIndex};
        YYPackage packet= new YYPackage(YYCommand.MODE_CMD, (byte) 0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    @Override
    public void readMode(byte bigModeIndex, byte smallModeIndex) {
        byte[] data1=new byte[]{smallModeIndex,bigModeIndex};
        YYPackage packet= new YYPackage(YYCommand.MODE_CMD, (byte) 0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    @Override
    public void saveMode() {
        YYPackage packet= new YYPackage(YYCommand.MODE_CMD, (byte) 0x03, null,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    @Override
    public void requestFeederInfo() {
        YYPackage packet= new YYPackage(YYCommand.FEEDER_CMD, (byte) 0x01, null,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    @Override
    public void setFeederValue(byte setType, byte chuteOrGroup, byte value) {
        byte[] data1=new byte[]{setType,chuteOrGroup,value};
        YYPackage packet= new YYPackage(YYCommand.FEEDER_CMD, (byte) 0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    @Override
    public void controlFeederSwitch(byte controlType, byte chuteOrGroup, byte value) {
        byte[] data1=new byte[]{controlType,chuteOrGroup,value};
        YYPackage packet= new YYPackage(YYCommand.FEEDER_CMD, (byte) 0x03, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void requestSenseInfo(byte group,byte reqType)
    {
        byte[] data1=new byte[]{group,reqType};
        YYPackage packet= new YYPackage(YYCommand.SENSE_CMD, (byte) 0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setSenseValue(byte group,byte view,byte setType,byte subType,byte extType,int value)
    {
        byte[] arr = ConvertUtils.intTo2Bytes(value);

        byte[] data1=new byte[]{group,view,setType,subType,extType,arr[0],arr[1]};
        YYPackage packet= new YYPackage(YYCommand.SENSE_CMD, (byte) 0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setSenseEnable(byte group,byte view,byte setType,byte subType,byte extType)
    {
        byte[] data1=new byte[]{group,view,setType,subType,extType};
        YYPackage packet= new YYPackage(YYCommand.SENSE_CMD, (byte) 0x03, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void requestSvmInfo(byte group)
    {
        byte[] data1=new byte[]{group};
        YYPackage packet= new YYPackage(YYCommand.SVM_CMD, (byte) 0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setSvmInfo(byte group,byte view,byte setType,int value)
    {
        byte[] arr = ConvertUtils.intTo2Bytes(value);
        byte[] data1=new byte[]{group,view,setType,arr[0],arr[1]};
        YYPackage packet= new YYPackage(YYCommand.SVM_CMD, (byte) 0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void reqHsvInfo(byte group,byte view)
    {
        byte[] data1=new byte[]{group,view};
        YYPackage packet= new YYPackage(YYCommand.HSV_CMD, (byte) 0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setHsvInfo(byte group,byte view,byte senseIndex,byte setType,int value)
    {
        byte[] arr = ConvertUtils.intTo2Bytes(value);
        byte[] data1=new byte[]{group,view,senseIndex,setType,arr[0],arr[1]};
        YYPackage packet= new YYPackage(YYCommand.HSV_CMD, (byte) 0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void hsvSwitch(byte switchType,byte value)
    {
        byte[] data1=new byte[]{switchType,value};
        YYPackage packet= new YYPackage(YYCommand.HSV_CMD, (byte) 0x03, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void pageSwitch(byte page,byte exitOrEnter)
    {
        byte[] data1=new byte[]{page,exitOrEnter};
        YYPackage packet= new YYPackage(YYCommand.PAGE_SWITCH_CMD, (byte) 0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void requestWave(byte waveType,byte[] params)
    {
        YYPackage packet= new YYPackage(YYCommand.WAVE_CMD,waveType, params,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void requestVersionInfo(byte versionType,byte layer,byte visable)
    {
        byte[] data1=new byte[]{layer,visable};
        YYPackage packet= new YYPackage(YYCommand.VERSION_CMD, versionType, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void requestLightInfo(byte layer)
    {
        byte[] data1=new byte[]{layer};
        YYPackage packet= new YYPackage(YYCommand.LIGHT_CMD,(byte)0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setLightData(byte layer,byte view,byte setType,byte value)
    {
        byte[] data1=new byte[]{layer,view,setType,value};
        YYPackage packet= new YYPackage(YYCommand.LIGHT_CMD,(byte)0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void requestCameraGain(byte layer, byte channel)
    {
        byte[] data1=new byte[]{layer,channel};
        YYPackage packet= new YYPackage(YYCommand.CAMERA_GAIN_CMD,(byte)0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setCameraGain(byte layer,byte view,byte channel,byte adjustType,byte setType,byte ad,int value)
    {
        byte[] arr = ConvertUtils.intTo2Bytes(value);
        byte[] data1=new byte[]{layer,view,channel,adjustType,setType,ad,arr[0],arr[1]};
        YYPackage packet= new YYPackage(YYCommand.CAMERA_GAIN_CMD,(byte)0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void switchCameraGain(byte layer, byte channel, byte ad)
    {
        byte[] data1=new byte[]{layer,channel,ad};
        YYPackage packet= new YYPackage(YYCommand.CAMERA_GAIN_CMD,(byte)0x03, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void requestShapeInfo(byte group)
    {
        byte[] data1=new byte[]{group};
        YYPackage packet= new YYPackage(YYCommand.SHAPE_CMD,(byte)0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setShapeInfo(byte group,byte setType,byte whatShape,byte whatAlgorithm,byte whatIndex,int value)
    {
        byte[] arr = ConvertUtils.intTo2Bytes(value);
        byte[] data1=new byte[]{group,setType,whatShape,whatAlgorithm,whatIndex,arr[0],arr[1]};
        YYPackage packet= new YYPackage(YYCommand.SHAPE_CMD,(byte)0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }


    public void requestValveRate(byte layer,byte chute)
    {
        byte[] data1=new byte[]{layer,chute};
        YYPackage packet= new YYPackage(YYCommand.VALVE_RATE_CMD,(byte)0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }


    @Override
    public void requestWorkInfo() {
        YYPackage packet= new YYPackage(YYCommand.WORKINFO_CMD,(byte)0x01, null,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setText(byte[] text)
    {
        YYPackage packet= new YYPackage(YYCommand.ADD_TEXT_CMD,(byte)0x01, null,(byte)0, (byte)0, (byte)0,text);
        sendPacketData(packet);
    }
}
