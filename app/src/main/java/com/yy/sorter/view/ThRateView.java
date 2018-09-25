package com.yy.sorter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.yy.sorter.utils.ConvertUtils;

import th.service.data.ThValveRateRet;

/**
 * Created by Administrator on 2017/3/30.
 * 阀频率绘制组件
 * canvas.drawPath避免循环调用导致UI卡顿
 */

public class ThRateView extends View {
    private static final String TAG="ThRateView";
    private static  final int POINT_SIZE=2;
    private static  final int TEXT_SIZE=13;
    private static  final int OFFSET_RIGHT= ConvertUtils.toPx(40);
    private static  final int LINE_WIDTH=2;
    private String[] yAxis=new String[]{"0","100","200","300","400","500"};
    private String[] xAxis=new String[]{"9","18","27","36","45","54","63","72","81"};
    private int fullLineColor=Color.parseColor("#70888888");
    private int dottedLineColor=Color.parseColor("#50888888");

    int viewWidth=0;
    int viewHeight=0;
    int yAxisHeight=0;
    int xAxisWidth=0;
    int vspace=0;
    private ThValveRateRet thValveRateRet;

    public void setThValveRateRet(ThValveRateRet thValveRateRet) {
        this.thValveRateRet = thValveRateRet;
        buildXaisValues();
    }

    private void buildXaisValues(){
        int valveNum=thValveRateRet.getValveNum();
        int sum=valveNum%9==0?valveNum/9:(valveNum/9+1);
        xAxis=new String[sum];
        for (int i=0;i<sum;i++){
            xAxis[i]= String.valueOf((i+1)*9);
        }
    }

    private float scale= 1.0f;

    Paint mPaint=new Paint();
    Rect rect=new Rect();
    PathEffect effects = new DashPathEffect(new float[]{8,8,8,8},1);//设置虚线的间隔和点的长度
    Path path = new Path();
    float scaleValue=5.0f/3.0f;
    public ThRateView(Context context) {
        super(context);
    }

    public ThRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThRateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        viewWidth=getWidth();
        viewHeight=getHeight();
        mPaint.reset();
        path.reset();
        /**
         * 绘制整体背景色
         */

        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0,0,viewWidth,viewHeight,mPaint);

        int yAxisSize=yAxis.length;
        int xAxisSize=xAxis.length;
        yAxisHeight=viewHeight/yAxisSize;
        xAxisWidth=(viewWidth-OFFSET_RIGHT*2)/(xAxisSize);
        vspace=yAxisHeight/2;
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(ConvertUtils.toPx(TEXT_SIZE));


        for (int i=0;i<yAxisSize;i++){
            mPaint.getTextBounds(yAxis[i],0,yAxis[i].length(),rect);
            if(i==0){
                canvas.drawText(yAxis[i],OFFSET_RIGHT-15,15+viewHeight-yAxisHeight*i-vspace+rect.height()/2.0f,mPaint);
            }else{
                canvas.drawText(yAxis[i],(OFFSET_RIGHT-rect.width())/2.0f,viewHeight-yAxisHeight*i-vspace+rect.height()/2.0f,mPaint);
            }
        }

        for (int i=0;i<xAxisSize;i++){
            mPaint.getTextBounds(xAxis[i],0,xAxis[i].length(),rect);
            canvas.drawText(xAxis[i],OFFSET_RIGHT-rect.width()/2f+xAxisWidth*(i+1),viewHeight-rect.height()/2.0f,mPaint);
        }


        /**
         * 绘制xy 轴
         */
        mPaint.setStrokeWidth(LINE_WIDTH);
        mPaint.setColor(fullLineColor);

        //y轴左边
        canvas.drawLine(OFFSET_RIGHT,viewHeight-vspace,OFFSET_RIGHT,vspace,mPaint);
        //y轴右边
        canvas.drawLine(viewWidth-OFFSET_RIGHT,viewHeight-vspace,viewWidth-OFFSET_RIGHT,vspace,mPaint);
        //x轴底部
        canvas.drawLine(OFFSET_RIGHT,viewHeight-vspace,viewWidth-OFFSET_RIGHT,viewHeight-vspace,mPaint);
        //x轴顶部
        canvas.drawLine(OFFSET_RIGHT,vspace,viewWidth-OFFSET_RIGHT,vspace,mPaint);

        /**
         * 绘制虚线
         */
        mPaint.setColor(dottedLineColor);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaint.setPathEffect(effects);

        path.reset();

        for(int i=0;i<yAxisSize-1;i++){
            /**
             * 绘制横虚线
             */
            if(i==yAxisSize-2){
                continue;
            }
            path.moveTo(OFFSET_RIGHT,viewHeight-vspace-yAxisHeight*(i+1));//起始坐标
            path.lineTo(viewWidth-OFFSET_RIGHT,viewHeight-vspace-yAxisHeight*(i+1));//终点坐标
            canvas.drawPath(path, mPaint);
            path.reset();
        }

        for (int j=0;j<xAxisSize-1;j++){
            /**
             * 绘制竖虚线
             */
            path.moveTo(OFFSET_RIGHT+xAxisWidth*(j+1), viewHeight-vspace);//起始坐标
            path.lineTo(OFFSET_RIGHT+xAxisWidth*(j+1),vspace);//终点坐标
            canvas.drawPath(path, mPaint);
            path.reset();
        }


        if(thValveRateRet==null){

            byte[] contents=new byte[64*4];

            for (int index=0;index<contents.length;index++){
                if(index%2==0){
                    contents[index]=0;
                }else{
                    contents[index]=1;
                }
            }
            thValveRateRet=new ThValveRateRet(64,false,contents);

            mPaint.setPathEffect(null);
            drawScrollLine(canvas,thValveRateRet.getFrontDatas(),Color.BLUE,Color.BLUE);
            drawScrollLine(canvas,thValveRateRet.getBackDatas(),Color.YELLOW,Color.YELLOW);



            return;
        }
        mPaint.setPathEffect(null);
        drawScrollLine(canvas,thValveRateRet.getFrontDatas(),Color.BLUE,Color.BLUE);
        drawScrollLine(canvas,thValveRateRet.getBackDatas(),Color.YELLOW,Color.YELLOW);

        thValveRateRet=null;

    }


    private void drawScrollLine(Canvas canvas,byte[] bytes,int lineColor,int pointColor){
        if(bytes!=null){
            path.reset();
            mPaint.setStyle(Paint.Style.FILL);

            for (int i=0;i<thValveRateRet.getValveNum();i++){

                int value1=ConvertUtils.bytes2ToInt(bytes[2*i],bytes[2*i+1]);
                float mx=(OFFSET_RIGHT+xAxisWidth*(i+1)/9.0f);

                float my=viewHeight- scale*(value1*scaleValue/100)*yAxisHeight-vspace;
                if(my>viewHeight-vspace){
                    my= viewHeight-vspace;
                }else if(my<vspace){
                    my=vspace;
                }
                if(i==0){
                    path.moveTo(mx,my);
                }else{
                    path.lineTo(mx,my);
                }
                mPaint.setColor(pointColor);

                canvas.drawCircle(mx,my,4,mPaint);

            }
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(lineColor);
            mPaint.setStrokeWidth(2);
            canvas.drawPath(path,mPaint);
        }
    }


}
