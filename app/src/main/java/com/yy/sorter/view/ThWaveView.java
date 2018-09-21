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

import th.service.data.ThWaveData;
import th.service.helper.ThCommand;

/**
 * Created by Administrator on 2017/3/30.
 * 波形绘制组件
 *
 * canvas.drawPath避免循环调用导致UI卡顿
 *
 */

public class ThWaveView extends View {
    private static final String TAG="ThWaveView";
    private static  final int POINT_SIZE=2;
    private static  final int TEXT_SIZE=13;
    private static  final int OFFSET_RIGHT= ConvertUtils.toPx(40);
    private static  final int LINE_WIDTH=2;
    private String[] yAxis=new String[]{"0","50","100","150","200","250"};
    private int fullLineColor=Color.parseColor("#70888888");
    private int dottedLineColor=Color.parseColor("#50888888");
    private int[] IRCOLORS=new int[]{Color.parseColor("#454545"),Color.parseColor("#b23aee")};
    private static final int CAMERA_ADJUST_LINES[] = new int[]{15, 149, 255, 361, 495};
    private static final int COMPRESS_DATA_LINES[] = new int[]{3+4, 87, 255, 423, 507-4};
    int viewWidth=0;
    int viewHeight=0;
    int yAxisHeight=0;
    int xAxisWidth=0;
    int vspace=0;
    private ThWaveData thWaveData;

    /**
     * 清除波形数据
     */
    public void clear(){
        setThWaveRet(null);
        invalidate();
    }
    public void setThWaveRet(ThWaveData thWaveRet) {
        this.thWaveData = thWaveRet;
    }

    private float scale= 250.0f/255.0f;

    public ThWaveView(Context context) {
        super(context);
    }

    public ThWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    Rect rect=new Rect();
    Path path = new Path();
    Paint mPaint=new Paint();
    PathEffect effects = new DashPathEffect(new float[]{8,8,8,8},1);//设置虚线的间隔和点的长度


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.reset();
        path.reset();
        rect.setEmpty();

        viewWidth=getWidth();
        viewHeight=getHeight();


        /**
         * 绘制整体背景色
         */
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0,0,viewWidth,viewHeight,mPaint);

        int yAxisSize=yAxis.length;
        yAxisHeight=viewHeight/yAxisSize;
        xAxisWidth=(viewWidth-OFFSET_RIGHT*2)/yAxisSize;
        vspace=yAxisHeight/2;
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(ConvertUtils.toPx(TEXT_SIZE));


        for (int i=0;i<yAxisSize;i++){
            mPaint.getTextBounds(yAxis[i],0,yAxis[i].length(),rect);
            canvas.drawText(yAxis[i],(OFFSET_RIGHT-rect.width())/2.0f,viewHeight-yAxisHeight*i-vspace+rect.height()/2.0f,mPaint);
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
             * 绘制竖虚线
             */
            path.moveTo(OFFSET_RIGHT+xAxisWidth*(i+1), viewHeight-vspace);//起始坐标
            path.lineTo(OFFSET_RIGHT+xAxisWidth*(i+1),vspace);//终点坐标
            canvas.drawPath(path, mPaint);
            path.reset();

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


        mPaint.setPathEffect(null);
        drawWave(canvas);


    }

    private void drawWave(Canvas canvas){
        /**
         * 绘制波形
         */
        if(thWaveData!=null){
            switch (thWaveData.getWaveType()){

                case ThCommand.WAVE_TYPE_CAMERA_ORIGIN:
                case ThCommand.WAVE_TYPE_CAMERA_TEST:
                case ThCommand.WAVE_TYPE_CAMERA_ADJUST:

                    drawScrollLine(canvas,thWaveData.getrBytes(),getColorValue(ThCommand.COLOR_COMB_CLR_RED));
                    drawScrollLine(canvas,thWaveData.getgBytes(),getColorValue(ThCommand.COLOR_COMB_CLR_GREEN));
                    drawScrollLine(canvas,thWaveData.getbBytes(),getColorValue(ThCommand.COLOR_COMB_CLR_BLUE));

                    drawScrollLine(canvas,thWaveData.getIr1Bytes(),IRCOLORS[0]);
                    drawScrollLine(canvas,thWaveData.getIr2Bytes(),IRCOLORS[1]);

                    showVerticalLines(canvas,thWaveData.getData1_5());

                    break;
                case ThCommand.WAVE_TYPE_BACKGROUN_LIGHT:
                case ThCommand.WAVE_TYPE_CAMERA_GAIN:
                    drawScrollLine(canvas,thWaveData.getrBytes(),getColorValue(ThCommand.COLOR_COMB_CLR_RED));
                    drawScrollLine(canvas,thWaveData.getgBytes(),getColorValue(ThCommand.COLOR_COMB_CLR_GREEN));
                    drawScrollLine(canvas,thWaveData.getbBytes(),getColorValue(ThCommand.COLOR_COMB_CLR_BLUE));

                    drawScrollLine(canvas,thWaveData.getIr1Bytes(),IRCOLORS[0]);
                    drawScrollLine(canvas,thWaveData.getIr2Bytes(),IRCOLORS[1]);
                    break;

            }

        }


    }

    private void showVerticalLines(Canvas canvas,byte dataType){
        if(dataType==2){
           for(int val:CAMERA_ADJUST_LINES){
               drawVerticalLine(canvas,val);
           }
        }else if(dataType==1){
            for(int val:COMPRESS_DATA_LINES){
                drawVerticalLine(canvas, val);
            }
        }
    }



    private int getColorValue(int colorType){
        int color=Color.RED;
        switch (colorType){
            case ThCommand.COLOR_COMB_CLR_RED:
                color=Color.RED;
                break;
            case ThCommand.COLOR_COMB_CLR_GREEN:
                color=Color.GREEN;
                break;
            case ThCommand.COLOR_COMB_CLR_BLUE:
                color=Color.BLUE;
                break;
            case ThCommand.COLOR_COMB_CLR_RED_BLUE:
                color=Color.parseColor("#FF00FF");
                break;
            case ThCommand.COLOR_COMB_CLR_RED_GREEN:
                color=Color.parseColor("#FFFF00");
                break;
            case ThCommand.COLOR_COMB_CLR_GREEN_BLUE:
                color=Color.parseColor("#00FFFF");
                break;
            case ThCommand.COLOR_COMB_CLR_RED_GREEN_BLUE:
                color=Color.parseColor("#000000");
                break;

        }

        return color;
    }

    /**
     * 默认 start：0   end: 1
     * @param canvas
     * @param value
     * @param start
     * @param end
     */
    private void drawLine(Canvas canvas,byte value,float start,float end){
        int all=(viewWidth-OFFSET_RIGHT*2);

        mPaint.setStrokeWidth(POINT_SIZE);
        mPaint.setColor(Color.BLACK);
        float dy=viewHeight- scale*(ConvertUtils.unsignByteToInt(value)*1.0f/50)*yAxisHeight-vspace;
        canvas.drawLine(OFFSET_RIGHT+all*start,
                        dy,
                        OFFSET_RIGHT+all*end,dy,mPaint);

    }
    private void drawLine(Canvas canvas,byte value){
        mPaint.setStrokeWidth(1);
        mPaint.setColor(Color.BLACK);
        float dy=viewHeight- scale*(ConvertUtils.unsignByteToInt(value)*1.0f/50)*yAxisHeight-vspace;
        canvas.drawLine(OFFSET_RIGHT,dy,viewWidth-OFFSET_RIGHT,dy,mPaint);
    }

    private void drawVerticalLine(Canvas canvas,int value){
        mPaint.setStrokeWidth(1);
        mPaint.setColor(Color.BLACK);
        float dx= (float)(OFFSET_RIGHT+pointXAxisWidth*(value-1));

        canvas.drawLine(dx,vspace,dx, viewHeight-vspace,mPaint);
    }



    /**
     * 波形绘制区域范围
     * x轴[OFFSET_RIGHT, WIDHT-OFFSET_RIGHT*2]
     * y轴[vspace,HEIGHT-vspace*2]
     * @param canvas
     * @param bytes
     * @param color
     */
    double pointXAxisWidth=0;
    private void drawScrollLine(Canvas canvas,byte[] bytes,int color){
        path.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        if(bytes!=null){
            mPaint.setColor(color);
            mPaint.setStrokeWidth(POINT_SIZE);
            pointXAxisWidth=(viewWidth-OFFSET_RIGHT*2)*1.0/bytes.length;

            for (int i=0;i<bytes.length;i++){
                int value1=ConvertUtils.unsignByteToInt(bytes[i]);
                if(i==0){
                    path.moveTo((float)(OFFSET_RIGHT+pointXAxisWidth*i),viewHeight- scale*(value1*1.0f/50)*yAxisHeight-vspace);
                }else{
                    path.lineTo((float)(OFFSET_RIGHT+pointXAxisWidth*i),viewHeight- scale*(value1*1.0f/50)*yAxisHeight-vspace);
                }
            }
            canvas.drawPath(path,mPaint);

        }
    }


}
