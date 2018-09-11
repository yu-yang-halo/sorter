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



	/**
	 *********************************协议接口定义**************************************
	 */




	/**
	 * 发送包数据
	 * @param thPackage
	 * @param ip
     */
	public void sendPacketData(ThPackage thPackage,String ip);
	public void closeConnect();
	public void reSetReconnectFlag();


}


