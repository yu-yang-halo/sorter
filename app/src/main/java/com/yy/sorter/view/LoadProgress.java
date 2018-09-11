package com.yy.sorter.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;

/**
 * Created by Administrator on 2017/6/2.
 */

public final class LoadProgress {
    private Dialog dialog;
    private Window window;
    private Context ctx;

    private View contentView;
    private ProgressBar progressBar;
    private TextView progressTxt;
    private TextView descriptionTxt;
    private Button   cancelDownloadBtn;


    private boolean isFisished;
    private int progressCount;
    private LoadProgress(){

    }
    public static LoadProgress getProgress(Context ctx,String description){
        LoadProgress loadProgress=new LoadProgress();
        loadProgress.init(ctx,description);
        return loadProgress;
    }
    private void init(Context ctx,String description){
        this.ctx=ctx;
        dialog=new Dialog(ctx);
        window=dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);//触摸屏幕取消窗体
        dialog.setCancelable(false);//按返回键取消窗体

        window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.Animation_Popup);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.requestFeature(Window.FEATURE_NO_TITLE);
        contentView= LayoutInflater.from(ctx).inflate(R.layout.item_load_progress,null);
        progressBar= (ProgressBar) contentView.findViewById(R.id.progressBar);
        progressTxt= (TextView) contentView.findViewById(R.id.progressTxt);
        descriptionTxt= (TextView) contentView.findViewById(R.id.descriptionTxt);
        cancelDownloadBtn= (Button) contentView.findViewById(R.id.cancelDownload);

        window.setContentView(contentView);
        WindowManager wm= (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        setSize(displayMetrics.widthPixels,displayMetrics.heightPixels);

        if(description!=null){
            descriptionTxt.setText(description);
        }
        progressCount=0;
        progressBar.setProgress(0);
        setProgress(0);

        cancelDownloadBtn.setText(FileManager.getInstance().getString(131));//131#取消
        cancelDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

    }

    public void setSize(int width, int height) {
        ViewGroup.LayoutParams params = contentView.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(width, height);
        } else {
            params.width = width;
            params.height = height;
        }
        contentView.setLayoutParams(params);
    }
    public void setOnCancelListener(DialogInterface.OnCancelListener listener){
        dialog.setOnCancelListener(listener);
    }
    public boolean isFisished() {
        return progressBar.getProgress()==progressBar.getMax();
    }

    public void setMax(int max){

        progressBar.setMax(max);
        progressCount=0;
        progressBar.setProgress(progressCount);

    }
    public void setProgress(int progress){
        progressCount+=progress;
        progressBar.setProgress(progressCount);
        progressTxt.setText(String.format("%.1f%%",progressCount*100.0f/progressBar.getMax()));

        if(isFisished()){
            dismiss();
        }
    }
    public void show(){
        dialog.show();

    }
    public void dismiss(){
        dialog.dismiss();
    }
    public void cancel(){
        dialog.cancel();
    }
}
