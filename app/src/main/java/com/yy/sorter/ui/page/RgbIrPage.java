package com.yy.sorter.ui.page;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.ConstantValues;
import th.service.helper.ThPackage;

public class RgbIrPage extends PageBaseUi {
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
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
    public int getID() {
        return ConstantValues.VIEW_PAGE_RGB_IR;
    }

    @Override
    public void receivePacketData(ThPackage packet) {

    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder>
    {
        public MyAdapter()
        {

        }
        @Override
        public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_rgbir_item,parent,false);
            MyItemHolder holder = new MyItemHolder(view);
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(MyItemHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
           return 10;
        }

        class MyItemHolder extends RecyclerView.ViewHolder
        {

            CheckBox ckSense;
            public MyItemHolder(View itemView) {
                super(itemView);
                ckSense = (CheckBox) itemView.findViewById(R.id.ckSense);
            }
        }
    }
}
