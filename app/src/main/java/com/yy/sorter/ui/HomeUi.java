package com.yy.sorter.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.kyleduo.switchbutton.SwitchButton;
import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.DigitalDialog;
import com.yy.sorter.utils.TextCacheUtils;

import java.util.Timer;
import java.util.TimerTask;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.ThDevice;
import th.service.helper.ThCommand;
import th.service.helper.ThLogger;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

/**
 * Created by Administrator on 2017/3/17.
 */

public class HomeUi extends BaseUi implements DigitalDialog.Builder.LVCallback {
    private static final String TAG = "HomeUi";
    private PullRefreshLayout layout;
    private TextView nameTxt;
    private TextView tv_feeder,tv_valve,tv_title_scheme;
    private SwitchButton btn_switch_feeder,btn_switch_valve;
    private Button btn_clean,btn_system,btn_save;
    private boolean progressBegin;
    private Timer progressTimer;
    private RelativeLayout modeLayout;
    public HomeUi(Context ctx) {
        super(ctx);
    }

    private MachineData machineData;


    @Override
    protected View onInitView() {
        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_home, null);

            layout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            modeLayout = (RelativeLayout)view.findViewById(R.id.modeLayout);
            nameTxt = (TextView) view.findViewById(R.id.nameTxt);
            tv_title_scheme = (TextView) view.findViewById(R.id.tv_title_scheme);
            tv_valve = (TextView) view.findViewById(R.id.tv_valve);
            tv_feeder = (TextView) view.findViewById(R.id.tv_feeder);
            btn_switch_valve = (SwitchButton) view.findViewById(R.id.btn_switch_valve);
            btn_switch_feeder = (SwitchButton) view.findViewById(R.id.btn_switch_feeder);
            btn_clean = (Button) view.findViewById(R.id.btn_clean);
            btn_system = (Button) view.findViewById(R.id.btn_system);
            btn_save = (Button) view.findViewById(R.id.btn_save);


            layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshDeviceInfo();
                }
            });

            btn_switch_feeder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AbstractDataServiceFactory.getInstance().controlDevice(ThCommand.EXTEND_CONTROL_CMD_FEEDER,isChecked);
                }
            });

            btn_switch_valve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AbstractDataServiceFactory.getInstance().controlDevice(ThCommand.EXTEND_CONTROL_CMD_VALVE,isChecked);
                }
            });

            btn_system.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String titleStr = FileManager.getInstance().getString(6);//6#提示
                    String messageStr = "";
                    String okStr = FileManager.getInstance().getString(7);//7#确定
                    String cancelStr = FileManager.getInstance().getString(8);// 8#取消

                    if(machineData != null)
                    {
                        if(machineData.getStartState() == 1)
                        {
                            messageStr = FileManager.getInstance().getString(126);//126#是否关闭系统
                        }else
                        {
                            messageStr = FileManager.getInstance().getString(125);//125#是否开启系统
                        }

                        AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                                .setTitle(titleStr)
                                .setMessage(messageStr)
                                .setPositiveButton(okStr, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AbstractDataServiceFactory.getInstance().controlDevice(ThCommand.EXTEND_CONTROL_CMD_START,true);
                                    }
                                }).setNegativeButton(cancelStr, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).create();

                        alertDialog.show();


                    }



                }
            });
            btn_clean.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AbstractDataServiceFactory.getInstance().controlDevice(ThCommand.EXTEND_CONTROL_CMD_CLEAR,true);
                }
            });


            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AbstractDataServiceFactory.getInstance().saveMode();
                }
            });

            modeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MiddleManger.getInstance().changeUI(ConstantValues.VIEW_MODE_LIST,43);//43#我的方案
                }
            });



        }

        return view;
    }

    @Override
    protected void refreshPageData() {
        super.refreshPageData();
        refreshDeviceInfo();
    }



    private void refreshDeviceInfo() {
        ThDevice device = AbstractDataServiceFactory.getInstance().getCurrentDevice();
        int lanCountryId = TextCacheUtils.getValueInt(TextCacheUtils.KEY_LAN_COUNTRY_ID, ConstantValues.LAN_COUNTRY_EN);

        if (device != null) {
            if (AbstractDataServiceFactory.isTcp()) {
                AbstractDataServiceFactory.getInstance().login(device.getDeviceSN(), (byte) lanCountryId);
            } else {
                AbstractDataServiceFactory.getInstance().login(null, (byte) lanCountryId);
            }
        }
    }

    @Override
    public void onViewStart() {
        super.onViewStart();

        setLanguage();
        machineData = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();

        if(machineData == null)
            return;

        nameTxt.setText(machineData.getModeName()
                +"["+machineData.getSortModeBig()
                +"-"+machineData.getSortModeSmall()
                +"]");

        initFeederStatus(machineData.getFeederState());
        initValveStatus(machineData.getValveState());

        initSystemButton(machineData.getStartState(),0);
        initCleanButton(machineData.getCleanState());

        ThDevice device = AbstractDataServiceFactory.getInstance().getCurrentDevice();
        MachineData machineData = device.getMachineData();
        int screenVersion =  ConvertUtils.getScreenProtocolVersion(machineData);
        int mobileVersion =  ConvertUtils.getPhoneProtocolVersion(machineData);
        if(mobileVersion < screenVersion){
            //手机版本低于屏版本--提示手机程序要升级才可以正常使用所有功能
            showLongToast(FileManager.getInstance().getString(1022));//1022#手机协议版本过旧，部分功能可能无法使用，升级版本正在开发中，请耐心等待.
        }

    }

    private void initFeederStatus(int feederStatus)
    {
        if(machineData == null)
        {
            return;
        }
        machineData.setFeederState((byte) feederStatus);
        if(feederStatus == 0x01)
        {
            btn_switch_feeder.setCheckedImmediatelyNoEvent(true);
        }else {
            btn_switch_feeder.setCheckedImmediatelyNoEvent(false);
        }
    }

    private void initValveStatus(int valveStatus)
    {
        if(machineData == null)
        {
            return;
        }
        machineData.setValveState((byte) valveStatus);

        if(valveStatus == 0x01)
        {
            btn_switch_valve.setCheckedImmediatelyNoEvent(true);
        }else {
            btn_switch_valve.setCheckedImmediatelyNoEvent(false);
        }

    }

    private void initCleanButton(int cleanStatus) {
        if(machineData == null)
        {
            return;
        }
        machineData.setCleanState((byte) cleanStatus);
        switch (cleanStatus) {
            case 0:
                if (hud != null) {
                    hud.dismiss();
                    hud = null;
                    progressBegin = false;
                    if(progressTimer != null)
                    {
                        progressTimer.cancel();
                    }
                }
                btn_clean.setSelected(false);
                btn_clean.setText(FileManager.getInstance().getString(36)); //36#清灰
                break;
            case 1:
                if (hud == null) {
                    hud = KProgressHUD.create(ctx).setLabel(FileManager.getInstance().getString(37)).show();//37#清灰中
                    hud.setCancellable(false);
                    progressBegin = true;

                    progressTimer = new Timer();
                    progressTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mainUIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (hud != null && progressBegin) {
                                        hud.dismiss();
                                        hud = null;
                                        refreshDeviceInfo();
                                    }
                                }
                            });
                        }
                    }, 30 * 1000);

                }

                btn_clean.setSelected(true);
                btn_clean.setText(FileManager.getInstance().getString(37)); //37#清灰中
                break;
        }
    }
    private void initSystemButton(int systemStatus, int errorStatus) {
        if(machineData == null)
        {
            return;
        }
        machineData.setStartState((byte) systemStatus);
        if (hud != null && machineData.getCleanState() != 1) {
            hud.dismiss();
            hud = null;
            progressBegin = false;
            if(progressTimer != null)
            {
                progressTimer.cancel();
            }
        }
        switch (systemStatus) {
            case 0:
                btn_system.setSelected(false);
                btn_system.setText(FileManager.getInstance().getString(33)); //33#系统关
                break;
            case 1:
                btn_system.setSelected(true);
                btn_system.setText(FileManager.getInstance().getString(34));// 34#系统开
                break;
            case 2:
                btn_system.setSelected(true);
                btn_system.setText(FileManager.getInstance().getString(35)); //35#切换中...
                if (hud == null) {
                    hud = KProgressHUD.create(ctx).setLabel(FileManager.getInstance().getString(35)).show();//35#切换中...
                    hud.setCancellable(false);
                    progressBegin = true;

                    progressTimer = new Timer();
                    progressTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mainUIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (hud != null && progressBegin) {
                                        hud.dismiss();
                                        hud = null;
                                        refreshDeviceInfo();
                                    }
                                }
                            });
                        }
                    }, 30 * 1000);
                }
                break;
            case 3:
                btn_system.setSelected(false);
                btn_system.setText(FileManager.getInstance().getString(33)); //33#系统关
                if (errorStatus == 1)
                    Toast.makeText(ctx, FileManager.getInstance().getString(1004), Toast.LENGTH_SHORT).show(); //1004#配置文件错误
                else if (errorStatus == 2)
                    Toast.makeText(ctx, FileManager.getInstance().getString(1005), Toast.LENGTH_SHORT).show(); //1005#控制板通信异常，不能启动
                else if (errorStatus == 3)
                    Toast.makeText(ctx, FileManager.getInstance().getString(1006), Toast.LENGTH_SHORT).show(); //1006#气压异常，不能启动
                break;
        }



    }

    @Override
    public void onViewStop() {
        super.onViewStop();
    }

    private void setLanguage() {
        btn_save.setText(FileManager.getInstance().getString(90));//90#保存
        tv_feeder.setText(FileManager.getInstance().getString(91));//91#给料器
        tv_valve.setText(FileManager.getInstance().getString(92));//92#喷阀
        tv_title_scheme.setText(FileManager.getInstance().getString(93));//93#当前方案
    }

    @Override
    public void onConfirmClick(int value, int par) {
        if (par == 1) {

        }
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_HOME;
    }

    @Override
    public int getLeaver() {
        return ConstantValues.LEAVER_TAB;
    }

    @Override
    public void onViewBackPress() {
        super.onViewBackPress();
    }

    @Override
    public void receivePacketData(ThPackage packet) {
        if (packet.getType() == ThCommand.CONTROL_CMD) {
            switch (packet.getExtendType()) {
                case 0x01:
                    initFeederStatus(packet.getData1()[0]);
                    break;
                case 0x02:
                    initValveStatus(packet.getData1()[0]);
                    break;
                case 0x03:
                    initSystemButton(packet.getData1()[0], packet.getData1()[1]);
                    break;
                case 0x04:
                    initCleanButton(packet.getData1()[0]);
                    break;
            }

        }else if (packet.getType() == ThCommand.LOGIN_CMD) {
            if (packet.getExtendType() == 0x01) {
                MachineData machineData = ThPackageHelper.parseMachineData(packet);
                ThDevice currentDevice = AbstractDataServiceFactory.getInstance().getCurrentDevice();
                if (currentDevice != null) {
                    currentDevice.setMachineData(machineData);
                }
                onViewStart();
            }
            layout.setRefreshing(false);
        }else if(packet.getType() == ThCommand.MODE_CMD)
        {
            if(packet.getExtendType() == 0x03)
            {
                if(packet.getData1()[0] == 1)
                {
                    showToast(FileManager.getInstance().getString(1029));// 1029#保存成功
                }else {
                    showToast(FileManager.getInstance().getString(1030));//1030#保存失败
                }
            }

        }
    }


}
