package com.yy.sorter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;

import th.service.helper.YYPackage;


/**
 * NotFoundUi
 */

public class NotFoundUi extends BaseUi {
    private TextView tv_page_not_found;
    public NotFoundUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view==null){
            view= LayoutInflater.from(ctx).inflate(R.layout.ui_not_found,null);
            tv_page_not_found= (TextView) view.findViewById(R.id.tv_page_not_found);
        }

        tv_page_not_found.setText(FileManager.getInstance().getString(399));//399#很抱歉，页面找不到了！

        return view;
    }



    @Override
    public int getID() {
        return ConstantValues.VIEW_NOT_FOUND;
    }

    @Override
    public void receivePacketData(YYPackage packet) {

    }

}
