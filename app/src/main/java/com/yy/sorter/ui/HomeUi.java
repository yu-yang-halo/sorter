package com.yy.sorter.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.kyleduo.switchbutton.SwitchButton;
import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
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

/**
 * Created by Administrator on 2017/3/17.
 */

public class HomeUi extends BaseUi implements DigitalDialog.Builder.LVCallback {
    private static final String TAG = "HomeUi";
    private PullRefreshLayout layout;
    private TextView nameTxt;
    private TextView tv_feeder,tv_valve;
    private SwitchButton btn_switch_feeder,btn_switch_valve;
    private Button btn_clean,btn_system;
    private boolean systemBegin,cleanBegin;
    private Timer systemTimer,cleanTimer;
    public HomeUi(Context ctx) {
        super(ctx);
    }

    private MachineData machineData;


    @Override
    protected View onInitView() {
        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_home, null);

            layout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            nameTxt = (TextView) view.findViewById(R.id.nameTxt);
            tv_valve = (TextView) view.findViewById(R.id.tv_valve);
            tv_feeder = (TextView) view.findViewById(R.id.tv_feeder);
            btn_switch_valve = (SwitchButton) view.findViewById(R.id.btn_switch_valve);
            btn_switch_feeder = (SwitchButton) view.findViewById(R.id.btn_switch_feeder);
            btn_clean = (Button) view.findViewById(R.id.btn_clean);
            btn_system = (Button) view.findViewById(R.id.btn_system);


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
                    AbstractDataServiceFactory.getInstance().controlDevice(ThCommand.EXTEND_CONTROL_CMD_START,true);
                }
            });
            btn_clean.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AbstractDataServiceFactory.getInstance().controlDevice(ThCommand.EXTEND_CONTROL_CMD_CLEAR,true);
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


    @Override
    public void initViewContent() {

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
        ThDevice device = AbstractDataServiceFactory.getInstance().getCurrentDevice();
        MachineData machineData = device.getMachineData();
        int screenVersion =  ConvertUtils.getScreenProtocolVersion(machineData);
        int mobileVersion =  ConvertUtils.getPhoneProtocolVersion(machineData);
        if(mobileVersion < screenVersion){
            //手机版本低于屏版本--提示手机程序要升级才可以正常使用所有功能
            showLongToast(FileManager.getInstance().getString(371));
        }else {
            //手机版本向下兼容，无需提示

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
                    cleanBegin = false;
                }
                btn_clean.setSelected(false);
                btn_clean.setText(FileManager.getInstance().getString(36)); //36#清灰
                break;
            case 1:
                if (hud == null) {
                    hud = KProgressHUD.create(ctx).setLabel(FileManager.getInstance().getString(37)).show();//37#清灰中
                    hud.setCancellable(false);
                    cleanBegin = true;

                    cleanTimer = new Timer();
                    cleanTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mainUIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (hud != null && cleanBegin) {
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
        if (systemStatus != 2) {
            if (hud != null) {
                hud.dismiss();
                hud = null;
                systemBegin = false;
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
                    hud = KProgressHUD.create(ctx).setLabel(FileManager.getInstance().getString(27)).show();//27#启动中...
                    hud.setCancellable(false);
                    systemBegin = true;

                    systemTimer = new Timer();
                    systemTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mainUIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (hud != null && systemBegin) {
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
    public void update(Object var1, Object var2) {
        super.update(var1, var2);
        /**
         * 更新数据
         */
        ThLogger.debug(TAG, var1 + "" + var2);

        if (var2.getClass() == ThPackage.class) {
            final ThPackage thPackage = (ThPackage) var2;

            mainUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (thPackage.getType() == 0x03) {
                        switch (thPackage.getExtendType()) {
                            case 0x01:
                                initFeederStatus(thPackage.getData1()[0]);
                                break;
                            case 0x02:
                                initValveStatus(thPackage.getData1()[0]);
                                break;
                            case 0x03:
                                initSystemButton(thPackage.getData1()[0], thPackage.getData1()[1]);
                                break;
                            case 0x04:
                                initCleanButton(thPackage.getData1()[0]);
                                break;
                        }


                    }

                }
            });

        }

    }

}
