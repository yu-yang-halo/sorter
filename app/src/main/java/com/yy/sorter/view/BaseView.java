package com.yy.sorter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.yy.sorter.utils.ConvertUtils;


/**
 * Created by Administrator on 2017/7/26.
 *
 * 基本图像view，主要负责绘制坐标点区域
 *
 */

public class BaseView extends View {
    /**
     * 默认常量值
     */
    protected final int OFFSET_RIGHT=ConvertUtils.toPx(15);//右偏移距离
    protected final int OFFSET_LEFT=ConvertUtils.toPx(40);//左偏移距离
    protected float verticalSpace=0;//距离顶端的空间
    protected float vertical255ExtendSpace=0;
    private final int DEFAULT_TEXT_SIZE=13;//sp dp
    private final int LINE_WIDTH=2;
    private final int fullLineColor=Color.parseColor("#ffffff");
    private final int dottedLineColor=Color.parseColor("#111111");
    PathEffect effects = new DashPathEffect(new float[]{2,2,2,2},0.1f);//设置虚线的间隔和点的长度
    PathEffect virtualEffects = new DashPathEffect(new float[]{8,8,8,8},1f);//设置虚线的间隔和点的长度
    protected Paint mPaint=new Paint();
    protected Rect rect=new Rect();
    protected Path path = new Path();


    protected int viewWidth;
    protected int viewHeight;

    protected String[] xAxisDescs;
    protected String[] yAxisDescs;
    protected float yCellHeight;//单元高度
    protected float xCellWidth;//单元宽度

    protected int yAxisSize;
    protected int xAxisSize;

    protected int xRectStart,xRectEnd,yRectStart,yRectEnd;

    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 绘制坐标轴
     * @param canvas
     */
    protected void drawXYAxis(Canvas canvas){
        if(xAxisDescs==null||yAxisDescs==null){
            return;
        }
        mPaint.setAntiAlias(true);
        viewWidth=getWidth();
        viewHeight=getHeight();
        /**
         * 绘制坐标区域
         */
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0,0,viewWidth,viewHeight,mPaint);

        yAxisSize=yAxisDescs.length;
        xAxisSize=xAxisDescs.length;

        yCellHeight=viewHeight/yAxisSize;
        xCellWidth=(viewWidth-OFFSET_RIGHT-OFFSET_LEFT)/yAxisSize;

        vertical255ExtendSpace=(int)(yCellHeight*5/50.0f);
        verticalSpace=yCellHeight/2.0f;
        mPaint.setColor(Color.parseColor("#333333"));
        mPaint.setTextSize(ConvertUtils.toPx(DEFAULT_TEXT_SIZE));

        /**
         * 绘制y轴的文字
         */
        for (int i=0;i<yAxisSize;i++){
            mPaint.getTextBounds(yAxisDescs[i],0,yAxisDescs[i].length(),rect);
            canvas.drawText(yAxisDescs[i],(OFFSET_LEFT-rect.width())/2.0f,
                    viewHeight-yCellHeight*i-verticalSpace+rect.height()/2.0f,mPaint);
        }
        /**
         * 绘制x轴文字
         */
        for (int i=0;i<xAxisSize;i++){
            mPaint.getTextBounds(xAxisDescs[i],0,xAxisDescs[i].length(),rect);
            canvas.drawText(xAxisDescs[i],xCellWidth*i+OFFSET_LEFT-rect.width()/2.0f,viewHeight-rect.height()/2.0f,mPaint);
        }

        /**
         * 绘制xy 轴
         */
        mPaint.setStrokeWidth(LINE_WIDTH);
        mPaint.setColor(fullLineColor);
        xRectStart=OFFSET_LEFT;
        xRectEnd=viewWidth-OFFSET_RIGHT;
        yRectStart=(int) (verticalSpace-vertical255ExtendSpace);
        yRectEnd=(int) (viewHeight-verticalSpace);

        //左边y轴
        canvas.drawLine(xRectStart,yRectEnd,xRectStart,yRectStart,mPaint);
        //右边y轴
        canvas.drawLine(xRectEnd,yRectEnd,xRectEnd,yRectStart,mPaint);
        //底部x轴
        canvas.drawLine(xRectStart,yRectEnd,xRectEnd,yRectEnd,mPaint);
        //顶部x轴
        canvas.drawLine(xRectStart,yRectStart,xRectEnd,yRectStart,mPaint);





    }

    protected void drawVirtualLine(Canvas canvas){

        /**
         * 绘制虚线
         */
        mPaint.setStrokeWidth(1);
        mPaint.setColor(dottedLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(effects);

        path.reset();
        for(int i=0;i<xAxisSize-2;i++){
            /**
             * 绘制竖虚线
             */
            path.moveTo(OFFSET_LEFT+xCellWidth*(i+1), viewHeight-verticalSpace);//起始坐标
            path.lineTo(OFFSET_LEFT+xCellWidth*(i+1),verticalSpace);//终点坐标
            canvas.drawPath(path, mPaint);
            path.reset();

        }
        for (int j=0;j<2*(yAxisSize-1);j++){

            /**
             * 绘制横虚线
             */
            path.moveTo(OFFSET_LEFT,viewHeight-verticalSpace-yCellHeight/2.0f*(j+1));//起始坐标
            path.lineTo(viewWidth-OFFSET_RIGHT,viewHeight-verticalSpace-yCellHeight/2.0f*(j+1));//终点坐标
            canvas.drawPath(path, mPaint);
            path.reset();
        }


        mPaint.setPathEffect(null);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.reset();
        path.reset();
        rect.setEmpty();


    }
}
