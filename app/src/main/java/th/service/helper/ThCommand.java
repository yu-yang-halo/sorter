package th.service.helper;

import com.yy.sorter.activity.BuildConfig;

public class ThCommand {

	/**
	 * 服务地址端口配置
	 */

	public static final int UDP_CORE_SERVER_PORT=1234; //udp核心服务端口
	public static final String TCP_CORE_SERVER_IP="www.taiheservice.com";//核心服务器 www.taiheservice.com
	public static final int TCP_CORE_SERVER_PORT=1234; //核心服务端口
	public static final String TCP_FILE_SERVER_IP="www.taiheservice.com";//文件服务器 www.taiheservice.com
	public static final int TCP_FILE_SERVER_PORT=10023;//文件服务端口
	public static final boolean DEBUG= BuildConfig.DEBUG;//调试版本


	/**
	 * 手机版本  1  用户版   2 工程师版
	 */
	public static final byte USER_VERSION_TYPE=0x01;
	public static final byte ENGINEER_VERSION_TYPE=0x02;

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
	public static final byte EXTEND_MACHINE_CFG_CMD=0x11;
	
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
	public static final byte MODE_CMD=0x0d;


	/**
	 * 给料量
	 */
	public static final byte FEEDER_CMD=0x05;



	/**
	 * 信号设置 灯光
	 */
	public static final byte SIGNAL_LIGHT_CMD=0x0B;
	public static final byte EXTEND_SIGNAL_LIGHT_GET_CMD=0x01;
	public static final byte EXTEND_SIGNAL_LIGHT_POST_CMD=0x02;

	/**
	 * 信号设置 相机增益
	 */
	public static final byte SIGNAL_CAMERA_PLUS_CMD=0x0C;
	public static final byte EXTEND_SIGNAL_CAMERA_PLUS_GET_CMD=0x01;
	public static final byte EXTEND_SIGNAL_CAMERA_PLUS_POST_CMD=0x02;
	public static final byte EXTEND_SIGNAL_CAMERA_PLUS_SWITCH_CMD=0x03;

	/**
	 * 方案设置
	 */

	public static final byte SCHEME_CMD=0x0D;
	public static final byte EXTEND_SCHEME_GET_CURRENT_CMD=0x01;//获取当前方案
	public static final byte EXTEND_SCHEME_SAVE_CMD=0x06;//保存方案
	public static final byte EXTEND_SCHEME_GET_CONFIG_CMD=0x03;//查看方案配置
	public static final byte EXTEND_SCHEME_SAVE_CONFIG_CMD=0x04;//保存方案配置
	public static final byte EXTEND_SCHEME_USE_CMD=0x05;//使用方案

	/**
	 * 喷阀指示
	 */
	public static final byte VALVE_RATE_CMD=0x0E;
	public static final byte EXTEND_VALVE_RATE_CMD=0x01;
	/**
	 * 履带
	 */
	public static final byte WHEEL_CMD=0x0F;
	public static final byte EXTEND_WHEEL_SWITCH_CMD=0x01;
	public static final byte EXTEND_WHEEL_CURRENT_SPEED_CMD=0x02;
	public static final byte EXTEND_WHEEL_SPEED_CONFIG_CMD=0x03;
	public static final byte EXTEND_WHEEL_SPEED_CMD=0x04;

	/**
	 * SVM
	 */
	public static final byte SVM_CMD=0x10;
	public static final byte EXTEND_SVM_REQ_CMD=0x01;
	public static final byte EXTEND_SVM_V3_REQ_CMD=0x11;
	public static final byte EXTEND_SVM_CONFIG_CMD=0x02;
	public static final byte EXTEND_SVM_V3_CONFIG_CMD=0x12;
	/**
	 * 光学校准
	 */
	public static final byte OPTICS_CMD=0x11;
	public static final byte EXTEND_OPTICS_REQ_CMD=0x01;
	public static final byte EXTEND_OPTICS_CONFIG_CMD=0x02;
	public static final byte EXTEND_OPTICS_ADJUST_CMD=0x03;
	public static final byte EXTEND_OPTICS_SWITCH_WAVE_CMD=0x04;
	public static final byte EXTEND_OPTICS_OR_RESET=0x05;//校准或重置
	/**
	 * HSV 色调 饱和度 明度
	 */
	public static final byte HSV_CMD=0x30;
	public static final byte EXTEND_HSV_GET_PARAMS_CMD=0x01;
	public static final byte EXTEND_HSV_SET_PARAMS_CMD=0x02;
	public static final byte EXTEND_HSV_SWITCH_SENSE_CMD=0x03;



	/**
	 * 腰果形选
	 */
	public static final byte SHAPE_CACHEWS_CMD=0x12;
	public static final byte EXTEND_SHAPE_CASHEWS_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_CASHEWS_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_CASHEWS_ENABLE_CMD=0x03;
	/**
	 * 花生牙头
	 */
	public static final byte SHAPE_PEANUTBUD_CMD=0x13;
	public static final byte EXTEND_SHAPE_PEANUTBUD_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_PEANUTBUD_PARAMS_V3_CMD=0x11;
	public static final byte EXTEND_SHAPE_PEANUTBUD_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_PEANUTBUD_SET_VALUE_V3_CMD=0x12;
	public static final byte EXTEND_SHAPE_PEANUTBUD_SET_ENABLE_CMD=0x03;
	public static final byte EXTEND_SHAPE_PEANUTBUD_SET_ENABLE_V3_CMD=0x13;
	/**
	 * 标准形选
	 */
	public static final byte SHAPE_STANDARD_CMD=0x14;
	public static final byte EXTEND_SHAPE_STANDARD_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_STANDARD_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_STANDARD_SET_EABLE_CMD=0x03;

	/**
	 * 铁观音 大红袍
	 */
	public static final byte SHAPE_TEA_CMD=0x15;
	public static final byte EXTEND_SHAPE_TEA_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_TEA_EDIT_CMD=0x02;
	public static final byte EXTEND_SHAPE_TEA_BUTTON_CMD=0x03;
	/**
	 * 大米形选
	 */
	public static final byte SHAPE_RICE_CMD=0x16;
	public static final byte EXTEND_SHAPE_RICE_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_RICE_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_RICE_SET_ENABLE_CMD=0x03;
	/**
	 * 甘草
	 */
	public static final byte SHAPE_LIQUORICE_CMD=0x17;
	public static final byte EXTEND_SHAPE_LIQUORICE_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_LIQUORICE_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_LIQUORICE_SET_ENABLE_CMD=0x03;
	/**
	 * 小麦
	 */
	public static final byte SHAPE_WHEAT_CMD=0x18;
	public static final byte EXTEND_SHAPE_WHEAT_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_WHEAT_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_WHEAT_SET_ENABLE_CMD=0x03;
	/**
	 * 稻种
	 */
	public static final byte SHAPE_SEED_CMD=0x19;
	public static final byte EXTEND_SHAPE_SEED_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_SEED_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_SEED_SET_ENABLE_CMD=0x03;
	/**
	 * 葵花籽
	 */
	public static final byte SHAPE_SUNFLOWER_CMD=0x1A;
	public static final byte EXTEND_SHAPE_SUNFLOWER_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_SUNFLOWER_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_SUNFLOWER_SET_ENABLE_CMD=0x03;
	/**
	 * 玉米
	 */
	public static final byte SHAPE_CORN_CMD=0x1B;
	public static final byte EXTEND_SHAPE_CORN_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_CORN_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_CORN_SET_ENABLE_CMD=0x03;
	/**
	 * 蚕豆
	 */
	public static final byte SHAPE_HORSEBEAN_CMD=0x1C;
	public static final byte EXTEND_SHAPE_HORSEBEAN_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_HORSEBEAN_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_HORSEBEAN_SET_ENABLE_CMD=0x03;
	/**
	 * 绿茶
	 */
	public static final byte SHAPE_GREENTEA_CMD=0x1D;
	public static final byte EXTEND_SHAPE_GREENTEA_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_GREENTEA_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_GREENTEA_SET_ENABLE_CMD=0x03;
	/**
	 * 红茶
	 */
	public static final byte SHAPE_REDTEA_CMD=0x1E;
	public static final byte EXTEND_SHAPE_REDTEA_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_REDTEA_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_REDTEA_SET_ENABLE_CMD=0x03;

	/**
	 * 绿茶短梗
	 */
	public static final byte SHAPE_GREENTEA_SG_CMD=0x1F;
	public static final byte EXTEND_SHAPE_GREENTEA_SG_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_GREENTEA_SG_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_GREENTEA_SG_SET_ENABLE_CMD=0x03;

	/**
	 * 选长米
	 */
	public static final byte SHAPE_RICE_LENGTH_CMD=0x20;
	public static final byte EXTEND_SHAPE_RICE_LENGTH_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_RICE_LENGTH_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_RICE_LENGTH_SET_ENABLE_CMD=0x03;

	/**
	 * 选碎米
	 */
	public static final byte SHAPE_RICE_BROKEN_CMD=0x21;
	public static final byte EXTEND_SHAPE_RICE_BROKEN_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_RICE_BROKEN_SET_VALUE_CMD=0x02;
	public static final byte EXTEND_SHAPE_RICE_BROKEN_SET_ENABLE_CMD=0x03;

	/**
	 * 大红袍
	 */
	public static final byte SHAPE_DAHONGPAO_CMD=0x22;
	public static final byte EXTEND_SHAPE_DAHONGPAO_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_DAHONGPAO_EDIT_CMD=0x02;
	public static final byte EXTEND_SHAPE_DAHONGPAO_BUTTON_CMD=0x03;

	/**
	 * 花生
	 */
	public static final byte SHAPE_PEANUT_NEW_CMD=0x23;
	public static final byte EXTEND_SHAPE_PEANUT_NEW_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_PEANUT_NEW_EDIT_CMD=0x02;
	public static final byte EXTEND_SHAPE_PEANUT_NEW_BUTTON_CMD=0x03;

	/**
	 * 辣椒
	 */
	public static final byte SHAPE_HOT_PEPPER_CMD=0x24;
	public static final byte EXTEND_SHAPE_HOT_PEPPER_PARAMS_CMD=0x01;
	public static final byte EXTEND_SHAPE_HOT_PEPPER_EDIT_CMD=0x02;
	public static final byte EXTEND_SHAPE_HOT_PEPPER_BUTTON_CMD=0x03;



	/**
	 * 层视切换
	 */

	public static final byte LAYER_VIEW_CHANGE_CMD=(byte) 0xa1;
	public static final byte EXTEND_LAYER_VIEW_CHANGE_CMD=(byte) 0x01;
	public static final byte EXTEND_GROUP_VIEW_CHANGE_CMD=(byte) 0x02;

	/**
	 * 其他（屏语言切换，请求自检信息，请求开机时间、工作时间，上报报警信息）
	 */
	public static final byte OTHER_CMD=(byte) 0xA0;
	public static final byte EXTEND_OTHER_CMD_LANGUAGE=0x01;
	public static final byte EXTEND_OTHER_CMD_SELF_CHECK=0x02;
	public static final byte EXTEND_OTHER_CMD_WORK_INFO=0x03;
	public static final byte EXTEND_OTHER_CMD_ALERT_INFO=0x04;
	public static final byte EXTEND_OTHER_CMD_PAGE_ENTER_EXIT=0x05;



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
	 * 颜色比较类型（色差使用）
	 */
	public static final int COLOR_COMPARE_CLR_RED_ABOVE_GREEN=0; //红>绿
	public static final int COLOR_COMPARE_CLR_RED_ABOVE_BLUE=1; //红>蓝
	public static final int COLOR_COMPARE_CLR_GREEN_ABOVE_RED=2; //绿>红
	public static final int COLOR_COMPARE_CLR_GREEN_ABOVE_BLUE=3;  //绿>蓝
	public static final int COLOR_COMPARE_CLR_BLUE_ABOVE_RED=4;  //蓝>红
	public static final int COLOR_COMPARE_CLR_BLUE_ABOVE_GREEN=5;  //蓝>绿



    /**
     * 语言标识
     */
    public static final int COUNTRY_CHINESE        = 1;//中文
    public static final int COUNTRY_ENGLISH        = 2;//英文
    public static final int COUNTRY_CHINESE_TAIWAN = 3;//繁体中文
    public static final int COUNTRY_RUSSIAN        = 4;//俄文
    public static final int COUNTRY_TURKEY         = 5;//土耳其
    public static final int	COUNTRY_CAMBODIA       = 6;//柬埔寨
    public static final int COUNTRY_VIETNAM        = 7;//越南语
    public static final int COUNTRY_THAI           = 8;//泰语
    public static final int COUNTRY_SPAIN          = 9;//西班牙
    public static final int	COUNTRY_POLAND         = 10;//波兰
    public static final int COUNTRY_BURMA          = 11;//缅甸
    public static final int	COUNTRY_PERSIA         = 12;//波斯语
    public static final int COUNTRY_UKRAINE        = 13;//乌克兰语
    public static final int COUNTRY_ITALY          = 14;//意大利语
    public static final int	COUNTRY_GREEK          = 15;//希腊语
    public static final int	COUNTRY_UIGHUR         = 16;//维吾尔族语
    public static final int	COUNTRY_ARABIC         = 17;//阿拉伯语
    public static final int COUNTRY_KOREAN         = 18;//韩语


	/**
	 *  len:16   前面15个为点数 最后一个为比例100
	 *
	 *  0--15
	 */
	public static final int[] G_AreaMapMaxSize={1,9,25,49,81,121,169,225,289,323,357,391,425,459,493,100};



}
