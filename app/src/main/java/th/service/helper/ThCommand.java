package th.service.helper;

import com.yy.sorter.activity.BuildConfig;

public class ThCommand {

	/**
	 * 服务地址端口配置
	 */

	public static final int UDP_CORE_SERVER_PORT=13133; //udp核心服务端口
	public static final String TCP_CORE_SERVER_IP="www.taiheservice.com";//核心服务器 www.taiheservice.com
	public static final int TCP_CORE_SERVER_PORT=13133; //核心服务端口
	public static final String TCP_FILE_SERVER_IP="www.taiheservice.com";//文件服务器 www.taiheservice.com
	public static final int TCP_FILE_SERVER_PORT=13132;//文件服务端口
	public static final boolean DEBUG= BuildConfig.DEBUG;//调试版本


	/**
	 * 手机版本  1  用户版   2 工程师版
	 */
	public static final byte USER_VERSION_TYPE=0x01;
	public static final byte ENGINEER_VERSION_TYPE=0x01;

	/**
	 * 在此修改构建版本 默认：普通用户版本
	 */

	public static final int BUILD_VERSION= BuildConfig.SOFT_VERSION_TYPE;

	/**
	 * 启用广播方式或采用群发方式（不建议）
	 */
	public static final boolean ENABLE_BROADCAST=true;


	public static final byte TCP_LOGIN_CMD=0x51;
	public static final byte TCP_HEART_CMD=0x52;
	public static final byte TCP_DEVICE_LIST_CMD=0x53;  //已过期
	public static final byte TCP_DEVICE_INFO_CMD=0x54;  //已过期
	public static final byte TCP_DEVICE_OFFLINE_CMD=0x55;
	public static final byte TCP_USER_REGISTER_CMD=0x56;

	public static final byte ANDROID_DEVICE=0x01;//默认android设备


	/**
	 * 配置文件的类型
	 * 1：配置文件  2：apk文件  3：语言包文件
	 */
	public static final byte DOWNLOAD_FILE_TYPE_CONFIG=0x01;
	public static final byte DOWNLOAD_FILE_TYPE_APK=0x02;
	public static final byte DOWNLOAD_FILE_TYPE_LANGUAGE=0x03;


	public static final byte TCP_REQ_DOWNLOAD_WHAT_FILE= (byte) 0xc1;
	public static final byte TCP_REQ_DOWNLOAD_FILE=(byte)0xc2;



	public static final byte UDP_HEART_CMD=0x06;


	/**
	 * 网络异常分析
	 * TCP:
	 *     1.服务器无法访问到的情况：：：            连接超时
	 *     2.服务器断开或本地网络断开情况：：：      连接中断
	 *     3.网络不稳定的情况，心跳包接收超时：：：  网络不稳定退出
	 *
	 * UDP:
	 *     1.广播之后没有应答：：：                   没有设备或者与设备不在同一局域网内
	 *     2.客户端发送心跳长时间没有收到回应：       可以判断：1.网络不稳定 2.设备掉线 3.不在同一网络中
	 *
	 *
	 */
	public static final int  UDP_NETWORK_UNREACHABLE=999;
	public static final int  UDP_HEART_CMD_TIMEOUT_TIPS=1000;
	public static final int  UDP_HEART_CMD_TIMEOUT_TIPS_RETURN=1001;
	public static final int  REFRESH_CURRENT_PAGE=1002;


	public static final int  TCP_CONNECT_CLOSE=2003;
	public static final int  TCP_CONNECT_TIMEOUT=2004;
	public static final int  TCP_RECEIVE_TIMEOUT=2005;
	public static final int  TCP_CONNECT_CLOSE_NOMESSAGE=2006;
	public static final int  TCP_CONNECT_OFFLINE=2007;


	public static final int  NETWORK_TIMEOUT_RECONNECT=0x2000;
	public static final int  NETWORK_LOGGIN_SUCCESS=0x2001;


	public static final byte LOGIN_CMD=0x01;
	public static final byte EXTEND_LOGIN_CMD=0x01;
	public static final byte EXTEND_LOGOUT_CMD=0x02;
	
	/**广播设备列表**/
	public static final byte BROADCAST_DEV_CMD=0x02;


	/**控制命令**/
	public static final byte CONTROL_CMD=0x03;
	public static final byte EXTEND_CONTROL_CMD_FEEDER=0x01;//给料器
	public static final byte EXTEND_CONTROL_CMD_VALVE=0x02;//阀
	public static final byte EXTEND_CONTROL_CMD_START=0x03;//启动
	public static final byte EXTEND_CONTROL_CMD_CLEAR=0x04;//清灰
	/**
	预留1:0/1 开/关
	清灰：1:表示清灰中，0：清灰结束
	启动：0：停止，1：启动成功 2：启动中 3：启动失败
	 **/


	/**
	 *   预留2：失败类型
	 *  1: 配置文件错误 2: 控制板通信异常，不能启动3: 气压异常，不能启动
     *
	 */
	public static final byte MODE_CMD = 0x04;
	public static final byte FEEDER_CMD = 0x05;
	public static final byte SENSE_CMD = 0x07;
	public static final byte SVM_CMD = 0x08;
	public static final byte HSV_CMD = 0x09;
	public static final byte WAVE_CMD = 0x0b;
	public static final byte VERSION_CMD = 0x0c;
	public static final byte PAGE_SWITCH_CMD = 0x0d;
	public static final byte VALVE_RATE_CMD = 0x0e;
	public static final byte LIGHT_CMD = 0x0f;
	public static final byte CAMERA_GAIN_CMD = 0x10;
	public static final byte SHAPE_CMD = 0x11;



	/**
	 * 颜色组合类型（灰度使用）
	 */
	public static final int COLOR_COMB_CLR_RED=0;//红色
	public static final int COLOR_COMB_CLR_GREEN=1;//绿色
	public static final int COLOR_COMB_CLR_BLUE=2;  //蓝色
	public static final int COLOR_COMB_CLR_RED_GREEN=3;//红色+绿色
	public static final int COLOR_COMB_CLR_RED_BLUE=4; //红色+蓝色
	public static final int COLOR_COMB_CLR_GREEN_BLUE=5;//绿色+蓝色
	public static final int COLOR_COMB_CLR_RED_GREEN_BLUE=6;//红色+绿色+蓝色
	public static final int COLOR_COMB_CLR_IR1=7; //红外1
	public static final int COLOR_COMB_CLR_IR2=8; //红外2

	/**
	 * 波形类型定义
	 */
	public static final int WAVE_TYPE_BACKGROUN_LIGHT = 0x03;
	public static final int WAVE_TYPE_CAMERA_GAIN = 0x04;
	//public static final int WAVE_TYPE_CAMERA_GAIN_DIGIT = 0x05;
	public static final int WAVE_TYPE_CAMERA_ORIGIN = 0x08;
	public static final int WAVE_TYPE_CAMERA_TEST = 0x0a;
	public static final int WAVE_TYPE_CAMERA_ADJUST = 0x09;
	public static final int WAVE_TYPE_HSV = 0x12;


}
