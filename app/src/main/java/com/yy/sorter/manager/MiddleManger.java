package com.yy.sorter.manager;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.yy.sorter.ui.base.BaseUI;
import com.yy.sorter.ui.base.ConstantValues;
import com.yy.sorter.utils.FadeUtils;
import com.yy.sorter.version.PageVersionManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import th.service.core.ThUIManger;

/**
 * Created by Administrator on 2017/3/17.
 * 中间容器管理类
 */

public class MiddleManger extends ThManagerSubject {
    public static final String KEY_TITLE = "key_title";
    private static MiddleManger instance = null;
    private RelativeLayout middleContainer;
    private Context ctx;
    private int animationTime = 1;

    /**
     * 当前中间层的UI
     */
    private BaseUI currentUI;

    public BaseUI getCurrentUI() {
        return currentUI;
    }

    /**
     * 历史记录
     */
    private LinkedList<Integer> HISTORYS = new LinkedList();
    /**
     * 页面缓存
     */
    private Map<Integer, BaseUI> VIEW_CACHES = new HashMap<>();

    private MiddleManger() {

    }

    public static MiddleManger getInstance() {
        synchronized (MiddleManger.class) {
            if (instance == null) {
                instance = new MiddleManger();
            }
        }
        return instance;
    }


    public void release() {
        clearCaches();
        reset();
    }

    /**
     * 退出activity时清楚页面缓存
     */
    private void clearCaches() {
        VIEW_CACHES.clear();
        HISTORYS.clear();
        currentUI = null;
        if (middleContainer != null) {
            middleContainer.removeAllViews();
        }
        deleteObservers();
        System.out.println("退出activity时清楚页面缓存");

    }

    private void reset() {
        synchronized (MiddleManger.class) {
            if (instance != null) {
                HISTORYS.clear();
                VIEW_CACHES.clear();
                instance = null;
            }
        }
        System.out.println("reset 页面实例");
    }


    public boolean isCurrentUI(BaseUI baseUI) {
        boolean isEqual = false;
        if (currentUI == null || baseUI == null) {
            return isEqual;
        }

        isEqual = (currentUI.getID() == baseUI.getID());

        return isEqual;
    }

    /**
     * 设置中间层容器
     *
     * @param middle
     */
    public void setMiddle(RelativeLayout middle) {
        this.middleContainer = middle;
    }

    public void init(Activity activity) {
        ctx = activity;
    }


    /**
     * 界面切换
     * <p>
     * 页面架构调整：：
     * onViewCreate
     * <p>
     * setContentView(View view);
     * <p>
     * onViewStart
     * onViewStop
     * <p>
     * onActivityStart
     * onActivityStop
     * <p>
     * onViewDestory
     */

    public synchronized void changeUI(int pageId, String title) {

        if (currentUI != null && currentUI.getID() == pageId) {
            return;
        }
        BaseUI targetUI = null;

        if (VIEW_CACHES.containsKey(pageId)) {
            cacheCheck(pageId);
            targetUI = VIEW_CACHES.get(pageId);
            targetUI.setTitle(title);
        } else {
            Map<Integer, Class> pagesMap = PageVersionManager.getInstance().getPagesMap();
            Class<? extends BaseUI> targetClazz = pagesMap.get(pageId);

            if (targetClazz == null) {
                Toast.makeText(ctx,"不支持的界面跳转",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                targetUI = targetClazz.getConstructor(Context.class).newInstance(ctx);
                targetUI.setTitle(title);
                VIEW_CACHES.put(pageId, targetUI);

                /**
                 * 所有被新创建的页面都加入UIManager中
                 */
                ThUIManger.getInstance().addObserver(targetUI);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        middleContainer.removeAllViews();

        View targetChild = targetUI.getChild();

        /**
         *  如果属于同意级别就替换
         *  避免返回时出问题
         */
        if (currentUI != null
                && currentUI.getLeaver() == targetUI.getLeaver()
                && targetUI.getLeaver() != ConstantValues.LEAVER_DEFAULT) {

            removeHistoryAndCachesPage(true);
        }
        HISTORYS.addFirst(pageId);
        long fadeInTime = animationTime;
        if (currentUI != null) {
            currentUI.onViewStop();
        }
        middleContainer.addView(targetChild);


        currentUI = targetUI;
        updateChanges(title);

        currentUI.onViewStart();
        FadeUtils.fadeIn(targetChild, 0, fadeInTime);
    }


    private void updateChanges(String message) {
        setChanged();
        notifyObservers(currentUI.getID(), message);
    }

    public Context getContext() {
        return middleContainer.getContext();
    }

    public void goBack(int pageId) {


        if (currentUI == null || currentUI.getID() == pageId) {
            return;
        }

        Integer key = HISTORYS.getFirst();

        while ((pageId != key) && HISTORYS.size() > 1) {
            removeHistoryAndCachesPage(false);
            key = HISTORYS.getFirst();
        }

        popToView(key);

    }

    /**
     *  暂不支持返回时替换最新版本界面（缺点）
     *  解决办法：缓存中获取该界面类型和页面管理器中的页面类型比较，
     *  如果不同则创建新的类型界面替换到缓存中。
     */

    private void cacheCheck(int pageId)
    {
        if(VIEW_CACHES.containsKey(pageId)) {
            Map<Integer, Class> pagesMap = PageVersionManager.getInstance().getPagesMap();
            Class<? extends BaseUI> targetClazz = pagesMap.get(pageId);
            if (targetClazz != null) {
                if (VIEW_CACHES.get(pageId).getClass() != targetClazz) {
                    BaseUI targetUI;
                    try {
                        targetUI = targetClazz.getConstructor(Context.class).newInstance(ctx);
                    } catch (Exception e) {
                        targetUI = null;
                    }
                    if (targetUI != null) {
                        BaseUI baseUI = VIEW_CACHES.get(pageId);

                        if (baseUI != null) {
                            targetUI.setTitle(baseUI.getTitle());
                            ThUIManger.getInstance().deleteObserver(baseUI);
                        }

                        VIEW_CACHES.put(pageId, targetUI);
                        ThUIManger.getInstance().addObserver(targetUI);
                    }
                }
            }
        }
    }

    private void popToView(Integer key) {

        cacheCheck(key);

        BaseUI targetUI = VIEW_CACHES.get(key);

        if(targetUI == null)
        {
            return;
        }

        if (currentUI != null) {
            currentUI.onViewStop();
        }
        middleContainer.removeAllViews();
        View view = targetUI.getChild();
        middleContainer.addView(view);
        currentUI = targetUI;
        updateChanges(currentUI.getTitle());

        targetUI.onViewStart();

    }

    public boolean goBack() {

        if (HISTORYS.size() > 0) {
            if (HISTORYS.size() == 1) {
                return false;
            }
            removeHistoryAndCachesPage(false);
            if (HISTORYS.size() > 0) {

                Integer key = HISTORYS.getFirst();
                popToView(key);
                return true;
            }

        }
        return false;
    }

    private void removeHistoryAndCachesPage(boolean onlyRemoveHistory) {
        if (HISTORYS != null) {
            Integer key = HISTORYS.removeFirst();

            if (!onlyRemoveHistory) {
                BaseUI baseUI = VIEW_CACHES.remove(key);

                if (baseUI != null) {
                    ThUIManger.getInstance().deleteObserver(baseUI);

                    if (baseUI.getLeaver() != ConstantValues.LEAVER_DEFAULT) {
                        for (Iterator<Map.Entry<Integer, BaseUI>> it = VIEW_CACHES.entrySet().iterator(); it.hasNext(); ) {
                            Map.Entry<Integer, BaseUI> entry = it.next();
                            if (baseUI.getLeaver() == entry.getValue().getLeaver()) {

                                ThUIManger.getInstance().deleteObserver(entry.getValue());
                                it.remove();

                            }
                        }
                    }
                }


            }


        }
    }
}
