package com.yy.sorter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.yy.sorter.activity.R;
import com.yy.sorter.manager.FileManager;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.ui.page.PageBaseUi;
import com.yy.sorter.utils.StringUtils;
import com.yy.sorter.version.PageVersionManager;
import com.yy.sorter.view.PageSwitchView;
import com.yy.sorter.view.ThAutoLayout;
import com.yy.sorter.view.ThGroupView;
import com.yy.sorter.view.ThSegmentView;

import java.util.ArrayList;
import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.data.MachineData;
import th.service.helper.ThPackage;

public class SenseUi extends BaseUi {
    private ThSegmentView segmentView;
    private ThAutoLayout groupLayout;
    private RelativeLayout container;
    private PageSwitchView pageSwitchView;

    private PageBaseUi rgbIrPage,svmPage,hsvPage,shapePage;
    private PageBaseUi currentPage;


    public SenseUi(Context ctx) {
        super(ctx);
    }

    @Override
    protected View onInitView() {
        if(view == null)
        {
            view = LayoutInflater.from(ctx).inflate(R.layout.ui_sense,null);
            segmentView = (ThSegmentView) view.findViewById(R.id.segmentView);
            groupLayout = (ThAutoLayout) view.findViewById(R.id.groupLayout);
            container = (RelativeLayout) view.findViewById(R.id.container);
            pageSwitchView = (PageSwitchView) view.findViewById(R.id.pageSwitchView);

            segmentView.setOnSelectedListenser(new ThSegmentView.OnSelectedListenser() {
                @Override
                public void onSelected(int pos, ThSegmentView.TSegmentItem tSegmentItem) {
                    loadChildPage();
                }
            });

            groupLayout.setOnSelectedListenser(new ThGroupView.OnSelectedListenser() {
                @Override
                public void onSelected(int pos) {
                    AbstractDataServiceFactory.getInstance().getCurrentDevice().setCurrentGroup((byte) pos);
                    if(currentPage != null)
                    {
                       currentPage.onGroupChanged();
                    }
                    pageSwitchView.setmCurrentIndex(pos);
                }
            });

            pageSwitchView.setPageSwitchListenser(new PageSwitchView.PageSwitchListenser() {
                @Override
                public void onPageSwitch(int pageIndex, int flag) {
                    AbstractDataServiceFactory.getInstance().getCurrentDevice().setCurrentGroup((byte) pageIndex);
                    if(currentPage != null)
                    {
                        currentPage.onGroupChanged();
                    }
                    groupLayout.setSelectPos(pageIndex);
                }
            });

        }
        return view;
    }

    private void loadChildPage()
    {
        if(segmentView.getSelectItem() != null)
        {
            int tag = segmentView.getSelectItem().getItemTag();
            if(tag == 0)
            {
                loadPageToContainer(rgbIrPage,ConstantValues.VIEW_PAGE_RGB_IR,tag);
            }else if(tag == 1)
            {
                loadPageToContainer(svmPage,ConstantValues.VIEW_PAGE_SVM,tag);
            }else if(tag == 2)
            {
                loadPageToContainer(hsvPage,ConstantValues.VIEW_PAGE_HSV,tag);
            }else if(tag == 3)
            {
                loadPageToContainer(shapePage,ConstantValues.VIEW_PAGE_SHAPE,tag);
            }
        }


    }
    private void loadPageToContainer(PageBaseUi page,int pageId,int tag)
    {
        if(page == null)
        {
            page = PageVersionManager.getInstance().createPage(pageId,ctx);
            System.out.println("create page "+pageId+" "+page.getClass());
        }else
        {
            Class targetClzz = PageVersionManager.getInstance().getPageClass(pageId);
            if(page.getClass() != targetClzz)
            {
                System.out.println("change page "+pageId+" "+page.getClass()+" to "+targetClzz);
                page = PageVersionManager.getInstance().createPage(pageId,ctx);
            }else
            {
                System.out.println("page "+pageId+" "+page.getClass()+" cache bingo");
            }
        }
        page.loadToContainer(container);
        switch (tag)
        {
            case 0:
                rgbIrPage = page;
                break;
            case 1:
                svmPage = page;
                break;
            case 2:
                hsvPage = page;
                break;
            case 3:
                shapePage = page;
                break;
        }
        if(currentPage != null)
        {
            currentPage.onViewStop();
        }
        currentPage = page;
        currentPage.onViewStart();

    }

    @Override
    public void onViewStart() {
        super.onViewStart();

        MachineData machineData = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData();

        List<ThSegmentView.TSegmentItem> items=new ArrayList<>();

        if(machineData.getUserColor() == 0x01)
        {
            ThSegmentView.TSegmentItem item0 = new ThSegmentView.TSegmentItem(FileManager.getInstance().getString(94),0);// 94#色选
            items.add(item0);
        }
        if(machineData.getUseSvm() == 0x01)
        {
            ThSegmentView.TSegmentItem item1 = new ThSegmentView.TSegmentItem(FileManager.getInstance().getString(95),1);// 95#智能分选
            items.add(item1);
        }
        if(machineData.getUseHsv() == 0x01)
        {
            ThSegmentView.TSegmentItem item2 = new ThSegmentView.TSegmentItem(FileManager.getInstance().getString(96),2);// 96#色度分选
            items.add(item2);
        }
        if(machineData.getUseShape() == 0x01)
        {
            ThSegmentView.TSegmentItem item3 = new ThSegmentView.TSegmentItem(FileManager.getInstance().getString(97),3);//   97#形选
            items.add(item3);
        }
//        if(machineData.getUseIR() == 0x01)
//        {
//            ThSegmentView.TSegmentItem item4 = new ThSegmentView.TSegmentItem(FileManager.getInstance().getString(98),4);//98#红外
//            items.add(item4);
//        }

        segmentView.setContents(items);

        List<ThAutoLayout.Item> itemList = StringUtils.getGroupItem();
        currentGroup = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();

        groupLayout.setContents(itemList,currentGroup,0);

        pageSwitchView.setmNumbers(machineData.getGroupNumbers());
        pageSwitchView.setmCurrentIndex(currentGroup);

        loadChildPage();

        if(items==null || items.size()<=0)
        {
            segmentView.setVisibility(View.GONE);
            groupLayout.setVisibility(View.GONE);
            pageSwitchView.setVisibility(View.GONE);
        }else
        {
            segmentView.setVisibility(View.VISIBLE);
            groupLayout.setVisibility(View.VISIBLE);
            pageSwitchView.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public void onViewStop() {
        super.onViewStop();
        if(currentPage != null)
        {
            currentPage.onViewStop();
        }
    }

    @Override
    public int getID() {
        return ConstantValues.VIEW_SENSE;
    }
    @Override
    public int getLeaver() {
        return ConstantValues.LEAVER_TAB;
    }

    @Override
    public void receivePacketData(ThPackage packet) {
        if(currentPage != null)
        {
            currentPage.receivePacketData(packet);
        }
    }
}
