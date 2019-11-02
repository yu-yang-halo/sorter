package com.yy.sorter.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.yy.sorter.activity.R;
import com.yy.sorter.utils.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YUYANG on 2018/11/6.
 */
public class ThAutoLayout extends HorizontalScrollView  implements View.OnClickListener {
    private static final int SCROLL_UPPER = 5;
    private Handler myHandler=new Handler(Looper.myLooper());
    private List<Item> groups;
    private int selectPos=0;
    private int lineWidth= ConvertUtils.toPx(1);
    private int buttonWidth = ConvertUtils.toPx(100);
    private int itemWidth;
    private float lineHeightScale=0.6f;
    private ThGroupView.OnSelectedListenser onSelectedListenser;
    private List<TextView> caches;

    /**
     * 字体放大
     */

    private int fontSizeMax=18;
    private int fontSizeMin=18;


    public ThAutoLayout(Context context) {
        super(context);
        init();
    }

    public ThAutoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThAutoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        setScrollBarSize(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility == VISIBLE)
        {
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    initLayout();
                }
            });
        }
    }

    private void initLayout(){
        int width=getWidth();
        int height=getHeight();

        if(width<=0||height<=0){
            return;
        }
        removeAllViews();

        LinearLayout linearLayout = new LinearLayout(getContext());

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        addView(linearLayout);


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
            itemWidth=buttonWidth;
            if(groups.size()<=SCROLL_UPPER)
            {
                itemWidth=( width-lineNumber*lineWidth)/size;
            }
            int itemHeight=height;
            int lineHeight=(int) (itemHeight*lineHeightScale);

            for (int i=0;i<size;i++){

                TextView itemBtn=new TextView(getContext());
                itemBtn.setText(groups.get(i).getName());
                itemBtn.setGravity(Gravity.CENTER);
                itemBtn.setTag(i);
                itemBtn.setOnClickListener(this);

                if(i==selectPos){
                    setButtonSelected(itemBtn,true);
                }else{
                    setButtonSelected(itemBtn,false);
                }

                caches.add(itemBtn);

                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(itemWidth,itemHeight);
                linearLayout.addView(itemBtn,layoutParams);
                if(lineNumber>0&&i<size-1){
                    TextView lineView=new TextView(getContext());
                    lineView.setBackgroundColor(Color.parseColor("#10cccccc"));
                    LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(lineWidth,lineHeight);
                    linearLayout.addView(lineView,layoutParams2);
                }

            }
            updateLocation();
        }


    }

    private void updateLocation()
    {
        myHandler.post(new Runnable() {
            @Override
            public void run() {

                int offsetX = (selectPos+1)* itemWidth - getWidth();
                if(offsetX> 0)
                {
                    smoothScrollTo(offsetX+(getWidth()-itemWidth)/2,0);
                }else {
                    smoothScrollTo(0,0);
                }

            }
        });
    }

    private void setButtonSelected(TextView btn,boolean selected)
    {
        int toTextColor;
        int toBackgroundColor;
        if (selected) {
            btn.setSelected(true);
            btn.setTextSize(fontSizeMax);

            toTextColor = Color.WHITE;
            toBackgroundColor =R.drawable.button_layer_view0;

        } else {
            btn.setSelected(false);
            btn.setTextSize(fontSizeMin);

            toTextColor = Color.WHITE;
            toBackgroundColor = R.drawable.button_layer_view1;
        }


        btn.setTextColor(toTextColor);
        //btn.setBackgroundColor(toBackgroundColor);
        btn.setBackground(getResources().getDrawable(toBackgroundColor));
    }

    public void setContents(List<Item> groups,int curTag,int curSubTag) {
        this.groups = groups;


        if(groups != null)
        {
            int findIdx = 0;
            for(int i=0;i<groups.size();i++)
            {
                Item item = groups.get(i);
                if( (item.getTag()) == curTag && item.getSubTag() == curSubTag)
                {
                    findIdx = i;
                    break;
                }
            }

            selectPos = findIdx;
        }

        myHandler.post(new Runnable() {
            @Override
            public void run() {
                initLayout();
            }
        });

        if(groups != null && groups.size() <= 1)
        {
            this.setVisibility(View.GONE);
        }else
        {
            this.setVisibility(View.VISIBLE);
        }
    }

    public int getSelectPos() {
        return selectPos;
    }

    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
        initLayout();
    }

    public ThGroupView.OnSelectedListenser getOnSelectedListenser() {
        return onSelectedListenser;
    }

    public void setOnSelectedListenser(ThGroupView.OnSelectedListenser onSelectedListenser) {
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
            setButtonSelected(view,false);
        }
        if(index<caches.size()){

            setButtonSelected( caches.get(index),true);
            if(onSelectedListenser!=null){
                onSelectedListenser.onSelected(index);
            }
        }

        updateLocation();
    }


    public static interface  OnSelectedListenser{
        public void onSelected(int pos);
    }


    public static class Item
    {
        private int tag;
        private int subTag;
        private String name;

        public Item(String name ,int tag, int subTag) {
            this.tag = tag;
            this.subTag = subTag;
            this.name = name;
        }

        public int getTag() {
            return tag;
        }

        public void setTag(int tag) {
            this.tag = tag;
        }

        public int getSubTag() {
            return subTag;
        }

        public void setSubTag(int subTag) {
            this.subTag = subTag;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
