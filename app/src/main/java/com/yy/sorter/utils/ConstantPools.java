package com.yy.sorter.utils;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/13.
 * 一些常用的常量池
 */

public class ConstantPools {
    public static final String SELECT_LOCALE_KEY = "select_locale_key";

    /**
     * 国际化对应表
     */
    public static final String LOCAL_CN="CN";
    public static final String LOCAL_US="US";
    public static Map<String, Locale> LOCALES = new HashMap<>();//排序使用

    public static Map<String,String> ERRORS=new HashMap<>();

    public static int[] GROUP_COLORS=new int[]{Color.parseColor("#ff9912"),Color.parseColor("#068043"),
            Color.parseColor("#005ae1"),Color.parseColor("#ba2835")};

    static {

        LOCALES.put(LOCAL_CN, Locale.SIMPLIFIED_CHINESE);
        LOCALES.put(LOCAL_US, Locale.US);







        ERRORS.put("0","校准失败");
        ERRORS.put("1","校准成功");
        ERRORS.put("2","校准失败 前视 平均值低于下限");
        ERRORS.put("3","校准失败 前视 有亮点存在");
        ERRORS.put("4","校准失败 前视 有暗点存在");
        ERRORS.put("5","校准失败 前视 未知异常");
        ERRORS.put("6","校准失败 后视 平均值低于下限");
        ERRORS.put("7","校准失败 后视 有亮点存在");
        ERRORS.put("8","校准失败 后视 有暗点存在");
        ERRORS.put("9","校准失败 后视 未知异常");




        }


}
