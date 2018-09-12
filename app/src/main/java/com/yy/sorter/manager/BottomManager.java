package com.yy.sorter.manager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.StringUtils;
import com.yy.sorter.view.AnimtionButton;

import th.service.core.AbstractDataServiceFactory;


/**
 * Created by Administrator on 2017/3/17.
 *
 * 底部布局管理器
 *
 *
 */

public class BottomManager implements ThMangerObserver{
    private Handler mainHandler=new Handler(Looper.getMainLooper());
    private static BottomManager instance=null;
    private BottomManager(){

    }
    public static BottomManager getInstance(){
        synchronized (BottomManager.class){
            if(instance==null){
                instance=new BottomManager();
            }
        }
        return instance;
    }


    /**
     * 底部容器
     */
    private   RelativeLayout bottomContainer;

    /**
     * Tab item 项 按钮
     */
    private LinearLayout layout_tab_home;
    private LinearLayout layout_tab_lmd;
    private LinearLayout layout_tab_xtsz;
    private LinearLayout layout_tab_xtxx;

    private ImageView button_tab_home;
    private ImageView button_tab_lmd;
    private ImageView button_tab_xtsz;
    private ImageView button_tab_xtxx;

    private TextView tv_tab_home;
    private TextView tv_tab_lmd;
    private TextView tv_tab_xtsz;
    private TextView tv_tab_xtxx;

    public void release(){
        synchronized (TopManager.class){
            if(instance!=null){
                instance=null;
            }
        }
    }

    /**
     * 初始化Item项
     * @param activity
     */
    public void init(Activity activity){
        bottomContainer= (RelativeLayout) activity.findViewById(R.id.bottom);

        button_tab_home= (ImageView) activity.findViewById(R.id.button_tab_home);
        button_tab_lmd= (ImageView) activity.findViewById(R.id.button_tab_lmd);
        button_tab_xtsz= (ImageView) activity.findViewById(R.id.button_tab_xtsz);
        button_tab_xtxx= (ImageView) activity.findViewById(R.id.button_tab_xtxx);

        layout_tab_home= (LinearLayout) activity.findViewById(R.id.home_layout);
        layout_tab_lmd= (LinearLayout) activity.findViewById(R.id.lmd_layout);
        layout_tab_xtsz= (LinearLayout) activity.findViewById(R.id.xtsz_layout);
        layout_tab_xtxx= (LinearLayout) activity.findViewById(R.id.xtxx_layout);

        tv_tab_home= (TextView) activity.findViewById(R.id.tv_tab_home);
        tv_tab_lmd= (TextView) activity.findViewById(R.id.tv_tab_lmd);
        tv_tab_xtsz= (TextView) activity.findViewById(R.id.tv_tab_xtsz);
        tv_tab_xtxx= (TextView) activity.findViewById(R.id.tv_tab_xtxx);

        setListenser();
        setLanguage();
    }



    private void setListenser() {
        layout_tab_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiddleManger.getInstance().changeUI(ConstantValues.VIEW_HOME, FileManager.getInstance().getString(32));//32#主页
            }
        });
        layout_tab_lmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiddleManger.getInstance().changeUI(ConstantValues.VIEW_SENSITIVE, FileManager.getInstance().getString(38));//38#灵敏度
            }
        });
        layout_tab_xtxx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiddleManger.getInstance().changeUI(ConstantValues.VIEW_FEEDER_SETTINGS, FileManager.getInstance().getString(39));//39#给料量
            }
        });
        layout_tab_xtsz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiddleManger.getInstance().changeUI(ConstantValues.VIEW_VALVE_RATE, FileManager.getInstance().getString(40));//40#更多
            }
        });



    }

    private void initTabStyle(int pageIndex){
        reImage();
        switch (pageIndex){
            case ConstantValues.VIEW_HOME:
                tv_tab_home.setTextColor(Color.rgb(0,152,157));
                button_tab_home.setBackgroundResource(R.mipmap.tab_home1);
                break;
            case ConstantValues.VIEW_SENSITIVE:
                tv_tab_lmd.setTextColor(Color.rgb(0,152,157));
                button_tab_lmd.setBackgroundResource(R.mipmap.tab_sensitive1);
                break;
            case ConstantValues.VIEW_FEEDER_SETTINGS:
                tv_tab_xtxx.setTextColor(Color.rgb(0,152,157));
                button_tab_xtxx.setBackgroundResource(R.mipmap.icon_feeder1);
                break;
            case ConstantValues.VIEW_VALVE_RATE:
                tv_tab_xtsz.setTextColor(Color.rgb(0,152,157));
                button_tab_xtsz.setBackgroundResource(R.mipmap.icon_valve1);
                break;
        }
    }

    private void setLanguage()
    {
        tv_tab_home.setText(FileManager.getInstance().getString(32)); //32#主页
        tv_tab_lmd.setText(FileManager.getInstance().getString(38)); //38#灵敏度
        tv_tab_xtxx.setText(FileManager.getInstance().getString(39)); //39#给料量
        tv_tab_xtsz.setText(FileManager.getInstance().getString(40)); //40#更多
    }


    public void showTabItemLayout(){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                bottomContainer.setVisibility(View.VISIBLE);
            }
        });

    }
    public void hideTabItemLayout(){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                bottomContainer.setVisibility(View.GONE);
            }
        });

    }

    public void reImage()
    {
        tv_tab_home.setTextColor(Color.rgb(0,0,0));
        tv_tab_lmd.setTextColor(Color.rgb(0,0,0));
        tv_tab_xtxx.setTextColor(Color.rgb(0,0,0));
        tv_tab_xtsz.setTextColor(Color.rgb(0,0,0));

        button_tab_home.setBackgroundResource(R.mipmap.tab_home0);
        button_tab_lmd.setBackgroundResource(R.mipmap.tab_sensitive0);
        button_tab_xtxx.setBackgroundResource(R.mipmap.icon_feeder0);
        button_tab_xtsz.setBackgroundResource(R.mipmap.icon_valve0);
    }

    @Override
    public void update(Object arg,String message) {
        String viewId=arg.toString();
        if( StringUtils.isNumeric(viewId) ){

            int targetViewID=Integer.parseInt(viewId);

            switch (targetViewID){
                case ConstantValues.VIEW_HOME:
                case ConstantValues.VIEW_SENSITIVE:
                case ConstantValues.VIEW_FEEDER_SETTINGS:
                case ConstantValues.VIEW_VALVE_RATE:
                {
                    initTabStyle(targetViewID);
                    showTabItemLayout();
                }
                break;
                case ConstantValues.VIEW_LOGIN:
                {
                    setLanguage();
                    hideTabItemLayout();
                }
                    break;
                default:
                    hideTabItemLayout();
                    break;
            }

        }
    }

}
