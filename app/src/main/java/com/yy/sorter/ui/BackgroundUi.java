package com.yy.sorter.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.yy.sorter.view.ThWaveView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.ThLightRet;
import th.service.data.ThWaveData;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

public class BackgroundUi extends BaseUi implements View.OnClickListener,DigitalDialog.Builder.LVCallback {
    private final byte FRONT = 0;
    private final byte REAR = 1;
    private final byte TYPE_RED = 1;
    private final byte TYPE_GREEN = 2;
    private final byte TYPE_BLUE = 3;
    private final byte TYPE_MAINLIGHT = 4;
    private final byte TYPE_IR = 5;

    private final int LIGHT = 3;
    private int m_iView=0; //0 前视 1 后视
    private int m_irType = 0;//0 单/双红外 2 分时
    private int m_isUseDigitalGain = 0; //1 开启数字增益 0 关闭数字增益
    private int grainType=0;
    private LinearLayout layout_light;
    private LinearLayout layoutBackgroundRear;

    private Button btn_irFrontLight;
    private Button btn_irRearLight;
    private Button btn_frontLed;
    private Button btn_rearLed;


    private KeyboardDigitalEdit et_frontLedR;
    private KeyboardDigitalEdit et_rearLedR;
    private KeyboardDigitalEdit et_frontLedG;
    private KeyboardDigitalEdit et_rearLedG;
    private KeyboardDigitalEdit et_frontLedB;
    private KeyboardDigitalEdit et_rearLedB;
    private KeyboardDigitalEdit et_frontLedIR1;
    private KeyboardDigitalEdit et_rearLedIR1;
    private KeyboardDigitalEdit et_frontLedIR2;
    private KeyboardDigitalEdit et_rearLedIR2;

    private KeyboardDigitalEdit et_frontLight;
    private KeyboardDigitalEdit et_rearLight;

    private ThWaveView thwaveView;
    private TextView tv_ir1;
    private TextView tv_ir2;
    private TextView tv_backgroundLight;
    private TextView tvLayer=null;
    private ThLightRet ret;
    private int index = 0;

    private KeyboardDigitalEdit et_chute;
    private int m_Channel=0;
    private Timer timer=null;
    private LinearLayout layoutIR;
    private TextView tvChute;
    private TextView tvLight;
    private TextView tvFrontLight;
    private TextView tvRearLight;
    private TextView tvTitleIR;
    private PageSwitchView pageSwitchView;

    private KeyboardDigitalEdit et_front_light_ir;
    private KeyboardDigitalEdit et_rear_light_ir;
    private TextView tv_front_light_ir;
    private TextView tv_rear_light_ir;
    private LinearLayout layout_ir_light_1,layout_ir_light_2;
    public BackgroundUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view==null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_background, null);

            initView();

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
                initLight(ret);
                updateLedEditLimit();
            }
            else if (packet.getExtendType() == 0x02)
            {
                setLight(packet.getData1()[1],packet.getData1()[2],packet.getData1()[3]);
            }
        }
        else if(packet.getType()==ThCommand.WAVE_CMD){
            //处理波形数据
            ThWaveData thWaveRet=ThPackageHelper.parseWaveData(packet);
            thWaveRet.setAlgorithm((byte) 0);

            thwaveView.setThWaveRet(thWaveRet);
            thwaveView.invalidate();

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

        updateView();
        initStatusView();
        updateIrRearLight();
        updateIrFrontLight();

        thwaveView.clear();
        updateUI();
        MachineData machineData = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();


        et_chute.setValue(machineData.getChuteNumber(),1,12);
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
            et_frontLight.setEnabled(false);
            et_frontLedR.setEnabled(false);
            et_frontLedB.setEnabled(false);
            et_frontLedIR1.setEnabled(false);
            et_frontLedG.setEnabled(false);
            et_frontLedIR2.setEnabled(false);

            et_rearLight.setEnabled(false);
            et_rearLedR.setEnabled(false);
            et_rearLedB.setEnabled(false);
            et_rearLedIR1.setEnabled(false);
            et_rearLedG.setEnabled(false);
            et_rearLedIR2.setEnabled(false);
        }
        else
        {
            et_frontLight.setEnabled(true);
            et_frontLedR.setEnabled(true);
            et_frontLedB.setEnabled(true);
            et_frontLedIR1.setEnabled(true);
            et_frontLedG.setEnabled(true);
            et_frontLedIR2.setEnabled(true);

            et_rearLight.setEnabled(true);
            et_rearLedR.setEnabled(true);
            et_rearLedB.setEnabled(true);
            et_rearLedIR1.setEnabled(true);
            et_rearLedG.setEnabled(true);
            et_rearLedIR2.setEnabled(true);
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
        int layer= AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        int layerNumber=AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getLayerNumber();

        if (tvLayer!=null) {
            if (layerNumber == 1) {
                tvLayer.setVisibility(View.GONE);
            } else {
                tvLayer.setVisibility(View.VISIBLE);
            }
            tvLayer.setText(StringUtils.getLayerStr(layer));
        }
        updateDataRequest();
        updateUI();
    }

    private void setLanguage()
    {
        tvChute.setText(FileManager.getInstance().getString(79)); //79#料槽
        tv_backgroundLight.setText(FileManager.getInstance().getString(80)); //80:背景灯
        btn_frontLed.setText(FileManager.getInstance().getString(75)); //75#前视
        btn_rearLed.setText(FileManager.getInstance().getString(76)); //76#后视
        tvLight.setText(FileManager.getInstance().getString(81)); //81#灯光
        tvFrontLight.setText(FileManager.getInstance().getString(75)); //75#前视
        tvRearLight.setText(FileManager.getInstance().getString(76)); //76#后视
        tvTitleIR.setText(FileManager.getInstance().getString(82)); //82#红外

        tv_front_light_ir.setText(FileManager.getInstance().getString(83));//83#红外前主灯
        tv_rear_light_ir.setText(FileManager.getInstance().getString(84));//84#红外后主灯
    }

    private void initView()
    {

        tvChute = (TextView)view.findViewById(R.id.tv_chute);
        tvLight = (TextView)view.findViewById(R.id.tv_light);
        tvFrontLight = (TextView)view.findViewById(R.id.tv_front_light);
        tvRearLight = (TextView)view.findViewById(R.id.tv_rear_light);
        tvTitleIR = (TextView)view.findViewById(R.id.tv_title_ir);

        thwaveView= (ThWaveView) view.findViewById(R.id.thwaveView);

        btn_irFrontLight = (Button)view.findViewById(R.id.button_ir_front_light);
        btn_irRearLight = (Button)view.findViewById(R.id.button_ir_rear_light);
        layout_light = (LinearLayout)view.findViewById(R.id.layout_light);

        layoutIR = (LinearLayout)view.findViewById(R.id.layout_ir);
        et_chute = (KeyboardDigitalEdit)view.findViewById(R.id.et_chute1);
        tv_ir1=(TextView)view.findViewById(R.id.tv_ir1);
        tv_ir2=(TextView)view.findViewById(R.id.tv_ir2);
        tvLayer = (TextView)view.findViewById(R.id.tv_layer);

        tv_backgroundLight = (TextView)view.findViewById(R.id.tv_background_light);

        btn_frontLed = (Button)view.findViewById(R.id.button_front_led);
        btn_rearLed = (Button)view.findViewById(R.id.button_rear_led);

        et_frontLight = (KeyboardDigitalEdit)view.findViewById(R.id.et_front_light);
        et_rearLight = (KeyboardDigitalEdit)view.findViewById(R.id.et_rear_light);

        et_frontLedR = (KeyboardDigitalEdit)view.findViewById(R.id.et_front_led_r);
        et_rearLedR = (KeyboardDigitalEdit)view.findViewById(R.id.et_rear_led_r);
        et_frontLedG = (KeyboardDigitalEdit)view.findViewById(R.id.et_front_led_g);
        et_rearLedG = (KeyboardDigitalEdit)view.findViewById(R.id.et_rear_led_g);
        et_frontLedB = (KeyboardDigitalEdit)view.findViewById(R.id.et_front_led_b);
        et_rearLedB = (KeyboardDigitalEdit)view.findViewById(R.id.et_rear_led_b);
        et_frontLedIR1 = (KeyboardDigitalEdit)view.findViewById(R.id.et_front_led_ir1);
        et_rearLedIR1 = (KeyboardDigitalEdit)view.findViewById(R.id.et_rear_led_ir1);
        et_frontLedIR2 = (KeyboardDigitalEdit)view.findViewById(R.id.et_front_led_ir2);
        et_rearLedIR2 = (KeyboardDigitalEdit)view.findViewById(R.id.et_rear_led_ir2);


        et_front_light_ir = (KeyboardDigitalEdit) view.findViewById(R.id.et_front_light_ir);
        et_rear_light_ir = (KeyboardDigitalEdit) view.findViewById(R.id.et_rear_light_ir);
        tv_front_light_ir = (TextView) view.findViewById(R.id.tv_front_light_ir);
        tv_rear_light_ir = (TextView) view.findViewById(R.id.tv_rear_light_ir);
        layout_ir_light_1 = (LinearLayout)view.findViewById(R.id.layout_ir_light_1);
        layout_ir_light_2 = (LinearLayout)view.findViewById(R.id.layout_ir_light_2);

        et_front_light_ir.setValue(255,0,13);
        et_front_light_ir.setLVCallback(this);

        et_rear_light_ir.setValue(255,0,14);
        et_rear_light_ir.setLVCallback(this);



        et_frontLight.setValue(255,0,0);
        et_frontLight.setLVCallback(this);
        et_rearLight.setValue(255,0,1);
        et_rearLight.setLVCallback(this);

        et_frontLedR.setValue(1023,0,2);
        et_frontLedR.setLVCallback(this);
        et_rearLedR.setValue(1023,0,3);
        et_rearLedR.setLVCallback(this);

        et_frontLedG.setValue(1023,0,4);
        et_frontLedG.setLVCallback(this);
        et_rearLedG.setValue(1023,0,5);
        et_rearLedG.setLVCallback(this);

        et_frontLedB.setValue(1023,0,6);
        et_frontLedB.setLVCallback(this);
        et_rearLedB.setValue(1023,0,7);
        et_rearLedB.setLVCallback(this);

        et_frontLedIR1.setValue(31,0,8);
        et_frontLedIR1.setLVCallback(this);
        et_rearLedIR1.setValue(31,0,9);
        et_rearLedIR1.setLVCallback(this);

        et_frontLedIR2.setValue(31,0,10);
        et_frontLedIR2.setLVCallback(this);
        et_rearLedIR2.setValue(31,0,11);
        et_rearLedIR2.setLVCallback(this);

        btn_irFrontLight.setOnClickListener(this);
        btn_irRearLight.setOnClickListener(this);

        btn_frontLed.setOnClickListener(this);
        btn_rearLed.setOnClickListener(this);

        layoutBackgroundRear = (LinearLayout)view.findViewById(R.id.layout_background_rear);

        MachineData machineData = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();
        List<String> list = new ArrayList<>();
        for (int i = 1;i<machineData.getChuteNumber()+1;i++)
        {
            list.add(Integer.toString(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);

        et_chute.setLVCallback(this);


        pageSwitchView = (PageSwitchView) view.findViewById(R.id.pageSwitchView);
        pageSwitchView.setPageSwitchListenser(new PageSwitchView.PageSwitchListenser() {
            @Override
            public void onPageSwitch(int pageIndex, int flag) {
                if(flag == PageSwitchView.FLAG_OK)
                {

                    updateUI();
                    updateDataRequest();

                }
            }
        });
        pageSwitchView.setmNumbers(2);
    }

    @Override
    public void onReloadLayerView() {
        super.onReloadLayerView();
        initStatusView();
    }

    @Override
    public void onConfirmClick(int value,int par)
    {
        currentLayer=AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();


        switch (par)
        {
            case 0: //前视主灯
                AbstractDataServiceFactory.getInstance().setLightData(currentLayer,(byte)0,(byte)4,(byte)value);
                break;
            case 1://后视主灯
                AbstractDataServiceFactory.getInstance().setLightData(currentLayer,(byte)1,(byte)4,(byte)value);
                break;
            case 2: //前视Red
                et_frontLedR.setText(Integer.toString(value));
                m_iView = 0;
                updateView();
                AbstractDataServiceFactory.getInstance().setLightData(currentLayer,(byte)0,(byte)1,(byte)value);
                break;
            case 3://后视Red
                et_rearLedR.setText(Integer.toString(value));
                m_iView = 1;
                updateView();
                AbstractDataServiceFactory.getInstance().setLightData(currentLayer,(byte)1,(byte)1,(byte)value);
                break;
            case 4: //前视GREEN
                et_frontLedG.setText(Integer.toString(value));

                m_iView = 0;
                updateView();
                AbstractDataServiceFactory.getInstance().setLightData(currentLayer,(byte)0,(byte)2,(byte)value);
                break;
            case 5://后视GREEN
                et_rearLedG.setText(Integer.toString(value));
                m_iView = 1;
                updateView();
                AbstractDataServiceFactory.getInstance().setLightData(currentLayer,(byte)1,(byte)2,(byte)value);
                break;
            case 6: //前视BLUE
                et_frontLedB.setText(Integer.toString(value));
                m_iView = 0;
                updateView();
                AbstractDataServiceFactory.getInstance().setLightData(currentLayer,(byte)0,(byte)3,(byte)value);
                break;
            case 7://后视BLUE
                et_rearLedB.setText(Integer.toString(value));
                m_iView = 1;
                updateView();
                AbstractDataServiceFactory.getInstance().setLightData(currentLayer,(byte)1,(byte)3,(byte)value);
                break;
            case 8://
                et_frontLedIR1.setText(Integer.toString(value));

                m_iView = 0;
                updateView();
                break;
            case 9://
                et_rearLedIR1.setText(Integer.toString(value));

                m_iView = 1;
                updateView();
                break;
            case 10://

                et_frontLedIR2.setText(Integer.toString(value));
                m_iView = 0;
                updateView();
                break;
            case 11://

                et_rearLedIR2.setText(Integer.toString(value));
                m_iView = 1;
                updateView();
                break;
            case 12:
                et_chute.setText(String.valueOf(value));
                if(value-1<0){
                    m_Channel=0;
                }else{
                    m_Channel=value-1;
                }
                updateDataRequest();
                break;
            case 13:

                break;
            case 14:

                break;

        }

    }

    private void updateUI()
    {
        MachineData machineData = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();

        layout_light.setVisibility(View.VISIBLE);

        tv_backgroundLight.setVisibility(View.VISIBLE);
        layoutIR.setVisibility(View.GONE);

        tv_ir1.setVisibility(View.GONE);
        tv_ir2.setVisibility(View.GONE);
        et_frontLedIR1.setVisibility(View.GONE);
        et_frontLedIR2.setVisibility(View.GONE);
        et_rearLedIR1.setVisibility(View.GONE);
        et_rearLedIR2.setVisibility(View.GONE);



        btn_irFrontLight.setVisibility(View.VISIBLE);
        btn_irRearLight.setVisibility(View.VISIBLE);

        tv_front_light_ir.setVisibility(View.VISIBLE);
        et_front_light_ir.setVisibility(View.VISIBLE);

        tv_rear_light_ir.setVisibility(View.VISIBLE);
        et_rear_light_ir.setVisibility(View.VISIBLE);



    }

    @Override
    public void onClick(View view)
    {
        byte layer=AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();

        int data=0;
        int chute= Integer.parseInt(et_chute.getText().toString())-1;
        switch (view.getId())
        {
            case R.id.button_ir_front_light: //红外前主灯
                if (!btn_irFrontLight.isSelected())
                    data = 1;
                else
                    data = 0;


                break;
            case R.id.button_ir_rear_light: //红外后主灯
                if (!btn_irRearLight.isSelected())
                    data = 1;
                else
                    data = 0;

                break;
            case R.id.button_front_led:
                m_iView = 0;
                updateView();
                break;
            case R.id.button_rear_led:
                m_iView = 1;
                updateView();
                break;
        }
    }

    private void updateIrRearLight()
    {
        if (false == btn_irRearLight.isSelected())
        {
            if (m_irType == 0)
                btn_irRearLight.setText(FileManager.getInstance().getString(108)); //108#红外后主灯关
            else
                btn_irRearLight.setText(FileManager.getInstance().getString(112));  //112#红外后波段关
        }
        else
        {
            if (m_irType == 0)
                btn_irRearLight.setText(FileManager.getInstance().getString(107)); //107#红外后主灯开
            else
                btn_irRearLight.setText(FileManager.getInstance().getString(111)); //111#红外后波段开
        }


    }

    private void updateIrFrontLight()
    {
        if (false ==btn_irFrontLight.isSelected())
        {
            if (m_irType == 0)
                btn_irFrontLight.setText(FileManager.getInstance().getString(106)); //106#红外前主灯关
            else
                btn_irFrontLight.setText(FileManager.getInstance().getString(110)); //110#红外前波段关
        }
        else
        {
            if (m_irType == 0)
                btn_irFrontLight.setText(FileManager.getInstance().getString(105)); //105#红外前主灯开
            else
                btn_irFrontLight.setText(FileManager.getInstance().getString(109)); //109#红外前波段开
        }
    }

    private void updateView()
    {
        if (m_iView == 0)
        {
            btn_frontLed.setSelected(true);
            btn_rearLed.setSelected(false);
        }
        else
        {
            btn_frontLed.setSelected(false);
            btn_rearLed.setSelected(true);
        }
    }



    private void updateLedEditLimit()
    {
        et_frontLedR.setMax(255);
        et_frontLedR.setMin(0);
        et_frontLedG.setMax(255);
        et_frontLedG.setMin(0);
        et_frontLedB.setMax(255);
        et_frontLedB.setMin(0);

        et_rearLedR.setMax(255);
        et_rearLedR.setMin(0);
        et_rearLedG.setMax(255);
        et_rearLedG.setMin(0);
        et_rearLedB.setMax(255);
        et_rearLedB.setMin(0);

    }


    private void initLight(ThLightRet ret)
    {
        byte red[] = ret.getFstsndReds();

        et_frontLedR.setText(Integer.toString(ConvertUtils.unsignByteToInt(red[0])));
        et_rearLedR.setText(Integer.toString(ConvertUtils.unsignByteToInt(red[1])));

        byte green[] = ret.getFstsndGreens();
        et_frontLedG.setText(Integer.toString(ConvertUtils.unsignByteToInt(green[0])));
        et_rearLedG.setText(Integer.toString(ConvertUtils.unsignByteToInt(green[1])));

        byte blue[] = ret.getFstsndBlues();
        et_frontLedB.setText(Integer.toString(ConvertUtils.unsignByteToInt(blue[0])));
        et_rearLedB.setText(Integer.toString(ConvertUtils.unsignByteToInt(blue[1])));

        byte mainLight[] = ret.getFstsndMainLights();
        et_frontLight.setText(Integer.toString(ConvertUtils.unsignByteToInt(mainLight[0])));
        et_rearLight.setText(Integer.toString(ConvertUtils.unsignByteToInt(mainLight[1])));

        byte ir[] = ret.getFstsndIr();

        if (ir[0] == 0)
            btn_irFrontLight.setSelected(false);
        else
            btn_irFrontLight.setSelected(true);

        if (ir[1] == 0)
            btn_irRearLight.setSelected(false);
        else
            btn_irRearLight.setSelected(true);

        updateIrRearLight();
        updateIrFrontLight();


    }

    private void setLight(byte view,byte type,byte data)
    { //1/2/3/4/5 :r/g/b/mainLight/ir
        if (view == FRONT)
        {
            switch (type)
            {
                case TYPE_RED:
                    et_frontLedR.setText(Integer.toString(data&0xFF));
                    break;
                case TYPE_GREEN:
                    et_frontLedG.setText(Integer.toString(data&0xFF));
                    break;
                case TYPE_BLUE:
                    et_frontLedB.setText(Integer.toString(data&0xFF));
                    break;
                case TYPE_MAINLIGHT:
                    et_frontLight.setText(Integer.toString(data&0xFF));
                    break;
                case TYPE_IR:
                    if (data == 0)
                        btn_irFrontLight.setSelected(false);
                    else
                        btn_irFrontLight.setSelected(true);
                    updateIrFrontLight();

                    break;
            }
        }
        else
        {
            switch (type)
            {
                case TYPE_RED:
                    et_rearLedR.setText(Integer.toString(data&0xFF));
                    break;
                case TYPE_GREEN:
                    et_rearLedG.setText(Integer.toString(data&0xFF));
                    break;
                case TYPE_BLUE:
                    et_rearLedB.setText(Integer.toString(data&0xFF));
                    break;
                case TYPE_MAINLIGHT:
                    et_rearLight.setText(Integer.toString(data&0xFF));
                    break;
                case TYPE_IR:
                    if (data == 0)
                        btn_irRearLight.setSelected(false);
                    else
                        btn_irRearLight.setSelected(true);
                    updateIrRearLight();

                    break;
            }
        }

    }
}
