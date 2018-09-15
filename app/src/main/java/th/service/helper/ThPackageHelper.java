package th.service.helper;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yy.sorter.manager.FileManager;
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
import th.service.data.ThConfig;
import th.service.data.ThDevice;
import th.service.data.ThFeeder;
import th.service.data.ThMode;

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
			if(name_time_mode.length==4) {

				//   名称#时间#小模式索引#方案flag

				byte smallIndex = (byte) ConvertUtils.toIntExt(name_time_mode[2],0);

				byte flag  = (byte) ConvertUtils.toIntExt(name_time_mode[3],0);;

				ThMode mode=new ThMode(bigIndex,smallIndex,name_time_mode[0],name_time_mode[1],flag);


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
