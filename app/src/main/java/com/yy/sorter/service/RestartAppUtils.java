package com.yy.sorter.service;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2017/12/6.
 * APP 异常重启工具类
 */

public class RestartAppUtils {

    public static void restartAPP(Context context, long Delayed,String ErrorMsg){

        Intent intent=new Intent(context,RestartService.class);
        intent.putExtra("Delayed",Delayed);
        intent.putExtra("ErrorMsg",ErrorMsg);
        context.startService(intent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    public static void restartAPP(Context context,String ErrorMsg){
        restartAPP(context,2000,ErrorMsg);
    }
    public static void restartAPP(Context context){
        restartAPP(context,2000,null);
    }
}
