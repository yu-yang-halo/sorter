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

public class ModeListUi extends BaseUi {
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    public ModeListUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view= LayoutInflater.from(ctx).inflate(R.layout.ui_mode_list,null);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx));

            myAdapter = new MyAdapter();
            recyclerView.setAdapter(myAdapter);
        }
        return view;
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_MODE_LIST;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder>
    {

        private String[] strList = new String[]{"选长米","选花生","选果子","选长米","选花生"
                ,"选果子","选长米","选花生","选果子","选长米",
                "选花生","选果子","选长米","选花生","选果子"
                ,"选长米","选花生","选果子"};
        public MyAdapter()
        {

        }
        @Override
        public MyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_mode_item,parent,false);
            MyItemHolder holder = new MyItemHolder(view);
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(MyItemHolder holder, int position) {
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
