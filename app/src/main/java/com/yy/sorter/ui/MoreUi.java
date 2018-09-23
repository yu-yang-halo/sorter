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

import th.service.helper.ThPackage;

public class MoreUi extends BaseUi {
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
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

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder>
    {
        private String[] strList = new String[]{
                FileManager.getInstance().getString(62),
                FileManager.getInstance().getString(113),
                FileManager.getInstance().getString(114),
                FileManager.getInstance().getString(115)};
        public MyAdapter()
        {

        }
        @Override
        public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_more_item,parent,false);
            MyItemHolder holder = new MyItemHolder(view);
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(MyAdapter.MyItemHolder holder, final int position) {
            holder.tv_modeName.setText(strList[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String titleName = "";
                    if(position>=0 && position<=3)
                    {
                        titleName = strList[position];
                    }

                    switch (position)
                    {
                        case 0:
                            MiddleManger.getInstance().changeUI(ConstantValues.VIEW_VERSION,titleName);
                            break;
                        case 1:
                            MiddleManger.getInstance().changeUI(ConstantValues.VIEW_CAMERAADJUST,titleName);
                            break;
                        case 2:
                            MiddleManger.getInstance().changeUI(ConstantValues.VIEW_BACKGROUND,titleName);
                            break;
                        case 3:
                            MiddleManger.getInstance().changeUI(ConstantValues.VIEW_VALVE_RATE,titleName);
                            break;
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return strList.length;
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
