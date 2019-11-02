package com.yy.sorter.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.yy.sorter.activity.R;

/**
 * Created by YUYANG on 2018/11/6.
 */
public class LogoView extends View {
    private Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.icon_404);
    private Paint  mPaint = new Paint();
    public LogoView(Context context) {
        super(context);
    }

    public LogoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LogoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();


        LogoView.this.setDegree(0);
        LogoView.this.setRotate(0);
        LogoView.this.setDegree2(0);

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(LogoView.this,"degree",0,45);
        animator1.setDuration(1000);


        ObjectAnimator animator2 = ObjectAnimator.ofFloat(LogoView.this,"rotate",0,270);
        animator2.setDuration(1000);
        animator2.setStartDelay(500);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(LogoView.this,"degree2",0,45);
        animator3.setDuration(1000);
        animator3.setStartDelay(500);


        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playSequentially(animator1,animator2,animator3);
        animatorSet.setInterpolator(new LinearInterpolator());

        animatorSet.start();

    }

    public void setDegree(float degree) {
        this.degree = degree;
        invalidate();
    }
    public void setRotate(float rotate) {
        this.rotate = rotate;
        invalidate();
    }


    private float degree = 0;
    private float rotate = 0;

    public void setDegree2(float degree2) {
        this.degree2 = degree2;
        invalidate();
    }

    private float degree2 = 0;
    Camera camera = new Camera();
    float sx,sy,cx,cy;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        sx = (getWidth() - logo.getWidth()) / 2;
        sy = (getHeight() - logo.getHeight()) / 2;

        cx = getWidth() / 2;
        cy = getHeight() / 2;

        canvas.save();

        camera.save();
        canvas.translate(cx,cy);
        canvas.rotate(-rotate);
        camera.rotateY(degree2);
        camera.applyToCanvas(canvas);
        canvas.clipRect(-getWidth()/2,-getHeight()/2,0,getHeight()/2);
        canvas.rotate(rotate);
        canvas.translate(-cx,-cy);
        camera.restore();
        canvas.drawBitmap(logo,sx,sy,mPaint);

        canvas.restore();


        canvas.save();

        camera.save();
        canvas.translate(cx,cy);
        canvas.rotate(-rotate);

        camera.rotateY(-degree);
        camera.applyToCanvas(canvas);
        canvas.clipRect(0,-getHeight()/2,getWidth()/2,getHeight()/2);
        canvas.rotate(rotate);
        canvas.translate(-cx,-cy);

        camera.restore();


        canvas.drawBitmap(logo,sx,sy,mPaint);

        canvas.restore();



    }
}
