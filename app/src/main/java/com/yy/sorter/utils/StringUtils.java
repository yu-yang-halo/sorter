package com.yy.sorter.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


import com.yy.sorter.manager.FileManager;
import com.yy.sorter.view.ThAutoLayout;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import th.service.core.AbstractDataServiceFactory;

/**
 * Created by Administrator on 2017/3/17.
 */

public class StringUtils {



    public static List<ThAutoLayout.Item> getGroupItem()
    {
        int groupNumber=AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getGroupNumbers();
        List<ThAutoLayout.Item> itemList = new ArrayList<>();
        for(int i=0;i<groupNumber;i++)
        {

            String layerStr = getGroupStr(i+1);
            if(groupNumber == 1)
            {
                layerStr = "";
            }

            ThAutoLayout.Item item = new ThAutoLayout.Item(layerStr,i,0);
            itemList.add(item);
        }

        return itemList;

    }

    public static List<ThAutoLayout.Item> getLayerItem()
    {
        int layerNumber=AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getLayerNumber();
        List<ThAutoLayout.Item> itemList = new ArrayList<>();
        for(int i=0;i<layerNumber;i++)
        {

            String layerStr = getLayerStr(i+1);
            if(layerNumber == 1)
            {
                layerStr = "";
            }

            ThAutoLayout.Item item = new ThAutoLayout.Item(layerStr,i,0);
            itemList.add(item);
        }

        return itemList;
    }


    /**
     * @param currentLayer start 1....
     * @return
     */
    public static String getLayerStr(int currentLayer){

        int layerNumber=AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getLayerNumber();
        int machineType=AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getMachineType();

        String layerStr="";
        String[] arrs = new String[0];

        if(currentLayer>=layerNumber||(currentLayer)<0){
            return layerStr;
        }
        switch (layerNumber){
            case 0:
                arrs=new String[]{"1 "+ FileManager.getInstance().getString(142)};
                break;
            case 1:
                if(machineType==2){//双层滑道式
                    arrs=new String[]{FileManager.getInstance().getString(397), FileManager.getInstance().getString(398)};  //398#后排 397#前排
                }else {
                    arrs=new String[]{FileManager.getInstance().getString(140), FileManager.getInstance().getString(139)};  //139#上层 140#下层
                }
                break;
            case 2:
                arrs=new String[]{FileManager.getInstance().getString(140), FileManager.getInstance().getString(141), FileManager.getInstance().getString(139)};//139#上层 140#下层 141#中层
                break;
            case 3:
                if(machineType==6){
                    arrs=new String[]{FileManager.getInstance().getString(396),FileManager.getInstance().getString(395),
                            FileManager.getInstance().getString(394),FileManager.getInstance().getString(393)};
                }else{
                    arrs=new String[]{"1 "+ FileManager.getInstance().getString(142),"2 "+ FileManager.getInstance().getString(142),"3 "
                            + FileManager.getInstance().getString(142),"4 "+ FileManager.getInstance().getString(142)}; // 142#层
                }
                break;
            case 4:
                //414#下中
                //415#前下
                //416#前中
                //417#前上
                //418#下后
                arrs=new String[]{FileManager.getInstance().getString(414,"下中"),
                                  FileManager.getInstance().getString(415,"前下"),
                                  FileManager.getInstance().getString(416,"前中"),
                                  FileManager.getInstance().getString(417,"前上"),
                                  FileManager.getInstance().getString(418,"下后")};
                break;
            case 5:
                arrs=new String[]{"1 "+ FileManager.getInstance().getString(142),"2 "+ FileManager.getInstance().getString(142),"3 "+ FileManager.getInstance().getString(142),
                        "4 "+ FileManager.getInstance().getString(142),"5 "+ FileManager.getInstance().getString(142),"6 "+ FileManager.getInstance().getString(142)};
                break;
        }

        if(currentLayer>=0 && currentLayer<arrs.length){
            return arrs[currentLayer];
        }else{
            return "";
        }
    }
    public static boolean isNumeric(String str){
        String regEx = "[0-9]+";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        // 字符串是否与正则表达式相匹配
        boolean rs = matcher.matches();
        return rs;
    }

    public static String getOpticsErrorType(int index)
    {
        String []strErrorType = new String[10];
        strErrorType[0] = FileManager.getInstance().getString(199); //199#校准失败
        strErrorType[1] = FileManager.getInstance().getString(200); //200#校准成功  75#前视 201#平均值低于下限
        strErrorType[2] = FileManager.getInstance().getString(199)+" "+ FileManager.getInstance().getString(75)+" "+
                FileManager.getInstance().getString(201);//     "校准失败 前视 平均值低于下限"; 202#有亮点存在
        strErrorType[3] = FileManager.getInstance().getString(199)+" "+ FileManager.getInstance().getString(75)+" "+
                FileManager.getInstance().getString(202);//"校准失败 前视 有亮点存在";
        strErrorType[4] = FileManager.getInstance().getString(199)+" "+ FileManager.getInstance().getString(75)+" "+//"校准失败 前视 有暗点存在";
                FileManager.getInstance().getString(203);  //203#有暗点存在
        strErrorType[5] = FileManager.getInstance().getString(199)+" "+ FileManager.getInstance().getString(75)+" "+ //"校准失败 前视 未知异常";
                FileManager.getInstance().getString(204);  //204#未知异常
        strErrorType[6] = FileManager.getInstance().getString(199)+" "+ FileManager.getInstance().getString(76)+" "+
                FileManager.getInstance().getString(201);//"校准失败 后视 平均值低于下限"; 76#后视
        strErrorType[7] = FileManager.getInstance().getString(199)+" "+ FileManager.getInstance().getString(76)+" "+
                FileManager.getInstance().getString(202); //"校准失败 后视 有亮点存在";
        strErrorType[8] = FileManager.getInstance().getString(199)+" "+ FileManager.getInstance().getString(76)+" "+
                FileManager.getInstance().getString(203); //"校准失败 后视 有暗点存在";
        strErrorType[9] = FileManager.getInstance().getString(199)+" "+ FileManager.getInstance().getString(76)+" "+
                FileManager.getInstance().getString(204);// "校准失败 后视 未知异常";

        if(index<strErrorType.length && index>=0){
            return strErrorType[index];
        }

        return "ERROR INDEX";
    }

    public static String getShapeStr(int index)
    {
        String[] strShape = new String[23];
        strShape[0] = "";
        strShape[1] = FileManager.getInstance().getString(155);//155#无形选
        strShape[2] = FileManager.getInstance().getString(156);//156#铁观音
        strShape[3] = FileManager.getInstance().getString(157);//157#大红袍
        strShape[4] = FileManager.getInstance().getString(158);//158#红茶
        strShape[5] = FileManager.getInstance().getString(159);//159#斯里兰卡红茶
        strShape[6] = FileManager.getInstance().getString(160);//160#腰果
        strShape[7] = FileManager.getInstance().getString(161);//161#玉米
        strShape[8] = FileManager.getInstance().getString(162);//162#花生芽头
        strShape[9] = FileManager.getInstance().getString(163);//163#小麦
        strShape[10] = FileManager.getInstance().getString(164);//164#稻种
        strShape[11] = FileManager.getInstance().getString(165);//165#葵花籽
        strShape[12] = FileManager.getInstance().getString(166);//166#枸杞
        strShape[13] = FileManager.getInstance().getString(167);//167#绿茶
        strShape[14] = FileManager.getInstance().getString(168);//168#花生
        strShape[15] = FileManager.getInstance().getString(169);//169#大米形选
        strShape[16] = FileManager.getInstance().getString(170);//170#标准形选
        strShape[17] = FileManager.getInstance().getString(171);//171#通用茶叶
        strShape[18] = FileManager.getInstance().getString(172);//172#甘草
        strShape[19] = FileManager.getInstance().getString(173);//173#蚕豆
        strShape[20] = FileManager.getInstance().getString(900);//900#绿茶短梗
        strShape[21] = FileManager.getInstance().getString(901);//901#选长米
        strShape[22] = FileManager.getInstance().getString(902);//902#选碎米


        if(index<strShape.length&&index>=0){
            return strShape[index];
        }

        return "";

    }
    /**
     44#一次
     45#二次
     46#三次
     47#四次
     */
    public static String getGroupStr(int groupNo){
        String groupStr="";
        switch (groupNo){
            case 1:
                groupStr=FileManager.getInstance().getString(44);
                break;
            case 2:
                groupStr=FileManager.getInstance().getString(45);
                break;
            case 3:
                groupStr=FileManager.getInstance().getString(46);
                break;
            case 4:
                groupStr=FileManager.getInstance().getString(47);
                break;
        }

        return groupStr;
    }



    public static List<String> getGroupList(int groupNumbers){
        List<String> stringList;
        switch (groupNumbers){
            case 1:
                stringList = Arrays.asList(new String[]{getGroupStr(1)});
                break;
            case 2:
                stringList = Arrays.asList(new String[]{getGroupStr(1),getGroupStr(2)});
                break;
            case 3:
                stringList = Arrays.asList(new String[]{getGroupStr(1),getGroupStr(2),getGroupStr(3)});
                break;
            case 4:
                stringList = Arrays.asList(new String[]{getGroupStr(1),getGroupStr(2),getGroupStr(3),getGroupStr(4)});
                break;
            default:
                stringList = new ArrayList<>();
                break;

        }
        return stringList;
    }

    public static String convertByteArrayToString(byte[] bytes){
        if(bytes==null){
            return "";
        }
        int pos=bytes.length;
        for (int i=0;i<bytes.length;i++){
            if(bytes[i]==0){
                pos=i;
                break;
            }
        }
        return new String(bytes,0, pos, Charset.forName("utf-8"));
    }
    public static byte[] convertStringToByteArray(String str)
    {
        byte[]  arr = str.getBytes(Charset.forName("utf-8"));
        return arr;
    }
    private static Map<Integer,String> historyTables=new HashMap<>();
    static {
        historyTables.put(1,TextCacheUtils.KEY_USERNAME_HISTORY);
        historyTables.put(2,TextCacheUtils.KEY_DEVICES_HISTORY);
    }
    public static String[] getLastThreeHistorys(Context ctx,int type){
        String key= historyTables.get(type);
        if(key==null){
            key=TextCacheUtils.KEY_USERNAME_HISTORY;
        }
       String historyStr=TextCacheUtils.getValueString(key,"");

       String[] arrsStr=historyStr.split(",");

       if(arrsStr!=null&&arrsStr.length>0){
           return arrsStr;
       }
        return new String[]{};
    }


    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versionCode=0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
           // versionCode=pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
        }
        return versionName;
    }



    public static void cacheLastThreeHistory(Context ctx,String username,int type){
        String key= historyTables.get(type);
        if(key==null){
            key=TextCacheUtils.KEY_USERNAME_HISTORY;
        }
        String historyStr=TextCacheUtils.getValueString(key,null);
        if(historyStr==null){
            TextCacheUtils.loadString(key,username);
        }else{
            String[]  arrs=historyStr.split(",");

            List<String> list=Arrays.asList(arrs);
            if(list.contains(username)){
                return;
            }


            StringBuilder sb=new StringBuilder();
            if(arrs.length>=3){
                sb.append(username);
                for(int i=0;i<2;i++){
                    sb.append(",");
                    sb.append(arrs[i]);
                }
            }else{
                sb.append(username);
                sb.append(",");
                sb.append(historyStr);
            }
            TextCacheUtils.loadString(key,sb.toString());
        }
    }

    /**
     * 宽松的设备编号验证 8-10 数字和字母组合
     */
    private static boolean isVaildDeviceSNSecond(String deviceSN) {
        String regEx = "[a-zA-Z0-9]{8,10}";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(deviceSN);
        boolean rs = matcher.matches();
        if(rs==false){
            System.out.println("宽松校验失败");
        }else{
            System.out.println("宽松校验成功");
        }
        return rs;
    }
    /**
     * 严格的设备编号验证
     */
    public static boolean isVaildDeviceSN(String deviceSN) {
        String regEx = "[A-Z]{0,1}[0-9]{8,9}";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(deviceSN);
        boolean rs = matcher.matches();
        if(rs==false){
            System.out.println("严格校验失败，启用宽松校验");
            rs=isVaildDeviceSNSecond(deviceSN);
        }else{
            System.out.println("严格校验成功");
        }
        return rs;
    }



}
