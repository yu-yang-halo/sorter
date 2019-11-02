package com.yy.sorter.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.CallSuper;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by YUYANG on 2018/11/6.
 * 底部弹窗基类
 */
public abstract class TopPopup<V extends View> implements DialogInterface.OnKeyListener {
    /**
     * The constant MATCH_PARENT.
     */
    public static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    /**
     * The constant WRAP_CONTENT.
     */
    public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    protected Activity activity;
    protected int screenWidthPixels;
    protected int screenHeightPixels;
    private Popup popup;
    private int width = 0, height = 0;
    private boolean isFillScreen = false;
    private boolean isHalfScreen = false;

    /**
     * Instantiates a new Bottom popup.
     *
     * @param activity the activity
     */
    public TopPopup(Activity activity) {
        this.activity = activity;
        WindowManager wm= (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        screenWidthPixels = displayMetrics.widthPixels;
        screenHeightPixels = displayMetrics.heightPixels;
        popup = new Popup(activity);
        popup.setOnKeyListener(this);
    }

    /**
     * Gets view.
     *
     * @return the view
     */
    protected abstract V makeContentView();

    /**
     * 弹出窗显示之前调用
     */
    private void onShowPrepare() {
        setContentViewBefore();
        V view = makeContentView();
        popup.setContentView(view);// 设置弹出窗体的布局
        setContentViewAfter(view);
        if (width == 0 && height == 0) {
            //未明确指定宽高，优先考虑全屏再考虑半屏然后再考虑包裹内容
            width = screenWidthPixels;
            if (isFillScreen) {
                height = MATCH_PARENT;
            } else if (isHalfScreen) {
                height = screenHeightPixels / 2;
            } else {
                height = WRAP_CONTENT;
            }
        }
        popup.setSize(width, height);
    }

    /**
     * 固定高度为屏幕的高
     *
     * @param fillScreen the fill screen
     */
    public void setFillScreen(boolean fillScreen) {
        isFillScreen = fillScreen;
    }
    public void setHalfScreen(boolean halfScreen) {
        isHalfScreen = halfScreen;
    }

    protected void setContentViewBefore() {
    }
    protected void setContentViewAfter(V contentView) {
    }
    public void setAnimationStyle(@StyleRes int animRes) {
        popup.setAnimationStyle(animRes);
    }
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        popup.setOnDismissListener(onDismissListener);
    }
    public void setSize(int width, int height) {
        // fixed: 2016/1/26 修复显示之前设置宽高无效问题
        this.width = width;
        this.height = height;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isShowing() {
        return popup.isShowing();
    }

    @CallSuper
    public void show() {
        onShowPrepare();
        popup.show();
    }

    public void dismiss() {
        popup.dismiss();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public final boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            return onKeyDown(keyCode, event);
        }
        return false;
    }
    public Window getWindow() {
        return popup.getWindow();
    }

    public ViewGroup getRootView() {
        return popup.getRootView();
    }

}
