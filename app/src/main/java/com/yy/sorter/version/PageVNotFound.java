package com.yy.sorter.version;


import com.yy.sorter.ui.NotFoundUI;
import com.yy.sorter.ui.base.ConstantValues;

import java.util.HashMap;


/**
 * Created by Administrator on 2017/11/6.
 * PageVNotFound 无法找到的页面：说明该协议下没有匹配的页面
 *
 */

public class PageVNotFound extends BasePageV {
    public PageVNotFound(){
        basePages=new HashMap<>();
        initPages();
    }

    @Override
    protected void initPages() {
        super.initPages();

        basePages.put(ConstantValues.VIEW_HOME, NotFoundUI.class);//主界面

    }
}
