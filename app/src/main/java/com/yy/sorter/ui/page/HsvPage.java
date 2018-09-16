package com.yy.sorter.ui.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.ConstantValues;

import th.service.helper.ThPackage;

public class HsvPage extends PageBaseUi {
    public HsvPage(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_page_hsv, null);
        }
        return view;
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_PAGE_HSV;
    }

    @Override
    public void receivePacketData(ThPackage packet) {

    }
}
