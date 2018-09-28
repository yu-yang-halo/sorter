package com.yy.sorter.utils;
import android.app.Service;
import android.content.Context;
import android.telephony.TelephonyManager;
import java.security.MessageDigest;
import th.service.helper.ThCommand;

/**
 * Created by Administrator on 2017/5/31.
 * 用户登录认证工具
 */

public class AuthUtils {
    private static final String BIND_ID="android";

    /**
     * 生成本地的认证文件
     */
    /**
     * 获取唯一的bindID SIM卡序列号和唯一设备ID组成
     * @param ctx
     * @return
     */
    private static boolean checkBindID(Context ctx,String md5Val){
        String bindIDMd5=string2MD5(getThBindID(ctx));

        if(bindIDMd5.equals(md5Val)){
            return true;
        }

        return false;

    }
    private static String getThBindID(Context ctx){

//        TelephonyManager telMgr = (TelephonyManager)ctx.getSystemService(Service.TELEPHONY_SERVICE);
//
//        String simSerialNumber=telMgr.getSimSerialNumber();
//        String deviceID=telMgr.getDeviceId();
//        String bindID=simSerialNumber+""+deviceID;

        return BIND_ID;
    }
    public static void buildLocalCertificationFile(Context ctx,String username, String password){
        StringBuilder sb=new StringBuilder();
        String bindID=getThBindID(ctx);
        sb.append(string2MD5(bindID));
        sb.append("#");
        sb.append(username);
        sb.append("#");
        sb.append(password);

        String data=sb.toString();

        TextCacheUtils.loadString(TextCacheUtils.KEY_CERTIFICATION_FILE,data);
    }

    /**
     * 获取证书文件信息
     * @return
     */
    public static String[] getLocalCertificationFileContents(){
        String data=TextCacheUtils.getValueString(TextCacheUtils.KEY_CERTIFICATION_FILE,null);
        if(data==null){
            return null;
        }
        String[] arrs=data.split("#");
        if(arrs==null||arrs.length!=3){
            return null;
        }
        return arrs;
    }

    /***
     * MD5加码 生成32位md5码
     */
    public static String string2MD5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    /**
     * 获取真实的版本类型 1 用户版 2 工程师版
     * 通过16进制取余获得
     * @param buildVersion
     * @return
     */
    public static int getRealVersionType(int buildVersion){
        return buildVersion%16;
    }

    /**
     *  提供一种方便的函数
     *  直接判断软件版本属于工程师版还是普通用户版
     */
//    public static boolean isEngineerVersion(){
//        int buildVersion=ThCommand.BUILD_VERSION;
//        return getRealVersionType(buildVersion)!=ThCommand.USER_VERSION_TYPE;
//    }

}