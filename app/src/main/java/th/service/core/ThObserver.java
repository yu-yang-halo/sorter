package th.service.core;

import java.util.Observable;

/**
 * Created by Administrator on 2017/3/18.
 * 观察者接口 每一个BaseUI基类都会实现该接口
 */

public interface ThObserver {
    void update(Object var1, Object var2);
}
