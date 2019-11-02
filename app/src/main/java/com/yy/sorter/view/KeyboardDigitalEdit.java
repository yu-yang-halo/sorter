package com.yy.sorter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.DigitalDialog;

/**
 * Created by YUYANG on 2018/11/6.
 */

public class KeyboardDigitalEdit extends android.support.v7.widget.AppCompatEditText implements View.OnClickListener{
	private final static String TAG = "KeyboardDigitalEdit";
	DigitalDialog.Builder dialog;
	private Context mContext;
	private int Max = 99;
	private int Min = 1;
	private int ParMsg = 0;
	private float x,tempX=-1;
	private float y,tempY=-1;
	private DigitalDialog.Builder.LVCallback pParentCallback;
	private DigitalDialog.Builder.LVCallback2 pParentCallback2;
	private  boolean enable;
	private int overLoadValue;
	private Paint paint=new Paint();
	private Rect rect=new Rect();
	private boolean enableNumber=false;
	private int     number=0;
	private Object  tag;
	public KeyboardDigitalEdit(Context context) {
		super(context);
		mContext = context;
		setClickable(true);
		init();
	}
	public void setOverLoadEnable(boolean enable,int overLoadValue){
		this.enable=enable;
		this.overLoadValue=overLoadValue;
		initEditStyle(getText());
	}

	private void initEditStyle(CharSequence text){
		if(enable){
			int value= ConvertUtils.toInt(text);
			if(value>=overLoadValue){
				setTextColor(Color.parseColor("#ff0000"));
			}else{
				setTextColor(Color.rgb(0,0,0));
			}
		}
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
		initEditStyle(text);
	}

	@Override
	public void onClick(View view)
	{

	}

	public void setValue(int max,int min, int par)
	{
		Max = max;
		Min = min;

		ParMsg = par;
	}
	public void setValue(int max,int min, int par,Object tag)
	{
		Max = max;
		Min = min;

		ParMsg = par;
		this.tag = tag;
	}

	public void setMax(int max)
	{
		Max = max;
	}

	public void setMin(int min)
	{
		Min = min;
	}

	public int getMax()
	{
		return Max;
	}

	public int getMin()
	{
		return Min;
	}

	public void setLVCallback(DigitalDialog.Builder.LVCallback pContent)
	{
		pParentCallback = pContent;
	}

	public void setLVCallback2(DigitalDialog.Builder.LVCallback2 pContent)
	{
		pParentCallback2 = pContent;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//获取相对屏幕的坐标，即以屏幕左上角为原点
		x = event.getRawX();
		y = event.getRawY();   //statusBarHeight是系统状态栏的高度
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:    //捕获手指触摸按下动作

				tempX=x;
				tempY=y;
				break;
			case MotionEvent.ACTION_MOVE:   //捕获手指触摸移动动作

				break;
			case MotionEvent.ACTION_UP:    //捕获手指触摸离开动作

				if(event.getX()>0&&event.getY()>0){
					onShowDialog();
				}
				break;
		}

		return true;
	}

	public void onShowDialog()
	{
		if (isEnabled())
		{
			String i = this.getText().toString();
			int value=ConvertUtils.toInt(i);

			if(pParentCallback2 !=null){
				dialog = new DigitalDialog.Builder(mContext, pParentCallback2,Max,Min,value,ParMsg,tag);
			}else{
				dialog = new DigitalDialog.Builder(mContext, pParentCallback,Max,Min,value,ParMsg);
			}


			DigitalDialog dlg = dialog.create();
			dialog.setTitle("数字键盘");


			dlg.show();
			Window dialogWindow = dlg.getWindow();
			dialogWindow.setGravity(Gravity.BOTTOM);

		}

	}

	public KeyboardDigitalEdit(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public KeyboardDigitalEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init() {
		this.setTextColor(Color.rgb(0,0,0));
	}


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void setEnableNumber(int number){
		this.enableNumber=true;
		this.number=number;
		invalidate();
	}
	public void disableNumber(){
		this.enableNumber=false;
		this.number=0;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if(enableNumber){
			int height = getHeight();
			int width  = getWidth();

			paint.setColor(Color.RED);

			float radius=height*0.4f;
			float cx=width,cy=0;
			canvas.drawCircle(cx,cy,radius,paint);

			paint.setTextSize(ConvertUtils.toPx(10));
			paint.setAntiAlias(true);
			paint.setDither(true);
			paint.setColor(Color.WHITE);
			String numberStr=String.valueOf(number);
			paint.getTextBounds(numberStr,0,numberStr.length(),rect);
			float x=(width-radius)+(radius-rect.width())*0.6f;
			float y=(radius-rect.height())*0.4f+rect.height();
			canvas.drawText(numberStr,x,y,paint);

		}
	}
}
