package com.yy.sorter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.yy.sorter.activity.R;
import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.ui.page.PageBaseUi;
import com.yy.sorter.utils.StringUtils;
import com.yy.sorter.version.PageVersionManager;
import com.yy.sorter.view.ThAutoLayout;
import com.yy.sorter.view.ThSegmentView;

import java.util.ArrayList;
import java.util.List;

import th.service.core.AbstractDataServiceFactory;
import th.service.helper.ThPackage;

public class SenseUi extends BaseUi {
    private ThSegmentView segmentView;
    private ThAutoLayout groupLayout;
    private RelativeLayout container;
    private int tabIndex = 0;

    private PageBaseUi rgbIrPage,svmPage,hsvPage;


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

            segmentView.setOnSelectedListenser(new ThSegmentView.OnSelectedListenser() {
                @Override
                public void onSelected(int pos, ThSegmentView.TSegmentItem tSegmentItem) {
                    tabIndex = pos;
                    loadChildPage();
                }
            });
        }
        return view;
    }

    private void loadChildPage()
    {
        if(tabIndex == 0)
        {
            loadPageToContainer(rgbIrPage,ConstantValues.VIEW_PAGE_RGB_IR);
        }else if(tabIndex == 1)
        {
            loadPageToContainer(svmPage,ConstantValues.VIEW_PAGE_SVM);
        }else if(tabIndex == 2)
        {
            loadPageToContainer(hsvPage,ConstantValues.VIEW_PAGE_HSV);
        }
    }
    private void loadPageToContainer(PageBaseUi page,int pageId)
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
        switch (tabIndex)
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
        }
    }

    @Override
    public void onViewStart() {
        super.onViewStart();

        List<ThSegmentView.TSegmentItem> items=new ArrayList<>();

        ThSegmentView.TSegmentItem item0 = new ThSegmentView.TSegmentItem("色选",0);
        ThSegmentView.TSegmentItem item1 = new ThSegmentView.TSegmentItem("智能分选",1);
        ThSegmentView.TSegmentItem item2 = new ThSegmentView.TSegmentItem("色度分选",2);
        ThSegmentView.TSegmentItem item3 = new ThSegmentView.TSegmentItem("形选",3);

        items.add(item0);
        items.add(item1);
        items.add(item2);
        items.add(item3);

        segmentView.setContents(items);

        List<ThAutoLayout.Item> itemList = StringUtils.getGroupItem();
        int currentGroup = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentGroup();

        groupLayout.setContents(itemList,currentGroup,0);

        loadChildPage();

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

    }
}
