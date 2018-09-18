package th.service.core;


import com.yy.sorter.manager.MiddleManger;
import com.yy.sorter.ui.base.BaseUi;

import java.util.Vector;

import th.service.helper.ThLogger;
import th.service.helper.ThStrategy;
import th.service.helper.ThPackage;

/**
 * 管理所有的UI界面
 * 并负责通知消息给每个页面
 *
 * this is Subject
 *
 */

public class ThUIManger {
    /**
     * ***********************************************************************************
     *   B_SEND_TO_CURRENT_PAGE
     *   true 只发送给当前页面         优点：更快速，不易出错            缺点：方案切换或者修改时界面不对应
     *
     *   false 发送给所有需要的页面    优点：页面协议分发处理，各司其责  缺点：会分发给每个感兴趣的页面，易出错，需要谨慎处理
     *
     *   暂时默认使用false
     * ***********************************************************************************
     */
    private static boolean B_SEND_TO_CURRENT_PAGE=false;

    private static ThUIManger instance=new ThUIManger();
    public static ThUIManger getInstance(){
        return instance;
    }


    /**
     * 实现被观察者方法
     */

    private Vector<ThObserver> observers=new Vector<>();
    private boolean changed=false;
    public synchronized void addObserver(ThObserver var1) {
        if(var1 == null) {
            throw new NullPointerException();
        } else {
            if(!this.observers.contains(var1)) {
                this.observers.addElement(var1);
            }

        }
    }

    public synchronized void clearObservers(){
        if(observers!=null){
            observers.clear();
        }
    }

    public synchronized void deleteObserver(ThObserver var1) {
        this.observers.removeElement(var1);
    }

    public void notifyObservers() {
        this.notifyObservers(null);
    }

    public void notifyObservers(Object var1) {
        Object[] var2;
        synchronized(this) {
            if(!this.changed) {
                return;
            }
            var2 = this.observers.toArray();
            this.clearChanged();
        }

        for(int var3 = var2.length - 1; var3 >= 0; --var3) {
            ThObserver observer= (ThObserver) var2[var3];

            if(observer instanceof BaseUi){
                BaseUi baseUi = (BaseUi) observer;

                /**
                 * 定制发送，发送给需要的界面
                 * 定义一个策略：：：
                 * LOGINUI ---  只接收 0x02
                 * DEVICELISTUI----只接收 0x01
                 * HOMEUI ----只接收 0x03
                 *
                 */
                if(B_SEND_TO_CURRENT_PAGE){
                    if(MiddleManger.getInstance().isCurrentUI(baseUi)){
                        baseUi.update(this, var1);
                        break;
                    }
                }else{
                    if(var1 instanceof ThPackage){
                        ThPackage packet= (ThPackage) var1;
                        if(ThStrategy.isMustBeReceiveMessage(packet.getType())){
                            baseUi.update(this, var1);
                        }else{
                            if(ThStrategy.isNeedSendMessage(baseUi,packet.getType())){
                                baseUi.update(this, var1);
                            }
                        }

                    }else{
                        if(MiddleManger.getInstance().isCurrentUI(baseUi)){
                            baseUi.update(this, var1);
                        }
                    }
                }


            }else{
                ThLogger.debug("提示","不继承之BaseUI 无法通知消息");
            }

        }

    }

    public synchronized void deleteObservers() {
        this.observers.removeAllElements();
    }

    protected synchronized void setChanged() {
        this.changed = true;
    }

    protected synchronized void clearChanged() {
        this.changed = false;
    }

    public synchronized boolean hasChanged() {
        return this.changed;
    }

    public synchronized int countObservers() {
        return this.observers.size();
    }
}
