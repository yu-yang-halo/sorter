package com.yy.sorter.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.yy.sorter.utils.ConvertUtils;

import java.util.List;
import th.service.data.YYHsvWave;
import th.service.data.YYHsvInfo;
import th.service.helper.YYCommand;

/**
 * Created by YUYANG on 2018/11/6.
 * 色度图形视图
 */

public class HsvView extends BaseView {
    /**
     * 绘制步骤：
     * 1.首先绘制
     */
    private static final int HSV_WIDTH = 360;
    private static final int HSV_HEIGHT = 256;
    private static final int HSV_FRAME_NUM = 360;                // HSV每帧数据个数
    private static final int SIGNAL_VALUE_MAX = 256;                // 数据最大值为256
    private int[][] m_hsv_offset = new int[2][6];
    private int[] hsvColors = new int[HSV_HEIGHT * HSV_WIDTH];
    private int m_cur_color_b, m_cur_color_g, m_cur_color_r, m_fill_color_b, m_fill_color_g, m_fill_color_r,
            m_point_color_b, m_point_color_g, m_point_color_r;
    Matrix mMatrix = new Matrix();  //Matrix对象
    private int m_bFlag_PicMove = 0;
    private byte m_bPaintWaveColor=0;
    private Canvas mCanvas;
    int PixelNum = 1024;
    private RectF[] rectArrs = new RectF[8];

    public void setM_bPaintWaveColor(byte m_bPaintWaveColor) {
        this.m_bPaintWaveColor = m_bPaintWaveColor;
    }

    public void setM_bFlag_PicMove(int m_bFlag_PicMove) {
        this.m_bFlag_PicMove = m_bFlag_PicMove;
        if (m_bFlag_PicMove == 1) {
            xAxisDescs = new String[]{"180", "240", "300", "0", "60", "120", "180"};
        } else {
            xAxisDescs = new String[]{"0", "60", "120", "180", "240", "300", "360"};
        }
    }

    private int getColorValue(int colorType){
        int color=Color.RED;
        switch (colorType){
            case YYCommand.COLOR_COMB_CLR_RED:
                color=Color.RED;
                break;
            case YYCommand.COLOR_COMB_CLR_GREEN:
                color=Color.GREEN;
                break;
            case YYCommand.COLOR_COMB_CLR_BLUE:
                color=Color.BLUE;
                break;
            case YYCommand.COLOR_COMB_CLR_RED_BLUE:
                color=Color.parseColor("#FF00FF");
                break;
            case YYCommand.COLOR_COMB_CLR_RED_GREEN:
                color=Color.parseColor("#FFFF00");
                break;
            case YYCommand.COLOR_COMB_CLR_GREEN_BLUE:
                color=Color.parseColor("#00FFFF");
                break;
            case YYCommand.COLOR_COMB_CLR_RED_GREEN_BLUE:
                color=Color.parseColor("#80000000");
                break;
            case 7:
                color=Color.parseColor("#000000");
                break;
        }

        return color;
    }

    public void setCurrenSenseIndex(int index)
    {
        senseIndex = index;
    }

    public YYHsvInfo findCurrentObj() {
        YYHsvInfo thHsvInfo = null;
        for (YYHsvInfo sense : hsvSenses) {
            if (sense.getIndex() == senseIndex) {
                thHsvInfo = sense;
                break;
            }
        }
        return thHsvInfo;
    }

    public boolean refreshPage(int moveDirect, int step) {
        YYHsvInfo sense = findCurrentObj();
        if(sense == null)
        {
            return false;
        }
        int upValue, downValue, leftValue, rightValue;
        boolean stopYN = false;
        switch (moveDirect) {
            case 0:
            case 3:
                step = (moveDirect == 0 ? step : (step * -1));
                upValue = ConvertUtils.unsignByteToInt(sense.getsStartEnd()[1]) + step;
                if (upValue > 255) {
                    upValue = 255;
                    stopYN = true;
                } else if (upValue < 1) {
                    upValue = 1;
                    stopYN = true;
                }
                downValue = ConvertUtils.unsignByteToInt(sense.getsStartEnd()[0]) + step;
                if (downValue >= upValue) {
                    downValue = upValue;
                    stopYN = true;
                } else if (downValue < 0) {
                    downValue = 0;
                    stopYN = true;
                }

                if(!stopYN){
                    sense.getsStartEnd()[1] = (byte) upValue;
                    sense.getsStartEnd()[0] = (byte) downValue;
                }
                break;
            case 1:
            case 2:
                step = (moveDirect == 1 ? (step * -1) : step);
                leftValue = (ConvertUtils.bytes2ToInt(sense.gethStart()) + step + 360) % 360;
                rightValue = (ConvertUtils.bytes2ToInt(sense.gethEnd()) + step + 360) % 360;
                sense.sethStart(ConvertUtils.intTo2Bytes(leftValue));
                sense.sethEnd(ConvertUtils.intTo2Bytes(rightValue));
                break;

        }

        return stopYN;
    }

    public HsvView(Context context) {
        super(context);
        hsvInitColor();
    }

    public HsvView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        hsvInitColor();
    }

    public HsvView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        hsvInitColor();
    }

    private int mColorStart = 0, mColorEnd = 0, mPurerStart = 0, mPurerEnd = 0;

    private List<YYHsvInfo> hsvSenses;
    private YYHsvWave thHSVWave;
    private int senseIndex;

    public void setHsvSenses(List<YYHsvInfo> hsvSenses) {
        this.hsvSenses = hsvSenses;
    }

    public void setThHSVWave(YYHsvWave thHSVWave) {
        this.thHSVWave = thHSVWave;
    }

    public static interface ISensesSwitchEvent {
        public void onSwitchSense(int index);
    }

    private ISensesSwitchEvent iSensesSwitchEvent;

    public void setiSensesSwitchEvent(ISensesSwitchEvent iSensesSwitchEvent) {
        this.iSensesSwitchEvent = iSensesSwitchEvent;
    }

    float mx = 0, my = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mx = event.getX();
                my = event.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                for(int i=0;i<rectArrs.length;i++)
                {
                    if(rectArrs[i].contains(mx,my))
                    {
                        iSensesSwitchEvent.onSwitchSense(i/2);
                    }
                }
                break;
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        super.onDraw(canvas);
        for(int i=0;i<rectArrs.length;i++)
        {
            rectArrs[i].setEmpty();
        }

        drawXYAxis(canvas);
        drawColorBackground(canvas);
        drawVirtualLine(canvas);

        if (hsvSenses == null) {
            drawRectImageGreen(mColorStart, mColorEnd, mPurerStart, mPurerEnd, false,0);
        } else {

            for(YYHsvInfo thHsvInfo:hsvSenses)
            {

                if(thHsvInfo.getIndex() == senseIndex)
                {
                    drawRectImageGreen(ConvertUtils.bytes2ToInt(thHsvInfo.gethStart()),
                            ConvertUtils.bytes2ToInt(thHsvInfo.gethEnd()),
                            ConvertUtils.unsignByteToInt(thHsvInfo.getsStartEnd()[0]),
                            ConvertUtils.unsignByteToInt(thHsvInfo.getsStartEnd()[1]),
                            false,thHsvInfo.getIndex());
                }else
                {
                    drawRectImageGray(ConvertUtils.bytes2ToInt(thHsvInfo.gethStart()),
                            ConvertUtils.bytes2ToInt(thHsvInfo.gethEnd()),
                            ConvertUtils.unsignByteToInt(thHsvInfo.getsStartEnd()[0]),
                            ConvertUtils.unsignByteToInt(thHsvInfo.getsStartEnd()[1]),
                            false,thHsvInfo.getIndex());
                }

                drawLine(canvas, thHsvInfo.getvStartEnd()[0]);
                drawLine(canvas, thHsvInfo.getvStartEnd()[1]);
            }


        }
        drawBlackPoint(canvas);


    }

    private void drawBlackPoint(Canvas canvas) {
        if (thHSVWave == null) {
            return;
        }
        int alpha = 255;
        for (int i = 0; i < hsvColors.length; i++) {
            hsvColors[i] = Color.TRANSPARENT;
        }

        int hs = 0;
        for (int i = 0; i < PixelNum; i++) {
            hs = ConvertUtils.unsignByteToInt(thHSVWave.getWaveData1()[i]) * 2;        // 数据发送时/2了

            if (m_bFlag_PicMove == 0)    // 没有偏移时
            {
                int j = ConvertUtils.unsignByteToInt(thHSVWave.getWaveData2()[i]);
                int index=(HSV_HEIGHT - j - 1) * HSV_WIDTH + hs;
                if(index<hsvColors.length){
                    hsvColors[index] = Color.argb(alpha, 0, 0, 0);
                }
            } else // 偏移180度
            {
                int temp = hs + 180;
                if (temp >= 360) temp -= 360;
                int j = ConvertUtils.unsignByteToInt(thHSVWave.getWaveData2()[i]);
                int index=(HSV_HEIGHT - j - 1) * HSV_WIDTH + temp;
                if(index<hsvColors.length){
                    hsvColors[index] = Color.argb(alpha, 0, 0, 0);
                }
            }
        }


        Bitmap srcBitmap = Bitmap.createBitmap(hsvColors, HSV_WIDTH, HSV_HEIGHT, Bitmap.Config.ARGB_8888);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(srcBitmap, xRectEnd - xRectStart, yRectEnd - yRectStart, true);
        canvas.drawBitmap(scaledBitmap, OFFSET_LEFT, verticalSpace - vertical255ExtendSpace, mPaint);

        drawScrollLine(canvas,thHSVWave.getWaveData3(),getColorValue(m_bPaintWaveColor));
    }


    private void hsvInitColor() {
        m_cur_color_b = 128;
        m_cur_color_g = 128;
        m_cur_color_r = 128;

        m_fill_color_b = 0;
        m_fill_color_g = 255;
        m_fill_color_r = 0;

        m_point_color_b = 255;
        m_point_color_g = 255;
        m_point_color_r = 255;

        m_hsv_offset[0][0] = 0;
        m_hsv_offset[0][1] = 60;
        m_hsv_offset[0][2] = 120;
        m_hsv_offset[0][3] = 180;
        m_hsv_offset[0][4] = 240;
        m_hsv_offset[0][5] = 300;
        m_hsv_offset[1][0] = 180;
        m_hsv_offset[1][1] = 240;
        m_hsv_offset[1][2] = 300;
        m_hsv_offset[1][3] = 0;
        m_hsv_offset[1][4] = 60;
        m_hsv_offset[1][5] = 120;

        for(int i=0;i<rectArrs.length;i++)
        {
            rectArrs[i]=new RectF();
        }


    }

    private void drawRectImageInternel(Canvas canvas, int colorStart,
                                       int colorEnd, int purerStart,
                                       int purerEnd, int color,
                                       boolean virtual,int splitIndex,int senseIndex) {

        mPaint.setColor(color);

        RectF rectF = rectArrs[senseIndex*2+splitIndex];

        rectF.top = yRectEnd - (purerEnd / 50.0f) * yCellHeight;
        rectF.bottom = yRectEnd - (purerStart / 50.0f) * yCellHeight;

        rectF.left = (colorStart / 60.0f) * xCellWidth + OFFSET_LEFT;
        rectF.right = (colorEnd / 60.0f) * xCellWidth + OFFSET_LEFT;
        if (virtual) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setPathEffect(virtualEffects);
            mPaint.setStrokeWidth(4);
        } else {
            mPaint.setPathEffect(null);
            mPaint.setStyle(Paint.Style.FILL);
        }
        canvas.drawRect(rectF, mPaint);
        mPaint.setPathEffect(null);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void drawRectCoreInternel(int colorStart, int colorEnd, int purerStart, int purerEnd,
                                      int color, boolean virtual,int idx) {
        /**
         *    0    60   120 180 240 300  360
         *
         *    180  240  300  0  60  120  180
         */
        if (mCanvas == null) {
            return;
        }

        if (m_bFlag_PicMove == 1) {

            colorStart = (colorStart + 180) % 360;
            colorEnd = (colorEnd + 180) % 360;
        }

        {
            if (colorStart > colorEnd) {
                /**
                 * 拆分
                 */
                drawRectImageInternel(mCanvas, colorStart, 360 + 2, purerStart, purerEnd, color, virtual,0,idx);
                drawRectImageInternel(mCanvas, 0, colorEnd, purerStart, purerEnd, color, virtual,1,idx);

            } else {
                drawRectImageInternel(mCanvas, colorStart, colorEnd, purerStart, purerEnd, color, virtual,0,idx);
            }
        }

    }

    protected void drawRectImageGreen(int colorStart, int colorEnd, int purerStart, int purerEnd, boolean virtual,int index) {
        drawRectCoreInternel(colorStart, colorEnd, purerStart, purerEnd, Color.GREEN, virtual,index);
    }

    protected void drawRectImageGray(int colorStart, int colorEnd, int purerStart, int purerEnd, boolean virtual,int index) {
        drawRectCoreInternel(colorStart, colorEnd, purerStart, purerEnd, Color.GRAY, virtual,index);
    }


    private void drawColorBackground(Canvas canvas) {
        /**
         * 绘制色彩背景图
         */
        int m_hsv_v = 200;//ModeParameter.Sense_HSV_LightValue;
        int p, q, t, index;
        int alpha = 215;
        for (int i = 0; i < 60; i++) {
            for (int j = 0; j < 256; j++) {
                p = (255 - j) * m_hsv_v / 255;
                q = (15300 - i * j) * m_hsv_v / 15300;
                t = (15300 + (i - 60) * j) * m_hsv_v / 15300;

                index = i + m_hsv_offset[m_bFlag_PicMove][0];

                hsvColors[(HSV_HEIGHT - j - 1) * HSV_WIDTH + index] = Color.argb(alpha, m_hsv_v, t, p);


                index = i + m_hsv_offset[m_bFlag_PicMove][1];

                hsvColors[(HSV_HEIGHT - j - 1) * HSV_WIDTH + index] = Color.argb(alpha, q, m_hsv_v, p);

                index = i + m_hsv_offset[m_bFlag_PicMove][2];

                hsvColors[(HSV_HEIGHT - j - 1) * HSV_WIDTH + index] = Color.argb(alpha, p, m_hsv_v, t);


                index = i + m_hsv_offset[m_bFlag_PicMove][3];

                hsvColors[(HSV_HEIGHT - j - 1) * HSV_WIDTH + index] = Color.argb(alpha, p, q, m_hsv_v);


                index = i + m_hsv_offset[m_bFlag_PicMove][4];

                hsvColors[(HSV_HEIGHT - j - 1) * HSV_WIDTH + index] = Color.argb(alpha, t, p, m_hsv_v);

                index = i + m_hsv_offset[m_bFlag_PicMove][5];

                hsvColors[(HSV_HEIGHT - j - 1) * HSV_WIDTH + index] = Color.argb(alpha, m_hsv_v, p, q);
            }
        }
        Bitmap srcBitmap = Bitmap.createBitmap(hsvColors, HSV_WIDTH, HSV_HEIGHT, Bitmap.Config.ARGB_8888);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(srcBitmap, xRectEnd - xRectStart, yRectEnd - yRectStart, true);
        canvas.drawBitmap(scaledBitmap, OFFSET_LEFT, verticalSpace - vertical255ExtendSpace, mPaint);

    }

    private void drawLine(Canvas canvas, byte value) {
        mPaint.setStrokeWidth(ConvertUtils.toPx(1));
        mPaint.setColor(Color.BLACK);

        float dy = yRectEnd - (ConvertUtils.unsignByteToInt(value) / 50.0f) * yCellHeight;
        canvas.drawLine(xRectStart, dy, xRectEnd, dy, mPaint);
    }

    private static final float POINT_SIZE =1.5f;
    double pointXAxisWidth = 0;
    private float scale = 255.0f / 255.0f;

    private void drawScrollLine(Canvas canvas, byte[] bytes, int color) {
        path.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        if (bytes != null) {
            mPaint.setColor(color);
            mPaint.setStrokeWidth(POINT_SIZE);
            pointXAxisWidth = (viewWidth - OFFSET_RIGHT - OFFSET_LEFT) * 1.0 / bytes.length;
            for (int i = 0; i < bytes.length; i++) {
                int value1 = ConvertUtils.unsignByteToInt(bytes[i]);
                if (i == 0) {
                    path.moveTo((float) (OFFSET_LEFT + pointXAxisWidth * i), viewHeight - scale * (value1 * 1.0f / 50) * yCellHeight - (verticalSpace - vertical255ExtendSpace));
                } else {
                    path.lineTo((float) (OFFSET_LEFT + pointXAxisWidth * i), viewHeight - scale * (value1 * 1.0f / 50) * yCellHeight - (verticalSpace - vertical255ExtendSpace));
                }
            }
            canvas.drawPath(path, mPaint);

        }
    }


    /**
     * 重写该方法来绘制坐标值
     *
     * @param canvas
     */
    @Override
    protected void drawXYAxis(Canvas canvas) {
        if (xAxisDescs == null) {
            if (m_bFlag_PicMove == 1) {
                xAxisDescs = new String[]{"180", "240", "300", "0", "60", "120", "180"};
            } else {
                xAxisDescs = new String[]{"0", "60", "120", "180", "240", "300", "360"};
            }

        }
        if (yAxisDescs == null) {
            yAxisDescs = new String[]{"0", "50", "100", "150", "200", "250"};
        }
        super.drawXYAxis(canvas);
    }
}

