package th.service.helper;

import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/18.
 * 消息发送策略
 *
 * 定义给每一个BaseUI 需要接收的消息类型
 * 悬浮框显示策略
 */

public class ThStrategy {
    private static  Map<Integer,List<Integer>> strategyTables=new HashMap<>();

    /**
     * 所有页面都会接收到此类消息
     */
    private static List<Integer> mustBeReceiveMessages=Arrays.asList(0x55,0xb0);

    static {
        /**
         * 配置页面消息接收权限
         */
        strategyTables.put(ConstantValues.VIEW_LOGIN,Arrays.asList(0x01,0x02,0x51,0x53,0xc1,0xc2));
        strategyTables.put(ConstantValues.VIEW_LOGIN_REMOTE,Arrays.asList(0x01,0x02,0x51,0x53,0xc1,0xc2));
        strategyTables.put(ConstantValues.VIEW_DEVICE_LIST,Arrays.asList(0x01,0x02));
        strategyTables.put(ConstantValues.VIEW_HOME,Arrays.asList(0x01,0x03,0x08,0x0d,0x0f));
        strategyTables.put(ConstantValues.VIEW_SENSE,Arrays.asList(0x07));
        strategyTables.put(ConstantValues.VIEW_MODE_LIST,Arrays.asList(0x04));
        strategyTables.put(ConstantValues.VIEW_FEEDER,Arrays.asList(0x05));








    }


    public static boolean isNeedSendMessage(BaseUi baseUi, int type){

        List<Integer> permissions=strategyTables.get(baseUi.getID());

        if(permissions!=null){
            boolean contain = permissions.contains(type);
            return  contain;
        }

        return false;
    }

    public static boolean isMustBeReceiveMessage(int type){
        if(mustBeReceiveMessages!=null){
            return mustBeReceiveMessages.contains(type);
        }
        return false;
    }



}
