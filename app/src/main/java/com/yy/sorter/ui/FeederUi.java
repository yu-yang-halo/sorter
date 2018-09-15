package com.yy.sorter.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.DigitalDialog;
import com.yy.sorter.utils.StringUtils;
import com.yy.sorter.view.KeyboardDigitalEdit;
import com.yy.sorter.view.ThAutoLayout;
import com.yy.sorter.view.ThGroupView;
import com.yy.sorter.view.ThSegmentView;

import java.util.ArrayList;
import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.ThFeeder;
import th.service.data.ThMode;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

public class FeederUi extends BaseUi implements DigitalDialog.Builder.LVCallback{
    private ThSegmentView segmentView;
    private RelativeLayout layoutBottom;
    private RecyclerView recyclerView;
    private int tabSelectPos = 0;
    private MyAdapter myAdapter;
    private ThFeeder thFeeder;
    private KeyboardDigitalEdit feederEditText;
    public FeederUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_feeder, null);
            segmentView = (ThSegmentView) view.findViewById(R.id.segmentView);
            layoutBottom = (RelativeLayout)view.findViewById(R.id.layoutBottom);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            feederEditText = (KeyboardDigitalEdit) view.findViewById(R.id.editText);

            feederEditText.setLVCallback(this);
            feederEditText.setValue(99,1,100);


            List<ThSegmentView.TSegmentItem> items=new ArrayList<>();

            ThSegmentView.TSegmentItem item0 = new ThSegmentView.TSegmentItem("分组调整",0);
            ThSegmentView.TSegmentItem item1 = new ThSegmentView.TSegmentItem("单通道调整",1);

            items.add(item0);
            items.add(item1);

            segmentView.setContents(items);
            segmentView.setOnSelectedListenser(new ThSegmentView.OnSelectedListenser() {
                @Override
                public void onSelected(int pos, ThSegmentView.TSegmentItem tSegmentItem) {
                    tabSelectPos = pos;
                    initLayout();
                    AbstractDataServiceFactory.getInstance().requestFeederInfo();
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(ctx));

            myAdapter = new MyAdapter();
            recyclerView.setAdapter(myAdapter);



        }
        return view;
    }
    private void initLayout()
    {
        if(tabSelectPos == 0)
        {
            layoutBottom.setVisibility(View.GONE);
        }else
        {
            layoutBottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewStart() {
        super.onViewStart();

        initLayout();

        AbstractDataServiceFactory.getInstance().requestFeederInfo();

    }

    private void refreshRecycleViewData()
    {
        List<Item> itemList = getCurrentItems();
        myAdapter.setItemList(itemList);
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_FEEDER;
    }
    @Override
    public int getLeaver() {
        return ConstantValues.LEAVER_TAB;
    }

    @Override
    public void receivePacketData(ThPackage packet) {
        if(packet.getType() == 0x05)
        {

            if(packet.getExtendType() == 0x01)
            {
                thFeeder = ThPackageHelper.parseThFeeder(packet);
                refreshRecycleViewData();

            }else if(packet.getExtendType() == 0x02)
            {
                if(thFeeder == null)
                {
                    return;
                }
                switch (packet.getData1()[0])
                {
                    case 0://单个通道
                    {
                        byte chuteIndex = packet.getData1()[1];
                        thFeeder.getVibdata()[chuteIndex] = packet.getData1()[2];
                        refreshRecycleViewData();
                    }
                        break;
                    case 1://所有加
                    case 2://所有减
                        AbstractDataServiceFactory.getInstance().requestFeederInfo();
                        break;
                    case 3:
                    {
                        byte groupIndex = packet.getData1()[1];
                        thFeeder.getGroupData()[groupIndex] = packet.getData1()[2];
                        refreshRecycleViewData();
                    }
                        break;
                }

            }else if(packet.getExtendType() == 0x03)
            {
                switch (packet.getData1()[0])
                {
                    case 0://单个通道
                    {
                        byte chuteIndex = packet.getData1()[1];
                        thFeeder.getVibOpen()[chuteIndex] = packet.getData1()[2];
                        refreshRecycleViewData();
                    }
                    break;
                    case 3://组
                    {
                        byte groupIndex = packet.getData1()[1];
                        thFeeder.getGroupOpen()[groupIndex] = packet.getData1()[2];
                        refreshRecycleViewData();
                    }
                    break;
                }
            }


        }
    }



    private List<Item> getCurrentItems()
    {

        if(thFeeder == null)
        {
            return null;
        }
        System.out.println("getCurrentItems   "+thFeeder.toString());
        List<Item> itemList = new ArrayList<>();

        if(tabSelectPos == 0)
        {
            for(int i=0;i<thFeeder.getGroupNumber();i++)
            {
                Item item = new Item();
                item.type = (byte) tabSelectPos;
                item.index = (byte) i;
                item.value = thFeeder.getGroupData()[i];
                item.status = thFeeder.getGroupOpen()[i];
                itemList.add(item);
            }
        }else
        {
            for(int i=0;i<thFeeder.getChuteNumber();i++)
            {
                Item item = new Item();
                item.type = (byte) tabSelectPos;
                item.index = (byte) i;
                item.value = thFeeder.getVibdata()[i];
                item.status = thFeeder.getVibOpen()[i];
                itemList.add(item);
            }
        }

        return itemList;

    }

    @Override
    public void onConfirmClick(int value, int par) {
        if(par == 100)
        {
            if(thFeeder != null)
            {
                int diff = value-ConvertUtils.unsignByteToInt(thFeeder.getVibdata()[0]);
                if(diff>=0)
                {
                    AbstractDataServiceFactory.getInstance().setFeederValue((byte) 1,(byte) 0,(byte) diff);
                }else
                {
                    AbstractDataServiceFactory.getInstance().setFeederValue((byte) 2,(byte) 0,(byte) (-diff));
                }
            }
        }
    }


    class Item
    {
        public byte type;//0 分组  1 单个
        public byte index;
        public byte value;
        public byte status;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder> implements DigitalDialog.Builder.LVCallback
    {
        private List<Item> itemList;
        public MyAdapter()
        {

        }
        @Override
        public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_feeder_item,parent,false);
            MyItemHolder holder = new MyItemHolder(view);
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(MyItemHolder holder, final int position) {


            if(itemList != null)
            {
                final Item item = itemList.get(position);

                if(item.type == 0)
                {
                    holder.tv_title.setText(StringUtils.getGroupStr(position+1));
                }else
                {
                    holder.tv_title.setText("通道"+(position+1));
                }

                holder.tv_title.setText(""+item.index);
                holder.btnSwitch.setCheckedImmediatelyNoEvent((item.status==1));
                holder.editText.setText(String.valueOf(ConvertUtils.unsignByteToInt(item.value)));
                holder.editText.setLVCallback(this);
                holder.editText.setValue(99,1,position);

                holder.btnSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(item.type == 0)//分组
                        {
                            AbstractDataServiceFactory.getInstance().controlFeederSwitch((byte)3,(byte) position,(byte)0);
                        }else
                        {
                            AbstractDataServiceFactory.getInstance().controlFeederSwitch((byte)0,(byte) position,(byte)0);
                        }
                    }
                });


            }

        }

        public void setItemList(List<Item> itemList) {
            this.itemList = itemList;
        }

        @Override
        public int getItemCount() {
            if(itemList == null)
            {
                return 0;
            }else
            {
                return itemList.size();
            }
        }

        @Override
        public void onConfirmClick(int value, int par) {
            if(itemList!=null)
            {
                itemList.get(par).value = (byte) value;
                notifyItemChanged(par);

                if(itemList.get(par).type == 0)//分组
                {
                    AbstractDataServiceFactory.getInstance().setFeederValue((byte) 3,(byte)par,(byte)value);
                }else
                {
                    AbstractDataServiceFactory.getInstance().setFeederValue((byte) 0,(byte)par,(byte)value);
                }
            }
        }

        class MyItemHolder extends RecyclerView.ViewHolder
        {

            TextView tv_title;
            SwitchButton btnSwitch;
            KeyboardDigitalEdit editText;
            public MyItemHolder(View itemView) {
                super(itemView);
                tv_title = (TextView) itemView.findViewById(R.id.tv_title);
                btnSwitch = (SwitchButton) itemView.findViewById(R.id.btnSwitch);
                editText = (KeyboardDigitalEdit) itemView.findViewById(R.id.editText);
            }
        }
    }
}
