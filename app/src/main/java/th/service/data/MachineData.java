package th.service.data;

import com.yy.sorter.utils.StringUtils;
import java.util.Arrays;

import th.service.core.TrafficManager;

public class MachineData {
	private byte valveState;         //阀状态
	private byte feederState;        //给料器
	private byte startState;         //启动状态
	private byte cleanState;         //清灰状态

	/**
	 * 新增几种数据
	 */
	private byte machineType;        //机器类型
	private byte layerNumber;        //层数
	private byte chuteNumber;        //通道数

	private byte  shapeType;                 //型选类型       0：未使用
	private byte  useIR;                     //是否使用红外    0：未使用
	private byte  useSvm;                    //是否使用SVM     0：未使用
	private byte  userColor;                 //是否使用色选算法
	private byte hasRearView[];  //6 bytes;


	private byte fristVib;	     //第一通道给料量

	private byte vibIn;              //进料开关状态 //>1,不显示
	private byte vibOut;             //出料开关状态 //>1,不显示
	private byte useSensor;          //使用料位传器
	private byte wheel[] ;          //履带开关   3层，目前只用二层[ 0：关 1：表示开] 3bytes

	//方案参数
	private byte			sortModeBig;     //大模式
	private byte 			sortModeSmall;   //小模式
	private String          modeName="";   //当前方案名称            char modeName[100]
	private byte            useHsv;                    //是否使用色度分选
	private byte hasUseIR[];  //6 bytes; 红外分层

	private String     schemeFullName;
	private final int OFFSET_BYTES=26;//偏移量26
	private final int BYTES_SIZE=27;
	private final int STRING_SIZE=100;

	private byte protocolVersionBig;
	private byte protocolVersionSmall;
	private byte protocolVersionType;//默认类型-一般机型 0   特殊机型-大米机型 1

	private int  groupInfo;//组信息bit解析  2bytes  可以知道 通道数 组数  分组索引
	private int  groupNumbers;//组数
	private int groupChutes;//通道数
	private int[] groupIndexs=new int[16];//组索引
	private boolean isCompositeInfrared = false;

	public boolean isCompositeInfrared() {
		return isCompositeInfrared;
	}

	public byte getProtocolVersionBig() {
		return protocolVersionBig;
	}

	public void setProtocolVersionBig(byte protocolVersionBig) {
		this.protocolVersionBig = protocolVersionBig;
	}

	public byte getProtocolVersionSmall() {
		return protocolVersionSmall;
	}

	public void setProtocolVersionSmall(byte protocolVersionSmall) {
		this.protocolVersionSmall = protocolVersionSmall;
	}

	/**
	 * 是否是履带机
	 * @return
     */
	public boolean isWheelMachine(){
		if(machineType==1||machineType==3){
			return true;
		}
		return false;
	}

	private boolean check(byte[] contents){
		if(contents==null||contents.length<(OFFSET_BYTES+STRING_SIZE)){
			TrafficManager.getInstance().showErrorMessage();
			return false;
		}
		return true;
	}

	public MachineData(byte[] contents) {
		this.hasRearView=new byte[6];
		this.wheel=new byte[3];
		this.hasUseIR=new byte[6];
		if(check(contents)){
			byte[] buffer=new byte[STRING_SIZE];
			System.arraycopy(contents, OFFSET_BYTES, buffer, 0, STRING_SIZE);
			String name= StringUtils.convertByteArrayToString(buffer);
			this.modeName=name;
			this.valveState=contents[0];
			this.feederState=contents[1];
			this.startState=contents[2];
			this.cleanState=contents[3];
			this.machineType=contents[4];
			this.layerNumber=contents[5];
			this.chuteNumber=contents[6];
			this.shapeType=contents[7];
			this.useIR=contents[8];
			this.useSvm=contents[9];
			this.userColor=contents[10];
			System.arraycopy(contents,11,hasRearView,0,6);
			this.fristVib=contents[17];
			this.vibIn=contents[18];
			this.vibOut=contents[19];
			this.useSensor=contents[20];
			System.arraycopy(contents,21,wheel,0,3);
			this.sortModeBig=contents[24];
			this.sortModeSmall=contents[25];
			if(contents.length>(OFFSET_BYTES+STRING_SIZE)){
				this.useHsv=contents[OFFSET_BYTES+STRING_SIZE];

				if(contents.length == (OFFSET_BYTES+STRING_SIZE+7))
				{
					System.arraycopy(contents,OFFSET_BYTES+STRING_SIZE+1,hasUseIR,0,6);
				}

			}

//			if(this.useIR == 12)//复合红外
//			{
//				isCompositeInfrared = true;
//				useIR = 3;
//			}else
//			{
//				isCompositeInfrared = false;
//			}
		}

	}

	public byte[] getWheel() {
		return wheel;
	}

	public void setWheel(byte[] wheel) {
		this.wheel = wheel;
	}

	public byte getValveState() {
		return valveState;
	}
	public void setValveState(byte valveState) {
		this.valveState = valveState;
	}
	public byte getFeederState() {
		return feederState;
	}
	public void setFeederState(byte feederState) {
		this.feederState = feederState;
	}
	public byte getStartState() {
		return startState;
	}
	public void setStartState(byte startState) {
		this.startState = startState;
	}
	public byte getCleanState() {
		return cleanState;
	}
	public void setCleanState(byte cleanState) {
		this.cleanState = cleanState;
	}
	public byte getFristVib() {
		return fristVib;
	}
	public void setFristVib(byte fristVib) {
		this.fristVib = fristVib;
	}
	public byte getSortModeBig() {
		return sortModeBig;
	}
	public void setSortModeBig(byte sortModeBig) {
		this.sortModeBig = sortModeBig;
	}
	public byte getSortModeSmall() {
		return sortModeSmall;
	}
	public void setSortModeSmall(byte sortModeSmall) {
		this.sortModeSmall = sortModeSmall;
	}
	public String getModeName() {
		return modeName;
	}

	public String getSchemeFullName() {

		return modeName+"["+(sortModeBig+1)+"-"+sortModeSmall+"]";
	}

	public void setModeName(String modeName) {
		this.modeName = modeName;
	}

	public byte getMachineType() {
		return machineType;
	}

	public void setMachineType(byte machineType) {
		this.machineType = machineType;
	}

	public byte getLayerNumber() {
		return layerNumber;
	}

	public void setLayerNumber(byte layerNumber) {
		this.layerNumber = layerNumber;
	}

	public byte getChuteNumber() {
		return chuteNumber;
	}

	public void setChuteNumber(byte chuteNumber) {
		this.chuteNumber = chuteNumber;
	}

	public byte getShapeType() {
		return shapeType;
	}

	public void setShapeType(byte shapeType) {
		this.shapeType = shapeType;
	}

	public byte getUseIR() {
		return useIR;
	}

	public void setUseIR(byte useIR) {
		this.useIR = useIR;
	}

	public byte getUseSvm() {
		return useSvm;
	}

	public void setUseSvm(byte useSvm) {
		this.useSvm = useSvm;
	}

	public byte getUserColor() {
		return userColor;
	}

	public void setUserColor(byte userColor) {
		this.userColor = userColor;
	}

	public byte[] getHasRearView() {
		return hasRearView;
	}

	public void setHasRearView(byte[] hasRearView) {
		this.hasRearView = hasRearView;
	}

	public byte getVibIn() {
		return vibIn;
	}

	public void setVibIn(byte vibIn) {
		this.vibIn = vibIn;
	}

	public byte getVibOut() {
		return vibOut;
	}

	public void setVibOut(byte vibOut) {
		this.vibOut = vibOut;
	}

	public byte getUseSensor() {
		return useSensor;
	}

	public void setUseSensor(byte useSensor) {
		this.useSensor = useSensor;
	}

	public byte getUseHsv() {
		return useHsv;
	}

	public void setUseHsv(byte useHsv) {
		this.useHsv = useHsv;
	}

	public byte getProtocolVersionType() {
		return protocolVersionType;
	}

	public void setProtocolVersionType(byte protocolVersionType) {
		this.protocolVersionType = protocolVersionType;
	}

	public void setGroupInfo(int groupInfo) {
		this.groupInfo = groupInfo;
		/**
		 * 算法说明：
		 *     groupInfo
		 *                  98  7654 3210     index
		 * ｛        0000 0001  0010 0010 ｝   2Bytes
		 *
		 *  例子中：组数=1的个数=3组
		 *        通道数=最后一个1的索引值+1=8+1=9通道
		 *        分组详情=[1,5,8]
		 *        分组详情=[0,0,1,1,1,1,2,2,2]
		 *
		 */
		groupNumbers=0;
		for(int i=0;i<16;i++){
			int bit=(groupInfo>>i)&0x01;
			groupIndexs[i]=groupNumbers;
			if(bit==1){
				groupChutes=(i+1);
				groupNumbers++;
			}
		}

		if(groupNumbers<1||groupNumbers>4){
			//组数必须在[1-4]范围内
		}
	}

	public int getGroupNumbers(){
		return groupNumbers;
	}
	public void setGroupNumbers(int groupNums){
		groupNumbers = groupNums;
	}
	public int getGroupChutes(){
		return groupChutes;
	}
	public int[] getGroupIndexs(){
		return groupIndexs;
	}

	@Override
	public String toString() {
		return "MachineData{" +
				"valveState=" + valveState +
				", feederState=" + feederState +
				", startState=" + startState +
				", cleanState=" + cleanState +
				", machineType=" + machineType +
				", layerNumber=" + layerNumber +
				", chuteNumber=" + chuteNumber +
				", shapeType=" + shapeType +
				", useIR=" + useIR +
				", useSvm=" + useSvm +
				", userColor=" + userColor +
				", hasRearView=" + Arrays.toString(hasRearView) +
				", fristVib=" + fristVib +
				", vibIn=" + vibIn +
				", vibOut=" + vibOut +
				", useSensor=" + useSensor +
				", wheel=" + Arrays.toString(wheel) +
				", sortModeBig=" + sortModeBig +
				", sortModeSmall=" + sortModeSmall +
				", modeName='" + modeName + '\'' +
				", useHsv=" + useHsv +
				", schemeFullName='" + schemeFullName + '\'' +
				", OFFSET_BYTES=" + OFFSET_BYTES +
				", BYTES_SIZE=" + BYTES_SIZE +
				", STRING_SIZE=" + STRING_SIZE +
				", protocolVersionBig=" + protocolVersionBig +
				", protocolVersionSmall=" + protocolVersionSmall +
				", protocolVersionType=" + protocolVersionType +
				'}';
	}

	public byte[] getHasUseIR() {
		return hasUseIR;
	}
}
