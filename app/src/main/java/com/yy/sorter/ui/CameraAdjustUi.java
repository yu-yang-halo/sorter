package com.yy.sorter.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.manager.TopManager;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.DigitalDialog;
import com.yy.sorter.utils.ScreenHelper;
import com.yy.sorter.utils.StringUtils;
import com.yy.sorter.view.AlwaysClickButton;
import com.yy.sorter.view.KeyboardDigitalEdit;
import com.yy.sorter.view.PageSwitchView;
import com.yy.sorter.view.ThWaveView;

import java.util.Timer;
import java.util.TimerTask;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.YYWaveData;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class CameraAdjustUi extends BaseUi implements DigitalDialog.Builder.LVCallback,
        View.OnClickListener,AlwaysClickButton.LVMuiltClickCallBack {
    private Button btnOrigin, btnCalibration,btnTest;
    private RadioButton radio1,radio2,radio3;
    private RadioGroup radio_group;
    private PageSwitchView pageSwitchView;
    private KeyboardDigitalEdit et_chute;
    private SeekBar seekBar;
    private TextView tv_layer,tv_chute;
    private ThWaveView thWaveView;
    private LinearLayout seekLayout,bottomLayout;
    private RelativeLayout layout_spot;
    private Activity activity;
    private byte dataType=0,waveType= ThCommand.WAVE_TYPE_CAMERA_ORIGIN;
    private int pos=0;
    private int orientation=ORIENTATION_PORTRAIT;
    private long lastTime=0;
    private int m_Channel;
    private AlwaysClickButton addBtn,minusBtn;
    public CameraAdjustUi(Context ctx) {
        super(ctx);
        activity= (Activity) ctx;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        orientation=newConfig.orientation;
        initView();
    }
    @Override
    protected View onInitView() {
        if(view==null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_camera_adjust, null);
            tv_layer= (TextView) view.findViewById(R.id.tv_layer);
            tv_chute = (TextView) view.findViewById(R.id.tv_chute);

            thWaveView= (ThWaveView) view.findViewById(R.id.thwaveView);
            radio_group= (RadioGroup) view.findViewById(R.id.radio_group);
            seekBar= (SeekBar) view.findViewById(R.id.seekBar);

            radio1= (RadioButton) view.findViewById(R.id.radio1);
            radio2= (RadioButton) view.findViewById(R.id.radio2);
            radio3= (RadioButton) view.findViewById(R.id.radio3);

            et_chute= (KeyboardDigitalEdit) view.findViewById(R.id.et_chute);
            btnOrigin= (Button) view.findViewById(R.id.btnOrigin);
            btnCalibration= (Button) view.findViewById(R.id.btnCalibration);
            btnTest= (Button) view.findViewById(R.id.btnTest);

            seekLayout= (LinearLayout) view.findViewById(R.id.seekLayout);
            bottomLayout= (LinearLayout) view.findViewById(R.id.bottomLayout);
            layout_spot= (RelativeLayout) view.findViewById(R.id.layout_spot);

            addBtn = (AlwaysClickButton) view.findViewById(R.id.addBtn);
            minusBtn  = (AlwaysClickButton) view.findViewById(R.id.minusBtn);

            addBtn.setValve(0,this);
            minusBtn.setValve(1,this);


            pageSwitchView = (PageSwitchView) view.findViewById(R.id.pageSwitchView);
            pageSwitchView.setPageSwitchListenser(new PageSwitchView.PageSwitchListenser() {
                @Override
                public void onPageSwitch(int pageIndex, int flag) {
                    if(flag == PageSwitchView.FLAG_OK)
                    {
                        AbstractDataServiceFactory.getInstance().getCurrentDevice().setCurrentView((byte) pageIndex);
                        onReloadLayerView();
                    }
                }
            });


            et_chute.setLVCallback(this);


            btnOrigin.setOnClickListener(this);
            btnCalibration.setOnClickListener(this);
            btnTest.setOnClickListener(this);


            radio2.setChecked(true);

            seekBar.setMax(100);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    pos=progress;

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });



            radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.radio1:
                            dataType=2;
                            seekLayout.setVisibility(View.GONE);
                            break;
                        case R.id.radio2:
                            dataType=0;
                            seekLayout.setVisibility(View.VISIBLE);
                            break;
                        case R.id.radio3:
                            dataType=1;
                            seekLayout.setVisibility(View.GONE);
                            break;
                    }
                }
            });

        }
        orientation=ORIENTATION_PORTRAIT;
        thWaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(System.currentTimeMillis()-lastTime>1000){
                    lastTime=System.currentTimeMillis();
                    //单击
                }else{
                    lastTime=0;
                    //双击
                    if(orientation==ORIENTATION_PORTRAIT){
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }else{
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }

                }
            }
        });


        initView();
        initStatusView();

        return view;
    }


    @Override
    public void onReloadLayerView() {
        super.onReloadLayerView();
        initStatusView();
    }
    private void initStatusView(){
        currentLayer = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        currentView = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentView();
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

        tv_layer.setText(sb);


        pageSwitchView.setmNumbers(2);
        pageSwitchView.setmCurrentIndex(currentView);


    }
    private void initView(){

        if(orientation==ORIENTATION_LANDSCAPE){
            mainUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams layoutParams=thWaveView.getLayoutParams();
                    layoutParams.height= ScreenHelper.screenHeight(ctx);
                    thWaveView.setLayoutParams(layoutParams);
                }
            });
            layout_spot.setVisibility(View.GONE);
            tv_layer.setVisibility(View.GONE);
            seekLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            TopManager.getInstance().hideTopView();

        }else{

            mainUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams layoutParams=thWaveView.getLayoutParams();
                    layoutParams.height= ConvertUtils.toPx(ctx,280);
                    thWaveView.setLayoutParams(layoutParams);
                }
            });

            layout_spot.setVisibility(View.VISIBLE);
            tv_layer.setVisibility(View.VISIBLE);
            seekLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
            TopManager.getInstance().showTopView();
        }


    }
    Timer timer;

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
                reqWave();
            }
        },500,1000);

    }
    public void cancelTimer(){
        ScreenHelper.screenOFF((Activity) ctx);
        if(timer!=null){
            timer.cancel();
        }
    }

    private void reqWave()
    {

        byte[] params=new byte[]{0,0,currentView,(byte) m_Channel,waveType,dataType, (byte) pos};
        AbstractDataServiceFactory.getInstance().requestWave(waveType,params);

    }

    private void setLanguage()
    {
        tv_chute.setText(FileManager.getInstance().getString(79));//79#料槽

        radio1.setText(FileManager.getInstance().getString(116));//116#相机调整
        radio2.setText(FileManager.getInstance().getString(117));//117#详细数据
        radio3.setText(FileManager.getInstance().getString(118));//118#压缩数据

        btnTest.setText(FileManager.getInstance().getString(119));//119#测试数据
        btnOrigin.setText(FileManager.getInstance().getString(120));//120#原始数据
        btnCalibration.setText(FileManager.getInstance().getString(121));//121#校正数据


    }

    @Override
    public void onViewStart() {
        super.onViewStart();
        setLanguage();
        waveButtonInit(0);
        thWaveView.clear();
        MachineData machineData=AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();

        et_chute.setValue(machineData.getChuteNumber(),1,0);
        if(m_Channel>=machineData.getChuteNumber()){
            m_Channel=0;
            et_chute.setText(String.valueOf(1));
        }else{
            et_chute.setText(String.valueOf(m_Channel+1));
        }
        startTimer();

        OnSystemStatusChanged();
    }
    @Override
    public void onViewStop() {
        super.onViewStop();
        AbstractDataServiceFactory.getInstance().pageSwitch((byte)0,(byte)0);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        cancelTimer();
    }

    @Override
    public void onActivityStart() {
        super.onActivityStart();
        if(MiddleManger.getInstance().isCurrentUI(this)){
            startTimer();
        }

    }

    @Override
    public void onActivityStop() {
        super.onActivityStop();
        cancelTimer();
    }
    private void OnSystemStatusChanged()
    {

    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_CAMERAADJUST;
    }

    @Override
    public void receivePacketData(ThPackage packet) {

        if(packet.getType() == ThCommand.WAVE_CMD)
        {
            if (packet.getExtendType() == ThCommand.WAVE_TYPE_CAMERA_ORIGIN
                    || packet.getExtendType() == ThCommand.WAVE_TYPE_CAMERA_ADJUST
                    || packet.getExtendType() == ThCommand.WAVE_TYPE_CAMERA_TEST) {
                YYWaveData thWaveRet = ThPackageHelper.parseWaveData(packet);

                thWaveView.setThWaveRet(thWaveRet);
                thWaveView.invalidate();
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOrigin:
                waveButtonInit(0);
                break;
            case R.id.btnCalibration:
                waveButtonInit(1);
                break;
            case R.id.btnTest:
                waveButtonInit(2);
                break;
        }
    }
    private void waveButtonInit(int type){
        btnOrigin.setSelected(false);
        btnCalibration.setSelected(false);
        btnTest.setSelected(false);


        if(dataType==0){
            seekLayout.setVisibility(View.VISIBLE);
        }else{
            seekLayout.setVisibility(View.GONE);
        }
        switch (type){
            case 0:
                btnOrigin.setSelected(true);
                waveType=ThCommand.WAVE_TYPE_CAMERA_ORIGIN;

                break;
            case 1:
                btnCalibration.setSelected(true);
                waveType=ThCommand.WAVE_TYPE_CAMERA_ADJUST;
                break;
            case 2:
                btnTest.setSelected(true);
                waveType=ThCommand.WAVE_TYPE_CAMERA_TEST;
                break;
        }

    }
    @Override
    public void onConfirmClick(int value, int par) {
        if (par == 0)
        {
            et_chute.setText(Integer.toString(value));
            m_Channel = value-1;
        }
    }

    @Override
    public void onMuiltClick(int par, int isSend) {
        int value,max,min;
        max = et_chute.getMax();
        min = et_chute.getMin();
        value = m_Channel+1;
        if(isSend == 1)
        {
            if(par == 0)
            {
                value++;
            }else
            {
                value--;
            }
            if(value<min)
            {
                value = min;
            }
            if(value>max)
            {
                value = max;
            }

        }
        et_chute.setText(Integer.toString(value));
        m_Channel = value -1;

    }
}
