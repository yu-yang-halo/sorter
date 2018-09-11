package com.yy.sorter.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.yy.sorter.utils.ConvertUtils;

public class AnimtionButton extends View {
    private Paint mPaint = new Paint();
    private String mText = "";
    private boolean isOn = false;
    private float  mFontSize = ConvertUtils.toPx(12);
    private float  mCircleWidth = ConvertUtils.toPx(2);
    public AnimtionButton(Context context) {
        super(context);
    }

    public AnimtionButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimtionButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if(width > height)
        {
            width = height;
        }else {
            height = width;
        }

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.getMode(widthMeasureSpec));
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.getMode(heightMeasureSpec));

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float cx,cy,radius;
    private RectF rectF = new RectF();

    public void setRScale(float rScale) {
        this.rScale = rScale;
        invalidate();
    }

    private float rScale = 1;

    public void setButtonTextStatus(String text,boolean isOn)
    {

        this.mText = text;

        if(this.isOn != isOn)
        {
            this.isOn = isOn;

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(AnimtionButton.this,"rScale",0.1f,1);

            objectAnimator.setDuration(300);
            objectAnimator.start();

        }



    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    private float textAngle;
    private Rect bounds = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        mPaint.reset();
        mPaint.setStyle(Paint.Style.FILL);


        mPaint.setAntiAlias(true);

        cx = getWidth()/2;
        cy = getHeight()/2;
        radius = getHeight()/2 * rScale;

        rectF.left = 0;
        rectF.top = 0;
        rectF.right = getWidth();
        rectF.bottom = getHeight();


        if(isOn)
        {
            //mPaint.setColor(Color.parseColor("#4ed265"));
            Shader shader = new LinearGradient(0, 0, getWidth(), getHeight(), Color.parseColor("#4ed265"),
                    Color.parseColor("#4efa65"), Shader.TileMode.CLAMP);
            mPaint.setShader(shader);
        }else
        {
            //mPaint.setColor(Color.parseColor("#4ec2a5"));
            Shader shader = new LinearGradient(0, 0, getWidth(), getHeight(), Color.parseColor("#4ec2a5"),
                    Color.parseColor("#4ef2a5"), Shader.TileMode.CLAMP);
            mPaint.setShader(shader);
        }


        canvas.drawCircle(cx,cy,radius,mPaint);
        mPaint.reset();
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(mFontSize*rScale);

        mPaint.getTextBounds(mText,0,mText.length(),bounds);


        canvas.drawText(mText,(getWidth()-bounds.width())/2,getHeight()-(getHeight()-bounds.height())/2,mPaint);


        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#80ffffff"));
        mPaint.setStrokeWidth(mCircleWidth);

        canvas.drawCircle(cx,cy,radius-mCircleWidth,mPaint);

    }
}
