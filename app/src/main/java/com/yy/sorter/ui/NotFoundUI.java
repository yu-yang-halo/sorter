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
 * Created by Administrator on 2017/12/13.
 */

public class NotFoundUI extends BaseUI {
    private TextView tv_page_not_found;
    public NotFoundUI(Context ctx) {
        super(ctx);
    }

    @Override
    public View getChild() {
        if(view==null){
            view= LayoutInflater.from(ctx).inflate(R.layout.ui_not_found,null);
            tv_page_not_found= (TextView) view.findViewById(R.id.tv_page_not_found);
        }
        /**
         * 初始化宽度和高度
         */
        initViewWidthHeight();

        tv_page_not_found.setText(FileManager.getInstance().getString(399));//399#很抱歉，页面找不到了！

        return view;
    }



    @Override
    public int getID() {
        return ConstantValues.VIEW_NOT_FOUND;
    }

}
