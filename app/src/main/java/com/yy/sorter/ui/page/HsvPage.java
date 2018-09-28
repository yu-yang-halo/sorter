package com.yy.sorter.ui.page;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.DigitalDialog;
import com.yy.sorter.utils.ScreenHelper;
import com.yy.sorter.view.AlwaysClickButton;
import com.yy.sorter.view.DirectionView;
import com.yy.sorter.view.HsvView;
import com.yy.sorter.view.KeyboardDigitalEdit;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.ThHsvInfo;
import th.service.data.ThHsvWave;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

public class HsvPage extends PageBaseUi implements DigitalDialog.Builder.LVCallback,AlwaysClickButton.LVMuiltClickCallBack{
    private DirectionView directionView;
    private HsvView hsvView;
    private CheckBox imageMoveFlagCK;
    private List<ThHsvInfo> thHsvInfoList;
    private int m_Channel;
    private KeyboardDigitalEdit et_Chute,vEdit1,vEdit2,sEdit1,sEdit2,hEdit1,hEdit2;
    private byte m_bFlag_PicMove,m_bWave_Color,m_used;
    private Button enableButton,frontBtn,rearBtn;
    private TextView lb_hsv_h,lb_hsv_s,lb_hsv_v,
            lb_start,lb_end,tv_chute;
    private int senseIndex = 0;
    private Timer timer;
    private AlwaysClickButton addBtn,minusBtn;
    public HsvPage(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_page_hsv, null);
            directionView= (DirectionView) view.findViewById(R.id.directionView);
            hsvView= (HsvView) view.findViewById(R.id.hsvView);
            imageMoveFlagCK= (CheckBox) view.findViewById(R.id.checkBox2);


            lb_hsv_h= (TextView) view.findViewById(R.id.lb_hsv_h);
            lb_hsv_s= (TextView) view.findViewById(R.id.lb_hsv_s);
            lb_hsv_v= (TextView) view.findViewById(R.id.lb_hsv_v);
            lb_start= (TextView) view.findViewById(R.id.lb_start);
            lb_end=(TextView) view.findViewById(R.id.lb_end);
            tv_chute= (TextView) view.findViewById(R.id.tv_chute);


            enableButton= (Button) view.findViewById(R.id.enableButton);
            frontBtn = (Button) view.findViewById(R.id.frontBtn);
            rearBtn = (Button) view.findViewById(R.id.rearBtn);

            addBtn = (AlwaysClickButton) view.findViewById(R.id.addBtn);
            minusBtn  = (AlwaysClickButton) view.findViewById(R.id.minusBtn);

            addBtn.setValve(1000,this);
            minusBtn.setValve(1001,this);



            vEdit1=(KeyboardDigitalEdit) view.findViewById(R.id.vEdit1);
            vEdit2=(KeyboardDigitalEdit) view.findViewById(R.id.vEdit2);
            sEdit1=(KeyboardDigitalEdit) view.findViewById(R.id.sEdit1);
            sEdit2=(KeyboardDigitalEdit) view.findViewById(R.id.sEdit2);
            hEdit1=(KeyboardDigitalEdit) view.findViewById(R.id.hEdit1);
            hEdit2=(KeyboardDigitalEdit) view.findViewById(R.id.hEdit2);


            vEdit1.setLVCallback(this);
            vEdit2.setLVCallback(this);
            sEdit1.setLVCallback(this);
            sEdit2.setLVCallback(this);
            hEdit1.setLVCallback(this);
            hEdit2.setLVCallback(this);

            vEdit1.setValue(254,0,1);
            vEdit2.setValue(255,1,2);
            sEdit1.setValue(255,1,3);
            sEdit2.setValue(255,1,4);

            hEdit1.setValue(359,0,5);
            hEdit2.setValue(359,0,6);

            et_Chute= (KeyboardDigitalEdit) view.findViewById(R.id.et_chute);
            et_Chute.setLVCallback(this);


            directionView.setLvMuiltClickCallBack(this);

            imageMoveFlagCK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AbstractDataServiceFactory.getInstance().hsvSwitch((byte) 2,m_bFlag_PicMove);
                    m_bFlag_PicMove =(m_bFlag_PicMove==(byte) 1?(byte)0:(byte)1);

                    hsvView.setM_bFlag_PicMove(m_bFlag_PicMove);
                    hsvView.invalidate();
                }
            });

            hsvView.setiSensesSwitchEvent(new HsvView.ISensesSwitchEvent() {
                @Override
                public void onSwitchSense(int index) {
                    senseIndex = index;
                    updateHsvPage();
                    AbstractDataServiceFactory.getInstance().hsvSwitch((byte) 1,(byte) senseIndex);
                }
            });

            enableButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
                    AbstractDataServiceFactory.getInstance().setHsvInfo(group, (byte) currentView,
                            (byte) senseIndex,(byte) 4,0);
                }
            });

            frontBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentView == 0)
                    {
                        return;
                    }
                    AbstractDataServiceFactory.getInstance().getCurrentDevice().setCurrentView((byte) 0);
                    initButtonStyle();
                    reqHsvInfo();
                }
            });
            rearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentView == 1)
                    {
                        return;
                    }
                    AbstractDataServiceFactory.getInstance().getCurrentDevice().setCurrentView((byte) 1);
                    initButtonStyle();
                    reqHsvInfo();
                }
            });

            hsvViewLayoutInit();
        }
        return view;
    }

    private void initButtonStyle()
    {
        currentView = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentView();
        if(currentView == 0)
        {
            frontBtn.setSelected(true);
            rearBtn.setSelected(false);
        }else
        {
            frontBtn.setSelected(false);
            rearBtn.setSelected(true);
        }
    }

    private void cancelTimer(){
        if(timer!=null){
            timer.cancel();
        }

    }
    private void startTimer(){
        if(timer!=null){
            timer.cancel();
        }
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
                AbstractDataServiceFactory.getInstance().requestWave((byte)ThCommand.WAVE_TYPE_HSV,
                        new byte[]{0,0,currentView,group});

            }
        },200,2000);
    }

    private void setLanguage()
    {
        frontBtn.setText(FileManager.getInstance().getString(75));//75#前视
        rearBtn.setText(FileManager.getInstance().getString(76));//76#后视
        imageMoveFlagCK.setText(FileManager.getInstance().getString(106));//106#图像平移

        lb_hsv_s.setText(FileManager.getInstance().getString(109));//109#纯度
        lb_hsv_h.setText(FileManager.getInstance().getString(110));//110#颜色

        lb_start.setText(FileManager.getInstance().getString(107));//107#起点
        lb_end.setText(FileManager.getInstance().getString(108));//108#终点

        tv_chute.setText(FileManager.getInstance().getString(79));//79#料槽


    }

    @Override
    public void onViewStart() {
        super.onViewStart();

        MachineData machineData=AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();

        et_Chute.setValue(machineData.getChuteNumber(),1,0);
        if(m_Channel>=machineData.getChuteNumber()){
            m_Channel=0;
            et_Chute.setText(String.valueOf(1));
        }else{
            et_Chute.setText(String.valueOf(m_Channel+1));
        }

        setLanguage();
        initButtonStyle();
        reqHsvInfo();
        startTimer();
    }

    @Override
    public void onViewStop() {
        super.onViewStop();
        cancelTimer();
        if(hsvView!=null){
            hsvView.setThHSVWave(null);
        }
    }

    private void reqHsvInfo()
    {
        currentGroup = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
        currentView = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentView();
        AbstractDataServiceFactory.getInstance().reqHsvInfo(currentGroup, currentView);
    }

    @Override
    public void onGroupChanged() {
        super.onGroupChanged();
        reqHsvInfo();
    }

    private void hsvViewLayoutInit(){
        int screenWidth= ScreenHelper.screenWidth(ctx);

        ViewGroup.LayoutParams layoutParams=hsvView.getLayoutParams();
        if(layoutParams==null){
            layoutParams=new ViewGroup.LayoutParams(screenWidth,screenWidth*26/36);
        }else{
            layoutParams.width=screenWidth;
            layoutParams.height=screenWidth*26/36;
        }

        hsvView.setLayoutParams(layoutParams);
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_PAGE_HSV;
    }

    @Override
    public void receivePacketData(ThPackage packet) {
        if (packet.getType() == ThCommand.HSV_CMD) {

            if(packet.getExtendType() == 0x01)
            {
                thHsvInfoList =  ThPackageHelper.parseThHsvInfos(packet);
                m_used = packet.getData1()[2];
                m_bFlag_PicMove = packet.getData1()[3];
                updateHsvPage();

            }else if(packet.getExtendType() == 0x02)
            {
                senseIndex = packet.getData1()[2];
                ThHsvInfo thHsvInfo = getSelectedHsv();
                int setType = packet.getData1()[3];
                switch (setType)
                {
                    case 0:
                        thHsvInfo.gethStart()[0] = packet.getData1()[4];
                        thHsvInfo.gethStart()[1] = packet.getData1()[5];
                        break;
                    case 1:
                        thHsvInfo.gethEnd()[0] = packet.getData1()[4];
                        thHsvInfo.gethEnd()[1] = packet.getData1()[5];
                        break;
                    case 2:
                        thHsvInfo.getsStartEnd()[0] = packet.getData1()[5];
                        break;
                    case 3:
                        thHsvInfo.getsStartEnd()[1] = packet.getData1()[5];
                        break;
                    case 4:
                        m_used = packet.getData1()[5];
                        break;
                }

                updateHsvPage();

            }
        }else if(packet.getType() == ThCommand.WAVE_CMD)
        {
            if(packet.getExtendType() == ThCommand.WAVE_TYPE_HSV)
            {
                /**
                 *   HSV波形
                 */
                ThHsvWave thHSVWave=ThPackageHelper.parseHSVWaveData(packet);
                hsvView.setThHSVWave(thHSVWave);
                hsvView.invalidate();
            }
        }
    }

    private void updateHsvPage()
    {
        imageMoveFlagCK.setChecked(m_bFlag_PicMove==1?true:false);

        hsvView.setCurrenSenseIndex(senseIndex);
        hsvView.setHsvSenses(thHsvInfoList);
        hsvView.setM_bFlag_PicMove(m_bFlag_PicMove);
        hsvView.setM_bPaintWaveColor(m_bWave_Color);
        hsvView.invalidate();


        ThHsvInfo thHsvInfo=getSelectedHsv();

        if(thHsvInfo!=null){

            hEdit1.setText(String.valueOf(ConvertUtils.bytes2ToInt(thHsvInfo.gethStart())));
            hEdit2.setText(String.valueOf(ConvertUtils.bytes2ToInt(thHsvInfo.gethEnd())));


            int s2=ConvertUtils.unsignByteToInt(thHsvInfo.getsStartEnd()[1]);
            int s1=ConvertUtils.unsignByteToInt(thHsvInfo.getsStartEnd()[0]);
            sEdit1.setText(String.valueOf(s1));
            sEdit2.setText(String.valueOf(s2));
            sEdit1.setValue(s2-1,0,3);
            sEdit2.setValue(255,s1+1,4);
        }else
        {
            hEdit1.setText(String.valueOf(0));
            hEdit2.setText(String.valueOf(0));

            sEdit1.setText(String.valueOf(0));
            sEdit2.setText(String.valueOf(0));
        }
        initUsed(m_used);
    }
    private void toChuteClick(int par,int isSend)
    {
        int value,max,min;
        max = et_Chute.getMax();
        min = et_Chute.getMin();
        value = m_Channel+1;
        if(isSend == 1)
        {
            if(par == 1000)
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
        et_Chute.setText(Integer.toString(value));
        m_Channel = value -1;
    }
    @Override
    public void onConfirmClick(int value, int par) {
        byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
        int valueType=0;
        switch (par){
            case 0:
                et_Chute.setText(Integer.toString(value));
                m_Channel = value-1;

                break;
            case 1:
            case 2://hsv ---V

                break;
            case 3:
            case 4://hsv ---S
                valueType=(par==3?2:3);
                AbstractDataServiceFactory.getInstance().setHsvInfo(group, (byte) currentView,
                        (byte) senseIndex,(byte) valueType,value);
                break;
            case 5:
            case 6://hsv ---H
                valueType=(par==5?0:1);
                AbstractDataServiceFactory.getInstance().setHsvInfo(group, (byte) currentView,
                        (byte) senseIndex,(byte) valueType,value);
                break;

        }
    }

    private ThHsvInfo getSelectedHsv()
    {
        if(thHsvInfoList == null)
        {
            return null;
        }
        ThHsvInfo tmp = null;
        for(ThHsvInfo hsvInfo:thHsvInfoList)
        {
            if(hsvInfo.getIndex() == senseIndex)
            {
                tmp = hsvInfo;
                break;
            }
        }
        return tmp;
    }
    int valueTempHorizontal=0;
    int valueTempVertical=0;
    int step=1;
    boolean stopYN=false;
    @Override
    public void onMuiltClick(int par, int isSend) {

        if(par >= 1000)
        {
            toChuteClick(par,isSend);
            return;
        }

/**
 *  par 0:上  1：左 2：右 3：下
 *
 */
        Log.e("onMuiltClick","par:"+par+" isSend:"+isSend);
        ThHsvInfo sense=getSelectedHsv();
        byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
        if(sense==null){
            return;
        }
        if(isSend==1){
            if(stopYN){
                return;
            }
            switch (par){
                case 0:
                    valueTempVertical+=step;
                    stopYN=hsvView.refreshPage(0,step);
                    sEdit1.setText(String.valueOf(ConvertUtils.unsignByteToInt(sense.getsStartEnd()[0])));
                    sEdit2.setText(String.valueOf(ConvertUtils.unsignByteToInt(sense.getsStartEnd()[1])));
                    break;
                case 3:
                    valueTempVertical+=step;
                    stopYN=hsvView.refreshPage(3,step);
                    sEdit1.setText(String.valueOf(ConvertUtils.unsignByteToInt(sense.getsStartEnd()[0])));
                    sEdit2.setText(String.valueOf(ConvertUtils.unsignByteToInt(sense.getsStartEnd()[1])));
                    break;
                case 1:
                    valueTempHorizontal+=step;
                    stopYN=hsvView.refreshPage(1,step);
                    hEdit1.setText(String.valueOf(ConvertUtils.bytes2ToInt(sense.gethStart())));
                    hEdit2.setText(String.valueOf(ConvertUtils.bytes2ToInt(sense.gethEnd())));
                    break;
                case 2:
                    valueTempHorizontal+=step;
                    stopYN=hsvView.refreshPage(2,step);
                    hEdit1.setText(String.valueOf(ConvertUtils.bytes2ToInt(sense.gethStart())));
                    hEdit2.setText(String.valueOf(ConvertUtils.bytes2ToInt(sense.gethEnd())));
                    break;
            }
            hsvView.invalidate();


        }else{
            /**
             *   h : 0,1
             *   s : 2,3
             */
            switch (par){
                case 0:
                case 3:

                    int sendUpValue=ConvertUtils.unsignByteToInt(sense.getsStartEnd()[1]);
                    int sendDownValue=ConvertUtils.unsignByteToInt(sense.getsStartEnd()[0]);


                    if(sendDownValue<sendUpValue){
                        AbstractDataServiceFactory.getInstance().setHsvInfo(group,(byte) currentView,
                                (byte) senseIndex,(byte) 2,sendDownValue);
                        AbstractDataServiceFactory.getInstance().setHsvInfo(group,(byte) currentView,
                                (byte) senseIndex,(byte) 3,sendUpValue);
                    }
                    break;
                case 1:
                case 2:

                    int sendLeftValue=ConvertUtils.bytes2ToInt(sense.gethStart());
                    int sendRightValue=ConvertUtils.bytes2ToInt(sense.gethEnd());

                    AbstractDataServiceFactory.getInstance().setHsvInfo(group,(byte) currentView,
                            (byte) senseIndex,(byte) 0,sendLeftValue);
                    AbstractDataServiceFactory.getInstance().setHsvInfo(group,(byte) currentView,
                            (byte) senseIndex,(byte) 1,sendRightValue);

                    break;

            }

            valueTempHorizontal=0;
            valueTempVertical=0;
            stopYN=false;
        }



    }

    private void initUsed(byte mUsed){
        if(mUsed==0x01){
            enableButton.setText(FileManager.getInstance().getString(103));   // 103#使用

            enableButton.setSelected(true);
        }else{
            enableButton.setText(FileManager.getInstance().getString(102)); //102#禁用
            enableButton.setSelected(false);
        }
    }
}
