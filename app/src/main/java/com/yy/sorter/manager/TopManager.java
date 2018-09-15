package com.yy.sorter.manager;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.StringUtils;

import th.service.core.AbstractDataServiceFactory;


/**
 * Created by Administrator on 2017/3/17
 *
 * 顶部容器管理类
 *
 */

public class TopManager implements ThMangerObserver{
    private static TopManager instance=null;
    private TopManager(){

    }
    public  static TopManager getInstance(){
        synchronized (TopManager.class){
            if(instance==null){
                instance=new TopManager();
            }
        }
        return instance;
    }

    private RelativeLayout topContainer;
    private RelativeLayout common_titleLayout;

    private ImageView leftButton;
    private ImageView rightButton;
    private TextView titleView;

    private DrawerLayout drawer_layout;

    private Activity context;
      //0:不隐藏 1:隐藏前视 2:隐藏后视 3:隐藏全部
    public void release(){
        synchronized (TopManager.class){
           if(instance!=null){
              instance=null;
           }
        }

    }
    public void init(Activity activity){
        this.context=activity;
        /**
         * 初始化顶部容器
         */
        topContainer= (RelativeLayout) activity.findViewById(R.id.top);

        /**
         * 初始化公共Title
         */
        common_titleLayout= (RelativeLayout) activity.findViewById(R.id.common_title);

        leftButton= (ImageView) activity.findViewById(R.id.leftBtn);
        rightButton= (ImageView) activity.findViewById(R.id.rightBtn);
        titleView= (TextView) activity.findViewById(R.id.top_title);


        drawer_layout= (DrawerLayout) activity.findViewById(R.id.drawer_layout);


        setListenser();


    }
    private void setListenser(){
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               MiddleManger.getInstance().getCurrentUI().onLeftClick();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               MiddleManger.getInstance().getCurrentUI().onRightClick();
            }
       });

    }


    private void initTitle(){
        showTopView();
        rightButton.setVisibility(View.GONE);
        leftButton.setVisibility(View.GONE);
        titleView.setVisibility(View.GONE);
    }

    public void showOnlyTitle(){
        initTitle();
        titleView.setVisibility(View.VISIBLE);
    }
    public void showOnlyTitleAndMenu(){
        initTitle();
        titleView.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);

    }
    public void showOnlyTitleAndBack(){
        initTitle();
        titleView.setVisibility(View.VISIBLE);
        leftButton.setVisibility(View.VISIBLE);
    }
    public void showAllContent(){
        initTitle();
        titleView.setVisibility(View.VISIBLE);
        leftButton.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);
    }

    public void hideTopView(){
        topContainer.setVisibility(View.GONE);
    }
    public void showTopView(){
        topContainer.setVisibility(View.VISIBLE);
    }


    public void changeTitle(String title) {
        titleView.setText(title);
    }


    @Override
    public void update(Object arg,String message) {

        String viewId=arg.toString();
        if(StringUtils.isNumeric(viewId) ){
            int targetViewID=Integer.parseInt(viewId);

            if(targetViewID!=ConstantValues.VIEW_LOGIN
                    &&targetViewID!=ConstantValues.VIEW_LAN){
                AbstractDataServiceFactory.getFileDownloadService().closeFileSocket();
            }
            String title=MiddleManger.getInstance().getCurrentUI().getTitle();
            if(title!=null){
                changeTitle(title);
            }else{
                changeTitle("");
            }
            switch (targetViewID) {
                case ConstantValues.VIEW_LOGIN:
                case ConstantValues.VIEW_NOT_FOUND: {
                    hideTopView();
                }
                break;
                case ConstantValues.VIEW_SENSE:
                case ConstantValues.VIEW_MORE:
                case ConstantValues.VIEW_FEEDER:
                case ConstantValues.VIEW_HOME: {
                    showOnlyTitle();
                }
                break;
                case ConstantValues.VIEW_WHEEL_SETTINGS:
                case ConstantValues.VIEW_OPTICS_ADJUST: {
                    showAllContent();
                }
                break;
                default:{
                    showOnlyTitleAndBack();
                }

            }


        }
    }
}
