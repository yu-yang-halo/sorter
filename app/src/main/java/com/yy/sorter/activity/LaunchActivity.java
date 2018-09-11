package com.yy.sorter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.yy.sorter.utils.ResUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/3/13.
 * 启动画面
 * 可以在此做一些宣传LOGO
 *
 */

public class LaunchActivity  extends BaseActivity{
    private static final int LOGO_SHOW_TIME=3500;//ms
    private static final String DEFAULT_LOGO="logo";
    ImageView logoView;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent intent=new Intent(LaunchActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        logoView= (ImageView) findViewById(R.id.logoView);

        String logoResourceParams= BuildConfig.LOGO;
        if(logoResourceParams==null){
            logoResourceParams=DEFAULT_LOGO;
        }

        logoView.setBackgroundResource(ResUtil.getMipmapId(this,logoResourceParams));

        if (!isTaskRoot()) {
            finish();
            return;
        }
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg=new Message();
                msg.what=1000;
                mHandler.sendMessage(msg);
            }
        },LOGO_SHOW_TIME);
    }


}
