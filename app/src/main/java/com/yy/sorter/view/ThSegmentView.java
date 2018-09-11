package com.yy.sorter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.yy.sorter.activity.R;
import com.yy.sorter.utils.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/4.
 */

public class ThSegmentView extends FrameLayout implements View.OnClickListener{

    private Handler myHandler=new Handler(Looper.myLooper());
    private List<TSegmentItem> segmentItems;
    private int selectPos=0;
    private TSegmentItem selectItem;

    private int lineWidth= ConvertUtils.toPx(1);
    private float lineHeightScale=0.5f;
    private OnSelectedListenser onSelectedListenser;
    private List<TextView> caches;

    public TSegmentItem getSelectItem() {
        if(segmentItems==null||segmentItems.size()<=0){
            return null;
        }
        if(selectPos>=0&&selectPos<segmentItems.size()){
            return segmentItems.get(selectPos);
        }
        selectPos=0;
        return segmentItems.get(selectPos);
    }

    /**
     * 字体放大
     */

    private int fontSizeMax=17;
    private int fontSizeMin=14;


    public ThSegmentView(Context context) {
        super(context);
    }

    public ThSegmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThSegmentView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        if(segmentItems!=null&&segmentItems.size()>0){
            if(selectPos>=segmentItems.size()||selectPos<0){
                selectPos=0;
            }

            if(caches==null){
                caches=new ArrayList<>();
            }else{
                caches.clear();
            }

           int size=segmentItems.size();
           int lineNumber=size-1;
           int itemWidth=( width-lineNumber*lineWidth)/size;
           int itemHeight=height;
           int lineHeight=(int) (itemHeight*lineHeightScale);


           for (int i=0;i<segmentItems.size();i++){

               TextView itemBtn=new TextView(getContext());
               itemBtn.setText(segmentItems.get(i).getItemName());
               itemBtn.setGravity(Gravity.CENTER);
               itemBtn.setTag(i);
               itemBtn.setOnClickListener(this);

               if(i==selectPos){
                   itemBtn.setSelected(true);
                   itemBtn.setTextSize(fontSizeMax);
               }else{
                   itemBtn.setSelected(false);
                   itemBtn.setTextSize(fontSizeMin);
               }
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                   itemBtn.setTextColor(getResources().getColorStateList(R.color.btn_text_color,getContext().getTheme()));
               }else{
                   itemBtn.setTextColor(getResources().getColorStateList(R.color.btn_text_color));
               }

               caches.add(itemBtn);

               LayoutParams layoutParams=new LayoutParams(itemWidth,itemHeight);
               layoutParams.leftMargin=itemWidth*i+lineWidth*i;
               addView(itemBtn,layoutParams);


               if(lineNumber>0&&i<segmentItems.size()-1){
                   TextView lineView=new TextView(getContext());
                   lineView.setBackgroundColor(Color.parseColor("#30aaaaaa"));

                   LayoutParams layoutParams2=new LayoutParams(lineWidth,lineHeight);
                   layoutParams2.leftMargin=itemWidth*(i+1)+lineWidth*i;;
                   layoutParams2.topMargin=(itemHeight-lineHeight)/2;
                   addView(lineView,layoutParams2);
               }




           }



        }
    }

    public List<TSegmentItem> getContents() {
        return segmentItems;
    }

    public void setContents(List<TSegmentItem> segmentItems) {
        this.segmentItems = segmentItems;
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
                onSelectedListenser.onSelected(index,segmentItems.get(index));
            }
        }
    }


    public static interface  OnSelectedListenser{
        public void onSelected(int pos,TSegmentItem tSegmentItem);
    }
    public static class TSegmentItem{
        private String itemName;
        private int    itemTag;

        public TSegmentItem(String itemName, int itemTag) {
            this.itemName = itemName;
            this.itemTag = itemTag;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getItemTag() {
            return itemTag;
        }

        public void setItemTag(int itemTag) {
            this.itemTag = itemTag;
        }
    }
}
