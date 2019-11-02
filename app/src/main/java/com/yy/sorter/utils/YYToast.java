package com.yy.sorter.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * YYToast
 */

public class YYToast {
    public static void showToast(Context ctx,String message){
        Toast.makeText(ctx,message,Toast.LENGTH_SHORT).show();
    }
}
