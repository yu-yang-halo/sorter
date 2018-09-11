package com.yy.sorter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.ui.base.BaseUI;
import com.yy.sorter.ui.base.ConstantValues;

/**
 * Created by Administrator on 2017/11/27.
 * 分享应用二维码  用户版和工程师版
 */

public class ShareQRUI extends BaseUI {
    private TextView  tv_app_share_desc_user,tv_app_share_desc_engineer,tv_app_share;
    public ShareQRUI(Context ctx) {
        super(ctx);
    }

    @Override
    public View getChild() {
        if(view==null){
            view= LayoutInflater.from(ctx).inflate(R.layout.ui_share_qr,null);
            tv_app_share_desc_user= (TextView) view.findViewById(R.id.tv_app_share_desc);
            tv_app_share_desc_engineer= (TextView) view.findViewById(R.id.tv_app_share_desc_engineer);
            tv_app_share= (TextView) view.findViewById(R.id.tv_app_share);
        }
        /**
         * 初始化宽度和高度
         */
        initViewWidthHeight();
        initView();
        return view;
    }
    private void initView(){
        tv_app_share_desc_engineer.setText(FileManager.getInstance().getString(259));// 259#工程师版
        tv_app_share_desc_user.setText(FileManager.getInstance().getString(258));//258#用户版
        tv_app_share.setText(FileManager.getInstance().getString(384));//384#让身边的好友扫一扫便可下载
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_SHARE_QR;
    }
}
