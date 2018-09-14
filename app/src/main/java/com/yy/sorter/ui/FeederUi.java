package com.yy.sorter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;

import th.service.helper.ThPackage;

public class FeederUi extends BaseUi {
    public FeederUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_feeder, null);
        }
        return view;
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_FEEDER;
    }
    @Override
    public int getLeaver() {
        return ConstantValues.LEAVER_TAB;
    }

    @Override
    public void receivePacketData(ThPackage packet) {

    }
}
