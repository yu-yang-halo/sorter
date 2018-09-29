package com.yy.sorter.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;

import java.util.ArrayList;
import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.helper.ThPackage;

public class MoreUi extends BaseUi {
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<ItemObj> objList;
    public MoreUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view= LayoutInflater.from(ctx).inflate(R.layout.ui_more,null);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx));

            myAdapter = new MoreUi.MyAdapter();
            recyclerView.setAdapter(myAdapter);
        }
        return view;
    }

    @Override
    public void onViewStart() {
        super.onViewStart();
        objList = buildItemObjList();
        myAdapter.setItemObjList(objList);
        myAdapter.notifyDataSetChanged();

    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_MORE;
    }
    @Override
    public int getLeaver() {
        return ConstantValues.LEAVER_TAB;
    }

    @Override
    public void receivePacketData(ThPackage packet) {

    }
    public static List<ItemObj> buildItemObjList()
    {
        List<ItemObj> objList = new ArrayList<>();

        ItemObj itemObj0 = new ItemObj(0,FileManager.getInstance().getString(62));
        ItemObj itemObj1 = new ItemObj(1,FileManager.getInstance().getString(113));
        ItemObj itemObj2 = new ItemObj(2,FileManager.getInstance().getString(114));
        ItemObj itemObj3 = new ItemObj(3,FileManager.getInstance().getString(115));

        objList.add(itemObj0);
        MachineData machineData = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();

        if(machineData != null)
        {
            if(machineData.getUserLevel() == MachineData.LEVEL_ENGINNER
                    || machineData.getUserLevel() == MachineData.LEVEL_PRODUCTOR)
            {
                objList.add(itemObj1);
                objList.add(itemObj2);
            }
        }

        objList.add(itemObj3);

        return objList;
    }
    public static class ItemObj
    {
        public  int itemId;
        public  String itemName;

        public ItemObj(int itemId, String itemName) {
            this.itemId = itemId;
            this.itemName = itemName;
        }

    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder>
    {

        private List<ItemObj> itemObjList;
        public MyAdapter()
        {

        }
        @Override
        public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_more_item,parent,false);
            MyItemHolder holder = new MyItemHolder(view);
            return holder;
        }

        public void setItemObjList(List<ItemObj> itemObjList) {
            this.itemObjList = itemObjList;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(MyAdapter.MyItemHolder holder, final int position) {
            if(itemObjList == null || position >= itemObjList.size())
            {
                return;
            }
            holder.tv_modeName.setText(itemObjList.get(position).itemName);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(itemObjList == null || position >= itemObjList.size())
                    {
                        return;
                    }
                    ItemObj itemObj = itemObjList.get(position);

                    if(itemObj == null)
                    {
                        return;
                    }

                    switch (itemObj.itemId)
                    {
                        case 0:
                            MiddleManger.getInstance().changeUI(ConstantValues.VIEW_VERSION,itemObj.itemName);
                            break;
                        case 1:
                            MiddleManger.getInstance().changeUI(ConstantValues.VIEW_CAMERAADJUST,itemObj.itemName);
                            break;
                        case 2:
                            MiddleManger.getInstance().changeUI(ConstantValues.VIEW_BACKGROUND,itemObj.itemName);
                            break;
                        case 3:
                            MiddleManger.getInstance().changeUI(ConstantValues.VIEW_VALVE_RATE,itemObj.itemName);
                            break;
                    }



                }
            });
        }

        @Override
        public int getItemCount() {
            if(itemObjList == null)
            {
                return 0;
            }
            return itemObjList.size();
        }

        class MyItemHolder extends RecyclerView.ViewHolder
        {
            View itemView;
            TextView tv_modeName;
            public MyItemHolder(final View itemView) {
                super(itemView);
                tv_modeName = (TextView) itemView.findViewById(R.id.tv_modeName);
                this.itemView = itemView;

            }
        }
    }

}
