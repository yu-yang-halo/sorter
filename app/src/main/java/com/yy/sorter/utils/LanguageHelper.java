package com.yy.sorter.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.yy.sorter.activity.BuildConfig;
import com.yy.sorter.activity.LaunchActivity;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.permission.PermissionUtils;
import com.yy.sorter.ui.LanUI;
import com.yy.sorter.ui.base.BaseUI;
import com.yy.sorter.view.LoadProgress;

import java.util.Locale;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.ThConfig;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

/**
 * Created by Administrator on 2017/3/13.
 * 国际化语言切换实现
 */

public class LanguageHelper {
    private static String currentDownloadURL="";
    private static int currentCountryId=0;

    public static String getCurrentDownloadURL() {
        return currentDownloadURL;
    }

    public static void setCurrentDownloadURL(String currentDownloadURL) {
        LanguageHelper.currentDownloadURL = currentDownloadURL;
    }

    public static int getCurrentCountryId() {
        return currentCountryId;
    }

    public static void setCurrentCountryId(int currentCountryId) {
        LanguageHelper.currentCountryId = currentCountryId;
    }

    public static void switchLanguage(Context ctx, Locale locale) {
        Resources resources = ctx.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale =locale;
        resources.updateConfiguration(config, dm);
    }
    public static void resetApp(Context ctx){
        Intent intent = new Intent(ctx, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ctx.startActivity(intent);
        // 杀掉进程
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
    }

    private static  void showToast(Context ctx,String message){
        Toast.makeText(ctx,message,Toast.LENGTH_SHORT).show();
    }

    static LoadProgress loadProgress;

    public static void onCallbackFileHandler(final Context ctx, final ThPackage thPackage, BaseUI currentUI, final IProgressListenser iProgressListenser){

        if(!MiddleManger.getInstance().isCurrentUI(currentUI)){
            return;
        }
        if(!AbstractDataServiceFactory.isTcpFile()){
            return;
        }
        if(thPackage.getType()== ThCommand.TCP_REQ_DOWNLOAD_WHAT_FILE){
            if(thPackage.getExtendType()==1){
                switch (thPackage.getData1()[0]){
                    case ThCommand.DOWNLOAD_FILE_TYPE_CONFIG:
                        ThPackageHelper.parseThConfig(thPackage, 0);
                        break;
                    case ThCommand.DOWNLOAD_FILE_TYPE_APK:
                        ThPackageHelper.parseFileData(thPackage,0);
                        byte[] bytes=new byte[4];
                        System.arraycopy(thPackage.getData1(),1,bytes,0,bytes.length);
                        int totalLength=ConvertUtils.bytesToInt(bytes);
                        loadProgress= LoadProgress.getProgress(ctx,FileManager.getInstance().getString(248));//248#应用正在下载...
                        loadProgress.setMax(totalLength);
                        loadProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                showToast(ctx,FileManager.getInstance().getString(247));//247#已取消
                                AbstractDataServiceFactory.getFileDownloadService().closeFileSocket();
                            }
                        });
                        loadProgress.show();
                        break;
                    case ThCommand.DOWNLOAD_FILE_TYPE_LANGUAGE:
                        ThPackageHelper.parseLanData(thPackage,0, LanguageHelper.getCurrentDownloadURL());
                        break;

                }
                AbstractDataServiceFactory.getFileDownloadService().requestDownloadFile();

            }else{
                showToast(ctx,FileManager.getInstance().getString(256));//256#没有找到文件
            }
        }else if(thPackage.getType()==ThCommand.TCP_REQ_DOWNLOAD_FILE) {
            if (thPackage.getExtendType() == 0) {
                switch (thPackage.getData1()[0]){
                    case ThCommand.DOWNLOAD_FILE_TYPE_CONFIG:
                        ThPackageHelper.parseThConfig(thPackage, 1);
                        break;
                    case ThCommand.DOWNLOAD_FILE_TYPE_APK:
                        ThPackageHelper.parseFileData(thPackage, 1);
                        loadProgress.setProgress(thPackage.getLen());
                        break;
                    case ThCommand.DOWNLOAD_FILE_TYPE_LANGUAGE:
                        ThPackageHelper.parseLanData(thPackage,1, LanguageHelper.getCurrentDownloadURL());
                        break;
                }
                AbstractDataServiceFactory.getFileDownloadService().requestDownloadFile();

            } else {
                //文件结束
                switch (thPackage.getData1()[0]){
                    case ThCommand.DOWNLOAD_FILE_TYPE_CONFIG:
                        ThConfig thConfig = ThPackageHelper.parseThConfig(thPackage, 2);
                        System.out.println("服务器端配置文件" + thConfig);
                        if (thConfig != null) {
                            FileManager.getInstance().readLocalConfigFile(new FileManager.IConfigHandler() {
                                @Override
                                public void onComplelete(ThConfig thConfig) {
                                    if(iProgressListenser!=null){
                                        iProgressListenser.onFinished(thPackage.getData1()[0],true,thConfig);
                                    }

                                }
                            }, thConfig);


                            boolean isLastestNew=true;

                            if(thConfig.getAndroid().get(BuildConfig.VKEY)!=null){
                                String newVersion= thConfig.getAndroid().get(BuildConfig.VKEY).get("version");
                                isLastestNew=StringUtils.getAppVersionName(ctx).equals(newVersion);
                            }

                            if(iProgressListenser!=null){
                                iProgressListenser.onVersionUpdate(isLastestNew);
                            }

                            if (!isLastestNew) {

                                if(!(MiddleManger.getInstance().getCurrentUI() instanceof LanUI)){

                                    AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                                            .setTitle(FileManager.getInstance().getString(6))//6#提示
                                            .setPositiveButton(FileManager.getInstance().getString(7), //7#确定
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            PermissionUtils.requestPermission((Activity) ctx, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, new PermissionUtils.PermissionGrant() {
                                                                @Override
                                                                public void onPermissionGranted(int requestCode) {
                                                                    if(requestCode== PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE){

                                                                        if(FileManager.getInstance().isExternalStorageWritable()){
                                                                            AbstractDataServiceFactory.getFileDownloadService().requestDownloadWhatFile(
                                                                                    (byte) ThCommand.BUILD_VERSION,
                                                                                    ThCommand.DOWNLOAD_FILE_TYPE_APK, null);
                                                                        }else{
                                                                            //9#请检查存储是否可用
                                                                            showToast(ctx,FileManager.getInstance().getString(9));
                                                                        }


                                                                    }

                                                                }
                                                            });




                                                        }
                                                    }).setNegativeButton(FileManager.getInstance().getString(8), //8#取消
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    }).setMessage(FileManager.getInstance().getString(10)).create();//10#发现新版本

                                    alertDialog.show();



                                }

                            }

                        }
                        break;
                    case ThCommand.DOWNLOAD_FILE_TYPE_APK:
                        ThPackageHelper.parseFileData(thPackage, 2);
                        loadProgress.setProgress(thPackage.getLen());
                        showToast(ctx, FileManager.getInstance().getString(11));//11#下载完成
                        break;
                    case ThCommand.DOWNLOAD_FILE_TYPE_LANGUAGE:
                        ThPackageHelper.parseLanData(thPackage,2, LanguageHelper.getCurrentDownloadURL());
                        showToast(ctx,FileManager.getInstance().getString(11));//11#下载完成
                        break;
                }
                if(iProgressListenser!=null){
                    iProgressListenser.onFinished(thPackage.getData1()[0],true,null);
                }

            }
        }
    }

    public static interface IProgressListenser{
        public void onFinished(byte fileType,boolean success,ThConfig config);
        public void onVersionUpdate(boolean isLastestNew);
    }


}
