package com.yy.sorter.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/3/13.
 * 文本缓存工具类
 *
 */

public class TextCacheUtils {
    private static final String SharePreference_name="thcolorSorter";
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    public static final String KEY_USERNAME="key_username";
    public static final String KEY_PASSWORD="key_password";
    public static final String KEY_DEVICE_NUMBER="key_device_number";
    public static final String KEY_VCODE="key_vcode";
    public static final String KEY_LAN_URL="LAN_URL";//语言包文件名称
    public static final String KEY_LAN_COUNTRY_ID="countryId";//语言id
    public static final String KEY_TRAFFIC_SEND="TRAFFIC_SEND";
    public static final String KEY_TRAFFIC_ACCEPT="TRAFFIC_ACCEPT";
    public static final String KEY_USERNAME_HISTORY="USER_NAME_HISTORY";
    public static final String KEY_DEVICES_HISTORY="USER_DEVICES_HISTORY";
    public static final String KEY_CERTIFICATION_FILE="KEY_CERTIFICATION_FILE";
    public static final String KEY_LOGIN_TYPE="KEY_LOGIN_TYPE";

    public static Context mContext;


    public static void setmContext(Context mContext) {
        TextCacheUtils.mContext = mContext;
    }


    public static void loadString(String key, String value) {
        if(mContext==null){
            return;
        }
        sp=mContext.getSharedPreferences(SharePreference_name, Context.MODE_PRIVATE);
        editor=sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getValueString(String key, String default_value){
        if(mContext==null){
            return "";
        }
        sp=mContext.getSharedPreferences(SharePreference_name, Context.MODE_PRIVATE);
        String value=sp.getString(key, default_value);
        return value;
    }

    public static void loadInt(String key, int value) {
        if(mContext==null){
            return ;
        }
        sp=mContext.getSharedPreferences(SharePreference_name, Context.MODE_PRIVATE);
        editor=sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getValueInt(String key, int default_value){
        if(mContext==null){
            return -1;
        }
        sp=mContext.getSharedPreferences(SharePreference_name, Context.MODE_PRIVATE);
        int value=sp.getInt(key, default_value);
        return value;
    }
    public static void loadLong(String key, long value) {
        if(mContext==null){
            return ;
        }
        sp=mContext.getSharedPreferences(SharePreference_name, Context.MODE_PRIVATE);
        editor=sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getValueLong(String key, long default_value){
        if(mContext==null){
            return -1;
        }
        sp=mContext.getSharedPreferences(SharePreference_name, Context.MODE_PRIVATE);
        long value=sp.getLong(key, default_value);
        return value;
    }


}
