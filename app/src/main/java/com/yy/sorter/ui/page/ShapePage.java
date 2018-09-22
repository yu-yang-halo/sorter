package com.yy.sorter.ui.page;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.ConstantValues;

import java.util.List;

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
        }

        return view;
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

            }else if(packet.getExtendType()==0x02)
            {

            }
        }
    }



    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder>
    {
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

        }

        @Override
        public int getItemCount() {

            return 0;
        }

        class MyItemHolder extends RecyclerView.ViewHolder
        {


            public MyItemHolder(View itemView) {
                super(itemView);


            }
        }
    }
}
