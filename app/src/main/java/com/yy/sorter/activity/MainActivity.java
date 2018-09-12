package com.yy.sorter.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.widget.RelativeLayout;

import com.yy.sorter.manager.BottomManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.manager.TopManager;
import com.yy.sorter.permission.PermissionUtils;
import com.yy.sorter.receiver.NetworkChangeListenser;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;

import th.service.core.AbstractDataServiceFactory;
import th.service.helper.ThCommand;


/**
 * Created by Administrator on 2017/3/17.
 */

public class MainActivity extends Activity implements NetworkChangeListenser {

    private RelativeLayout middleContainer;
    private DrawerLayout drawer_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        middleContainer= (RelativeLayout) findViewById(R.id.middle);
        drawer_layout= (DrawerLayout) findViewById(R.id.drawer_layout);


        BottomManager.getInstance().init(this);
        BottomManager.getInstance().hideTabItemLayout();

        TopManager.getInstance().init(this);
        TopManager.getInstance().hideTopView();



        MiddleManger.getInstance().init(this);
        MiddleManger.getInstance().addObserver(BottomManager.getInstance());
        MiddleManger.getInstance().addObserver(TopManager.getInstance());


        MiddleManger.getInstance().setMiddle(middleContainer);
        MiddleManger.getInstance().changeUI(ConstantValues.VIEW_LOGIN,"");

        /**
         * 每次重新启用应用下载配置文件
         */

      //  AbstractDataServiceFactory.getFileDownloadService().requestDownloadWhatFile((byte) ThCommand.BUILD_VERSION,ThCommand.DOWNLOAD_FILE_TYPE_CONFIG,null);



        PermissionUtils.requestPermission(this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, new PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int requestCode) {

            }
        });

    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        BaseUi currentUI=MiddleManger.getInstance().getCurrentUI();
        if(currentUI!=null){
            currentUI.onConfigurationChanged(newConfig);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        BaseUi currentUI= MiddleManger.getInstance().getCurrentUI();
        if(currentUI!=null){
            currentUI.onActivityStart();
        }

    }
    @Override
    protected void onStop() {
        super.onStop();
        BaseUi currentUI=MiddleManger.getInstance().getCurrentUI();
        if(currentUI!=null){
            currentUI.onActivityStop();
        }
    }

    @Override
    public void onBackPressed() {
        BaseUi currentUI=MiddleManger.getInstance().getCurrentUI();
        if(currentUI!=null){
            currentUI.onViewBackPress();
        }else{
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    /**
     * 网络发生变化通知
     * @param enable
     */
    @Override
    public void onNetworkChange(boolean enable) {

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
