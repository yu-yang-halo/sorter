package com.yy.sorter.utils;
import android.content.Context;
import android.graphics.Point;
import android.text.TextPaint;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/3/17.
 * 动画功能
 */

public class FadeUtils {
    public static void fadeOut(View view,long duration){
        AlphaAnimation alphaAnimation=new AlphaAnimation(1,0);

        alphaAnimation.setDuration(duration);
        view.startAnimation(alphaAnimation);
    }
    public static void fadeIn(View view,long delay,long duration){
        AlphaAnimation alphaAnimation=new AlphaAnimation(0,1);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setStartOffset(delay);
        view.startAnimation(alphaAnimation);
    }

    public static void translateIn(Context ctx,View view,long delay,long duration){


        TranslateAnimation translateAnimation=new TranslateAnimation(0,
                0, getDefaultDisplay(ctx).y, 0);
        translateAnimation.setDuration(duration);
        translateAnimation.setStartOffset(delay);


        view.startAnimation(translateAnimation);
    }
    public static void translateOut(Context ctx,View view,long duration){

        TranslateAnimation translateAnimation=new TranslateAnimation(0,0, 0, getDefaultDisplay(ctx).x);
        translateAnimation.setDuration(duration);


        view.startAnimation(translateAnimation);
    }



    public static void translate(Context ctx,TextView view,String text,int repeatCount){
        view.setText(text);
        TextPaint textPaint=view.getPaint();
        float textPaintMeasureWidht=textPaint.measureText(text);

        TranslateAnimation translateAnimation=new TranslateAnimation((textPaintMeasureWidht+80)*(-1),getDefaultDisplay(ctx).x, 0, 0);
        translateAnimation.setRepeatCount(repeatCount);
        translateAnimation.setDuration(5000*2);


        view.startAnimation(translateAnimation);
    }

    public static Point getDefaultDisplay(Context ctx){
        WindowManager windowManager= (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Point point=new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point;

    }


}
