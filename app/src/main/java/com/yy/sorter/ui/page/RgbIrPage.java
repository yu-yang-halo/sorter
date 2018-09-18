package com.yy.sorter.ui.page;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.DigitalDialog;
import com.yy.sorter.view.KeyboardDigitalEdit;

import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.ThSense;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

public class RgbIrPage extends PageBaseUi {
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<ThSense> thSenseList;

    public RgbIrPage(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_page_rgbir_sense, null);
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

        reqSenseInfo();
    }
    private void reqSenseInfo()
    {
        byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
        AbstractDataServiceFactory.getInstance().requestSenseInfo(group,(byte) 0);//0-rgb 1-ir
    }


    @Override
    public void onGroupChanged() {
        super.onGroupChanged();
        reqSenseInfo();
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_PAGE_RGB_IR;
    }

    @Override
    public void receivePacketData(ThPackage packet) {
        if(packet.getType()== ThCommand.SENSE_CMD)
        {
            if(packet.getExtendType() == 0x01)
            {
                thSenseList = ThPackageHelper.parseThSenses(packet);
                myAdapter.setThSenseList(thSenseList);
                myAdapter.notifyDataSetChanged();
            }else if(packet.getExtendType() == 0x02)
            {
                int hashId = ThPackageHelper.getHashId(packet.getData1()[1],packet.getData1()[2],
                        packet.getData1()[3],packet.getData1()[4]);

                int updatePos = 0;
                for (int i=0;i<thSenseList.size();i++)
                {
                    if(thSenseList.get(i).getHashId() == hashId)
                    {
                        thSenseList.get(i).setSense(new byte[]{packet.getData1()[5],packet.getData1()[6]});
                        updatePos = i;
                        break;
                    }
                }
                myAdapter.setThSenseList(thSenseList);
                myAdapter.notifyItemChanged(updatePos);
            }else if(packet.getExtendType() == 0x03)
            {
                int hashId = ThPackageHelper.getHashId(packet.getData1()[1],packet.getData1()[2],
                        packet.getData1()[3],packet.getData1()[4]);

                int updatePos = 0;
                for (int i=0;i<thSenseList.size();i++)
                {
                    if(thSenseList.get(i).getHashId() == hashId)
                    {
                        thSenseList.get(i).setUsed(packet.getData1()[5]);
                        updatePos = i;
                        break;
                    }
                }
                myAdapter.setThSenseList(thSenseList);
                myAdapter.notifyItemChanged(updatePos);
            }
        }
    }





    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder> implements DigitalDialog.Builder.LVCallback
    {
        private List<ThSense> thSenseList;
        public MyAdapter()
        {

        }
        @Override
        public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyItemHolder holder = null;
            switch (viewType)
            {
                case ConstantValues.VIEW_TYPE_ITEM:
                    View view0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_rgbir_item,parent,false);
                    holder = new MyItemHolder(view0);
                    break;
                default:
                    View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_space_item,parent,false);
                    holder = new MyItemHolder(view1);
                    break;
            }
        
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            if(thSenseList == null)
            {
                return ConstantValues.VIEW_TYPE_ITEM;
            }else {
                return thSenseList.get(position).getViewType();
            }
        }

        @Override
        public void onBindViewHolder(MyItemHolder holder, final int position) {
            ThSense senseItem = thSenseList.get(position);
            if(holder.getItemViewType() == ConstantValues.VIEW_TYPE_ITEM)
            {
                holder.editText.setText(String.valueOf(ConvertUtils.bytes2ToInt(senseItem.getSense())));
                holder.editText.setValue(ConvertUtils.bytes2ToInt(senseItem.getSenseMax()),
                        ConvertUtils.bytes2ToInt(senseItem.getSenseMin()),position);
                holder.editText.setLVCallback(this);

                holder.ckSense.setChecked(senseItem.getUsed() == 0x01);

                holder.ckSense.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
                        ThSense thSense = thSenseList.get(position);

                        AbstractDataServiceFactory.getInstance().setSenseEnable(group,thSense.getView(),
                                thSense.getType(),thSense.getSubType(),thSense.getExtType());

                    }
                });
                holder.ckSense.setText(senseItem.getName());

            }else
            {
                if(holder.tv_space != null)
                {
                    holder.tv_space.setText(senseItem.getName());
                }
            }
        }

        @Override
        public int getItemCount() {
           if(thSenseList == null)
           {
               return 0;
           }else
           {
               return thSenseList.size();
           }

        }

        @Override
        public void onConfirmClick(int value, int par) {
            ThSense thSense = thSenseList.get(par);
            thSense.setSense(ConvertUtils.intTo2Bytes(value));
            byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();

            AbstractDataServiceFactory.getInstance().setSenseValue(group,thSense.getView(),
                    thSense.getType(),thSense.getSubType(),thSense.getExtType(),value);


        }

        class MyItemHolder extends RecyclerView.ViewHolder
        {

            TextView tv_space;
            CheckBox ckSense;
            KeyboardDigitalEdit editText;
            public MyItemHolder(View itemView) {
                super(itemView);
                tv_space = (TextView) itemView.findViewById(R.id.tv_space);
                ckSense = (CheckBox) itemView.findViewById(R.id.ckSense);
                editText = (KeyboardDigitalEdit) itemView.findViewById(R.id.editText);
            }
        }

        public void setThSenseList(List<ThSense> thSenseList) {
            this.thSenseList = thSenseList;
        }
    }
}
