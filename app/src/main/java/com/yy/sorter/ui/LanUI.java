package com.yy.sorter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.yy.sorter.activity.R;
import com.yy.sorter.adapter.LanAdapter;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.LanguageHelper;
import com.yy.sorter.view.LoadProgress;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.ThConfig;
import th.service.helper.IPUtils;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;

/**
 * Created by Administrator on 2017/4/6.
 * 语言选择界面
 */

public class LanUi extends BaseUi {
    private View view;
    private ListView lanListView;
    private List<ThConfig.LanguageVersion> thLanList;
    private LanAdapter lanAdapter;
    private PullRefreshLayout layout;
    private LoadProgress loadProgress;
    private boolean isConnectToInternet=false;
    private ExecutorService executorService= Executors.newCachedThreadPool();
    public LanUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view==null){
            view=LayoutInflater.from(ctx).inflate(R.layout.ui_lan,null);
            lanListView= (ListView) view.findViewById(R.id.lanListView);
            layout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);


            lanAdapter=new LanAdapter(ctx,null);
            lanListView.setAdapter(lanAdapter);
            layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    /**
                     * 更新配置文件
                     */
                    AbstractDataServiceFactory
                            .getFileDownloadService()
                            .requestDownloadWhatFile((byte) ThCommand.BUILD_VERSION,
                                    ThCommand.DOWNLOAD_FILE_TYPE_CONFIG,null);

                    mainUIHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reloadUI();
                        }
                    },2500);


                }
            });
        }

        reloadUI();
        return view;
    }

    private void reloadUI(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                isConnectToInternet=IPUtils.netAvaliable();
                mainUIHandler.post(new Runnable() {
                     @Override
                     public void run() {
                         lanAdapter.setConnectToInternet(isConnectToInternet);
                         lanAdapter.notifyDataSetChanged();
                         layout.setRefreshing(false);
                     }
                });

            }
        });
    }

    @Override
    public void onViewStart() {
        super.onViewStart();

        FileManager.getInstance().readLocalConfigFile(new FileManager.IConfigHandler() {
            @Override
            public void onComplelete(final ThConfig thConfig) {
                mainUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(thConfig!=null){

                            lanAdapter.setNewConfig(thConfig);
                            lanAdapter.notifyDataSetChanged();
                        }
                        layout.setRefreshing(false);
                    }
                });

            }
        },null);




    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_LAN;
    }

    @Override
    public int getLeaver() {
        return ConstantValues.LEAVER_DEFAULT;
    }


    @Override
    public void update(Object var1, final Object var2) {
        super.update(var1, var2);
        mainUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if(var2.getClass()== ThPackage.class){
                    ThPackage thPackage= (ThPackage) var2;
                    /**
                     * 网络下载逻辑处理
                     */
                    LanguageHelper.onCallbackFileHandler(ctx, thPackage, LanUi.this, new LanguageHelper.IProgressListenser() {
                        @Override
                        public void onFinished(byte fileType, boolean success,ThConfig config) {
                            if(success){
                                if(fileType==ThCommand.DOWNLOAD_FILE_TYPE_LANGUAGE){
                                    lanAdapter.notifyDownloadSuccess();
                                }else if(fileType==ThCommand.DOWNLOAD_FILE_TYPE_CONFIG){

                                    onViewStart();
                                }
                            }
                        }

                        @Override
                        public void onVersionUpdate(boolean isLastestNew) {

                        }
                    });

                }
            }
        });

    }
}
