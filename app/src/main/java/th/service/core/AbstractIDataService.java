package th.service.core;

import com.yy.sorter.utils.AuthUtils;
import com.yy.sorter.utils.ConvertUtils;

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
        ThPackage packet= new ThPackage(ThCommand.BROADCAST_DEV_CMD,(byte)0, null,(byte)0, (byte)0, (byte)0,sendData.getBytes());

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
        byte[] data1=new byte[]{lanCountryId,loginType};
        ThPackage packet= new ThPackage(ThCommand.LOGIN_CMD,ThCommand.EXTEND_LOGIN_CMD, data1,(byte)0, (byte)0, (byte)0,null);

        sendPacketData(packet);

    }


    @Override
    public void logout() {
        ThPackage packet= new ThPackage(ThCommand.LOGIN_CMD,ThCommand.EXTEND_LOGOUT_CMD, null,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    @Override
    public void controlDevice(byte extendType, boolean isOn) {
        ThPackage packet= new ThPackage(ThCommand.CONTROL_CMD,extendType, null,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }


    @Override
    public void requestModeList(byte bigModeIndex) {
        byte[] data1=new byte[]{bigModeIndex};
        ThPackage packet= new ThPackage(ThCommand.MODE_CMD, (byte) 0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    @Override
    public void readMode(byte bigModeIndex, byte smallModeIndex) {
        byte[] data1=new byte[]{smallModeIndex,bigModeIndex};
        ThPackage packet= new ThPackage(ThCommand.MODE_CMD, (byte) 0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    @Override
    public void saveMode() {
        ThPackage packet= new ThPackage(ThCommand.MODE_CMD, (byte) 0x03, null,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    @Override
    public void requestFeederInfo() {
        ThPackage packet= new ThPackage(ThCommand.FEEDER_CMD, (byte) 0x01, null,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    @Override
    public void setFeederValue(byte setType, byte chuteOrGroup, byte value) {
        byte[] data1=new byte[]{setType,chuteOrGroup,value};
        ThPackage packet= new ThPackage(ThCommand.FEEDER_CMD, (byte) 0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    @Override
    public void controlFeederSwitch(byte controlType, byte chuteOrGroup, byte value) {
        byte[] data1=new byte[]{controlType,chuteOrGroup,value};
        ThPackage packet= new ThPackage(ThCommand.FEEDER_CMD, (byte) 0x03, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void requestSenseInfo(byte group,byte reqType)
    {
        byte[] data1=new byte[]{group,reqType};
        ThPackage packet= new ThPackage(ThCommand.SENSE_CMD, (byte) 0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setSenseValue(byte group,byte view,byte setType,byte subType,byte extType,int value)
    {
        byte[] arr = ConvertUtils.intTo2Bytes(value);

        byte[] data1=new byte[]{group,view,setType,subType,extType,arr[0],arr[1]};
        ThPackage packet= new ThPackage(ThCommand.SENSE_CMD, (byte) 0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setSenseEnable(byte group,byte view,byte setType,byte subType,byte extType)
    {
        byte[] data1=new byte[]{group,view,setType,subType,extType};
        ThPackage packet= new ThPackage(ThCommand.SENSE_CMD, (byte) 0x03, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void requestSvmInfo(byte group)
    {
        byte[] data1=new byte[]{group};
        ThPackage packet= new ThPackage(ThCommand.SVM_CMD, (byte) 0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setSvmInfo(byte group,byte view,byte setType,int value)
    {
        byte[] arr = ConvertUtils.intTo2Bytes(value);
        byte[] data1=new byte[]{group,view,setType,arr[0],arr[1]};
        ThPackage packet= new ThPackage(ThCommand.SVM_CMD, (byte) 0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void reqHsvInfo(byte group,byte view)
    {
        byte[] data1=new byte[]{group,view};
        ThPackage packet= new ThPackage(ThCommand.HSV_CMD, (byte) 0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setHsvInfo(byte group,byte view,byte senseIndex,byte setType,int value)
    {
        byte[] arr = ConvertUtils.intTo2Bytes(value);
        byte[] data1=new byte[]{group,view,senseIndex,setType,arr[0],arr[1]};
        ThPackage packet= new ThPackage(ThCommand.HSV_CMD, (byte) 0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void hsvSwitch(byte switchType,byte value)
    {
        byte[] data1=new byte[]{switchType,value};
        ThPackage packet= new ThPackage(ThCommand.HSV_CMD, (byte) 0x03, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void pageSwitch(byte page,byte exitOrEnter)
    {
        byte[] data1=new byte[]{page,exitOrEnter};
        ThPackage packet= new ThPackage(ThCommand.PAGE_SWITCH_CMD, (byte) 0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void requestWave(byte waveType,byte[] params)
    {
        ThPackage packet= new ThPackage(ThCommand.WAVE_CMD,waveType, params,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void requestVersionInfo(byte versionType,byte layer,byte visable)
    {
        byte[] data1=new byte[]{layer,visable};
        ThPackage packet= new ThPackage(ThCommand.VERSION_CMD, versionType, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void requestLightInfo(byte layer)
    {
        byte[] data1=new byte[]{layer};
        ThPackage packet= new ThPackage(ThCommand.LIGHT_CMD,(byte)0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }
    public void setLightData(byte layer,byte view,byte setType,byte value)
    {
        byte[] data1=new byte[]{layer,view,setType,value};
        ThPackage packet= new ThPackage(ThCommand.LIGHT_CMD,(byte)0x02, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

    public void requestValveRate(byte layer,byte chute)
    {
        byte[] data1=new byte[]{layer,chute};
        ThPackage packet= new ThPackage(ThCommand.VALVE_RATE_CMD,(byte)0x01, data1,(byte)0, (byte)0, (byte)0,null);
        sendPacketData(packet);
    }

}
