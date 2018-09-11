package com.yy.sorter.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.yy.sorter.activity.R;
import com.yy.sorter.utils.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/4.
 * 分组组件
 */

public class ThGroupView extends FrameLayout implements View.OnClickListener{

    private Handler myHandler=new Handler(Looper.myLooper());
    private List<String> groups;
    private int selectPos=0;
    private int lineWidth= ConvertUtils.toPx(1);
    private float lineHeightScale=0.6f;
    private OnSelectedListenser onSelectedListenser;
    private List<TextView> caches;

    /**
     * 字体放大
     */

    private int fontSizeMax=18;
    private int fontSizeMin=14;


    public ThGroupView(Context context) {
        super(context);
    }

    public ThGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
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
                initLayout();
            }
        });

    }
    private void initLayout(){
        int width=getWidth();
        int height=getHeight();

        if(width<=0||height<=0){
            return;
        }
        removeAllViews();

        if(groups!=null&&groups.size()>0){
            if(selectPos>=groups.size()||selectPos<0){
                selectPos=0;
            }
            if(caches==null){
                caches=new ArrayList<>();
            }else{
                caches.clear();
            }
           int size=groups.size();
           int lineNumber=size-1;
           int itemWidth=( width-lineNumber*lineWidth)/size;
           int itemHeight=height;
           int lineHeight=(int) (itemHeight*lineHeightScale);


           for (int i=0;i<size;i++){

               TextView itemBtn=new TextView(getContext());
               itemBtn.setText(groups.get(i));
               itemBtn.setGravity(Gravity.CENTER);
               itemBtn.setTag(i);
               itemBtn.setOnClickListener(this);
               itemBtn.setBackgroundColor(Color.parseColor("#ffffff"));

               if(i==selectPos){
                   itemBtn.setSelected(true);
                   itemBtn.setTextSize(fontSizeMax);
               }else{
                   itemBtn.setSelected(false);
                   itemBtn.setTextSize(fontSizeMin);
               }
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                   itemBtn.setTextColor(getResources().getColorStateList(R.color.btn_text_color2,getContext().getTheme()));
               }else{
                   itemBtn.setTextColor(getResources().getColorStateList(R.color.btn_text_color2));
               }

               caches.add(itemBtn);

               LayoutParams layoutParams=new LayoutParams(itemWidth,itemHeight);
               layoutParams.leftMargin=itemWidth*i+lineWidth*i;
               addView(itemBtn,layoutParams);
               if(lineNumber>0&&i<size-1){
                   TextView lineView=new TextView(getContext());
                   lineView.setBackgroundColor(Color.parseColor("#10cccccc"));

                   LayoutParams layoutParams2=new LayoutParams(lineWidth,lineHeight);
                   layoutParams2.leftMargin=itemWidth*(i+1)+lineWidth*i;
                   layoutParams2.topMargin=(itemHeight-lineHeight)/2;
                   addView(lineView,layoutParams2);
               }
           }

        }
    }


    public void setContents(List<String> groups) {
        this.groups = groups;
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                initLayout();
            }
        });
    }

    public int getSelectPos() {
        return selectPos;
    }

    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
        initLayout();
    }

    public OnSelectedListenser getOnSelectedListenser() {
        return onSelectedListenser;
    }

    public void setOnSelectedListenser(OnSelectedListenser onSelectedListenser) {
        this.onSelectedListenser = onSelectedListenser;
    }

    @Override
    public void onClick(View v) {
        if(caches==null){
            return;
        }
        int index= (int) v.getTag();

        initSelectedItem(index);
    }
    private void initSelectedItem(int index){
        if(selectPos==index){
            return;
        }
        selectPos=index;
        for(TextView view:caches){
            view.setSelected(false);
            view.setTextSize(fontSizeMin);
        }
        if(index<caches.size()){

            caches.get(index).setSelected(true);
            caches.get(index).setTextSize(fontSizeMax);
            if(onSelectedListenser!=null){
                onSelectedListenser.onSelected(index);
            }
        }
    }


    public static interface  OnSelectedListenser{
        public void onSelected(int pos);
    }
}
