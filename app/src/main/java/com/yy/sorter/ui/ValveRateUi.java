package com.yy.sorter.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.DigitalDialog;
import com.yy.sorter.utils.ScreenHelper;
import com.yy.sorter.utils.StringUtils;
import com.yy.sorter.view.KeyboardDigitalEdit;
import com.yy.sorter.view.PageSwitchView;
import com.yy.sorter.view.ThAutoLayout;
import com.yy.sorter.view.ThGroupView;
import com.yy.sorter.view.ThRateView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.ThValveRateRet;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

public class ValveRateUi extends BaseUi implements DigitalDialog.Builder.LVCallback{
    private ThRateView thrateView;
    private Timer timer;
    private KeyboardDigitalEdit et_Chute;
    private int m_Channel=0;
    private TextView tv_layer;
    private TextView tvChute;
    private TextView mfrontText,mbackText;
    private PageSwitchView pageSwitchView;
    private TextView tv_chutenumbers;
    private ThAutoLayout autoLayout;
    private List<ThAutoLayout.Item> itemList;
    public ValveRateUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_valve_rate, null);
            thrateView= (ThRateView) view.findViewById(R.id.thrateView);

            tv_layer = (TextView)view.findViewById(R.id.tv_layer);
            et_Chute = (KeyboardDigitalEdit)view.findViewById(R.id.et_chute);
            tvChute = (TextView)view.findViewById(R.id.tv_chute);

            mfrontText= (TextView) view.findViewById(R.id.mfrontText);
            mbackText= (TextView) view.findViewById(R.id.mbackText);

            et_Chute.setLVCallback(this);
            pageSwitchView = (PageSwitchView) view.findViewById(R.id.pageSwitchView);

            pageSwitchView.setPageSwitchListenser(new PageSwitchView.PageSwitchListenser() {
                @Override
                public void onPageSwitch(int pageIndex, int flag) {
                    if(flag == PageSwitchView.FLAG_OK)
                    {
                        et_Chute.setText(Integer.toString(pageIndex+1));
                        m_Channel = pageIndex;

                    }

                }
            });
            tv_chutenumbers = (TextView) view.findViewById(R.id.tv_chutenumbers);

            autoLayout = (ThAutoLayout) view.findViewById(R.id.autoLayout);
            autoLayout.setOnSelectedListenser(new ThGroupView.OnSelectedListenser() {
                @Override
                public void onSelected(int pos) {
                    if(itemList != null && pos < itemList.size())
                    {
                        ThAutoLayout.Item item = itemList.get(pos);
                        currentLayer = (byte) (item.getTag());
                        currentView = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentView();
                        AbstractDataServiceFactory.getInstance().getCurrentDevice().setCurrentLayer(currentLayer);

                       // AbstractDataServiceFactory.getInstance().switchLayer(currentLayer,currentView);

                        onReloadLayerView();

                    }
                }
            });
        }
        return view;
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_VALVE_RATE;
    }

    @Override
    public void receivePacketData(ThPackage packet) {
        if(packet.getType()== ThCommand.VALVE_RATE_CMD){
            if(packet.getExtendType()==0x01){
                ThValveRateRet thValveRateRet= ThPackageHelper.parseThValveRateRet(packet);
                thrateView.setThValveRateRet(thValveRateRet);
                thrateView.invalidate();

            }
        }

    }
    public void setLanguage()
    {
        tvChute.setText(FileManager.getInstance().getString(79)); //79#料槽

        mfrontText.setText(FileManager.getInstance().getString(75)); //75#前视
        mbackText.setText(FileManager.getInstance().getString(76));  //76#后视

    }

    @Override
    public void onConfirmClick(int value,int par) {
        if (par == 0)
        {
            et_Chute.setText(Integer.toString(value));
            m_Channel = value-1;
            pageSwitchView.setmCurrentIndex(m_Channel);
        }
    }

    @Override
    public void onReloadLayerView() {
        super.onReloadLayerView();
        initStatusView();
    }

    private void initStatusView(){
        currentLayer= AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        int layerNumber=AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getLayerNumber();

        if (tv_layer!=null)
            tv_layer.setText(StringUtils.getLayerStr(currentLayer));

        if(layerNumber==1){
            autoLayout.setVisibility(View.GONE);
        }else{
            autoLayout.setVisibility(View.VISIBLE);
        }

    }
    private void sendReq(){
        ScreenHelper.screenON((Activity) ctx);
        if(timer!=null){
            timer.cancel();
        }
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                AbstractDataServiceFactory.getInstance().requestValveRate(currentLayer,(byte) m_Channel);
            }
        },200,1000);
    }
    private void cancelReq(){
        ScreenHelper.screenOFF((Activity) ctx);
        if(timer!=null){
            timer.cancel();
        }
    }

    @Override
    public void onViewStart() {
        super.onViewStart();
        setLanguage();
        MachineData machineData= AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();
        et_Chute.setValue(machineData.getChuteNumber(),1,0);

        int chuteNumbers = machineData.getChuteNumber();
        pageSwitchView.setmNumbers(chuteNumbers);
        tv_chutenumbers.setText("~ "+chuteNumbers);


        if(m_Channel>=machineData.getChuteNumber()){
            m_Channel=0;
            et_Chute.setText(String.valueOf(1));
        }
        pageSwitchView.setmCurrentIndex(m_Channel);

        initStatusView();
        sendReq();


        itemList = StringUtils.getLayerItem();
        currentLayer = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        autoLayout.setContents(itemList,currentLayer,0);


    }


    @Override
    public void onActivityStart() {
        super.onActivityStart();
        if(MiddleManger.getInstance().isCurrentUI(this)){
            sendReq();
        }

    }

    @Override
    public void onViewStop() {
        super.onViewStop();
        cancelReq();
    }

    @Override
    public void onActivityStop() {
        super.onActivityStop();
        cancelReq();
    }

}
