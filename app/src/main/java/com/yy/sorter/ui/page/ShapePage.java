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
import com.yy.sorter.view.AlwaysClickButton;
import com.yy.sorter.view.KeyboardDigitalEdit;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.ThShape;
import th.service.data.ThShapeItem;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;

public class ShapePage extends PageBaseUi {
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<ThShape> thShapeList;

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


            thShapeList = new ArrayList<>();

//            for(int k=0;k<2;k++)
//            {
//                ThShape thShape = new ThShape();
//                thShape.setShapeItemCount(3);
//                thShape.setShapeName("形选"+k);
//                thShape.setShapeType(k);
//
//                List<ThShapeItem> thShapeItemList = new ArrayList<>();
//
//
//                for(int i=0;i<2;i++)
//                {
//                    ThShapeItem thShapeItem = new ThShapeItem();
//                    thShapeItem.setShapeType((byte) (k));
//                    thShapeItem.setUsed((byte)(i%2));
//                    thShapeItem.setShapeId((byte) i);
//                    thShapeItem.setCount((byte) 5);
//                    thShapeItem.setName("Shape Item "+i);
//                    List<ThShapeItem.MiniItem> miniItems = new ArrayList<>();
//                    for(int j=0;j<2;j++)
//                    {
//                        ThShapeItem.MiniItem item = new ThShapeItem.MiniItem();
//                        item.setIndex((byte) j);
//                        item.setName("Mini Item "+j);
//                        item.setMin(ConvertUtils.intTo2Bytes(1));
//                        item.setMax(ConvertUtils.intTo2Bytes(199));
//                        item.setValue(ConvertUtils.intTo2Bytes(j*2+50));
//                        miniItems.add(item);
//                    }
//
//                    thShapeItem.setMiniItemList(miniItems);
//
//                    thShapeItemList.add(thShapeItem);
//                }
//
//                thShape.setShapeItemList(thShapeItemList);
//
//                thShapeList.add(thShape);
//
//            }


            myAdapter.setThShapeList(thShapeList);
            myAdapter.notifyDataSetChanged();

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
               thShapeList = ThPackageHelper.parseThShapeList(packet);
               myAdapter.setThShapeList(thShapeList);
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
        if(thShapeList == null)
        {
            return;
        }
        byte type = packet.getData1()[1];//0 使能  1 值
        byte shapeType = packet.getData1()[2];
        byte shapeId = packet.getData1()[3];
        byte index = packet.getData1()[4];
        int  value = ConvertUtils.bytes2ToInt(packet.getData1()[5],packet.getData1()[6]);

        for(ThShape thShape:thShapeList)
        {
            List<ThShapeItem> thShapeItemList = thShape.getShapeItemList();
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
        }



        myAdapter.setThShapeList(thShapeList);
        myAdapter.notifyDataSetChanged();

    }

    static class ItemAdapterTwo extends BaseAdapter implements DigitalDialog.Builder.LVCallback,AlwaysClickButton.LVMuiltClickCallBack
    {
        private ThShapeItem thShapeItem;
        private WeakReference<ListView> listView;
        public ItemAdapterTwo()
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_listview_shape_mini,null);

                holder = new ListViewHolder();
                holder.tv_itemName= (TextView) convertView.findViewById(R.id.tv_itemName);
                holder.edit_itemValue = (KeyboardDigitalEdit) convertView.findViewById(R.id.edit_itemValue);

                holder.addBtn = (AlwaysClickButton) convertView.findViewById(R.id.addBtn);
                holder.minusBtn = (AlwaysClickButton) convertView.findViewById(R.id.minusBtn);

                holder.addBtn.setValve(position,this);
                holder.minusBtn.setValve(position+100,this);

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
            ThShapeItem.MiniItem miniItem = (ThShapeItem.MiniItem) getItem(pos);

            if(miniItem == null)
            {
                return;
            }

            value = ConvertUtils.bytes2ToInt(miniItem.getValue());
            max = ConvertUtils.bytes2ToInt(miniItem.getMax());
            min = ConvertUtils.bytes2ToInt(miniItem.getMin());

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
                miniItem.setValue(ConvertUtils.intTo2Bytes(value));

                if(thShapeItem != null && thShapeItem.getMiniItemList() != null)
                {
                    thShapeItem.getMiniItemList().get(pos).setValue(ConvertUtils.intTo2Bytes(value));
                }

                updateItemView(pos,value);

            }else
            {
                byte group = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();
                AbstractDataServiceFactory.getInstance().setShapeInfo(group,(byte)1,
                        thShapeItem.getShapeType(),
                        thShapeItem.getShapeId(),
                        miniItem.getIndex(),value);

            }
        }

        class ListViewHolder
        {
            public TextView tv_itemName;
            public KeyboardDigitalEdit edit_itemValue;
            public AlwaysClickButton addBtn,minusBtn;
        }
    }



    static class ItemAdapterOne extends BaseAdapter
    {
        private ThShape thShape;
        private WeakReference<ListView> listView;

        public void setThShape(ThShape thShape) {
            this.thShape = thShape;
        }

        @Override
        public int getCount() {
            if(thShape == null || thShape.getShapeItemList() == null)
            {
                return 0;
            }
            return thShape.getShapeItemList().size();
        }

        @Override
        public Object getItem(int position) {
            if(thShape == null || thShape.getShapeItemList() == null)
            {
                return null;
            }else
            {
                return thShape.getShapeItemList().get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ShapeItemHolder holder;
            ItemAdapterTwo itemAdapter2;
            if(convertView == null)
            {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_listview_shape_item,null);

                holder = new ShapeItemHolder();
                holder.listView= (ListView) convertView.findViewById(R.id.listView);
                holder.ckEnable = (CheckBox) convertView.findViewById(R.id.ckEnable);
                holder.tv_ShapeName = (TextView) convertView.findViewById(R.id.tv_ShapeName);
                itemAdapter2 = new ItemAdapterTwo();
                holder.listView.setAdapter(itemAdapter2);
                itemAdapter2.setListView(new WeakReference<>(holder.listView));

                convertView.setTag(holder);
            }else
            {
                holder = (ShapeItemHolder) convertView.getTag();
                itemAdapter2 = (ItemAdapterTwo) holder.listView.getAdapter();

            }



            ThShapeItem thShapeItem = (ThShapeItem) getItem(position);
            if(thShapeItem == null)
            {
                return convertView;
            }


            itemAdapter2.setThShapeItem(thShapeItem);
            itemAdapter2.notifyDataSetChanged();

            ViewGroup.LayoutParams params = convertView.getLayoutParams();

            if(params == null)
            {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            }

            if(thShapeItem.getMiniItemList() != null && thShapeItem.getMiniItemList().size()>0)
            {
                int dividerHeight = holder.listView.getDividerHeight();
                params.height = ConvertUtils.toPx(50)*(thShapeItem.getMiniItemList().size())
                        + ConvertUtils.toPx(40)
                        + dividerHeight*(thShapeItem.getMiniItemList().size()-1);


            }else
            {
                params.height = 0;
            }
            convertView.setLayoutParams(params);

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
                    ThShapeItem thShapeItem = (ThShapeItem) getItem(position);

                    AbstractDataServiceFactory.getInstance().setShapeInfo(group,(byte)0,
                            thShapeItem.getShapeType(),thShapeItem.getShapeId(),(byte)0,0);

                }
            });


            return convertView;
        }

        public void setListView(WeakReference<ListView> listView) {
            this.listView = listView;
        }



        class ShapeItemHolder
        {
            public CheckBox ckEnable;
            public ListView listView;
            public TextView tv_ShapeName;
        }

    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder>
    {
        private List<ThShape> thShapeList;
        public MyAdapter()
        {

        }
        @Override
        public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_shape,parent,false);
            MyItemHolder holder = new MyItemHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyItemHolder holder, final int position) {
            ItemAdapterOne itemAdapter1 = new ItemAdapterOne();
            holder.listView.setAdapter(itemAdapter1);
            itemAdapter1.setListView(new WeakReference<>(holder.listView));

            if(thShapeList != null)
            {
                ThShape thShape = thShapeList.get(position);

                holder.tv_Shape.setText(thShape.getShapeName());


                itemAdapter1.setThShape(thShape);
                itemAdapter1.notifyDataSetChanged();

                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                if(params == null)
                {
                    params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
                }
                if(thShape.getShapeItemList() != null && thShape.getShapeItemList().size()>0)
                {


                    int height = 0;
                    for(ThShapeItem item:thShape.getShapeItemList())
                    {
                        height+=ConvertUtils.toPx(50)*(item.getMiniItemList().size())
                                + ConvertUtils.toPx(40)
                                + holder.listView.getDividerHeight()*(item.getMiniItemList().size()-1);
                    }

                    params.height = height
                            + ConvertUtils.toPx(40);


                }else
                {
                    params.height = 0;
                }
                holder.itemView.setLayoutParams(params);

            }


        }

        @Override
        public int getItemCount() {

            if(thShapeList == null)
            {
                return 0;
            }else
            {
                return thShapeList.size();
            }

        }

        public void setThShapeList(List<ThShape> thShapeList) {
            this.thShapeList = thShapeList;
        }

        class MyItemHolder extends RecyclerView.ViewHolder
        {
            TextView tv_Shape;
            ListView listView;
            View     itemView;
            public MyItemHolder(View itemView) {
                super(itemView);
                tv_Shape = (TextView) itemView.findViewById(R.id.tv_Shape);
                listView = (ListView) itemView.findViewById(R.id.listView);
                this.itemView = itemView;
            }
        }
    }
}
