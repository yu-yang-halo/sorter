package com.yy.sorter.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.DigitalDialog;
import com.yy.sorter.utils.StringUtils;
import com.yy.sorter.view.AlwaysClickButton;
import com.yy.sorter.view.KeyboardDigitalEdit;
import com.yy.sorter.view.PageSwitchView;
import com.yy.sorter.view.ThSegmentView;

import java.util.ArrayList;
import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.YYFeeder;
import th.service.helper.YYCommand;
import th.service.helper.YYPackage;
import th.service.helper.YYPackageHelper;

public class FeederUi extends BaseUi implements DigitalDialog.Builder.LVCallback,AlwaysClickButton.LVMuiltClickCallBack{
    private ThSegmentView segmentView;
    private RelativeLayout layoutBottom;
    private RecyclerView recyclerView;
    private int tabSelectPos = 0;
    private MyAdapter myAdapter;
    private YYFeeder thFeeder;
    private KeyboardDigitalEdit feederEditText;
    private PageSwitchView pageSwitchView;
    private AlwaysClickButton addBtn,minusBtn;
    private TextView tv_all;
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
            pageSwitchView = (PageSwitchView) view.findViewById(R.id.pageSwitchView);

            addBtn = (AlwaysClickButton) view.findViewById(R.id.addBtn);
            minusBtn = (AlwaysClickButton) view.findViewById(R.id.minusBtn);
            tv_all = (TextView) view.findViewById(R.id.tv_all);

            feederEditText.setLVCallback(this);
            feederEditText.setValue(99,1,100);

            addBtn.setValve(1,this);
            minusBtn.setValve(0,this);


            List<ThSegmentView.TSegmentItem> items=new ArrayList<>();

            //111#分组调整
            //112#分料槽调整
            ThSegmentView.TSegmentItem item0 = new ThSegmentView.TSegmentItem(FileManager.getInstance().getString(111),0);
            ThSegmentView.TSegmentItem item1 = new ThSegmentView.TSegmentItem(FileManager.getInstance().getString(112),1);

            items.add(item0);
            items.add(item1);

            segmentView.setContents(items);
            segmentView.setOnSelectedListenser(new ThSegmentView.OnSelectedListenser() {
                @Override
                public void onSelected(int pos, ThSegmentView.TSegmentItem tSegmentItem) {
                    tabSelectPos = pos;
                    initLayout();
                    AbstractDataServiceFactory.getInstance().requestFeederInfo();
                    myAdapter.setItemList(null);
                    myAdapter.notifyDataSetChanged();
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(ctx));

            myAdapter = new MyAdapter();
            recyclerView.setAdapter(myAdapter);


            pageSwitchView.setPageSwitchListenser(new PageSwitchView.PageSwitchListenser() {
                @Override
                public void onPageSwitch(int pageIndex, int flag) {
                    if(flag == PageSwitchView.FLAG_OK)
                    {
                        segmentView.setSelectPos(pageIndex);
                        tabSelectPos = pageIndex;
                        initLayout();

                        AbstractDataServiceFactory.getInstance().requestFeederInfo();
                        myAdapter.setItemList(null);
                        myAdapter.notifyDataSetChanged();
                    }

                }
            });

            pageSwitchView.setmNumbers(2);



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

        pageSwitchView.setmCurrentIndex(tabSelectPos);
    }

    @Override
    public void onViewStart() {
        super.onViewStart();

        tv_all.setText(FileManager.getInstance().getString(127));//127#供料量

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
    public void receivePacketData(YYPackage packet) {
        if(packet.getType() == YYCommand.FEEDER_CMD)
        {

            if(packet.getExtendType() == 0x01)
            {
                thFeeder = YYPackageHelper.parseThFeeder(packet);
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
                        if(chuteIndex<0 || chuteIndex > 9)
                        {
                            return;
                        }
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
                        if(groupIndex<0 || groupIndex >3)
                        {
                            return;
                        }
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
                        if(chuteIndex<0 || chuteIndex > 9)
                        {
                            return;
                        }
                        thFeeder.getVibOpen()[chuteIndex] = packet.getData1()[2];
                        refreshRecycleViewData();
                    }
                    break;
                    case 3://组
                    {
                        byte groupIndex = packet.getData1()[1];
                        if(groupIndex<0 || groupIndex >3)
                        {
                            return;
                        }
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


        feederEditText.setText(String.valueOf(ConvertUtils.unsignByteToInt(thFeeder.getVibdata()[0])));

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

    int tmp = 0;
    @Override
    public void onMuiltClick(int par, int isSend) {
        if(isSend == 1)
        {
            tmp++;
            for(int i=0;i<10;i++)
            {
                if(par == 0)
                {
                    int value = ConvertUtils.unsignByteToInt(thFeeder.getVibdata()[i])-1;
                    if(value<0)
                    {
                        value = 0;
                    }
                    if(value>99)
                    {
                        value = 99;
                    }
                    thFeeder.getVibdata()[i] = (byte) value;

                }else
                {
                    int value = ConvertUtils.unsignByteToInt(thFeeder.getVibdata()[i])+1;
                    if(value<0)
                    {
                        value = 0;
                    }
                    if(value>99)
                    {
                        value = 99;
                    }
                    thFeeder.getVibdata()[i] = (byte) value;

                }

            }

            refreshRecycleViewData();

        }else
        {
            if(par==1)
            {
                AbstractDataServiceFactory.getInstance().setFeederValue((byte) 1,(byte) 0,(byte) tmp);
            }else
            {
                AbstractDataServiceFactory.getInstance().setFeederValue((byte) 2,(byte) 0,(byte) tmp);
            }
            tmp = 0;
        }
    }


    class Item
    {
        public byte type;//0 分组  1 单个
        public byte index;
        public byte value;
        public byte status;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder>
            implements DigitalDialog.Builder.LVCallback,AlwaysClickButton.LVMuiltClickCallBack
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
        public void onBindViewHolder(MyItemHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
            if(payloads.isEmpty())
            {
                onBindViewHolder(holder,position);
            }else
            {
                Item item = itemList.get(position);
                holder.editText.setText(String.valueOf(ConvertUtils.unsignByteToInt(item.value)));
            }

        }

        @Override
        public void onBindViewHolder(MyItemHolder holder, final int position) {


            if(itemList != null)
            {
                final Item item = itemList.get(position);

                if(item.type == 0)
                {
                    holder.tv_title.setText(StringUtils.getGroupStr(position+1));
                    holder.addBtn.setVisibility(View.VISIBLE);
                    holder.minusBtn.setVisibility(View.VISIBLE);
                }else
                {
                    holder.tv_title.setText(String.valueOf((position+1)));//79#料槽

                    holder.addBtn.setVisibility(View.VISIBLE);
                    holder.minusBtn.setVisibility(View.VISIBLE);
                }

                holder.btnSwitch.setCheckedImmediatelyNoEvent((item.status==1));
                holder.editText.setText(String.valueOf(ConvertUtils.unsignByteToInt(item.value)));
                holder.editText.setLVCallback(this);
                holder.editText.setValue(99,1,position);

                holder.addBtn.setValve(position,this);
                holder.minusBtn.setValve(position+100,this);

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

        @Override
        public void onMuiltClick(int par, int isSend) {
            int pos = 0;
            boolean isAdd = true;
            int value,max,min;
            if(par >= 100)
            {
                isAdd = false;
                pos = par-100;
            }else {
                isAdd = true;
                pos = par;
            }
            Item item = itemList.get(pos);
            value = ConvertUtils.unsignByteToInt(item.value);
            max = 99;
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
                item.value = (byte) value;
                itemList.get(pos).value = (byte) value;

                notifyItemChanged(pos,"update");

            }else
            {
                if(itemList.get(pos).type == 0)//分组
                {
                    AbstractDataServiceFactory.getInstance().setFeederValue((byte) 3,(byte)pos,(byte)value);
                }else
                {
                    AbstractDataServiceFactory.getInstance().setFeederValue((byte) 0,(byte)pos,(byte)value);
                }
            }

        }

        class MyItemHolder extends RecyclerView.ViewHolder
        {

            TextView tv_title;
            SwitchButton btnSwitch;
            KeyboardDigitalEdit editText;
            AlwaysClickButton addBtn,minusBtn;
            public MyItemHolder(View itemView) {
                super(itemView);
                tv_title = (TextView) itemView.findViewById(R.id.tv_title);
                btnSwitch = (SwitchButton) itemView.findViewById(R.id.btnSwitch);
                editText = (KeyboardDigitalEdit) itemView.findViewById(R.id.editText);
                addBtn = (AlwaysClickButton) itemView.findViewById(R.id.addBtn);
                minusBtn = (AlwaysClickButton) itemView.findViewById(R.id.minusBtn);
            }
        }
    }
}
