package com.yy.sorter.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yy.sorter.activity.BuildConfig;
import com.yy.sorter.utils.ConvertUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import th.service.data.ThConfig;

/**
 * Created by Administrator on 2017/4/5.
 * 语言包管理模块
 * 负责：
 *     语言包的下载到本地目录
 *
 *
 */

public class FileManager {
    private static final String TAG="FileManager";
    private static FileManager instance=new FileManager();
    private Map<Integer,String> lanIndexs=new HashMap<>();
    private static final String CONFIG_FILE_NAME="config.json";
    private static final String INSTALL_APK="thcolorsort.apk";
    private static final String ZH_LAN="strings_zh.txt";
    private static final String US_LAN="strings_us.txt";
    private static final String TH_APK_DIR="th_apk";

    private Handler mainHandler=new Handler(Looper.getMainLooper());

    Context ctx;
    private FileManager(){
    }
    public static FileManager getInstance(){
        if(instance==null){
             instance=new FileManager();
        }
        return instance;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public String getString(int index){
        String value=lanIndexs.get(index);
        return value==null?"":value;

    }
    public String getString(int index,String placeHolder){
        String value = getString(index);

        if(value==null || value.trim().isEmpty())
        {
            value = placeHolder;
        }

        return value;

    }

    public boolean switchLanguage(String lanPacketFile,ILanguageHandler handler){
        readLangugageFile(lanPacketFile,handler);
        return true;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    public void openFile() {
        File localFile=getStorageApkFile(INSTALL_APK);

        if(localFile.exists()) {

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);

            if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri =
                        FileProvider.getUriForFile(ctx, BuildConfig.AUTH_PROVIDER, localFile);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            }else{
                intent.setDataAndType(Uri.fromFile(localFile),
                        "application/vnd.android.package-archive");
            }

            ctx.startActivity(intent);
        }else{
            Toast.makeText(ctx,FileManager.getInstance().getString(257),Toast.LENGTH_SHORT).show();//257#APK文件不存在
        }

    }
    File apkFile;
    FileOutputStream apkFos = null;
    File lanFile;
    FileOutputStream lanFos = null;

    public void writeErrorLogToLocal(String message,String fileName){
        File  logFile=getStorageApkFile(fileName);
        if(logFile.exists()) {
            logFile.delete();
        }

        try {
            logFile.createNewFile();
            OutputStream os=new FileOutputStream(logFile);
            byte[] bytes=message.getBytes();
            os.write(bytes,0,bytes.length);
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void saveApkFile(byte[] caches,int type) {
        try {
        if(type==0){
            apkFile=getStorageApkFile(INSTALL_APK);
            if(apkFile.exists()) {
                apkFile.delete();
            }
            apkFile.createNewFile();
            apkFos=new FileOutputStream(apkFile);
        }

        if(type==1||type==2){
            if(apkFos!=null){
                apkFos.write(caches,0,caches.length);
                apkFos.flush();
            }
        }
        if(type==2){
            if(apkFos!=null){
                apkFos.close();
            }
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  openFile();
                }
            },500);
        }


    } catch (IOException e) {
        e.printStackTrace();
    }

    }
    public File getStorageApkFile(String fileName) {

        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), TH_APK_DIR);

        if(!dir.exists()){
            dir.mkdirs();
        }

        return new File(dir,fileName);
    }
    public void saveLanguageFile(byte[] caches,int type,String fileName) {
        try {
            if(type==0){
                lanFile=new File(ctx.getFilesDir(),fileName);

                if(lanFile.exists()) {
                    lanFile.delete();
                }
                lanFile.createNewFile();
                lanFos=ctx.openFileOutput(fileName,Context.MODE_PRIVATE);
            }
            if(type==1||type==2){
                lanFos.write(caches,0,caches.length);
                lanFos.flush();
            }
            if(type==2){
                lanFos.close();
                fetchLocalFile(new FileInputStream(lanFile),fileName);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void bufferStreamCopy(File source, File target) {
        InputStream fis = null;
        OutputStream fos = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(target);
            byte[] buf = new byte[4096];
            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param is
     * @param fileName
     * @return
     *
     * 如果提供is流，则更新本地文件，存在更新，不存在创建
     * 如果不提供is流，则返回本地文件，不存在返回为空
     */

    private File fetchLocalFile(InputStream is,String fileName){
        File localFile=new File(ctx.getFilesDir(),fileName);
        boolean isFirst=true;

        FileOutputStream fos = null;
        if(is==null){
            if(localFile.exists()){
                return  localFile;
            }else{
                return  null;
            }
        }
        if(localFile.exists()) {
           localFile.delete();
        }
        try {
            localFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
                System.out.println(localFile);
                fos=new FileOutputStream(localFile);
                byte[] buffer=new byte[1024];
                int len=-1;
                while ((len=is.read(buffer))!=-1){
                    if(isFirst){
                        if(len>=3){
                            if(buffer[0]==(byte)0xEF&&buffer[1]==(byte)0xBB&&buffer[2]==(byte)0xBF){
                                fos.write(buffer,3,len-3);
                            }else{
                                fos.write(buffer,0,len);
                            }
                        }

                        isFirst=false;
                    }else{
                        fos.write(buffer,0,len);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        if(isFirst){
            return null;
        }

        return localFile;

    }
    /**
     * 保存ThConfig文件到本地
     */
    public ThConfig saveConfig(ThConfig serverConfig){
       return readLocalConfig(objectToInputStream(serverConfig));
    }
    private boolean localFileIsExists(String fileName){
        File localFile=new File(ctx.getFilesDir(),fileName);


        return  localFile.exists();
    }
    public void copyAssetsFileToLocal(){
        boolean isHasConfigFile=localFileIsExists(CONFIG_FILE_NAME);
        boolean isHasZHLAN=localFileIsExists(ZH_LAN);
        boolean isHasUSLAN=localFileIsExists(US_LAN);
        if(!isHasConfigFile){
            cachesToLocal(CONFIG_FILE_NAME);
        }
        if(!isHasUSLAN){
            cachesToLocal(US_LAN);
        }
        if(!isHasZHLAN){
            cachesToLocal(ZH_LAN);
        }

    }
    private void cachesToLocal(String fileName){
        InputStream is= null;
        try {
            is = ctx.getAssets().open(fileName);
            fetchLocalFile(is,fileName);
        } catch (IOException e) {
            //find not
        }

    }


    /**
     *读取文件到内存对象 并保存输入流数据
     */
    private ThConfig readLocalConfig(InputStream is){
        File localFile=fetchLocalFile(is,CONFIG_FILE_NAME);
        if(localFile==null){
            return null;
        }
        FileInputStream fis= null;
        try {
            fis = new FileInputStream(localFile);
            BufferedReader br=new BufferedReader(new InputStreamReader(fis,"UTF-8"));
            String kv=null;
            StringBuilder sb=new StringBuilder();
            while ((kv=br.readLine())!=null){
                sb.append(kv);
            }
            Gson gson=new Gson();
            ThConfig thConfig=gson.fromJson(sb.toString(), ThConfig.class);
            return thConfig;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 对象转化流
     * @param serverConfig
     * @return
     */
    private InputStream objectToInputStream(ThConfig serverConfig){
        InputStream is=null;
        if(serverConfig!=null){
            Gson gson=new Gson();
            String serverConfigJSON=gson.toJson(serverConfig);
            try {
                is=new ByteArrayInputStream(serverConfigJSON.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                is=null;
            }
        }
        return is;
    }

    /**
     * 更新文件到本地
     * @param handler
     * @param serverConfig
     */
    public void readLocalConfigFile(final IConfigHandler handler, final ThConfig serverConfig){

        new Thread(new Runnable() {
            @Override
            public void run() {
                    ThConfig thConfig=readLocalConfig(null);
                    if(serverConfig!=null){
                        if(thConfig==null){
                            //没有本地配置文件
                            thConfig=readLocalConfig(objectToInputStream(serverConfig));
                        }else{
                            //存在本地配置文件则更新
                            thConfig=thConfig.transferConfig(serverConfig);
                            thConfig=readLocalConfig(objectToInputStream(thConfig));
                        }
                    }

                    if(handler!=null){
                        handler.onComplelete(thConfig);
                    }
            }
        }).start();
    }
    public void readLocalFile(final String fileName, final ILanguageHandler handler){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File localFile=fetchLocalFile(ctx.getAssets().open(fileName),fileName);
                    if(localFile==null){
                        if(handler!=null){
                            handler.onComplelete(false);
                        }
                        return;
                    }
                    FileInputStream fis=new FileInputStream(localFile);
                    BufferedReader br=new BufferedReader(new InputStreamReader(fis,"UTF-8"));
                    String kv=null;
                    boolean isFirst=true;
                    while ((kv=br.readLine())!=null){

                        String[] arrs=kv.split("\\#");
                        if(arrs!=null&&arrs.length==2){
                            lanIndexs.put(Integer.parseInt(arrs[0].trim()),arrs[1]);
                        }
                    }
                    if(handler!=null){
                        handler.onComplelete(lanIndexs.size()>0);
                    }

                } catch (IOException e) {
                    if(handler!=null){
                        handler.onComplelete(false);
                    }
                }
            }
        }).start();

    }


    public void readLangugageFile(final String fileName, final ILanguageHandler handler){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File localFile=fetchLocalFile(null,fileName);
                    if(localFile==null){
                        if(handler!=null){
                            handler.onComplelete(false);
                        }
                        return;
                    }
                    FileInputStream fis=new FileInputStream(localFile);
                    BufferedReader br=new BufferedReader(new InputStreamReader(fis,"UTF-8"));
                    String kv=null;
                    boolean isFirst=true;
                    while ((kv=br.readLine())!=null){
                        String[] arrs=kv.split("\\#");
                        if(arrs!=null&&arrs.length==2){
                            lanIndexs.put(ConvertUtils.toIntExt(arrs[0].trim()),arrs[1]);
                        }
                    }
                    if(handler!=null){
                        handler.onComplelete(lanIndexs.size()>0);
                    }

                } catch (IOException e) {
                    if(handler!=null){
                        handler.onComplelete(false);
                    }
                }
            }
        }).start();

    }

    public static interface ILanguageHandler{
        public void onComplelete(boolean success);
    }
    public static interface IConfigHandler{
        public void onComplelete(ThConfig thConfig);
    }
}
