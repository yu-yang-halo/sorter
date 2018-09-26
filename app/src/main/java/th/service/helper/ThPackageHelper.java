package th.service.helper;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.StringUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.ThCameraPlusRet;
import th.service.data.ThConfig;
import th.service.data.ThDevice;
import th.service.data.ThFeeder;
import th.service.data.ThHsvInfo;
import th.service.data.ThHsvWave;
import th.service.data.ThLightRet;
import th.service.data.ThMode;
import th.service.data.ThSVersion;
import th.service.data.ThSense;
import th.service.data.ThShapeItem;
import th.service.data.ThSvmInfo;
import th.service.data.ThValveRateRet;
import th.service.data.ThWaveData;

/**
 * 注意包体的长度计算，同意TCP和UDP包体长度的计算
 * 总长度-包头长度==包体长度
 * TCP其实可以直接获取len包体长度
 */

public class ThPackageHelper {
	private static final int PACKET_HEADER_SIZE=ThPackage.PACKET_HEADER_SIZE;
	/**
	 * 解析UDP搜索到的设备
	 * @param retData
	 * @return
	 */
	public static ThDevice parseMyDevice(ThPackage retData){
		//茶叶机#SN0000

		if(retData!=null){
			byte[] contents=retData.getContents();
			if(retData.getType()==(byte)0x02&&contents!=null){

				String data= StringUtils.convertByteArrayToString(contents);

				String[] snAndName=data.split("#");

				if(snAndName!=null&&snAndName.length==2){

					ThDevice thDevice=new ThDevice(retData.getSenderIP(),snAndName[1], snAndName[0]);
					thDevice.setControlStatus(retData.getData1()[0]);//是否控制状态
					return thDevice;

				}


			}


		}
		return null;
	}

	static byte[] buffers;
	static int  buffersLength=0;
	static int  pos=0;

	public static ThConfig parseThConfig(ThPackage retData,int type){
		Log.v("ThConfig",retData+" "+type);
		if(type==0){
			buffersLength=0;
			pos=0;
			buffers=new byte[buffersLength];
			return null;
		}
		byte[] contents=retData.getContents();
		if(contents!=null){
			buffersLength+=contents.length;
			buffers=Arrays.copyOf(buffers,buffersLength);
			System.arraycopy(contents,0,buffers,pos,contents.length);
			pos=buffersLength;
		}
		if(type==2){
			String jsonStr=new String(buffers,Charset.defaultCharset());
			Gson gson=new Gson();
			ThConfig thConfig= null;
			try {
				thConfig = gson.fromJson(jsonStr,ThConfig.class);
			} catch (JsonSyntaxException e) {
				return null;
			}

			return thConfig;
		}

		return null;
	}

	private static ExecutorService executorService= Executors.newSingleThreadExecutor();

	public static void parseFileData(final ThPackage retData, final int type){
		if(type==0){
			buffersLength=0;
			pos=0;
			buffers=new byte[buffersLength];
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					FileManager.getInstance().saveApkFile(buffers,0);
				}
			});

		}else{
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					byte[] contents=retData.getContents();
					FileManager.getInstance().saveApkFile(contents,type);
				}
			});

		}
	}
	public static void parseLanData(ThPackage retData,int type,String fileName){
		if(type==0){
			buffersLength=0;
			pos=0;
			buffers=new byte[buffersLength];

			FileManager.getInstance().saveLanguageFile(buffers,0,fileName);
		}else{
			byte[] contents=retData.getContents();
			FileManager.getInstance().saveLanguageFile(contents,type,fileName);

		}
	}
	/**
	 * 解析机器信息数据
	 * @param retData
	 * @return
	 */
	public static MachineData parseMachineData(ThPackage retData){
		byte[] contents=retData.getContents();
		if(contents!=null){
			MachineData machineData=new MachineData(contents);

			machineData.setProtocolVersionBig(retData.getData1()[0]);
			machineData.setProtocolVersionSmall(retData.getData1()[1]);
			machineData.setProtocolVersionType(retData.getData1()[2]);//协议类型 0-普通机型


			ThLogger.debug("MachineData",machineData.toString());
			return machineData;
		}
		return null;
	}

	/**
	 * 解析方案
	 * @param retData
	 * @return
	 */
	public static List<ThMode> parseThModeList(ThPackage retData){

		List<ThMode> modeList=new ArrayList<>();

		byte bigIndex = retData.getData1()[0];
		byte currentSmallIndex = retData.getData1()[1];

		byte[] contents=retData.getContents();
		String devsStr=StringUtils.convertByteArrayToString(contents);
		String[] arr=devsStr.split("\\$");
		for (int i=0;i<arr.length;i++){
			String[] name_time_mode=arr[i].split("#");
			if(name_time_mode.length==3) {

				//   名称#时间#小模式索引

				byte smallIndex = (byte) ConvertUtils.toIntExt(name_time_mode[2],0);

				ThMode mode=new ThMode(bigIndex,smallIndex,name_time_mode[0],name_time_mode[1], (byte) 0);

				if(currentSmallIndex == smallIndex)
				{
					mode.setCurrentMode(true);
				}else {
					mode.setCurrentMode(false);
				}

				modeList.add(mode);
			}
		}

		return modeList;
	}
	/**
	 * 解析给料量
	 * @param retData
	 * @return
	 */
	public static ThFeeder parseThFeeder(ThPackage retData){
		byte[] contents=retData.getContents();
		if(contents!=null){
			ThFeeder thFeeder=new ThFeeder(contents);

			ThLogger.debug("thFeeder",thFeeder.toString());
			return thFeeder;
		}
		return null;
	}
	public static List<ThSense> parseThSenses(ThPackage retData)
	{
		List<ThSense> thSenseList=new ArrayList<>();
		int size=ThSense.TEXT_MAX_BYTES + 12;
		int totalSize = (retData.getLength()-PACKET_HEADER_SIZE);
		if(totalSize % size != 0)
		{
			ThLogger.debug("Error","协议格式有误");
			return null;
		}
		int count=totalSize/size;

		byte[] contents=retData.getContents();

		ThLogger.debug("算法数","count="+count);
		byte[] buffer=new byte[size];

		ThSense item0=ThSense.createThSense((byte) 0);
		item0.setView((byte) 0);
		item0.setViewType(ConstantValues.VIEW_TYPE_FRONT);
		item0.setName(FileManager.getInstance().getString(75));//75#前视
		thSenseList.add(item0);

		for(int i=0;i<count;i++){
			System.arraycopy(contents,i*size,buffer,0,size);

			ThSense thSense=new ThSense(buffer);
			thSense.setViewType(ConstantValues.VIEW_TYPE_ITEM);


			if(thSenseList.size()>=1)
			{

				int length=thSenseList.size();
				if(thSense.getView() == 1 && thSenseList.get(length-1).getView() == 0 )
				{

					ThSense item1=ThSense.createThSense((byte) 0);
					item1.setView((byte) 1);
					item1.setViewType(ConstantValues.VIEW_TYPE_REAR);
					item1.setName(FileManager.getInstance().getString(76));//76#后视
					thSenseList.add(item1);
				}

			}


			thSenseList.add(thSense);


		}



		if(retData.getData1()[2] == 0x01)
		{

			ThSense item2=ThSense.createThSense((byte) 0);
			item2.setViewType(ConstantValues.VIEW_TYPE_NONE);
			item2.setName("");
			thSenseList.add(item2);


			ThSense thSense=ThSense.createThSense(retData.getData1()[3]);
			thSense.setViewType(ConstantValues.VIEW_TYPE_ITEM);
			thSenseList.add(thSense);
		}

		return thSenseList;
	}
	public static List<ThSvmInfo> parseThSvmInfos(ThPackage retData)
	{
		List<ThSvmInfo> thSvmInfoList=new ArrayList<>();
		int totalSize = (retData.getLength()-PACKET_HEADER_SIZE);
		if(totalSize % ThSvmInfo.SIZE != 0)
		{
			ThLogger.debug("Error","协议格式有误");
			return null;
		}
		int count=totalSize/ThSvmInfo.SIZE;
		byte[] buffer=new byte[ThSvmInfo.SIZE];
		byte[] contents=retData.getContents();
		for(int i=0;i<count;i++)
		{
			System.arraycopy(contents,i*ThSvmInfo.SIZE,buffer,0,ThSvmInfo.SIZE);

			ThSvmInfo thSvmInfo = new ThSvmInfo(buffer);
			thSvmInfoList.add(thSvmInfo);
		}

		return thSvmInfoList;
	}

	public static List<ThHsvInfo> parseThHsvInfos(ThPackage retData)
	{
		List<ThHsvInfo> thHsvInfoList=new ArrayList<>();
		int totalSize = (retData.getLength()-PACKET_HEADER_SIZE);
		if(totalSize % ThHsvInfo.SIZE != 0)
		{
			ThLogger.debug("Error","协议格式有误");
			return null;
		}
		int count=totalSize/ThHsvInfo.SIZE;
		byte[] buffer=new byte[ThHsvInfo.SIZE];
		byte[] contents=retData.getContents();
		for(int i=0;i<count;i++)
		{
			System.arraycopy(contents,i*ThHsvInfo.SIZE,buffer,0,ThHsvInfo.SIZE);

			ThHsvInfo thHsvInfo = new ThHsvInfo(buffer);

			thHsvInfoList.add(thHsvInfo);
		}

		return thHsvInfoList;
	}

	public static ThHsvWave parseHSVWaveData(ThPackage retData){
		ThHsvWave thHSVWave=new ThHsvWave(retData.getContents());
		return thHSVWave;
	}
	/**
	 * 解析波形数据
	 * @param retData
	 */
	public static ThWaveData parseWaveData(ThPackage retData){

		ThWaveData ret=new ThWaveData(retData);
		return ret;
	}
	private static int binaryFindLocation(byte[] bytes,byte val){
		int pos=-1;
		for (int i=0;i<bytes.length;i++){
			if(val==bytes[i]){
				pos=i;
				break;
			}
		}
		return pos;
	}
	/**
	 * 解析软件版本数据
	 * @param retData
	 * @return
	 */
	public static ThSVersion parseThSVersion(ThPackage retData){
		int chuteNumber= AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getChuteNumber();
		ThSVersion sVersion=ThSVersion.getInstance();
		switch (retData.getExtendType()){
			case 0x01:
				int split='#';
				int pos=binaryFindLocation(retData.getContents(),(byte) split);
				if(pos>0&&pos<(retData.getLength()-PACKET_HEADER_SIZE)){
					byte[] str=new byte[pos];
					System.arraycopy(retData.getContents(),0,str,0,str.length);
					byte[] others=new byte[retData.getLength()-PACKET_HEADER_SIZE-pos-1];
					System.arraycopy(retData.getContents(),pos+1,others,0,others.length);
					ThSVersion.BaseVersion baseVersion=new ThSVersion.BaseVersion(StringUtils.convertByteArrayToString(str),others);
					sVersion.setBaseVersion(baseVersion);
				}
				break;
			case 0x02:
				List<ThSVersion.ColorVersion> colorVersionList=collectorColorVersion(retData,chuteNumber);
				sVersion.setColorVersions(colorVersionList);
				break;
			case 0x03:
				List<ThSVersion.CameraVersion> cameraVersionList=collectorCameraVersion(retData,chuteNumber);
				sVersion.setCameraVersions(cameraVersionList);
				break;
		}

		return sVersion;
	}


	/**
	 * 解析灯光返回数据
	 * @param retData
	 * @return
	 */
	public static ThLightRet parseThLightRet(ThPackage retData){
		byte[] contents=retData.getContents();
		if(contents!=null){
			int realLength=retData.getLength()-PACKET_HEADER_SIZE;
			byte[] buffer=new byte[realLength];
			System.arraycopy(contents,0,buffer,0,realLength);
			ThLightRet ret=new ThLightRet(buffer);
			ThLogger.debug("ThLightRet",ret.toString());
			return ret;
		}
		return null;
	}

	/**
	 * 解析相机增益返回数据
	 * @param retData
	 * @return
	 */
	public static ThCameraPlusRet parseThCameraPlusRet(ThPackage retData){
		byte[] contents=retData.getContents();
		if(contents!=null){
			int realLength=retData.getLength()-PACKET_HEADER_SIZE;
			byte[] buffer=new byte[realLength];
			System.arraycopy(contents,0,buffer,0,realLength);
			ThCameraPlusRet ret=new ThCameraPlusRet(buffer);
			ThLogger.debug("ThCameraPlusRet",ret.toString());
			return ret;
		}
		return null;
	}

	/**
	 * 解析阀频率
	 * @param retData
	 * @return
	 */
	public static ThValveRateRet parseThValveRateRet(ThPackage retData){
		byte[] contents=retData.getContents();
		if(contents!=null){
			int valveNum=retData.getData1()[0];
			boolean onlyFrontView=retData.getData1()[1]==1;

			ThValveRateRet valveRateRet=new ThValveRateRet(valveNum,onlyFrontView,contents);

			return valveRateRet;
		}

		return null;
	}
	/**
	 *  解析形选
	 */
	public static List<ThShapeItem> parseThShapeItemList(ThPackage retData)
	{
		byte[] contents=retData.getContents();
		List<ThShapeItem> thShapeItemList = new ArrayList<>();
		if(contents!=null)
		{
			int size = retData.getData1()[1];
			if(size <= 0)
			{
				return thShapeItemList;
			}
			int pos = 0;

			while (pos != contents.length)
			{
				if(pos+54<contents.length)
				{
					int miniCount = contents[pos+54];
					int thShapeItemSize = miniCount*ThShapeItem.MiniItem.SIZE + ThShapeItem.MIN_SIZE;

					if(thShapeItemSize + pos > contents.length)
					{
						break;
					}
					byte[] buffer = new byte[thShapeItemSize];


					System.arraycopy(contents,pos,buffer,0,buffer.length);

					ThShapeItem thShapeItem = new ThShapeItem(buffer);
					thShapeItemList.add(thShapeItem);

					pos+=thShapeItemSize;
				}
			}
			System.out.println("pos == "+pos+" size == "+size+" contents.length="+contents.length);
		}

		return thShapeItemList;
	}

	/**
	 * 搜集软件版本中色选板版本数据
	 * @param retData
	 * @param chuteNumber
	 * @return
	 */

	public static  List<ThSVersion.ColorVersion> collectorColorVersion(ThPackage retData,int chuteNumber){
		int COLOR_SIZE=7;
		List<ThSVersion.ColorVersion> colorVersionList=new ArrayList<>();
		int len=retData.getLength()-PACKET_HEADER_SIZE;
		if(len%COLOR_SIZE!=0){
			throw  new IllegalStateException("len"+len+"%"+COLOR_SIZE+"!=0");
		}
		byte[] ch0=new byte[COLOR_SIZE];
		int getSize=len/COLOR_SIZE;
		int diff=getSize-1;
		System.arraycopy(retData.getContents(),0,ch0,0,ch0.length);
		/**
		 * 默认初始化第一项
		 */
		for (int i=0;i<chuteNumber;i++){
			ch0[0]=(byte) i;
			ThSVersion.ColorVersion colorVersion=new ThSVersion.ColorVersion(ch0);
			colorVersionList.add(colorVersion);
		}

		for (int m=0;m<diff;m++){
			byte[] ch1=new byte[COLOR_SIZE];
			System.arraycopy(retData.getContents(),COLOR_SIZE*(m+1),ch1,0,ch1.length);
			ThSVersion.ColorVersion colorVersion=new ThSVersion.ColorVersion(ch1);
			colorVersionList.set(ch1[0],colorVersion);
		}

		return colorVersionList;
	}


	/**
	 * 搜集软件版本中相机版本数据
	 * @param retData
	 * @param chuteNumber
	 * @return
	 */
	public static  List<ThSVersion.CameraVersion> collectorCameraVersion(ThPackage retData,int chuteNumber){
		int CAMERA_SIZE=9;
		List<ThSVersion.CameraVersion> cameraVersionList=new ArrayList<>();
		int len=retData.getLength()-PACKET_HEADER_SIZE;
		if(len%CAMERA_SIZE!=0){
			throw  new IllegalStateException("len"+len+"%"+CAMERA_SIZE+"!=0");
		}
		byte[] ch0=new byte[CAMERA_SIZE];
		int getSize=len/CAMERA_SIZE;
		int diff=getSize-1;
		System.arraycopy(retData.getContents(),0,ch0,0,ch0.length);

		/**
		 * 默认初始化第一项
		 */
		for (int i=0;i<chuteNumber;i++){
			ch0[0]=(byte) i;
			ThSVersion.CameraVersion cameraVersion=new ThSVersion.CameraVersion(ch0,retData.getData1()[1]);
			cameraVersionList.add(cameraVersion);
		}

		for (int m=0;m<diff;m++){
			byte[] ch1=new byte[CAMERA_SIZE];
			System.arraycopy(retData.getContents(),CAMERA_SIZE*(m+1),ch1,0,ch1.length);
			ThSVersion.CameraVersion cameraVersion=new ThSVersion.CameraVersion(ch1,retData.getData1()[1]);
			cameraVersionList.set(ch1[0],cameraVersion);
		}

		return cameraVersionList;
	}

	public static int getHashId(byte view,byte type,byte subType,byte extType)
	{
		int h = 0;
		h = 31 * h + view;
		h = 31 * h + type;
		h = 31 * h + subType;
		h = 31 * h + extType;
		return h;
	}
}
