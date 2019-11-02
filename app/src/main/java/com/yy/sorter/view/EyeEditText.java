package com.yy.sorter.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Editable;
import android.text.InputType;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;

import com.yy.sorter.activity.R;
import com.yy.sorter.utils.ConvertUtils;


/**
 * Created by YUYANG on 2018/11/6.
 * 自定义密码眼EditText
 */

public class EyeEditText extends android.support.v7.widget.AppCompatEditText implements TextWatcher{
    private Paint mPaint=new Paint();
    private final int EYE_WIDTH= ConvertUtils.toPx(25);
    private final int EYE_HEIGHT=ConvertUtils.toPx(25);
    private final int SPACE=ConvertUtils.toPx(10);
    private final RectF clickRect=new RectF();
    private boolean isShowing=false;
    private int resId=R.mipmap.eye_close;
    public EyeEditText(Context context) {
        super(context);
        addTextChangedListener(this);
    }
    public EyeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(this);
    }

    public EyeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextChangedListener(this);
    }

    float mx,my;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mx = event.getX();
                my = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if(clickRect.contains(mx,my)){
                    isShowing=isShowing?false:true;
                    if(isShowing){
                        resId=R.mipmap.eye_open;
                        setInputType(InputType.TYPE_CLASS_TEXT|EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }else{
                        resId=R.mipmap.eye_close;
                        setInputType(InputType.TYPE_CLASS_TEXT|EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    invalidate();
                }
                break;
        }
        if(clickRect.contains(mx,my)){
            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 先测量       onMeasure
     * 通知大小变化 onSizeChange
     * 再布局位置   onLayout
     * 最后绘制     onDraw
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        left=w-EYE_WIDTH-SPACE*2;
        reInitEditable(getEditableText());
    }

    int left=0;
    int right=0;
    int top=0;
    int bottom=0;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width=getWidth();
        int height=getHeight();

        Bitmap srcBitmap=BitmapFactory.decodeResource(getContext().getResources(),resId);
        Bitmap dstBitmap=Bitmap.createScaledBitmap(srcBitmap,EYE_WIDTH,EYE_HEIGHT,false);
        left=width-EYE_WIDTH-SPACE*2;
        right=width;
        top=0;
        bottom=height;
        clickRect.set(left,top,right,bottom);
        canvas.drawBitmap(dstBitmap,left+SPACE,(height-EYE_HEIGHT)/2,mPaint);

    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
    int max_len=0;
    private void reInitEditable(Editable s){
        TextPaint textPaint = getPaint();
        float textPaintWidth = textPaint.measureText(s.toString());
        if(left<=0){
            return;
        }
        float totalWidth=(left-getPaddingLeft());
        if(textPaintWidth>=totalWidth){
            max_len=(int) ((totalWidth/textPaintWidth)*s.length());
            if(max_len>=0&&max_len<s.length()){
                s.delete(max_len,s.length());
            }
        }
    }
    @Override
    public void afterTextChanged(Editable s) {
        reInitEditable(s);
    }
}
