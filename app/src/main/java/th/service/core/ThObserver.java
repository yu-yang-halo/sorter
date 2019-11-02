package th.service.core;


/**
 * Created by YUYANG on 2018/11/6.
 * 观察者接口 每一个BaseUI基类都会实现该接口
 */

public interface ThObserver {
    void update(Object var1, Object var2);
}
