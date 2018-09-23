package com.yy.sorter.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.DigitalDialog;
import com.yy.sorter.utils.ScreenHelper;
import com.yy.sorter.utils.StringUtils;
import com.yy.sorter.view.KeyboardDigitalEdit;
import com.yy.sorter.view.PageSwitchView;
import com.yy.sorter.view.ThSegmentView;
import com.yy.sorter.view.ThWaveView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.ThCameraPlusRet;
import th.service.data.ThLightRet;
import th.service.data.ThWaveData;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

public class BackgroundUi extends BaseUi implements DigitalDialog.Builder.LVCallback {
    private final byte TYPE_RED = 1;
    private final byte TYPE_GREEN = 2;
    private final byte TYPE_BLUE = 3;
    private final byte TYPE_MAINLIGHT = 4;
    private final byte TYPE_IR = 5;
    private int m_isUseDigitalGain = 0; //1 开启数字增益 0 关闭数字增益
    private int grainType=0;


    private KeyboardDigitalEdit et_bg_led_r;
    private KeyboardDigitalEdit et_bg_led_g;
    private KeyboardDigitalEdit et_bg_led_b;
    private KeyboardDigitalEdit et_bg_led_ir1;
    private KeyboardDigitalEdit et_bg_led_ir2;

    private KeyboardDigitalEdit et_gain_led_r;
    private KeyboardDigitalEdit et_gain_led_g;
    private KeyboardDigitalEdit et_gain_led_b;
    private KeyboardDigitalEdit et_gain_led_ir1;
    private KeyboardDigitalEdit et_gain_led_ir2;

    private KeyboardDigitalEdit et_main_light;
    private KeyboardDigitalEdit et_chute;

    private ThWaveView thwaveView;
    private TextView tvLayer;
    private ThLightRet ret;


    private int m_Channel=0;
    private Timer timer=null;
    private TextView tvChute,tv_main_light,tv_ir1,tv_ir2,text_gain_led,text_bg_led;
    private PageSwitchView pageSwitchView;

    private Button btn_digitalGain;
    private CheckBox ck_adjustAll;
    private final int GAIN_DIGITAL = 1;
    private final int GAIN_ANOLOG= 2;


    public BackgroundUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view==null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_background, null);
            tvChute = (TextView)view.findViewById(R.id.tv_chute);
            tv_main_light = (TextView)view.findViewById(R.id.tv_main_light);
            tv_ir1 = (TextView) view.findViewById(R.id.tv_ir1);
            tv_ir2 = (TextView) view.findViewById(R.id.tv_ir2);
            text_gain_led = (TextView) view.findViewById(R.id.text_gain_led);
            text_bg_led = (TextView) view.findViewById(R.id.text_bg_led);


            thwaveView= (ThWaveView) view.findViewById(R.id.thwaveView);


            et_chute = (KeyboardDigitalEdit)view.findViewById(R.id.et_chute1);
            tvLayer = (TextView)view.findViewById(R.id.tv_layer);

            btn_digitalGain = (Button)view.findViewById(R.id.button_digital_gain);
            ck_adjustAll = (CheckBox)view.findViewById(R.id.ck_adjust_all);


            et_main_light = (KeyboardDigitalEdit)view.findViewById(R.id.et_main_light);


            et_bg_led_r = (KeyboardDigitalEdit)view.findViewById(R.id.et_bg_led_r);
            et_bg_led_g = (KeyboardDigitalEdit)view.findViewById(R.id.et_bg_led_g);
            et_bg_led_b = (KeyboardDigitalEdit)view.findViewById(R.id.et_bg_led_b);
            et_bg_led_ir1 = (KeyboardDigitalEdit)view.findViewById(R.id.et_bg_led_ir1);
            et_bg_led_ir2 = (KeyboardDigitalEdit)view.findViewById(R.id.et_bg_led_ir2);

            et_gain_led_r = (KeyboardDigitalEdit)view.findViewById(R.id.et_gain_led_r);
            et_gain_led_g = (KeyboardDigitalEdit)view.findViewById(R.id.et_gain_led_g);
            et_gain_led_b = (KeyboardDigitalEdit)view.findViewById(R.id.et_gain_led_b);
            et_gain_led_ir1 = (KeyboardDigitalEdit)view.findViewById(R.id.et_gain_led_ir1);
            et_gain_led_ir2 = (KeyboardDigitalEdit)view.findViewById(R.id.et_gain_led_ir2);





            et_main_light.setValue(255,0,0);
            et_main_light.setLVCallback(this);


            et_bg_led_r.setValue(255,0,1);
            et_bg_led_r.setLVCallback(this);
            et_bg_led_g.setValue(255,0,2);
            et_bg_led_g.setLVCallback(this);
            et_bg_led_b.setValue(255,0,3);
            et_bg_led_b.setLVCallback(this);

            et_gain_led_r.setValue(1023,0,4);
            et_gain_led_r.setLVCallback(this);
            et_gain_led_g.setValue(1023,0,5);
            et_gain_led_g.setLVCallback(this);
            et_gain_led_b.setValue(1023,0,6);
            et_gain_led_b.setLVCallback(this);
            et_gain_led_ir1.setValue(31,0,7);
            et_gain_led_ir1.setLVCallback(this);
            et_gain_led_ir2.setValue(31,0,8);
            et_gain_led_ir2.setLVCallback(this);


            et_chute.setValue(10,1,9);
            et_chute.setLVCallback(this);


            pageSwitchView = (PageSwitchView) view.findViewById(R.id.pageSwitchView);
            pageSwitchView.setPageSwitchListenser(new PageSwitchView.PageSwitchListenser() {
                @Override
                public void onPageSwitch(int pageIndex, int flag) {
                    if(flag == PageSwitchView.FLAG_OK)
                    {

                        currentView = (byte) pageIndex;
                        initLight();
                        initStatusView();

                    }
                }
            });
            pageSwitchView.setmNumbers(2);

            tv_ir1.setVisibility(View.GONE);
            tv_ir2.setVisibility(View.GONE);
            et_bg_led_ir1.setVisibility(View.GONE);
            et_bg_led_ir2.setVisibility(View.GONE);
            et_gain_led_ir1.setVisibility(View.GONE);
            et_gain_led_ir2.setVisibility(View.GONE);

        }
        return view;
    }


    @Override
    public int getID() {
        return ConstantValues.VIEW_BACKGROUND;
    }

    @Override
    public void receivePacketData(ThPackage packet) {
        if (packet.getType() == ThCommand.LIGHT_CMD) {
            if (packet.getExtendType() == 0x01) {
                ret = ThPackageHelper.parseThLightRet(packet);
                initLight();
            }
            else if (packet.getExtendType() == 0x02)
            {
                byte view = packet.getData1()[1];
                if(ret != null)
                {
                    switch (packet.getData1()[2])
                    {
                        case 1:
                            ret.getFstsndReds()[view] = packet.getData1()[3];
                            break;
                        case 2:
                            ret.getFstsndGreens()[view] = packet.getData1()[3];
                            break;
                        case 3:
                            ret.getFstsndBlues()[view] = packet.getData1()[3];
                            break;
                        case 4:
                            ret.getFstsndMainLights()[view] = packet.getData1()[3];
                            break;
                    }
                }

                setLight(packet.getData1()[2],packet.getData1()[3]);
            }
        }
        else if(packet.getType()==ThCommand.WAVE_CMD){
            //处理波形数据
            ThWaveData thWaveRet=ThPackageHelper.parseWaveData(packet);
            thWaveRet.setAlgorithm((byte) 0);

            thwaveView.setThWaveRet(thWaveRet);
            thwaveView.invalidate();

        }else if(packet.getType() == ThCommand.CAMERA_GAIN_CMD) {

        }


    }

    @Override
    protected void refreshPageData() {
        super.refreshPageData();
        onViewStart();
    }

    private void updateDataRequest()
    {
        byte layer= AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        AbstractDataServiceFactory.getInstance().requestLightInfo(layer);

    }

    public void startTimer()
    {
        ScreenHelper.screenON((Activity) ctx);
        if(timer!=null){
            timer.cancel();
        }
        timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getWave();
            }
        },1000,1000);
    }

    private void getWave()
    {

        byte[] params=new byte[]{(byte)0,currentLayer,currentView, (byte) m_Channel,ThCommand.WAVE_TYPE_BACKGROUN_LIGHT};

        AbstractDataServiceFactory.getInstance().requestWave(
                (byte) ThCommand.WAVE_TYPE_BACKGROUN_LIGHT,
                params);


    }

    public void cancelTimer()
    {
        ScreenHelper.screenOFF((Activity) ctx);
        if (timer != null)
        {
            timer.cancel();
        }

    }
    @Override
    public void onViewStart() {
        super.onViewStart();
        setLanguage();
        updateDataRequest();

        initStatusView();

        thwaveView.clear();
        MachineData machineData = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();


        et_chute.setMax(machineData.getChuteNumber());
        if(m_Channel>=machineData.getChuteNumber()){
            m_Channel=0;
            et_chute.setText(String.valueOf(1));
        }

        startTimer();
        OnSystemStatusChanged();
    }

    private void OnSystemStatusChanged()
    {
        MachineData machineData = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();
        int SystemStart = machineData.getStartState();
        if(SystemStart == 1)
        {
            et_bg_led_r.setEnabled(false);
            et_bg_led_g.setEnabled(false);
            et_bg_led_b.setEnabled(false);
            et_bg_led_ir1.setEnabled(false);
            et_bg_led_ir2.setEnabled(false);

            et_gain_led_r.setEnabled(false);
            et_gain_led_g.setEnabled(false);
            et_gain_led_b.setEnabled(false);
            et_gain_led_ir1.setEnabled(false);
            et_gain_led_ir2.setEnabled(false);
        }
        else
        {
            et_bg_led_r.setEnabled(true);
            et_bg_led_g.setEnabled(true);
            et_bg_led_b.setEnabled(true);
            et_bg_led_ir1.setEnabled(true);
            et_bg_led_ir2.setEnabled(true);

            et_gain_led_r.setEnabled(true);
            et_gain_led_g.setEnabled(true);
            et_gain_led_b.setEnabled(true);
            et_gain_led_ir1.setEnabled(true);
            et_gain_led_ir2.setEnabled(true);
        }
    }

    @Override
    public void onActivityStop() {
        super.onActivityStop();

        cancelTimer();

    }

    @Override
    public void onViewStop() {
        super.onViewStop();
        cancelTimer();

    }
    private void initStatusView(){
        currentLayer= AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        int layerNumbers = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getLayerNumber();


        StringBuilder sb=new StringBuilder();
        if(layerNumbers > 1)
        {
            sb.append(StringUtils.getLayerStr(currentLayer));
            sb.append(" >> ");
        }
        //75#前视
        //76#后视
        sb.append(currentView== 0? FileManager.getInstance().getString(75):
                FileManager.getInstance().getString(76));

        tvLayer.setText(sb);

    }

    private void setLanguage()
    {
        tvChute.setText(FileManager.getInstance().getString(79)); //79#料槽
        ck_adjustAll.setText(FileManager.getInstance().getString(122));//122#整体调整
        tv_main_light.setText(FileManager.getInstance().getString(123));//123#主灯
        text_gain_led.setText(FileManager.getInstance().getString(124));//124#相机增益
        text_bg_led.setText(FileManager.getInstance().getString(80));//80#背景灯

    }

    @Override
    public void onReloadLayerView() {
        super.onReloadLayerView();
        updateDataRequest();
        initStatusView();
    }

    @Override
    public void onConfirmClick(int value,int par)
    {
        currentLayer=AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        byte isAll = 0;
        if (ck_adjustAll.isChecked())
            isAll = 1;
        else
            isAll = 0;

        byte grainType = 0;
        if (btn_digitalGain.isSelected())
            grainType = 1;
        else
            grainType = 0;

        switch (par)
        {
            case 0: //主灯
                AbstractDataServiceFactory.getInstance().setLightData(currentLayer,currentView,(byte)4,(byte)value);
                break;
            case 9: //通道
                et_chute.setText(String.valueOf(value));
                if(value-1<0){
                    m_Channel=0;
                }else{
                    m_Channel=value-1;
                }
                updateDataRequest();
                break;
            case 1://背景灯R
                et_bg_led_r.setText(Integer.toString(value));
                AbstractDataServiceFactory.getInstance().setLightData(currentLayer,currentView,(byte)1,(byte)value);
                break;
            case 2: //背景灯G
                et_bg_led_g.setText(Integer.toString(value));
                AbstractDataServiceFactory.getInstance().setLightData(currentLayer,currentView,(byte)2,(byte)value);
                break;
            case 3://背景灯B
                et_bg_led_b.setText(Integer.toString(value));
                AbstractDataServiceFactory.getInstance().setLightData(currentLayer,currentView,(byte)3,(byte)value);

                break;
            case 4://增益 R

                break;
            case 5://增益 G

                break;
            case 6: //增益 B

                break;
            case 7://增益 IR 1

                break;
            case 8://增益 IR 2

                break;

        }

    }


    private void initLight()
    {

        if(ret == null)
        {
            return;
        }

        int view = currentView;
        if(view<0 || view >1)
        {
            return;
        }

        byte red[] = ret.getFstsndReds();
        byte green[] = ret.getFstsndGreens();
        byte blue[] = ret.getFstsndBlues();
        byte mainLight[] = ret.getFstsndMainLights();

        et_bg_led_r.setText(Integer.toString(ConvertUtils.unsignByteToInt(red[view])));
        et_bg_led_g.setText(Integer.toString(ConvertUtils.unsignByteToInt(green[view])));
        et_bg_led_b.setText(Integer.toString(ConvertUtils.unsignByteToInt(blue[view])));
        et_main_light.setText(Integer.toString(ConvertUtils.unsignByteToInt(mainLight[view])));



    }

    private void setLight(byte type,byte data)
    {    //1/2/3/4/5 :r/g/b/mainLight/ir

        switch (type)
        {
            case TYPE_RED:
                et_bg_led_r.setText(Integer.toString(data&0xFF));
                break;
            case TYPE_GREEN:
                et_bg_led_g.setText(Integer.toString(data&0xFF));
                break;
            case TYPE_BLUE:
                et_bg_led_b.setText(Integer.toString(data&0xFF));
                break;
            case TYPE_MAINLIGHT:
                et_main_light.setText(Integer.toString(data&0xFF));
                break;
        }

    }
}
