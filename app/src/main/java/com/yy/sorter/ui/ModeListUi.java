package com.yy.sorter.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.TextCacheUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.data.ThMode;
import th.service.helper.ThCommand;
import th.service.helper.ThPackage;
import th.service.helper.ThPackageHelper;
interface ICallBack
{
    public void onDataBeginSend();

}

public class ModeListUi extends BaseUi implements ICallBack{
    private KProgressHUD hud;
    private MachineData machineData;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<ThMode> thModeList;
    private PullRefreshLayout swipeRefreshLayout;
    public ModeListUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view= LayoutInflater.from(ctx).inflate(R.layout.ui_mode_list,null);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            swipeRefreshLayout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx));

            myAdapter = new MyAdapter();
            myAdapter.setICallback(new WeakReference<ICallBack>(this));
            recyclerView.setAdapter(myAdapter);

            swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    reqModeList();
                }
            });
        }
        return view;
    }

    @Override
    public void onViewStart() {
        super.onViewStart();
        reqModeList();
    }

    private void reqModeList()
    {
        machineData = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();

        AbstractDataServiceFactory.getInstance().requestModeList(machineData.getSortModeBig());
    }

    @Override
    public void receivePacketData(ThPackage packet) {
        if(packet.getType() == ThCommand.MODE_CMD)
        {
            if(packet.getExtendType() == 0x01)
            {
                thModeList = ThPackageHelper.parseThModeList(packet);

                myAdapter.setThModeList(thModeList);
                myAdapter.notifyDataSetChanged();

                swipeRefreshLayout.setRefreshing(false);
            }else if(packet.getExtendType() == 0x02)
            {
                byte currentSmallIndex = packet.getData1()[0];
                byte bigIndex = packet.getData1()[1];
                if(packet.getData1()[2] == 0x01)
                {
                    //138#读取方案成功
                    showToast(FileManager.getInstance().getString(138,"读取方案成功"));
                    if(thModeList != null)
                    {
                        for(ThMode mode : thModeList)
                        {
                            if(mode.getBigIndex() == bigIndex && mode.getSmallIndex() == currentSmallIndex)
                            {
                                mode.setCurrentMode(true);
                            }else
                            {
                                mode.setCurrentMode(false);
                            }
                        }

                    }
                    refreshDeviceInfo();

                }else
                {
                    //139#读取方案失败
                    showToast(FileManager.getInstance().getString(139,"读取方案失败"));
                }
                if(hud != null)
                {
                    hud.dismiss();
                }

                myAdapter.notifyDataSetChanged();

            }
        }
    }
    private void refreshDeviceInfo(){
        int lanCountryId= TextCacheUtils.getValueInt(TextCacheUtils.KEY_LAN_COUNTRY_ID,ConstantValues.LAN_COUNTRY_EN);

        AbstractDataServiceFactory.getInstance().login(null,(byte)lanCountryId);
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_MODE_LIST;
    }

    @Override
    public void onDataBeginSend() {
        if(hud != null)
        {
            hud.dismiss();
        }
        //140#方案读取中...
        hud = KProgressHUD.create(ctx)
                .setLabel(FileManager.getInstance().getString(140,"方案读取中...")).show();
    }

    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyItemHolder>
    {
        List<ThMode> thModeList;
        private WeakReference<ICallBack> callBack;
        public MyAdapter()
        {

        }
        public void setICallback(WeakReference<ICallBack> callBack)
        {
            this.callBack = callBack;
        }

        public void setThModeList(List<ThMode> thModeList) {
            this.thModeList = thModeList;
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
        public void onBindViewHolder(MyItemHolder holder, final int position) {
            final ThMode mode = thModeList.get(position);
            holder.tv_modeName.setText(mode.getModeName());
            if(mode.isCurrentMode())
            {
                holder.ckMode.setChecked(true);
                holder.tv_modeName.setTextColor(Color.parseColor("#ff0000"));
            }else
            {
                holder.ckMode.setChecked(false);
                holder.tv_modeName.setTextColor(Color.parseColor("#8c8c8c"));
            }

            holder.ckMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v instanceof CheckBox)
                    {
                        CheckBox ckBox = (CheckBox) v;
                        ckBox.setChecked(false);
                    }
                    AbstractDataServiceFactory.getInstance().readMode(mode.getBigIndex(),mode.getSmallIndex());

                    callBack.get().onDataBeginSend();
                }
            });
        }

        @Override
        public int getItemCount() {
            if(thModeList == null)
            {
                return 0;
            }else
            {
                return thModeList.size();
            }
        }

        class MyItemHolder extends RecyclerView.ViewHolder
        {

            TextView tv_modeName;
            CheckBox ckMode;
            public MyItemHolder(View itemView) {
                super(itemView);
                tv_modeName = (TextView) itemView.findViewById(R.id.tv_modeName);
                ckMode = (CheckBox) itemView.findViewById(R.id.ckMode);
            }
        }
    }



}
