package com.yy.sorter.ui.page;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.ConstantValues;

import th.service.helper.ThPackage;

public class RgbIrPage extends PageBaseUi {


    public RgbIrPage(Context ctx) {
        super(ctx);
    }


    @Override
    protected View onInitView() {
        if(view == null)
        {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_page_rgbir_sense, null);
        }
        return view;
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_PAGE_RGB_IR;
    }

    @Override
    public void receivePacketData(ThPackage packet) {

    }
}
