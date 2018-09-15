package th.service.core;

import th.service.helper.PacketExt;
import th.service.helper.ThPackage;
import th.service.repeat.RepeatManager;

/**
 *
 * UDPManager 代理类
 */
public class IDataServiceUdpImpl extends AbstractIDataService {
	private static UdpCoreManager udpCoreManager=UdpCoreManager.getInstance();

	@Override
	public void register(String username, String password) {

	}

	@Override
	public void closeConnect() {
		udpCoreManager.closeConnect();
	}

	@Override
	public void reSetReconnectFlag() {

	}


	@Override
	public void sendPacketData(ThPackage thPackage, String ip) {
		thPackage.setSenderIP(ip);
		if(thPackage.getType()==0x02
				|| (thPackage.getType()==0x01&&thPackage.getExtendType()==0x02)
				||thPackage.getType()==0x06){

			/**
			 * -----忽略------
			 *
			 * 心跳              [无需重发]
			 * 登出              [重发无意义]
			 * 广播列表           [重发无意义]
			 * 波形 喷发频率       [重发无意义 定时再发送即可]
			 * 大米机器相机校准
			 * 光学校准进入退出    [重发问题,会导致反复发送 ]
			 */
			thPackage.setPacketNumber((byte) 0);//不处理的包号
		}else{
			PacketExt packetExt=new PacketExt(System.currentTimeMillis());
			thPackage.setPacketExt(packetExt);
			RepeatManager.getInstance().add(thPackage);
		}
		udpCoreManager.sendData(thPackage.myByteArrays(),ip);
	}

	@Override
	public void sendPacketData(ThPackage thPackage) {
		String currentIp=this.getCurrentDevice().getLocalIp();
		sendPacketData(thPackage,currentIp);
	}

}
