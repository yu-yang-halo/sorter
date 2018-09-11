package com.yy.sorter.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Administrator on 2017/5/10.
 */

public class ScreenHelper {
    public static void screenON(Activity activity){
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    public static void screenOFF(Activity activity){
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static int screenWidth(Context ctx){
        WindowManager wm= (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;

    }
    public static int screenHeight(Context ctx){
        WindowManager wm= (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.heightPixels;

    }
    public static int screenHeightExceptStatus(Context ctx){
        return screenHeight(ctx)-ConvertUtils.toPx(ctx,25);
    }
}
