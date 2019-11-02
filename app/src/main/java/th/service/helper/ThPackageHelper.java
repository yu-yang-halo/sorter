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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.YYCameraPlusRet;
import th.service.data.YYConfig;
import th.service.data.YYDevice;
import th.service.data.YYFeeder;
import th.service.data.YYHsvInfo;
import th.service.data.YYHsvWave;
import th.service.data.YYLightRet;
import th.service.data.YYMode;
import th.service.data.YYRelate;
import th.service.data.YYSVersion;
import th.service.data.YYSense;
import th.service.data.YYShape;
import th.service.data.YYShapeItem;
import th.service.data.YYSvmInfo;
import th.service.data.YYValveRateRet;
import th.service.data.YYWaveData;
import th.service.data.YYWorkInfo;

/**
 * Created by YUYANG on 2018/11/6.
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
	public static YYDevice parseMyDevice(ThPackage retData){
		//茶叶机#SN0000

		if(retData!=null){
			byte[] contents=retData.getContents();
			if(retData.getType()==(byte)0x02&&contents!=null){

				String data= StringUtils.convertByteArrayToString(contents);

				String[] snAndName=data.split("#");

				if(snAndName!=null&&snAndName.length==2){

					YYDevice thDevice=new YYDevice(retData.getSenderIP(),snAndName[1], snAndName[0]);
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

	public static YYConfig parseThConfig(ThPackage retData, int type){
		Log.v("YYConfig",retData+" "+type);
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
			YYConfig thConfig= null;
			try {
				thConfig = gson.fromJson(jsonStr, YYConfig.class);
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
	public static List<YYMode> parseThModeList(ThPackage retData){

		List<YYMode> modeList=new ArrayList<>();

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

				YYMode mode=new YYMode(bigIndex,smallIndex,name_time_mode[0],name_time_mode[1], (byte) 0);

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
	public static YYFeeder parseThFeeder(ThPackage retData){
		byte[] contents=retData.getContents();
		if(contents!=null){
			YYFeeder thFeeder=new YYFeeder(contents);

			ThLogger.debug("thFeeder",thFeeder.toString());
			return thFeeder;
		}
		return null;
	}
	public static List<YYSense> parseThSenses(ThPackage retData)
	{
		List<YYSense> thSenseList=new ArrayList<>();
		int size= YYSense.TEXT_MAX_BYTES + 12;
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



		for(int i=0;i<count;i++){
			System.arraycopy(contents,i*size,buffer,0,size);

			YYSense thSense=new YYSense(buffer);
			thSense.setViewType(ConstantValues.VIEW_TYPE_ITEM);


			if(i == 0 && thSense.getView() == 0)
			{
				YYSense item0= YYSense.createThSenseNone((byte) 0);
				item0.setViewType(ConstantValues.VIEW_TYPE_FRONT);
				item0.setName(FileManager.getInstance().getString(75));//75#前视
				thSenseList.add(item0);

			}


			if(thSenseList.size()>=1)
			{

				int length=thSenseList.size();
				if(thSense.getView() == 1 && thSenseList.get(length-1).getView() == 0 )
				{

					YYSense item1= YYSense.createThSenseNone((byte) 1);
					item1.setViewType(ConstantValues.VIEW_TYPE_REAR);
					item1.setName(FileManager.getInstance().getString(76));//76#后视
					thSenseList.add(item1);
				}

			}else
            {
                YYSense item1= YYSense.createThSenseNone((byte) 1);
                item1.setViewType(ConstantValues.VIEW_TYPE_REAR);
                item1.setName(FileManager.getInstance().getString(76));//76#后视
                thSenseList.add(item1);
            }


			thSenseList.add(thSense);


		}



		if(retData.getData1()[2] == 0x01)
		{

			YYSense item2= YYSense.createThSenseNone((byte) 2);
			item2.setViewType(ConstantValues.VIEW_TYPE_NONE);
			thSenseList.add(item2);


			YYSense thSense= YYSense.createThSense(retData.getData1()[3]);
			thSense.setViewType(ConstantValues.VIEW_TYPE_ITEM);
			thSenseList.add(thSense);
		}

		return thSenseList;
	}
	public static List<YYSvmInfo> parseThSvmInfos(ThPackage retData)
	{
		List<YYSvmInfo> thSvmInfoList=new ArrayList<>();
		int totalSize = (retData.getLength()-PACKET_HEADER_SIZE);
		if(totalSize % YYSvmInfo.SIZE != 0)
		{
			ThLogger.debug("Error","协议格式有误");
			return null;
		}
		int count=totalSize/ YYSvmInfo.SIZE;
		byte[] buffer=new byte[YYSvmInfo.SIZE];
		byte[] contents=retData.getContents();
		for(int i=0;i<count;i++)
		{
			System.arraycopy(contents,i* YYSvmInfo.SIZE,buffer,0, YYSvmInfo.SIZE);

			YYSvmInfo thSvmInfo = new YYSvmInfo(buffer);
			thSvmInfoList.add(thSvmInfo);
		}

		return thSvmInfoList;
	}

	public static List<YYHsvInfo> parseThHsvInfos(ThPackage retData)
	{
		List<YYHsvInfo> thHsvInfoList=new ArrayList<>();
		int totalSize = (retData.getLength()-PACKET_HEADER_SIZE);
		if(totalSize % YYHsvInfo.SIZE != 0)
		{
			ThLogger.debug("Error","协议格式有误");
			return null;
		}
		int count=totalSize/ YYHsvInfo.SIZE;
		byte[] buffer=new byte[YYHsvInfo.SIZE];
		byte[] contents=retData.getContents();
		for(int i=0;i<count;i++)
		{
			System.arraycopy(contents,i* YYHsvInfo.SIZE,buffer,0, YYHsvInfo.SIZE);

			YYHsvInfo thHsvInfo = new YYHsvInfo(buffer);

			thHsvInfoList.add(thHsvInfo);
		}

		return thHsvInfoList;
	}

	public static YYHsvWave parseHSVWaveData(ThPackage retData){
		YYHsvWave thHSVWave=new YYHsvWave(retData.getContents());
		return thHSVWave;
	}
	/**
	 * 解析波形数据
	 * @param retData
	 */
	public static YYWaveData parseWaveData(ThPackage retData){

		YYWaveData ret=new YYWaveData(retData);
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
	public static YYSVersion parseThSVersion(ThPackage retData){
		int chuteNumber= AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getChuteNumber();
		YYSVersion sVersion= YYSVersion.getInstance();
		switch (retData.getExtendType()){
			case 0x01:
				int split='#';
				int pos=binaryFindLocation(retData.getContents(),(byte) split);
				if(pos>0&&pos<(retData.getLength()-PACKET_HEADER_SIZE)){
					byte[] str=new byte[pos];
					System.arraycopy(retData.getContents(),0,str,0,str.length);
					byte[] others=new byte[retData.getLength()-PACKET_HEADER_SIZE-pos-1];
					System.arraycopy(retData.getContents(),pos+1,others,0,others.length);
					YYSVersion.BaseVersion baseVersion=new YYSVersion.BaseVersion(StringUtils.convertByteArrayToString(str),others);
					sVersion.setBaseVersion(baseVersion);
				}
				break;
			case 0x02:
				List<YYSVersion.ColorVersion> colorVersionList=collectorColorVersion(retData,chuteNumber);
				sVersion.setColorVersions(colorVersionList);
				break;
			case 0x03:
				List<YYSVersion.CameraVersion> cameraVersionList=collectorCameraVersion(retData,chuteNumber);
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
	public static YYLightRet parseThLightRet(ThPackage retData){
		byte[] contents=retData.getContents();
		if(contents!=null){
			int realLength=retData.getLength()-PACKET_HEADER_SIZE;
			byte[] buffer=new byte[realLength];
			System.arraycopy(contents,0,buffer,0,realLength);
			YYLightRet ret=new YYLightRet(buffer);
			ThLogger.debug("YYLightRet",ret.toString());
			return ret;
		}
		return null;
	}

	/**
	 * 解析相机增益返回数据
	 * @param retData
	 * @return
	 */
	public static YYCameraPlusRet parseThCameraPlusRet(ThPackage retData){
		byte[] contents=retData.getContents();
		if(contents!=null){
			int realLength=retData.getLength()-PACKET_HEADER_SIZE;
			byte[] buffer=new byte[realLength];
			System.arraycopy(contents,0,buffer,0,realLength);
			YYCameraPlusRet ret=new YYCameraPlusRet(buffer);
			ThLogger.debug("YYCameraPlusRet",ret.toString());
			return ret;
		}
		return null;
	}
	public static YYWorkInfo parseThWorkInfo(ThPackage retData)
	{
		byte[] contents=retData.getContents();
		if(contents!=null){
			int realLength=retData.getLength()-PACKET_HEADER_SIZE;
			byte[] buffer=new byte[realLength];
			System.arraycopy(contents,0,buffer,0,realLength);
			YYWorkInfo ret=new YYWorkInfo(buffer);
			ThLogger.debug("YYWorkInfo",ret.toString());
			return ret;
		}
		return null;
	}

	/**
	 * 解析阀频率
	 * @param retData
	 * @return
	 */
	public static YYValveRateRet parseThValveRateRet(ThPackage retData){
		byte[] contents=retData.getContents();
		if(contents!=null){
			int valveNum=retData.getData1()[0];
			boolean onlyFrontView=retData.getData1()[1]==1;

			YYValveRateRet valveRateRet=new YYValveRateRet(valveNum,onlyFrontView,contents);

			return valveRateRet;
		}

		return null;
	}
	public static List<YYRelate> parseThRelateList(ThPackage retData)
	{
		List<YYRelate> thRelateList=new ArrayList<>();
		int totalSize = (retData.getLength()-PACKET_HEADER_SIZE);
		if(totalSize % YYRelate.SIZE != 0)
		{
			ThLogger.debug("Error","协议格式有误");
			return null;
		}
		int count=totalSize/ YYRelate.SIZE;
		byte[] buffer=new byte[YYRelate.SIZE];
		byte[] contents=retData.getContents();
		for(int i=0;i<count;i++)
		{
			System.arraycopy(contents,i* YYRelate.SIZE,buffer,0, YYRelate.SIZE);

			YYRelate thRelate = new YYRelate(buffer);
			thRelateList.add(thRelate);
		}
		return thRelateList;
	}
	/**
	 *  解析形选
	 */
	public static List<YYShape> parseThShapeList(ThPackage retData)
	{
		byte[] contents=retData.getContents();
		List<YYShape> thShapeList = new ArrayList<>();
		if(contents!=null) {
			int size = retData.getData1()[1];
			if (size <= 0) {
				return thShapeList;
			}

			int pos = 0;
			for(int i=0;i<size;i++)
			{

				if(pos+1 >= contents.length)
				{
					break;
				}

				int shapeItemCount = ConvertUtils.unsignByteToInt(contents[pos + 1]);
				int sum = 0;
				for(int j=0;j<shapeItemCount;j++)
				{
					int miniCountIndex = pos+ YYShape.MIN_SIZE + YYShapeItem.MIN_SIZE -1 +sum;
					if(miniCountIndex >= contents.length)
					{
						break;
					}
					int miniItemCount = ConvertUtils.unsignByteToInt(contents[miniCountIndex]);
					sum += YYShapeItem.MIN_SIZE
							+ miniItemCount* YYShapeItem.MiniItem.SIZE;
				}
				int shapeSize = sum + YYShape.MIN_SIZE;
				byte[] buffer = new byte[shapeSize];

				if(pos+shapeSize > contents.length)
				{
					break;
				}
				System.arraycopy(contents,pos,buffer,0,shapeSize);

				YYShape thShape = new YYShape(buffer);

				thShapeList.add(thShape);

				pos += shapeSize;
			}


		}

		return thShapeList;
	}

	/**
	 * 搜集软件版本中色选板版本数据
	 * @param retData
	 * @param chuteNumber
	 * @return
	 */

	public static  List<YYSVersion.ColorVersion> collectorColorVersion(ThPackage retData, int chuteNumber){
		int COLOR_SIZE=7;
		List<YYSVersion.ColorVersion> colorVersionList=new ArrayList<>();
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
			YYSVersion.ColorVersion colorVersion=new YYSVersion.ColorVersion(ch0);
			colorVersionList.add(colorVersion);
		}

		for (int m=0;m<diff;m++){
			byte[] ch1=new byte[COLOR_SIZE];
			System.arraycopy(retData.getContents(),COLOR_SIZE*(m+1),ch1,0,ch1.length);
			YYSVersion.ColorVersion colorVersion=new YYSVersion.ColorVersion(ch1);
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
	public static  List<YYSVersion.CameraVersion> collectorCameraVersion(ThPackage retData, int chuteNumber){
		int CAMERA_SIZE=9;
		List<YYSVersion.CameraVersion> cameraVersionList=new ArrayList<>();
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
			YYSVersion.CameraVersion cameraVersion=new YYSVersion.CameraVersion(ch0,retData.getData1()[1]);
			cameraVersionList.add(cameraVersion);
		}

		for (int m=0;m<diff;m++){
			byte[] ch1=new byte[CAMERA_SIZE];
			System.arraycopy(retData.getContents(),CAMERA_SIZE*(m+1),ch1,0,ch1.length);
			YYSVersion.CameraVersion cameraVersion=new YYSVersion.CameraVersion(ch1,retData.getData1()[1]);
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
