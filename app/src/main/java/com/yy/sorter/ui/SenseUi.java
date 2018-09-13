package com.yy.sorter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;

public class SenseUi extends BaseUi {
    public SenseUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_sense,null);
        }
        return view;
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_SENSE;
    }
    @Override
    public int getLeaver() {
        return ConstantValues.LEAVER_TAB;
    }
}
