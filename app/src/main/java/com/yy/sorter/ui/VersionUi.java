package com.yy.sorter.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.adapter.CameraVersionAdapter;
import com.yy.sorter.adapter.ColorVersionAdapter;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.StringUtils;
import com.yy.sorter.view.PageSwitchView;

import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.ThDevice;
import th.service.data.ThSVersion;
import th.service.helper.SystemHelper;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

public class VersionUi extends BaseUi implements View.OnClickListener{
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;
    private LinearLayout linearLayout4,linearcamerair;
    private LinearLayout linearLayout5,linearcamerair2;
    private LinearLayout linearLayout_layer;
    private TextView tvSoft;
    private ImageView ivSoft;
    private TextView tvAlgorithm;
    private ImageView ivAlgorithm;
    private TextView tvCamera;
    private ImageView ivCamera;
    private TextView tvCameraIr,tvCameraIr2;
    private ImageView ivCameraIr,ivCameraIr2;


    private TextView tvShowVer;
    private TextView tvControlVer;
    private TextView tvLedVer1,tvLedVer12;
    private TextView tvLedVer2,tvLedVer22;
    private TextView tvPedrailVer;
    private TextView tvSensorVer;
    private ColorVersionAdapter colorVersionAdapter;
    private ListView colorVersionList;
    private ListView cameraVersionList;
    private CameraVersionAdapter cameraVersionAdapter;
    private ListView irCameraVersionList,irCameraVersionList2;
    private CameraVersionAdapter irCameraVersionAdapter,irCameraVersionAdapter2;

    private TextView tvLayer;
    TextView tvDisplay;
    TextView tvControl;
    TextView tvLed,tv_led2;
    TextView tvPedrail;
    TextView tv_FeedSenser;
    TextView tvArm;
    TextView tvFPGA;
    TextView tvHardVersion;
    TextView tvFrontCamera;
    TextView tvRearCamera;
    TextView tvFrontCameraIr,tvFrontCameraIr2;
    TextView tvRearCameraIr,tvRearCameraIr2;
    TextView tvSoftCameraFront;
    TextView tvSoftCameraRear;
    TextView tvHardCameraFront;
    TextView tvHardCameraRear;
    TextView tvSoftCameraFrontIR,tvSoftCameraFrontIR2;
    TextView tvSoftCameraRearIR,tvSoftCameraRearIR2;
    TextView tvHardCameraFrontIR,tvHardCameraFrontIR2;
    TextView tvHardCameraRearIR,tvHardCameraRearIR2;

    MachineData machineData;
    LinearLayout linearldb,linearlwcgq,linearledb2;

    TextView phoneProtocolLabel,machineProtocolLabel,compatibilityLabel;
    PageSwitchView pageSwitchView;
    public VersionUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view==null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_version, null);
            linearLayout1 = (LinearLayout) view.findViewById(R.id.linearLayoutrjbbxx);
            linearLayout_layer= (LinearLayout) view.findViewById(R.id.linearLayout_layer);
            linearldb= (LinearLayout) view.findViewById(R.id.linearldb);
            linearlwcgq= (LinearLayout) view.findViewById(R.id.linearlwcgq);
            linearledb2= (LinearLayout) view.findViewById(R.id.linearledb2);
            linearLayout1.setVisibility(View.GONE);
            linearLayout2 = (LinearLayout) view.findViewById(R.id.linearsfbb);
            linearLayout2.setVisibility(View.GONE);
            linearLayout3 = (LinearLayout) view.findViewById(R.id.linearcamera);
            linearLayout3.setVisibility(View.GONE);
            linearLayout4 = (LinearLayout) view.findViewById(R.id.linearcamerair);
            linearLayout4.setVisibility(View.GONE);
            linearLayout5 = (LinearLayout) view.findViewById(R.id.linearcamerair2);
            linearLayout5.setVisibility(View.GONE);
            linearcamerair = (LinearLayout) view.findViewById(R.id.linearlayout4);
            linearcamerair2 = (LinearLayout) view.findViewById(R.id.linearlayout5);

            linearcamerair.setVisibility(View.GONE);
            linearcamerair2.setVisibility(View.GONE);


            tvSoft = (TextView) view.findViewById(R.id.tv_soft);
            ivSoft = (ImageView) view.findViewById(R.id.iv_soft);
            tvSoft.setOnClickListener(this);
            ivSoft.setOnClickListener(this);
            tvAlgorithm = (TextView) view.findViewById(R.id.tv_algorithm);
            ivAlgorithm = (ImageView) view.findViewById(R.id.iv_algorithm);
            tvAlgorithm.setOnClickListener(this);
            ivAlgorithm.setOnClickListener(this);
            tvCamera = (TextView) view.findViewById(R.id.tv_camera);
            ivCamera = (ImageView) view.findViewById(R.id.iv_camera);
            tvCamera.setOnClickListener(this);
            ivCamera.setOnClickListener(this);

            tvCameraIr = (TextView) view.findViewById(R.id.tv_camerair);
            ivCameraIr = (ImageView) view.findViewById(R.id.iv_camerair);
            tvCameraIr.setOnClickListener(this);
            ivCameraIr.setOnClickListener(this);

            tvCameraIr2 = (TextView) view.findViewById(R.id.tv_camerair2);
            ivCameraIr2 = (ImageView) view.findViewById(R.id.iv_camerair2);
            tvCameraIr2.setOnClickListener(this);
            ivCameraIr2.setOnClickListener(this);

            tvShowVer = (TextView) view.findViewById(R.id.tv_show_ver);
            tvControlVer = (TextView) view.findViewById(R.id.tv_control_ver);
            tvLedVer1 = (TextView) view.findViewById(R.id.tv_led1_ver);
            tvLedVer2 = (TextView) view.findViewById(R.id.tv_led2_ver);

            tvLedVer12 = (TextView) view.findViewById(R.id.tv_led1_ver2);
            tvLedVer22 = (TextView) view.findViewById(R.id.tv_led2_ver2);


            tvPedrailVer = (TextView) view.findViewById(R.id.tv_pedrail_ver);
            tvSensorVer = (TextView) view.findViewById(R.id.tv_senser_ver);

            colorVersionList = (ListView)view.findViewById(R.id.color_version_list);
            colorVersionAdapter = new ColorVersionAdapter(ctx,null);
            colorVersionList.setAdapter(colorVersionAdapter);

            cameraVersionList = (ListView)view.findViewById(R.id.camera_version_list);
            cameraVersionAdapter = new CameraVersionAdapter(ctx,null);
            cameraVersionList.setAdapter(cameraVersionAdapter);

            irCameraVersionList = (ListView)view.findViewById(R.id.ircamera_version_list);
            irCameraVersionAdapter = new CameraVersionAdapter(ctx,null);
            irCameraVersionList.setAdapter(irCameraVersionAdapter);

            irCameraVersionList2 = (ListView)view.findViewById(R.id.ircamera_version_list2);
            irCameraVersionAdapter2 = new CameraVersionAdapter(ctx,null);
            irCameraVersionList2.setAdapter(irCameraVersionAdapter2);

            tvLayer = (TextView)view.findViewById(R.id.tv_layer);
            tvDisplay = (TextView)view.findViewById(R.id.tv_display);
            tvLed = (TextView)view.findViewById(R.id.tv_led);
            tv_led2 = (TextView)view.findViewById(R.id.tv_led2);
            tvPedrail = (TextView)view.findViewById(R.id.tv_pedrail);
            tv_FeedSenser = (TextView)view.findViewById(R.id.tv_feed_senser);
            tvArm = (TextView)view.findViewById(R.id.tv_arm);
            tvFPGA = (TextView)view.findViewById(R.id.tv_fpga);
            tvHardVersion = (TextView)view.findViewById(R.id.tv_hardware);
            tvFrontCamera = (TextView)view.findViewById(R.id.tv_front_camera);
            tvRearCamera = (TextView)view.findViewById(R.id.tv_rear_camera);

            tvFrontCameraIr = (TextView)view.findViewById(R.id.tv_front_camera_ir);
            tvRearCameraIr = (TextView)view.findViewById(R.id.tv_rear_camera_ir);
            tvFrontCameraIr2 = (TextView)view.findViewById(R.id.tv_front_camera_ir2);
            tvRearCameraIr2 = (TextView)view.findViewById(R.id.tv_rear_camera_ir2);

            tvSoftCameraFront = (TextView)view.findViewById(R.id.tv_camera_soft_front);
            tvSoftCameraRear = (TextView)view.findViewById(R.id.tv_camera_soft_rear);
            tvHardCameraFront = (TextView)view.findViewById(R.id.tv_camera_hard_front);
            tvHardCameraRear = (TextView)view.findViewById(R.id.tv_camera_hard_rear);
            tvControl = (TextView)view.findViewById(R.id.tv_control);

            tvSoftCameraFrontIR = (TextView)view.findViewById(R.id.tv_camera_ir_soft_front);
            tvSoftCameraRearIR = (TextView)view.findViewById(R.id.tv_camera_ir_soft_rear);
            tvHardCameraFrontIR = (TextView)view.findViewById(R.id.tv_camera_ir_hard_front);
            tvHardCameraRearIR = (TextView)view.findViewById(R.id.tv_camera_ir_hard_rear);


            tvSoftCameraFrontIR2 = (TextView)view.findViewById(R.id.tv_camera_ir_soft_front2);
            tvSoftCameraRearIR2 = (TextView)view.findViewById(R.id.tv_camera_ir_soft_rear2);
            tvHardCameraFrontIR2 = (TextView)view.findViewById(R.id.tv_camera_ir_hard_front2);
            tvHardCameraRearIR2 = (TextView)view.findViewById(R.id.tv_camera_ir_hard_rear2);


            phoneProtocolLabel= (TextView) view.findViewById(R.id.phoneProtocolLabel);
            machineProtocolLabel= (TextView) view.findViewById(R.id.machineProtocolLabel);
            compatibilityLabel= (TextView) view.findViewById(R.id.compatibilityLabel);

            pageSwitchView = (PageSwitchView) view.findViewById(R.id.pageSwitchView);

            pageSwitchView.setPageSwitchListenser(new PageSwitchView.PageSwitchListenser() {
                @Override
                public void onPageSwitch(int pageIndex, int flag) {
                    if(flag == PageSwitchView.FLAG_OK)
                    {
                        AbstractDataServiceFactory.getInstance().getCurrentDevice().setCurrentLayer((byte)(pageIndex+1));
                        onReloadLayerView();
                    }
                }
            });

        }
        ThDevice device = AbstractDataServiceFactory.getInstance().getCurrentDevice();
        if(!device.isDeviceIsNull()){
            MachineData machineData = device.getMachineData();
            int bigVersion = ConvertUtils.unsignByteToInt(machineData.getProtocolVersionBig());
            int smallVersion = ConvertUtils.unsignByteToInt(machineData.getProtocolVersionSmall());

            machineProtocolLabel.setText(FileManager.getInstance().getString(49)+"v"+bigVersion+"."+smallVersion);//49#机器协议版本
            phoneProtocolLabel.setText(FileManager.getInstance().getString(50)+
                    "v"+ ConvertUtils.getBuildConfigVersionBig(machineData)
                    +"."+ ConvertUtils.getBuildConfigVersionSmall(machineData));//50#手机协议版本


            if (ConvertUtils.getPhoneProtocolVersion(machineData)>=ConvertUtils.getScreenProtocolVersion(machineData)){
                compatibilityLabel.setText("（"+FileManager.getInstance().getString(51)+"）");//51#兼容
                compatibilityLabel.setTextColor(Color.parseColor("#80aaaaaa"));
            }else{
                compatibilityLabel.setText("（"+FileManager.getInstance().getString(52)+"）");//52#不兼容
                compatibilityLabel.setTextColor(Color.parseColor("#ff0000"));
            }


        }else{
            machineProtocolLabel.setText("");
            phoneProtocolLabel.setText("");
            compatibilityLabel.setText("");
        }

        return view;
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_VERSION;
    }

    @Override
    public void receivePacketData(ThPackage packet) {
        if(packet.getType()== ThCommand.VERSION_CMD){
            ThSVersion ts = ThPackageHelper.parseThSVersion(packet);
            switch (packet.getExtendType())
            {
                case 0x01:
                    initBaseVersion(ts.getBaseVersion());
                    break;
                case 0x02:
                    initColorVersion(ts.getColorVersions());
                    break;
                case 0x03:
                    if (packet.getData1()[1] == 1)
                        initCameraVersion(ts.getCameraVersions());//可见相机
                    else if (packet.getData1()[1]==2)
                        initIrCameraVersion(ts.getCameraVersions());//红外相机
                    else if(packet.getData1()[1]==3)
                        initIrCamera2Version(ts.getCameraVersions());//红外相机2
                    break;
            }

        }
    }

    @Override
    protected void refreshPageData() {
        super.refreshPageData();
        onViewStart();
    }

    @Override
    public void onViewStart() {
        super.onViewStart();
        machineData=AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();
        setLanguage();
        initStatusView();
        versionLayoutInit();
        showLinearLayout(0);

        if(machineData.getUseSensor() <= 0)
        {
            linearlwcgq.setVisibility(View.GONE);
        }else
        {
            linearlwcgq.setVisibility(View.VISIBLE);
        }
    }

    private void versionLayoutInit(){


        tvFrontCameraIr.setVisibility(View.VISIBLE);
        tvFrontCameraIr2.setVisibility(View.VISIBLE);
        tvSoftCameraFrontIR.setVisibility(View.VISIBLE);
        tvHardCameraFrontIR.setVisibility(View.VISIBLE);
        tvSoftCameraFrontIR2.setVisibility(View.VISIBLE);
        tvHardCameraFrontIR2.setVisibility(View.VISIBLE);

        tvRearCameraIr.setVisibility(View.VISIBLE);
        tvRearCameraIr2.setVisibility(View.VISIBLE);
        tvSoftCameraRearIR.setVisibility(View.VISIBLE);
        tvHardCameraRearIR.setVisibility(View.VISIBLE);
        tvSoftCameraRearIR2.setVisibility(View.VISIBLE);
        tvHardCameraRearIR2.setVisibility(View.VISIBLE);


    }

    public void setLanguage()
    {
        tvSoft.setText(FileManager.getInstance().getString(63)); //63#基础版本
        tvAlgorithm.setText(FileManager.getInstance().getString(64)); //64#色选板版本
        tvCamera.setText(FileManager.getInstance().getString(65)); //65#可见相机版本
        tvCameraIr.setText(FileManager.getInstance().getString(66)); //66#红外相机版本
        tvCameraIr2.setText(FileManager.getInstance().getString(66)+"2"); //66#红外相机版本
        tvDisplay.setText(FileManager.getInstance().getString(67));//67#显示板
        tvControl.setText(FileManager.getInstance().getString(68));//68#控制板
        tvLed.setText(FileManager.getInstance().getString(69));//69#Led板
        tvPedrail.setText(FileManager.getInstance().getString(70));//70#履带
        tv_FeedSenser.setText(FileManager.getInstance().getString(71));//71#料位传感器
        tvArm.setText(FileManager.getInstance().getString(72));//72#ARM版本
        tvFPGA.setText(FileManager.getInstance().getString(73));//73#FPGA版本
        tvHardVersion.setText(FileManager.getInstance().getString(74));//74#硬件版本
        tvFrontCamera.setText(FileManager.getInstance().getString(75));//75#前视
        tvRearCamera.setText(FileManager.getInstance().getString(76));//76#后视
        tvFrontCameraIr.setText(FileManager.getInstance().getString(75));//75#前视
        tvFrontCameraIr2.setText(FileManager.getInstance().getString(75));//75#前视
        tvRearCameraIr.setText(FileManager.getInstance().getString(76));//76#后视
        tvRearCameraIr2.setText(FileManager.getInstance().getString(76));//76#后视

        tvSoftCameraFrontIR.setText(FileManager.getInstance().getString(77));//77#软件版本
        tvSoftCameraFrontIR2.setText(FileManager.getInstance().getString(77));//77#软件版本
        tvSoftCameraFront.setText(FileManager.getInstance().getString(77));//77#软件版本
        tvSoftCameraRearIR.setText(FileManager.getInstance().getString(77));//77#软件版本
        tvSoftCameraRearIR2.setText(FileManager.getInstance().getString(77));//77#软件版本
        tvSoftCameraRear.setText(FileManager.getInstance().getString(77));//77#软件版本


        tvSoftCameraFrontIR2.setText(FileManager.getInstance().getString(77));//77#软件版本
        tvSoftCameraRearIR2.setText(FileManager.getInstance().getString(77));//77#软件版本

        tvHardCameraFront.setText(FileManager.getInstance().getString(74));//74#硬件版本
        tvHardCameraFrontIR.setText(FileManager.getInstance().getString(74));//74#硬件版本
        tvHardCameraRear.setText(FileManager.getInstance().getString(74));//74#硬件版本
        tvHardCameraRearIR.setText(FileManager.getInstance().getString(74));//74#硬件版本
        tvHardCameraFrontIR2.setText(FileManager.getInstance().getString(74));//74#硬件版本
        tvHardCameraRearIR2.setText(FileManager.getInstance().getString(74));//74#硬件版本

        tv_led2.setText(FileManager.getInstance().getString(221));//221 分时LED板


    }

    private void updateBaseVersion(boolean bShow)
    {
        currentLayer= AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        if (bShow)
        {
            linearLayout1.setVisibility(View.VISIBLE);
            ivSoft.setImageResource(R.mipmap.versionlist1);
            //请求基础版本信息
            AbstractDataServiceFactory.getInstance().requestVersionInfo((byte)0x01,currentLayer,(byte)0x01); //ex_type =0x01：基础版本，data1[0]:层
        }
        else
        {
            linearLayout1.setVisibility(View.GONE);
            ivSoft.setImageResource(R.mipmap.versionlist2);
        }
    }

    private void initStatusView(){
        currentLayer = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        int layerNumber=AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getLayerNumber();

        if(layerNumber<=1){
            linearLayout_layer.setVisibility(View.GONE);
        }else{
            linearLayout_layer.setVisibility(View.VISIBLE);
            tvLayer.setText(StringUtils.getLayerStr(currentLayer));
        }

        pageSwitchView.setmNumbers(layerNumber);
        pageSwitchView.setmCurrentIndex(currentLayer);


    }

    private void updateColorVersion(boolean bShow)
    {
        byte layer= AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        if (bShow)
        {
            linearLayout2.setVisibility(View.VISIBLE);
            ivAlgorithm.setImageResource(R.mipmap.versionlist1);
            //请求色选板版本信息
            AbstractDataServiceFactory.getInstance().requestVersionInfo((byte)0x02,layer,(byte)0x01); // ex_type =0x02 色选板版本
        }
        else
        {
            linearLayout2.setVisibility(View.GONE);
            ivAlgorithm.setImageResource(R.mipmap.versionlist2);
        }
    }

    private void updateCameraVersion(boolean bShow)
    {
        byte layer= AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        if (bShow)
        {
            linearLayout3.setVisibility(View.VISIBLE);
            ivCamera.setImageResource(R.mipmap.versionlist1);

            //请求可见相机版本信息
            AbstractDataServiceFactory.getInstance().requestVersionInfo((byte)0x03,layer,(byte)0x01); // ex_type =0x03 相机版本，data1[0]:层，data1[1]:可见/红外 1/2
        }
        else
        {
            linearLayout3.setVisibility(View.GONE);
            ivCamera.setImageResource(R.mipmap.versionlist2);
        }
    }

    private void updateIrCameraVersion(boolean bShow)
    {
        currentLayer= AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        if (bShow)
        {
            linearLayout4.setVisibility(View.VISIBLE);
            ivCameraIr.setImageResource(R.mipmap.versionlist1);
            //请求红外相机版本信息
            AbstractDataServiceFactory.getInstance().requestVersionInfo((byte)0x03,currentLayer,(byte)0x02); // ex_type =0x03 相机版本，data1[0]:层，data1[1]:可见/红外 1/2
        }
        else
        {
            linearLayout4.setVisibility(View.GONE);
            ivCameraIr.setImageResource(R.mipmap.versionlist2);
        }
    }
    private void updateIrCamera2Version(boolean bShow)
    {
        currentLayer= AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();
        if (bShow)
        {
            linearLayout5.setVisibility(View.VISIBLE);
            ivCameraIr2.setImageResource(R.mipmap.versionlist1);
            //请求红外相机版本信息
            AbstractDataServiceFactory.getInstance().requestVersionInfo((byte)0x03,currentLayer,(byte)0x03); // ex_type =0x03 相机版本，data1[0]:层，data1[1]:可见/红外 1/2
        }
        else
        {
            linearLayout5.setVisibility(View.GONE);
            ivCameraIr2.setImageResource(R.mipmap.versionlist2);
        }
    }

    private void showLinearLayout(int index)
    {
        switch (index)
        {
            case 0:
                updateBaseVersion(false);
                updateColorVersion(false);
                updateCameraVersion(false);
                updateIrCameraVersion(false);
                updateIrCamera2Version(false);
                break;
            case 1:
                updateBaseVersion(true);
                updateColorVersion(false);
                updateCameraVersion(false);
                updateIrCameraVersion(false);
                updateIrCamera2Version(false);
                break;
            case 2:
                updateBaseVersion(false);
                updateColorVersion(true);
                updateCameraVersion(false);
                updateIrCameraVersion(false);
                updateIrCamera2Version(false);
                break;
            case 3:
                updateBaseVersion(false);
                updateColorVersion(false);
                updateCameraVersion(true);
                updateIrCameraVersion(false);
                updateIrCamera2Version(false);
                break;
            case 4:
                updateBaseVersion(false);
                updateColorVersion(false);
                updateCameraVersion(false);
                updateIrCameraVersion(true);
                updateIrCamera2Version(false);
                break;
            case 5:
                updateBaseVersion(false);
                updateColorVersion(false);
                updateCameraVersion(false);
                updateIrCameraVersion(false);
                updateIrCamera2Version(true);
                break;
        }

    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.tv_soft:
            case R.id.iv_soft:
                if (linearLayout1.getVisibility() == View.GONE)
                {
                    showLinearLayout(1);
                }
                else
                {
                    showLinearLayout(0);
                }

                break;
            case R.id.tv_algorithm:
            case R.id.iv_algorithm:
                if (linearLayout2.getVisibility() == View.GONE)
                {
                    showLinearLayout(2);

                }
                else
                {
                    showLinearLayout(0);
                }

                break;
            case R.id.tv_camera:
            case R.id.iv_camera:
                if (linearLayout3.getVisibility() == View.GONE)
                {
                    showLinearLayout(3);
                }
                else
                {
                    showLinearLayout(0);
                }
                break;
            case R.id.tv_camerair:
            case R.id.iv_camerair:
                if (linearLayout4.getVisibility() == View.GONE)
                {
                    showLinearLayout(4);
                }
                else
                {
                    showLinearLayout(0);
                }

                break;
            case R.id.tv_camerair2:
            case R.id.iv_camerair2:
                if (linearLayout5.getVisibility() == View.GONE)
                {
                    showLinearLayout(5);
                }
                else
                {
                    showLinearLayout(0);
                }

                break;
        }
    }


    @Override
    public void onReloadLayerView() {
        super.onReloadLayerView();
        versionLayoutInit();
        showLinearLayout(0);
        initStatusView();
    }

    private void initBaseVersion(ThSVersion.BaseVersion ts)
    {
        tvShowVer.setText(ts.getShowversion());
        tvControlVer.setText(Float.toString(ConvertUtils.bytes2ToFloatV2(ts.getControl())) );
        tvLedVer1 .setText(Float.toString(ConvertUtils.bytes2ToFloatV2(ts.getLed())));
        tvLedVer2 .setText(Float.toString(ConvertUtils.bytes2ToFloatV2(ts.getLed())));

        tvLedVer12 .setText(Float.toString(ConvertUtils.bytes2ToFloatV2(ts.getTimeLed())));
        tvLedVer22 .setText(Float.toString(ConvertUtils.bytes2ToFloatV2(ts.getTimeLed())));
        tvSensorVer.setText(Float.toString(ConvertUtils.bytes2ToFloatV2(ts.getSensor())));

    }

    private void initListViewLayout(ListView listview){

        int totalHeight=0;
        BaseAdapter adapter= (BaseAdapter) listview.getAdapter();
        for (int i=0;i<adapter.getCount();i++){
            View view=adapter.getView(0,null,null);
            view.measure(0,0);
            totalHeight+=view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams layoutParams=listview.getLayoutParams();


        layoutParams.height=totalHeight+25;

        listview.setLayoutParams(layoutParams);

    }

    private void initColorVersion( List<ThSVersion.ColorVersion> listColorVersion)
    {
        colorVersionAdapter.setRet(listColorVersion);
        initListViewLayout(colorVersionList);
        colorVersionAdapter.notifyDataSetChanged();
    }

    private void initCameraVersion(List<ThSVersion.CameraVersion> ts)
    {
        cameraVersionAdapter.setRet(ts);
        initListViewLayout(cameraVersionList);
        cameraVersionAdapter.notifyDataSetChanged();
    }

    private void initIrCameraVersion(List<ThSVersion.CameraVersion> ts)
    {
        irCameraVersionAdapter.setRet(ts);
        initListViewLayout(irCameraVersionList);
        irCameraVersionAdapter.notifyDataSetChanged();
    }

    private void initIrCamera2Version(List<ThSVersion.CameraVersion> ts)
    {
        irCameraVersionAdapter2.setRet(ts);
        initListViewLayout(irCameraVersionList2);
        irCameraVersionAdapter2.notifyDataSetChanged();
    }
}
