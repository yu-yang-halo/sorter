package com.yy.sorter.ui.page;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.DigitalDialog;
import com.yy.sorter.view.KeyboardDigitalEdit;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.ThShapeItem;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

public class ShapePage extends PageBaseUi {
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<ThShapeItem> thShapeItemList;

    public ShapePage(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_page_shape, null);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx));

            myAdapter = new MyAdapter();
            recyclerView.setAdapter(myAdapter);


//            thShapeItemList = new ArrayList<>();
//
//            for(int i=0;i<10;i++)
//            {
//                ThShapeItem thShapeItem = new ThShapeItem();
//                thShapeItem.setShapeType((byte) (i%2));
//                thShapeItem.setUsed((byte)(i%2));
//                thShapeItem.setShapeId((byte) i);
//                thShapeItem.setCount((byte) 5);
//                thShapeItem.setName("Shape Item "+i);
//                List<ThShapeItem.MiniItem> miniItems = new ArrayList<>();
//                for(int j=0;j<5;j++)
//                {
//                    ThShapeItem.MiniItem item = new ThShapeItem.MiniItem();
//                    item.setIndex((byte) j);
//                    item.setName("Mini Item "+j);
//                    item.setMin(ConvertUtils.intTo2Bytes(1));
//                    item.setMax(ConvertUtils.intTo2Bytes(199));
//                    item.setValue(ConvertUtils.intTo2Bytes(j*2+50));
//                    miniItems.add(item);
//                }
//
//                thShapeItem.setMiniItemList(miniItems);
//
//                thShapeItemList.add(thShapeItem);
//            }

//            myAdapter.setThShapeItemList(thShapeItemList);

        }

        return view;


    }

    @Override
    public void onGroupChanged() {
        super.onGroupChanged();
        reqShapeInfo();
    }
    private void reqShapeInfo()
    {
        currentGroup = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
        AbstractDataServiceFactory.getInstance().requestShapeInfo(currentGroup);
    }

    @Override
    public void onViewStart() {
        super.onViewStart();
        reqShapeInfo();
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_PAGE_SHAPE;
    }

    @Override
    public void receivePacketData(ThPackage packet) {
        if(packet.getType() == ThCommand.SHAPE_CMD)
        {
            if(packet.getExtendType() == 0x01)
            {
               thShapeItemList = ThPackageHelper.parseThShapeItemList(packet);
               myAdapter.setThShapeItemList(thShapeItemList);
               myAdapter.notifyDataSetChanged();

            }else if(packet.getExtendType()==0x02)
            {

                updateShapeItem(packet);
                //reqShapeInfo();

            }
        }
    }
    private void updateShapeItem(ThPackage packet)
    {
        if(thShapeItemList == null)
        {
            return;
        }
        byte type = packet.getData1()[1];//0 使能  1 值
        byte shapeType = packet.getData1()[2];
        byte shapeId = packet.getData1()[3];
        byte index = packet.getData1()[4];
        int  value = ConvertUtils.bytes2ToInt(packet.getData1()[5],packet.getData1()[6]);


        for(ThShapeItem thShapeItem:thShapeItemList)
        {
            if(thShapeItem.getShapeType() == shapeType
                    && thShapeItem.getShapeId() == shapeId)
            {
                if(type == 0)
                {
                   thShapeItem.setUsed((byte) value);
                   if(value == 0)
                   {
                       continue;
                   }
                   for(ThShapeItem tmp:thShapeItemList)
                   {
                       if(thShapeItem.getShapeType() == tmp.getShapeType()
                               && thShapeItem.getMutex() == tmp.getShapeId())
                       {
                           if(value == 1)
                           {
                               tmp.setUsed((byte) 0);
                           }
                           break;
                       }
                   }
                }else
                {
                    for(ThShapeItem.MiniItem miniItem:thShapeItem.getMiniItemList())
                    {
                        if(miniItem.getIndex() == index)
                        {
                            miniItem.setValue(ConvertUtils.intTo2Bytes(value));
                        }
                    }
                }
            }
        }

        myAdapter.setThShapeItemList(thShapeItemList);
        myAdapter.notifyDataSetChanged();

    }

    class ItemAdapter extends BaseAdapter implements DigitalDialog.Builder.LVCallback
    {
        private ThShapeItem thShapeItem;
        private WeakReference<ListView> listView;
        public ItemAdapter()
        {

        }
        @Override
        public int getCount() {
            if(thShapeItem == null || thShapeItem.getMiniItemList() == null)
            {
                return 0;
            }else
            {
                return thShapeItem.getMiniItemList().size();
            }
        }

        @Override
        public Object getItem(int position) {
            if(thShapeItem == null || thShapeItem.getMiniItemList() == null)
            {
                return null;
            }else
            {
                return thShapeItem.getMiniItemList().get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListViewHolder holder;
            if(convertView == null)
            {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_listview_shape,null);

                holder = new ListViewHolder();
                holder.tv_itemName= (TextView) convertView.findViewById(R.id.tv_itemName);
                holder.edit_itemValue = (KeyboardDigitalEdit) convertView.findViewById(R.id.edit_itemValue);

                convertView.setTag(holder);
            }else
            {
                holder = (ListViewHolder) convertView.getTag();
            }

            ThShapeItem.MiniItem miniItem = (ThShapeItem.MiniItem) getItem(position);
            if(miniItem != null)
            {
                holder.edit_itemValue.setValue(ConvertUtils.bytes2ToInt(miniItem.getMax()),
                        ConvertUtils.bytes2ToInt(miniItem.getMin()),position);
                holder.edit_itemValue.setText(String.valueOf(ConvertUtils.bytes2ToInt(miniItem.getValue())));
                holder.edit_itemValue.setLVCallback(this);

                holder.tv_itemName.setText(miniItem.getName());
            }

            return convertView;
        }

        public ThShapeItem getThShapeItem() {
            return thShapeItem;
        }

        public void setThShapeItem(ThShapeItem thShapeItem) {
            this.thShapeItem = thShapeItem;
        }

        public void updateItemView(int position, int value){
            int firstPos = listView.get().getFirstVisiblePosition();
            int lastPos = listView.get().getLastVisiblePosition();
            if(position >= firstPos && position <= lastPos){  //可见才更新，不可见则在getView()时更新
                //listview.getChildAt(i)获得的是当前可见的第i个item的view
                View view = listView.get().getChildAt(position - firstPos);
                ListViewHolder holder = (ListViewHolder)view.getTag();
                holder.edit_itemValue.setText(String.valueOf(value));
            }
        }

        @Override
        public void onConfirmClick(int value, int par) {
            ThShapeItem.MiniItem miniItem = (ThShapeItem.MiniItem) getItem(par);
            if(miniItem != null)
            {
                byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
                AbstractDataServiceFactory.getInstance().setShapeInfo(group,(byte)1,
                        thShapeItem.getShapeType(),
                        thShapeItem.getShapeId(),
                        miniItem.getIndex(),value);

                updateItemView(par,value);

            }
        }

        public void setListView(WeakReference<ListView> listView) {
            this.listView = listView;
        }

        class ListViewHolder
        {
            public TextView tv_itemName;
            public KeyboardDigitalEdit edit_itemValue;
        }
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder>
    {
        private List<ThShapeItem> thShapeItemList;
        public MyAdapter()
        {

        }
        @Override
        public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_shape_item,parent,false);
            MyItemHolder holder = new MyItemHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyItemHolder holder, final int position) {
            ItemAdapter itemAdapter = new ItemAdapter();
            holder.listView.setAdapter(itemAdapter);
            itemAdapter.setListView(new WeakReference<>(holder.listView));

            if(thShapeItemList != null)
            {
                ThShapeItem thShapeItem = thShapeItemList.get(position);
                itemAdapter.setThShapeItem(thShapeItem);
                itemAdapter.notifyDataSetChanged();

                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                if(thShapeItem.getMiniItemList() != null)
                {
                    params.height = ConvertUtils.toPx(50)*(thShapeItem.getMiniItemList().size())+
                            ConvertUtils.toPx(40);
                }else
                {
                    params.height = 0;
                }
                holder.itemView.setLayoutParams(params);

                if(thShapeItem.getUsed() == 1)
                {
                    holder.ckEnable.setChecked(true);
                }else
                {
                    holder.ckEnable.setChecked(false);
                }

                holder.tv_ShapeName.setText(thShapeItem.getName());


                holder.ckEnable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
                        ThShapeItem thShapeItem = thShapeItemList.get(position);

                        AbstractDataServiceFactory.getInstance().setShapeInfo(group,(byte)0,
                                thShapeItem.getShapeType(),thShapeItem.getShapeId(),(byte)0,0);

                    }
                });
            }


        }

        @Override
        public int getItemCount() {

            if(thShapeItemList == null)
            {
                return 0;
            }else
            {
                return thShapeItemList.size();
            }

        }

        public void setThShapeItemList(List<ThShapeItem> thShapeItemList) {
            this.thShapeItemList = thShapeItemList;
        }

        class MyItemHolder extends RecyclerView.ViewHolder
        {
            CheckBox ckEnable;
            TextView tv_ShapeName;
            ListView listView;
            View     itemView;
            public MyItemHolder(View itemView) {
                super(itemView);
                ckEnable = (CheckBox) itemView.findViewById(R.id.ckEnable);
                tv_ShapeName = (TextView) itemView.findViewById(R.id.tv_ShapeName);
                listView = (ListView) itemView.findViewById(R.id.listView);
                this.itemView = itemView;
            }
        }
    }
}
