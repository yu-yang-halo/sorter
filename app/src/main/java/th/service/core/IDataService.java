package th.service.core;

import th.service.helper.ThCommand;
import th.service.helper.ThPackage;

/**
 * 
 * @author yuyang
 *
 */

public interface IDataService {
	/**
	 *
	 *  用户注册及下载文件功能
	 *
	 */
	public void register(String username, String password);
	public void requestDownloadWhatFile(byte softVersion,byte whatTypeFile,String fileName);
	public void requestDownloadFile();
	public void closeFileSocket();

	/**
	 *********************************协议接口定义**************************************
	 */

	public void requestDeviceList(String broadcastAddress,String username, String password,int code);
	public void login(String devSn,byte lanCountryId);
	public void logout();
	public void controlDevice(byte extendType, boolean isOn);

	public void requestModeList(byte bigModeIndex);
	public void readMode(byte bigModeIndex,byte smallModeIndex);
	public void saveMode();


	public void requestFeederInfo();
	public void setFeederValue(byte setType,byte chuteOrGroup,byte value);
	public void controlFeederSwitch(byte controlType,byte chuteOrGroup,byte value);

	public void requestSenseInfo(byte group,byte reqType);
	public void setSenseValue(byte group,byte view,byte setType,byte subType,byte extType,int value);
	public void setSenseEnable(byte group,byte view,byte setType,byte subType,byte extType);

	public void requestSvmInfo(byte group);
	public void setSvmInfo(byte group,byte view,byte setType,int value);

	public void reqHsvInfo(byte group,byte view);
	public void setHsvInfo(byte group,byte view,byte senseIndex,byte setType,int value);
	public void hsvSwitch(byte switchType,byte value);

	public void requestWave(byte waveType,byte[] params);

	public void requestVersionInfo(byte versionType,byte layer,byte visable);

	public void pageSwitch(byte page,byte exitOrEnter);//page:0-相机校准  exitOrEnter：0 退出  1 进入

	public void requestLightInfo(byte layer);
	public void setLightData(byte layer,byte view,byte setType,byte value);

	public void requestCameraGain(byte layer, byte channel);
	public void switchCameraGain(byte layer, byte channel, byte ad);
	public void setCameraGain(byte layer,byte view,byte channel,byte adjustType,byte setType,byte ad,int value);

	public void requestValveRate(byte layer,byte chute);

	public void requestShapeInfo(byte group);
	public void setShapeInfo(byte group,byte setType,byte whatShape,byte whatAlgorithm,byte whatIndex,int value);


	public void requestWorkInfo();



	/**
	 *********************************协议接口定义**************************************
	 */




	/**
	 * 发送包数据
	 * @param thPackage
	 * @param ip
     */
	public void sendPacketData(ThPackage thPackage,String ip);
	public void sendPacketData(ThPackage thPackage);
	public void closeConnect();
	public void reSetReconnectFlag();


}


