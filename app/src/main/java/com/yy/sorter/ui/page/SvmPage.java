package com.yy.sorter.ui.page;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.DigitalDialog;
import com.yy.sorter.view.AlwaysClickButton;
import com.yy.sorter.view.KeyboardDigitalEdit;

import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.YYSvmInfo;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

public class SvmPage extends PageBaseUi {
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<YYSvmInfo> thSvmInfoList;
    public SvmPage(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_page_svm, null);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx));

            myAdapter = new MyAdapter();
            recyclerView.setAdapter(myAdapter);
        }
        return view;
    }

    @Override
    public void onViewStart() {
        super.onViewStart();
        reqSvmInfo();
    }
    private void reqSvmInfo()
    {
        currentGroup = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
        AbstractDataServiceFactory.getInstance().requestSvmInfo(currentGroup);
    }

    @Override
    public void onGroupChanged() {
        super.onGroupChanged();
        reqSvmInfo();
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_PAGE_SVM;
    }

    @Override
    public void receivePacketData(ThPackage packet) {
        if(packet.getType() == ThCommand.SVM_CMD)
        {
            if(packet.getExtendType() == 0x01)
            {
                thSvmInfoList = ThPackageHelper.parseThSvmInfos(packet);
                myAdapter.setThSvmInfoList(thSvmInfoList);
                myAdapter.notifyDataSetChanged();
            }else if(packet.getExtendType() == 0x02)
            {
                byte view = packet.getData1()[1];
                byte type = packet.getData1()[2];
                for(YYSvmInfo svmInfo:thSvmInfoList)
                {
                    if(type == 1)
                    {
                        svmInfo.setBlowSample(packet.getData1()[4]);
                    }else
                    {
                        if(svmInfo.getView() == view)
                        {
                            switch (type)
                            {
                                case 0:
                                    svmInfo.setUsed(packet.getData1()[4]);
                                    break;
                                case 2:
                                    svmInfo.getSpotDiff()[0] = packet.getData1()[3];
                                    svmInfo.getSpotDiff()[1] = packet.getData1()[4];
                                    break;
                                case 3:
                                    svmInfo.setSpotSensor(packet.getData1()[4]);
                                    break;
                            }
                        }
                    }


                }

                myAdapter.setThSvmInfoList(thSvmInfoList);
                myAdapter.notifyDataSetChanged();

            }

        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder>
            implements DigitalDialog.Builder.LVCallback,AlwaysClickButton.LVMuiltClickCallBack
    {
        private List<YYSvmInfo> thSvmInfoList;
        public MyAdapter()
        {

        }
        @Override
        public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_svm_item,parent,false);
            MyItemHolder holder = new MyItemHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyItemHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
            if(payloads.isEmpty())
            {
                onBindViewHolder(holder,position);
            }else
            {
                YYSvmInfo thSvmInfo = thSvmInfoList.get(position);
                holder.sensor_Edit.setText(String.valueOf(ConvertUtils.unsignByteToInt(thSvmInfo.getSpotSensor())));
                holder.spotDiff_Edit.setText(String.valueOf(ConvertUtils.bytes2ToInt(thSvmInfo.getSpotDiff())));

            }

        }

        @Override
        public void onBindViewHolder(MyItemHolder holder, final int position) {
            YYSvmInfo thSvmInfo = thSvmInfoList.get(position);
            if(thSvmInfo.getView() == 0)
            {
                holder.tv_front.setText(FileManager.getInstance().getString(75));//75#前视
            }else{
                holder.tv_front.setText(FileManager.getInstance().getString(76));//76#后视
            }
            holder.sensor_Edit.setValue(100,0,position*2);
            holder.spotDiff_Edit.setValue(ConvertUtils.bytes2ToInt(thSvmInfo.getSpotDiffMax()),1,position*2+1);
            holder.sensor_Edit.setLVCallback(this);
            holder.spotDiff_Edit.setLVCallback(this);


            holder.sensor_Edit.setText(String.valueOf(ConvertUtils.unsignByteToInt(thSvmInfo.getSpotSensor())));
            holder.spotDiff_Edit.setText(String.valueOf(ConvertUtils.bytes2ToInt(thSvmInfo.getSpotDiff())));

            if(thSvmInfo.getBlowSample() == 0)
            {
                holder.blowSampleBtn.setText(FileManager.getInstance().getString(100));//100#剔除负样本
                holder.blowSampleBtn.setSelected(false);
            }else {
                holder.blowSampleBtn.setText(FileManager.getInstance().getString(101));//101#剔除正样本
                holder.blowSampleBtn.setSelected(true);
            }


            if(thSvmInfo.getUsed() == 0)
            {
                holder.usedBtn.setText(FileManager.getInstance().getString(102));//102#禁用
                holder.usedBtn.setSelected(false);
            }else {
                holder.usedBtn.setText(FileManager.getInstance().getString(103));//103#使用
                holder.usedBtn.setSelected(true);
            }
            
            holder.tv_sense.setText(FileManager.getInstance().getString(104));//104#灵敏度
            holder.tv_defect_ratio.setText(FileManager.getInstance().getString(105));//105#杂质比

            holder.blowSampleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
                    YYSvmInfo thSvmInfo = thSvmInfoList.get(position);
                    AbstractDataServiceFactory.getInstance().setSvmInfo(group,thSvmInfo.getView(), (byte) 1,0);
                }
            });
            holder.usedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
                    YYSvmInfo thSvmInfo = thSvmInfoList.get(position);
                    AbstractDataServiceFactory.getInstance().setSvmInfo(group,thSvmInfo.getView(), (byte) 0,0);
                }
            });


            holder.defect_addBtn.setValve(position,this);
            holder.defect_minusBtn.setValve(position+100,this);

            holder.sense_addBtn.setValve(position+200,this);
            holder.sense_minusBtn.setValve(position+300,this);
        }

        @Override
        public int getItemCount() {
            if(thSvmInfoList == null)
            {
                return 0;
            }else {
                return thSvmInfoList.size();
            }

        }

        @Override
        public void onConfirmClick(int value, int par) {
            int pos = par/2;
            int type = par%2;
            YYSvmInfo thSvmInfo = thSvmInfoList.get(pos);
            byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();

            if(type == 0)//灵敏度
            {
                AbstractDataServiceFactory.getInstance().setSvmInfo(group,thSvmInfo.getView(), (byte) 3,value);
            }else if(type == 1)//杂质比
            {
                AbstractDataServiceFactory.getInstance().setSvmInfo(group,thSvmInfo.getView(), (byte) 2,value);
            }

        }

        private void clickSpotDiff(int pos,int isSend,boolean isAdd)
        {
            if(thSvmInfoList == null || pos >= thSvmInfoList.size())
            {
                return;
            }
            int value,max,min;
            YYSvmInfo thSvmInfo = thSvmInfoList.get(pos);
            value = ConvertUtils.bytes2ToInt(thSvmInfo.getSpotDiff());
            max = ConvertUtils.bytes2ToInt(thSvmInfo.getSpotDiffMax());
            min = 1;

            if(isSend == 1)
            {
                if(isAdd)
                {
                    value=value+1;
                }else
                {
                    value=value-1;
                }
                if(value<min)
                {
                    value = min;
                }
                if(value>max)
                {
                    value = max;
                }
                thSvmInfo.setSpotDiff(ConvertUtils.intTo2Bytes(value));
                thSvmInfoList.get(pos).setSpotDiff(ConvertUtils.intTo2Bytes(value));

                notifyItemChanged(pos,"update");

            }else
            {
                byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
                AbstractDataServiceFactory.getInstance().setSvmInfo(group,thSvmInfo.getView(), (byte) 2,value);
            }
        }
        private void clickSense(int pos,int isSend,boolean isAdd)
        {

            if(thSvmInfoList == null || pos >= thSvmInfoList.size())
            {
                return;
            }


            int value,max,min;
            YYSvmInfo thSvmInfo = thSvmInfoList.get(pos);
            value = ConvertUtils.unsignByteToInt(thSvmInfo.getSpotSensor());
            max = 100;
            min = 0;

            if(isSend == 1)
            {
                if(isAdd)
                {
                    value=value+1;
                }else
                {
                    value=value-1;
                }
                if(value<min)
                {
                    value = min;
                }
                if(value>max)
                {
                    value = max;
                }
                thSvmInfo.setSpotSensor((byte) value);
                thSvmInfoList.get(pos).setSpotSensor((byte) value);

                notifyItemChanged(pos,"update");

            }else
            {
                byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
                AbstractDataServiceFactory.getInstance().setSvmInfo(group,thSvmInfo.getView(), (byte) 3,value);
            }
        }


        @Override
        public void onMuiltClick(int par, int isSend) {
            int pos = 0;
            boolean isAdd = true;

            if(par < 200)
            {

                if(par >= 100)
                {
                    isAdd = false;
                    pos = par-100;
                }else {
                    isAdd = true;
                    pos = par;
                }

                clickSpotDiff(pos,isSend,isAdd);

            }else
            {
                if(par >= 300)
                {
                    isAdd = false;
                    pos = par-300;
                }else {
                    isAdd = true;
                    pos = par-200;
                }

                clickSense(pos,isSend,isAdd);
            }

        }

        class MyItemHolder extends RecyclerView.ViewHolder
        {
            TextView tv_front,tv_defect_ratio,tv_sense;
            Button blowSampleBtn,usedBtn;
            KeyboardDigitalEdit spotDiff_Edit,sensor_Edit;
            AlwaysClickButton defect_minusBtn,defect_addBtn,sense_minusBtn,sense_addBtn;

            public MyItemHolder(View itemView) {
                super(itemView);
                tv_front = (TextView) itemView.findViewById(R.id.tv_front);
                tv_defect_ratio = (TextView) itemView.findViewById(R.id.tv_defect_ratio);
                tv_sense = (TextView) itemView.findViewById(R.id.tv_sense);

                blowSampleBtn = (Button) itemView.findViewById(R.id.blowSampleBtn);
                usedBtn = (Button) itemView.findViewById(R.id.usedBtn);
                spotDiff_Edit = (KeyboardDigitalEdit) itemView.findViewById(R.id.spotDiff_Edit);
                sensor_Edit = (KeyboardDigitalEdit) itemView.findViewById(R.id.sensor_Edit);

                defect_addBtn = (AlwaysClickButton) itemView.findViewById(R.id.addBtn);
                defect_minusBtn = (AlwaysClickButton) itemView.findViewById(R.id.minusBtn);

                sense_addBtn = (AlwaysClickButton) itemView.findViewById(R.id.addBtn2);
                sense_minusBtn = (AlwaysClickButton) itemView.findViewById(R.id.minusBtn2);



            }
        }


        public void setThSvmInfoList(List<YYSvmInfo> thSvmInfoList) {
            this.thSvmInfoList = thSvmInfoList;
        }
    }
}
