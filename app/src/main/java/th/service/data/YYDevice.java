package th.service.data;
/**
 *
 * Created by YUYANG on 2018/11/6.
 *  定义设备列表项中的数据信息：：：：
 *  设备局域网ip地址
 *  设备名称
 *  设备编号
 *
 *  当前层
 *  当前视
 */
public class YYDevice {
	/**
	 * 默认设备不为空
	 */
	private boolean deviceIsNull=false;
    public YYDevice(){
		
	}
	public YYDevice(String localIp, String deviceName, String deviceSN) {
		super();
		this.localIp = localIp;
		this.deviceName = deviceName;
		this.deviceSN = deviceSN;
	}
	
	@Override
	public String toString() {
		return "YYDevice [localIp=" + localIp + ", deviceName=" + deviceName + ", deviceSN=" + deviceSN + "]";
	}

	public boolean isDeviceIsNull() {
		return deviceIsNull;
	}

	public void setDeviceIsNull(boolean deviceIsNull) {
		this.deviceIsNull = deviceIsNull;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceSN == null) ? 0 : deviceSN.hashCode());
		return result;
	}

	public String getIPNumber(){
		if(localIp==null){
			return "";
		}
		String[] values=localIp.split("\\.");

		if(values.length==4){
			return "("+values[3]+")";
		}
		return "";
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		YYDevice other = (YYDevice) obj;
		if (deviceSN == null) {
			if (other.deviceSN != null)
				return false;
		} else if (!deviceSN.equals(other.deviceSN))
			return false;
		if (localIp == null) {
			if (other.localIp != null)
				return false;
		} else if (!localIp.equals(other.localIp))
			return false;
		return true;
	}




	private String localIp;            //设备局域网dip地址
	private String deviceName;         //设备名称
	private String deviceSN;           //设备编号
	private MachineData machineData;   //设备状态数据
	private byte controlStatus;//0 没有被控制 1 已被控制


	private byte currentLayer=0; //当前层  默认0层  从0开始
	private byte currentView=0;  //当前视  前视
	private byte currentGroup=0;//当前组
	private byte currentAlgorithm=-1;  //当前算法
	private byte currentEnable=-1;  //当前算法使能
	private int currentType=0;  //当前类型 0.色选 1.红外

	private byte lookSelectSmallMode;
	private byte lookSelectBigMode;
	private boolean disableChange;



	public byte getCurrentGroup() {
		return currentGroup;
	}

	public void setCurrentGroup(byte currentGroup) {
		this.currentGroup = currentGroup;
	}

	public int getCurrentType() {
		return currentType;
	}

	public void setCurrentType(int currentType) {
		this.currentType = currentType;
	}

	public byte getLookSelectSmallMode() {
		return lookSelectSmallMode;
	}

	public void setLookSelectSmallMode(byte lookSelectSmallMode) {
		this.lookSelectSmallMode = lookSelectSmallMode;
	}

	public boolean isDisableChange() {
		return disableChange;
	}

	public void setDisableChange(boolean disableChange) {
		this.disableChange = disableChange;
	}

	public byte getLookSelectBigMode() {
		return lookSelectBigMode;
	}

	public void setLookSelectBigMode(byte lookSelectBigMode) {
		this.lookSelectBigMode = lookSelectBigMode;
	}

	public byte getCurrentEnable() {
		return currentEnable;
	}
	public void setCurrentEnable(byte currentEnable) {
		this.currentEnable = currentEnable;
	}

	public byte getCurrentAlgorithm() {
		return currentAlgorithm;
	}

	public void setCurrentAlgorithm(byte currentAlgorithm) {
		this.currentAlgorithm = currentAlgorithm;
	}

	public byte getCurrentLayer() {
		return currentLayer;
	}

	public void setCurrentLayer(byte currentLayer) {
		this.currentLayer = currentLayer;
	}

	public byte getCurrentView() {
		return currentView;
	}

	public void setCurrentView(byte currentView) {
		this.currentView = currentView;
	}

	public MachineData getMachineData() {
		if(machineData==null){
			return new MachineData(null);
		}
		return machineData;
	}

	public void setMachineData(MachineData machineData) {
		this.machineData = machineData;
	}

	public String getLocalIp() {
		return localIp;
	}
	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceSN() {
		return deviceSN;
	}
	public void setDeviceSN(String deviceSN) {
		this.deviceSN = deviceSN;
	}

	public byte getControlStatus() {
		return controlStatus;
	}

	public void setControlStatus(byte controlStatus) {
		this.controlStatus = controlStatus;
	}

}
