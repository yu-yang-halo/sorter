package com.yy.sorter.ui.page;

import android.content.Context;
import android.view.ViewGroup;

import com.yy.sorter.ui.base.BaseUi;

public abstract  class PageBaseUi extends BaseUi {

    public PageBaseUi(Context ctx) {
        super(ctx);
    }
    public void loadToContainer(ViewGroup container) {
        if(container != null)
        {
            container.removeAllViews();
            view = getView();
            container.addView(view);
        }
    }

}
