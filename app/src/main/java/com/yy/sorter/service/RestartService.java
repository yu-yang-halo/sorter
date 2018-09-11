package com.yy.sorter.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.yy.sorter.utils.TextCacheUtils;

/**
 * Created by Administrator on 2017/12/6.
 * 自重启服务
 */

public class RestartService extends Service {
    /**关闭应用后多久重新启动*/
    private static final long defaultDelayed=2000;
    private static final long defaultMaxInterval=300000;//重启的最大间隔时间5min
    private Handler handler;
    private long stopDelayed;
    private boolean isNeedRestart=true;
    public RestartService() {
        handler=new Handler();
    }
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        stopDelayed=intent.getLongExtra("Delayed",defaultDelayed);
        String ErrorMsg=intent.getStringExtra("ErrorMsg");
        long currentTime=System.currentTimeMillis();
        long lastTime=TextCacheUtils.getValueLong("Time",-1);
        TextCacheUtils.loadLong("Time",currentTime);
        long diff=0;
        if(lastTime>0&&currentTime>0){
            diff=currentTime-lastTime;//毫秒数   10分钟*60*1000
            if(diff<defaultMaxInterval){//5min
                isNeedRestart=false;
            }else{
                isNeedRestart=true;
            }
        }
        if(isNeedRestart){
            if(ErrorMsg==null){
                ErrorMsg="很抱歉，程序出问题了，重启中...";
            }
            showMessage(ErrorMsg);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                    startActivity(LaunchIntent);
                    RestartService.this.stopSelf();
                }
            }, stopDelayed);
        }else{
            //时间太短 可能是频繁crash 不应该重启
            showMessage("很抱歉，程序出问题了，请重新启动");
            RestartService.this.stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    protected void showMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}
