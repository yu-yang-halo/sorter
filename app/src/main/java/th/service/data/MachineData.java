package th.service.data;

import com.yy.sorter.utils.StringUtils;

import th.service.core.TrafficManager;

public class MachineData {
	public static final int LEVEL_USER = 0;
	public static final int LEVEL_ENGINNER = 1;
	public static final int LEVEL_PRODUCTOR = 2;

	private byte valveState;         //阀状态
	private byte feederState;        //给料器
	private byte startState;         //启动状态
	private byte cleanState;         //清灰状态

	private byte machineType;        //机器类型
	private byte layerNumber;        //层数
	private byte chuteNumber;        //通道数
	private byte groupNumbers;        //分组数
	private byte useShape;           //形选
	private byte useIR;                     //是否使用红外    0：未使用
	private byte useSvm;                    //是否使用SVM     0：未使用
	private byte userColor;                 //是否使用色选算法
	private byte useHsv;                    //是否使用色度分选
	private byte useSensor;          //使用料位传器
	private byte userLevel;//用户权限 0:user 1:工程师 2:厂家
	private byte pixelType;//像素类型


	//方案参数
	private byte			sortModeBig;     //大模式
	private byte 			sortModeSmall;   //小模式
	private String          modeName="";   //当前方案名称            char modeName[100]



	private final int FIX_SIZE=18;//固定大小部分
	private final int OFFSET_BYTES = FIX_SIZE;
	private final int STRING_SIZE=100;
	private final int RESERVE_SIZE=100;

	private byte protocolVersionBig;
	private byte protocolVersionSmall;
	private byte protocolVersionType;//默认类型-一般机型 0



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


	private boolean check(byte[] contents){
		if(contents==null||contents.length != (FIX_SIZE+STRING_SIZE+RESERVE_SIZE)){
			TrafficManager.getInstance().showErrorMessage();
			return false;
		}
		return true;
	}

	public MachineData(byte[] contents) {
		if(check(contents)){
			byte[] buffer=new byte[STRING_SIZE];
			System.arraycopy(contents, OFFSET_BYTES, buffer, 0, STRING_SIZE);
			String name= StringUtils.convertByteArrayToString(buffer);
			this.modeName=name;
			this.valveState = contents[0];
			this.feederState = contents[1];
			this.startState = contents[2];
			this.cleanState  = contents[3];

			this.machineType= contents[4];
			this.layerNumber= contents[5];
			this.chuteNumber= contents[6];
			this.groupNumbers= contents[7];
			this.useShape= contents[8];
			this.useIR= contents[9];
			this.useSvm= contents[10];
			this.userColor= contents[11];
			this.useHsv= contents[12];
			this.useSensor= contents[13];
			this.userLevel= contents[14];
			this.pixelType= contents[15];


			this.sortModeBig= contents[16];
			this.sortModeSmall= contents[17];


		}

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
				", groupNumbers=" + groupNumbers +
				", useShape=" + useShape +
				", useIR=" + useIR +
				", useSvm=" + useSvm +
				", userColor=" + userColor +
				", useHsv=" + useHsv +
				", useSensor=" + useSensor +
				", sortModeBig=" + sortModeBig +
				", sortModeSmall=" + sortModeSmall +
				", modeName='" + modeName + '\'' +
				", FIX_SIZE=" + FIX_SIZE +
				", OFFSET_BYTES=" + OFFSET_BYTES +
				", STRING_SIZE=" + STRING_SIZE +
				", RESERVE_SIZE=" + RESERVE_SIZE +
				", protocolVersionBig=" + protocolVersionBig +
				", protocolVersionSmall=" + protocolVersionSmall +
				", protocolVersionType=" + protocolVersionType +
				'}';
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

	public byte getGroupNumbers() {
		return groupNumbers;
	}

	public void setGroupNumbers(byte groupNumbers) {
		this.groupNumbers = groupNumbers;
	}

	public byte getUseShape() {
		return useShape;
	}

	public void setUseShape(byte useShape) {
		this.useShape = useShape;
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

	public byte getUseHsv() {
		return useHsv;
	}

	public void setUseHsv(byte useHsv) {
		this.useHsv = useHsv;
	}

	public byte getUseSensor() {
		return useSensor;
	}

	public void setUseSensor(byte useSensor) {
		this.useSensor = useSensor;
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

	public void setModeName(String modeName) {
		this.modeName = modeName;
	}

	public byte getProtocolVersionType() {
		return protocolVersionType;
	}

	public void setProtocolVersionType(byte protocolVersionType) {
		this.protocolVersionType = protocolVersionType;
	}

	public byte getPixelType() {
		return pixelType;
	}

	public void setPixelType(byte pixelType) {
		this.pixelType = pixelType;
	}

	public byte getUserLevel() {
		return userLevel;
	}
}
