package th.service.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.utils.TextCacheUtils;
import com.yy.sorter.utils.ThToast;

import th.service.helper.ThLogger;

/**
 * Created by Administrator on 2017/4/19.
 * 流量管理 目前只关注TCP流量
 *
 */

public class TrafficManager {
    private static String TAG="TrafficManager";
    private static TrafficManager instance;
    private boolean forceWrite=false;
    private long sendSum=0;
    private long acceptSum=0;
    private Context ctx;
    private Handler mainHandler=new Handler(Looper.getMainLooper());
    private TrafficManager(){

    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public static TrafficManager getInstance(){
        synchronized (TrafficManager.class){
            if(instance==null){
                instance=new TrafficManager();
            }
        }
        return instance;
    }

    public void showMessage(String message){
        ThToast.showToast(ctx,message);
    }
    public void showErrorMessage(){
        ThToast.showToast(ctx, FileManager.getInstance().getString(241));//241#协议不匹配请检查版本是否一致
    }

    public void forceWrite() {
        forceWrite=true;

        addAcceptSize(0);
        addSendSize(0);

        forceWrite=false;
    }

    public synchronized void addAcceptSize(int size){
        acceptSum+=size;
        if(acceptSum<=0){
            return;
        }
        if(acceptSum>1000||forceWrite){
            ThLogger.debug(TAG,"接收流量：："+acceptSum+" forceWrite:"+forceWrite);
            final long collectSum=acceptSum;
            //重置
            acceptSum=0;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //开始写入
                    long currentVal= TextCacheUtils.getValueLong(TextCacheUtils.KEY_TRAFFIC_ACCEPT,0);
                    currentVal+=collectSum;
                    TextCacheUtils.loadLong(TextCacheUtils.KEY_TRAFFIC_ACCEPT,currentVal);
                }
            });
        }

    }

    public synchronized void addSendSize(int size){
        sendSum+=size;
        if(sendSum<=0){
            return;
        }
       if(sendSum>200||forceWrite){
           ThLogger.debug(TAG,"发送流量：："+sendSum+" forceWrite:"+forceWrite);
           final long collectSum=sendSum;
           //重置
           sendSum=0;
           mainHandler.post(new Runnable() {
               @Override
               public void run() {
                   //开始写入
                   long currentVal=TextCacheUtils.getValueLong(TextCacheUtils.KEY_TRAFFIC_SEND,0);
                   currentVal+=collectSum;
                   TextCacheUtils.loadLong(TextCacheUtils.KEY_TRAFFIC_SEND,currentVal);
               }
           });
       }

    }



}
