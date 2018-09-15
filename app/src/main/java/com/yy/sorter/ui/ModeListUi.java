package com.yy.sorter.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;

import java.util.List;

import javax.crypto.Mac;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.ThMode;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

public class ModeListUi extends BaseUi {
    private MachineData machineData;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<ThMode> thModeList;
    public ModeListUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view= LayoutInflater.from(ctx).inflate(R.layout.ui_mode_list,null);
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
        machineData = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();

        AbstractDataServiceFactory.getInstance().requestModeList(machineData.getSortModeBig());
    }

    @Override
    public void receivePacketData(ThPackage packet) {
        if(packet.getType() == ThCommand.MODE_CMD)
        {
            if(packet.getExtendType() == 0x01)
            {
                thModeList = ThPackageHelper.parseThModeList(packet);

                myAdapter.setThModeList(thModeList);
                myAdapter.notifyDataSetChanged();
            }else if(packet.getExtendType() == 0x02)
            {
                byte currentSmallIndex = packet.getData1()[0];
                byte bigIndex = packet.getData1()[1];
                if(packet.getData1()[2] == 0x01)
                {
                    showToast("读取方案成功");
                    if(thModeList != null)
                    {
                        for(ThMode mode : thModeList)
                        {
                            if(mode.getBigIndex() == bigIndex && mode.getSmallIndex() == currentSmallIndex)
                            {
                                mode.setCurrentMode(true);
                            }else
                            {
                                mode.setCurrentMode(false);
                            }
                        }

                    }

                }else
                {
                    showToast("读取方案失败");
                }

                myAdapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_MODE_LIST;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder>
    {
        List<ThMode> thModeList;
        public MyAdapter()
        {

        }
        public void setThModeList(List<ThMode> thModeList) {
            this.thModeList = thModeList;
        }

        @Override
        public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_mode_item,parent,false);
            MyItemHolder holder = new MyItemHolder(view);
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(MyItemHolder holder, int position) {
            final ThMode mode = thModeList.get(position);
            holder.tv_modeName.setText(mode.getModeName());
            if(mode.isCurrentMode())
            {
                holder.ckMode.setChecked(true);
                holder.tv_modeName.setTextColor(Color.parseColor("#ff0000"));
            }else
            {
                holder.ckMode.setChecked(false);
                holder.tv_modeName.setTextColor(Color.parseColor("#8c8c8c"));
            }

            holder.ckMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AbstractDataServiceFactory.getInstance().readMode(mode.getBigIndex(),mode.getSmallIndex());
                }
            });
        }

        @Override
        public int getItemCount() {
            if(thModeList == null)
            {
                return 0;
            }else
            {
                return thModeList.size();
            }
        }

        class MyItemHolder extends RecyclerView.ViewHolder
        {

            TextView tv_modeName;
            CheckBox ckMode;
            public MyItemHolder(View itemView) {
                super(itemView);
                tv_modeName = (TextView) itemView.findViewById(R.id.tv_modeName);
                ckMode = (CheckBox) itemView.findViewById(R.id.ckMode);
            }
        }
    }



}
