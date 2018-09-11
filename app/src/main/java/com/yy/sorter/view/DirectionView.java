package com.yy.sorter.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yy.sorter.activity.R;
import com.yy.sorter.utils.AlwaysClickButton;

/**
 * Created by Administrator on 2017/7/28.
 */

public class DirectionView extends FrameLayout implements AlwaysClickButton.LVMuiltClickCallBack {
    private Handler myHandler=new Handler(Looper.myLooper());
    private int[] imageIds=new int[]{R.drawable.button_top_selector,
            R.drawable.button_left_selector,
            R.drawable.button_right_selector,
            R.drawable.button_bottom_selector};
    private AlwaysClickButton.LVMuiltClickCallBack lvMuiltClickCallBack;


    public void setLvMuiltClickCallBack(AlwaysClickButton.LVMuiltClickCallBack lvMuiltClickCallBack) {
        this.lvMuiltClickCallBack = lvMuiltClickCallBack;
    }

    public DirectionView(@NonNull Context context) {
        super(context);
    }

    public DirectionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DirectionView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                layoutInit();
            }
        });

    }
    private void layoutInit(){

        int width=getWidth();
        int height=getHeight();
        int minSize=width>height?height:width;
        if(minSize<=0){
            return;
        }
        removeAllViews();

        int itemSize=(int)(minSize/3.0f);
        int horizontalSpace=(width-minSize)/2;

        for (int i=0;i<imageIds.length;i++){
            AlwaysClickButton btn=new AlwaysClickButton(getContext());
            btn.setBackgroundResource(imageIds[i]);
            btn.setTag(i);
            btn.setValve(i,this);
            LayoutParams layoutParams=new LayoutParams(itemSize,itemSize);
            switch (i){
                case 0:
                    layoutParams.leftMargin=horizontalSpace+itemSize;
                    layoutParams.topMargin=0;
                    break;
                case 1:
                    layoutParams.leftMargin=horizontalSpace;
                    layoutParams.topMargin=itemSize;
                    break;
                case 2:
                    layoutParams.leftMargin=horizontalSpace+itemSize*2;
                    layoutParams.topMargin=itemSize;
                    break;
                case 3:
                    layoutParams.leftMargin=horizontalSpace+itemSize;
                    layoutParams.topMargin=itemSize*2;
                    break;
            }


            addView(btn,layoutParams);

        }

    }

    @Override
    public void onMuiltClick(int par, int isSend) {
        if(lvMuiltClickCallBack!=null){
            lvMuiltClickCallBack.onMuiltClick(par,isSend);
        }
    }
}
