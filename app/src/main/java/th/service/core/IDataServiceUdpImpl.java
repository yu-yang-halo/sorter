package th.service.core;

import th.service.helper.PacketExt;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.repeat.RepeatManager;

/**
 * Created by YUYANG on 2018/11/6.
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
		if(thPackage.getType()==ThCommand.BROADCAST_DEV_CMD
				|| (thPackage.getType()== ThCommand.LOGIN_CMD&&thPackage.getExtendType()==0x02)
				|| thPackage.getType()==ThCommand.UDP_HEART_CMD
				|| thPackage.getType()==ThCommand.WAVE_CMD
				|| thPackage.getType()==ThCommand.VALVE_RATE_CMD){

			/**
			 * -----忽略------
			 * 广播列表           [重发无意义]
			 * 登出              [重发无意义]
			 * 心跳              [无需重发]
			 *
			 *
			 * 波形 喷发频率       [重发无意义 定时再发送即可]
			 *
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
