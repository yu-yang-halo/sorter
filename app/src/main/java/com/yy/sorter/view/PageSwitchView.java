package com.yy.sorter.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;



public class PageSwitchView extends FrameLayout {

    private int mMoveDirection=0;//-1 向左  1 向右  0 不移动

    private boolean mIsBeingScrolled = false;
    private int mActivePointerId;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private int mTouchSlop;

    public void setmCurrentIndex(int mCurrentIndex) {
        if(mCurrentIndex<mNumbers)
        {
            this.mCurrentIndex = mCurrentIndex;
        }else
        {
            this.mCurrentIndex = 0;
        }

    }

    private int mCurrentIndex;

    public void setmNumbers(int mNumbers) {
        mCurrentIndex = 0;
        this.mNumbers = mNumbers;
    }

    private int mNumbers;
    private PageSwitchListenser mPageSwitchListenser;

    public PageSwitchView(Context context) {
        super(context);
    }

    public PageSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private float getMotionEventX(MotionEvent ev, int activePointerId) {
        final int index = ev.findPointerIndex(activePointerId);
        if (index < 0) {
            return -1;
        }
        return ev.getX(index);
    }
    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = ev.findPointerIndex(activePointerId);
        if (index < 0) {
            return -1;
        }
        return ev.getY(index);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!isEnabled() ) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsBeingScrolled = false;
                mActivePointerId = ev.getPointerId(0);
                mIsBeingScrolled = false;
                mMoveDirection = 0;
                final float initialMotionX = getMotionEventX(ev, mActivePointerId);
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionX == -1) {
                    return false;
                }
                mInitialMotionX = initialMotionX;
                mInitialMotionY = initialMotionY;

                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == -1) {
                    return false;
                }
                final float x = getMotionEventX(ev, mActivePointerId);
                final float y = getMotionEventY(ev, mActivePointerId);
                if (x == -1) {
                    return false;
                }
                final float xDiff = x - mInitialMotionX;
                final float yDiff = y - mInitialMotionY;

                mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

                if(Math.abs(xDiff)> mTouchSlop && Math.abs(yDiff)<mTouchSlop)
                {
                    mIsBeingScrolled = true;
                }else
                {
                    mIsBeingScrolled = false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingScrolled = false;
                mActivePointerId = -1;
                break;
            case MotionEventCompat.ACTION_POINTER_UP:

                break;
        }

        return mIsBeingScrolled;
    }




    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float x = ev.getX(pointerIndex);
                final float xDiff = x - mInitialMotionX;

                mTouchSlop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();

                if(Math.abs(xDiff)> mTouchSlop)
                {
                    if(xDiff>0)
                    {
                        mMoveDirection = 1;
                    }else if(xDiff < 0)
                    {
                        mMoveDirection = -1;
                    }else
                    {
                        mMoveDirection = 0;
                    }
                }


                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = ev.getPointerId(index);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == -1) {
                    return false;
                }
                onMoveDirection(mMoveDirection);
                mActivePointerId = -1;
                mIsBeingScrolled = false;
                mMoveDirection = 0;
                return false;
            }
        }

        return true;
    }
    private void onMoveDirection(int moveDirection)
    {
        int duration = 200;//动画时间 180ms

        int flag = FLAG_NO_MOVE;

        if(moveDirection == 1)
        {

            mCurrentIndex--;
            if(mCurrentIndex<0)
            {
                mCurrentIndex = 0;
                flag = FLAG_LEFT_END;
            }else
            {
                flag = FLAG_OK;

                ObjectAnimator translation= ObjectAnimator.ofFloat(this, "translationX", -getWidth(),0);
                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(this, "alpha", 0.3f, 1f);
                translation.setDuration(duration);
                fadeAnim.setDuration(duration);


                AnimatorSet animSet = new AnimatorSet();
                animSet.playTogether(translation,fadeAnim);
                animSet.start();
            }

        }else if(mMoveDirection == -1)
        {
            mCurrentIndex++;
            if(mCurrentIndex >= mNumbers)
            {
                mCurrentIndex = mNumbers - 1;
                flag = FLAG_RIGHT_END;
            }else
            {
                flag = FLAG_OK;

                ObjectAnimator translation= ObjectAnimator.ofFloat(this, "translationX", getWidth(),0);
                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(this, "alpha", 0.3f, 1f);
                translation.setDuration(duration);
                fadeAnim.setDuration(duration);

                AnimatorSet animSet = new AnimatorSet();
                animSet.playTogether(translation,fadeAnim);
                animSet.start();
            }

        }
        if(mNumbers <= 1 )
        {
            flag = FLAG_NO_MOVE;
        }
        if(mPageSwitchListenser != null)
        {
            mPageSwitchListenser.onPageSwitch(mCurrentIndex,flag);
        }

        if(flag == FLAG_LEFT_END)
        {
            SpringForce spring = new SpringForce(0)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY)
                    .setStiffness(SpringForce.STIFFNESS_LOW);

            SpringAnimation springAnimation = new SpringAnimation(this, DynamicAnimation.X);

            springAnimation.setMinValue(-20);
            springAnimation.setMaxValue(20);
            springAnimation.setSpring(spring);
            springAnimation.setStartValue(20);

            springAnimation.start();
        }else if(flag == FLAG_RIGHT_END)
        {
            SpringForce spring = new SpringForce(0)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY)
                    .setStiffness(SpringForce.STIFFNESS_LOW);

            SpringAnimation springAnimation = new SpringAnimation(this, DynamicAnimation.X);

            springAnimation.setMinValue(-20);
            springAnimation.setMaxValue(20);
            springAnimation.setSpring(spring);
            springAnimation.setStartValue(-20);

            springAnimation.start();
        }




    }
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }


    public void setPageSwitchListenser(PageSwitchListenser listenser)
    {
        mPageSwitchListenser = listenser;
    }



    public static interface PageSwitchListenser{
        public void onPageSwitch(int pageIndex,int flag);
    }

    public static final int FLAG_LEFT_END = 0xff;
    public static final int FLAG_RIGHT_END = 0xfe;
    public static final int FLAG_OK = 0x01;
    public static final int FLAG_NO_MOVE = 0xfd;


}
