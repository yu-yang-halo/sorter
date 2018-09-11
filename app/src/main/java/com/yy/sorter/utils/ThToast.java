package com.yy.sorter.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/3/18.
 */

public class ThToast {
    public static void showToast(Context ctx,String message){
        Toast.makeText(ctx,message,Toast.LENGTH_SHORT).show();
    }
}
