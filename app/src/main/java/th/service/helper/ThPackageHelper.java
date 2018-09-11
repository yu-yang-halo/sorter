package th.service.helper;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.utils.ConvertUtils;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import th.service.data.MachineData;
import th.service.data.ThConfig;

/**
 * 注意包体的长度计算，同意TCP和UDP包体长度的计算
 * 总长度-包头长度==包体长度
 * TCP其实可以直接获取len包体长度
 */

public class ThPackageHelper {
	private static final int PACKET_HEADER_SIZE=ThPackage.PACKET_HEADER_SIZE;
	

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
			machineData.setProtocolVersionType(retData.getData1()[2]);//协议类型 0-普通机型 1-大米机型 2-1R大米机


			if(machineData.getProtocolVersionType() == 2){
				machineData.setGroupNumbers(retData.getData1()[3]);
			}else if(machineData.getProtocolVersionType() == 1){
				int groupInfo= ConvertUtils.bytes2ToInt(retData.getData1()[3],retData.getData1()[4]);
				machineData.setGroupInfo(groupInfo);
			}

			ThLogger.debug("MachineData",machineData.toString());
			return machineData;
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
