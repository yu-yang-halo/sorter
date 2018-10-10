package com.yy.sorter.application;

import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.yy.sorter.activity.MainActivity;
import com.yy.sorter.manager.BottomManager;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.manager.TopManager;
import com.yy.sorter.receiver.NetworkChangeListenser;
import com.yy.sorter.receiver.NetworkReceiver;
import com.yy.sorter.service.RestartAppUtils;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.TextCacheUtils;

import th.service.core.AbstractDataServiceFactory;
import th.service.core.ThUIManger;
import th.service.core.TrafficManager;
import th.service.helper.IPUtils;
import th.service.helper.ThLogger;

/**
 * Created by Administrator on 2017/3/13.
 * 自定义全局的应用容器
 */

public class ThApplication extends Application {
    private static final String TAG="ThApplication";
    Handler mainUIHandler=new Handler(Looper.getMainLooper());
    NetworkReceiver networkReceiver;
    Activity currentActivity;
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks();
        IPUtils.localIpAddress(this);
        TextCacheUtils.setmContext(this);
        ThLogger.setContext(this);


        try {
            Class.forName("com.th.colorsorter.utils.ConstantPools");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        TrafficManager.getInstance().setCtx(this);
        languageInit();

//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread t, final Throwable e) {
//                StringBuilder sb = new StringBuilder();
//                sb.append("BUG 收集日志 " + e + "\n");
//                for (StackTraceElement element : e.getStackTrace()) {
//                    sb.append(element.toString() + "\n");
//                }
//                ThLogger.addErrorLog(sb.toString());
//                ThLogger.outputErrorLog();
//
//
//                RestartAppUtils.restartAPP(getApplicationContext());
//            }
//        });
    }

    public void languageInit(){
        /**
         * 初始化默认语言 中文
         */
        String lanPacketName= TextCacheUtils.getValueString(TextCacheUtils.KEY_LAN_URL,null);
        if(lanPacketName==null){
            TextCacheUtils.loadInt(TextCacheUtils.KEY_LAN_COUNTRY_ID, ConstantValues.LAN_COUNTRY_CN);
            TextCacheUtils.loadString(TextCacheUtils.KEY_LAN_URL,"strings_zh.txt");
            lanPacketName=TextCacheUtils.getValueString(TextCacheUtils.KEY_LAN_URL,"strings_zh.txt");
        }
        FileManager.getInstance().setCtx(this);
        FileManager.getInstance().copyAssetsFileToLocal();

        FileManager.getInstance().switchLanguage(lanPacketName, new FileManager.ILanguageHandler() {
            @Override
            public void onComplelete(final boolean success) {
                mainUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!success){
                            showMessage(FileManager.getInstance().getString(151)); //151#语言切换失败,切换默认的英文语言
                            FileManager.getInstance().readLocalFile("strings_zh.txt",null);
                            TextCacheUtils.loadInt(TextCacheUtils.KEY_LAN_COUNTRY_ID,ConstantValues.LAN_COUNTRY_CN);
                            TextCacheUtils.loadString(TextCacheUtils.KEY_LAN_URL,"strings_zh.txt");
                        }
                    }
                });

            }
        });
    }
    protected void showMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private  void registerNetworkReceiver(Activity activity){
        unregisterNetworkReceiver();
        if(activity instanceof NetworkChangeListenser){
            IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            networkReceiver=new NetworkReceiver();
            networkReceiver.setListenser((NetworkChangeListenser) activity);
            this.registerReceiver(networkReceiver, filter);
        }

    }
    private  void unregisterNetworkReceiver(){
        if(networkReceiver!=null){
            this.unregisterReceiver(networkReceiver);
            networkReceiver.setListenser(null);
            networkReceiver=null;
        }

    }

    private void registerActivityLifecycleCallbacks(){
        /**
         * 监听所有Activity生命周期
         */
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.d(TAG,"onActivityCreated....");
                currentActivity=activity;
                if(activity instanceof MainActivity){
                    registerNetworkReceiver(activity);
                }

            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d(TAG,"onActivityStarted....");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d(TAG,"onActivityResumed....");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d(TAG,"onActivityPaused....");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d(TAG,"onActivityStopped....");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.d(TAG,"onActivitySaveInstanceState....");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d(TAG,"onActivityDestroyed....");

                if(activity instanceof MainActivity){
                    unregisterNetworkReceiver();
                    releaseCache();
                    android.os.Process.killProcess(android.os.Process.myPid()) ;
                    System.exit(0);
                }
            }
        });
    }

    /**
     * 应用退出时释放缓存数据
     */
    private void releaseCache(){

        MiddleManger.getInstance().release();
        ThUIManger.getInstance().clearObservers();

        BottomManager.getInstance().release();
        TopManager.getInstance().release();


        AbstractDataServiceFactory.getInstance().emptyDeviceSets();
        AbstractDataServiceFactory.getInstance().closeConnect();
        AbstractDataServiceFactory.getFileDownloadService().closeFileSocket();
        AbstractDataServiceFactory.release();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ThLogger.addErrorLog("............低内存情况...........");
        ThLogger.outputErrorLog();
    }

}
