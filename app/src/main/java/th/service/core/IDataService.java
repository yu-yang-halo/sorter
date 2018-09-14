package th.service.core;

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


