package com.yy.sorter.utils;
import android.content.Context;
import java.security.MessageDigest;

/**
 * AuthUtils
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
}