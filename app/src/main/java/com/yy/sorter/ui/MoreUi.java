package com.yy.sorter.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;

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

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder>
    {

        private String[] strList = new String[]{"版本信息","光学校准","信号设置"};
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
        public void onBindViewHolder(MyAdapter.MyItemHolder holder, int position) {
            holder.tv_modeName.setText(strList[position]);
        }

        @Override
        public int getItemCount() {
            return strList.length;
        }

        class MyItemHolder extends RecyclerView.ViewHolder
        {

            TextView tv_modeName;
            public MyItemHolder(View itemView) {
                super(itemView);
                tv_modeName = (TextView) itemView.findViewById(R.id.tv_modeName);
            }
        }
    }

}
