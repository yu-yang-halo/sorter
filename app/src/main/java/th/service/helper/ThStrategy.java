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
        strategyTables.put(ConstantValues.VIEW_SENSE,Arrays.asList(0x04,0x10,0x12,0x13,0x14,
                0x15,0x16,0x17,0x18,0x19,0x1A,0x1B,0x1C,0x1D,0x1E,0x1F,0x20,0x21,0x30,0x05));
        strategyTables.put(ConstantValues.VIEW_MODE_LIST,Arrays.asList(0x0d));




        /***
         * ****************************************************************************
         *    灵敏度高级界面  拆分为灰度，色差，亮度，红外灰度，红外比值 五个界面
         * ****************************************************************************
         */




        strategyTables.put(ConstantValues.VIEW_VALVE_SETTINGS,Arrays.asList(0x09,0x03));
        strategyTables.put(ConstantValues.VIEW_DEASH_SETTINGS,Arrays.asList(0x07));
        strategyTables.put(ConstantValues.VIEW_VERSION,Arrays.asList(0x0a));
        strategyTables.put(ConstantValues.VIEW_SIGNAL_SETTINGS,Arrays.asList(0x0b,0x0c,0x05));
        strategyTables.put(ConstantValues.VIEW_SCHEME_LIST,Arrays.asList(0x0d));
        strategyTables.put(ConstantValues.VIEW_SCHEME_DETAIL,Arrays.asList(0x0d));
        strategyTables.put(ConstantValues.VIEW_VALVE_RATE,Arrays.asList(0x0e));
        strategyTables.put(ConstantValues.VIEW_WHEEL_SETTINGS,Arrays.asList(0x0f));
        strategyTables.put(ConstantValues.VIEW_OPTICS_ADJUST,Arrays.asList(0x11,0x05));
        strategyTables.put(ConstantValues.VIEW_CAMERA_ADJUST,Arrays.asList(0x11,0x05));//相机校准
        strategyTables.put(ConstantValues.VIEW_LAN,Arrays.asList(0xc1,0xc2));
        strategyTables.put(ConstantValues.VIEW_REGISTER,Arrays.asList(0x56));
        strategyTables.put(ConstantValues.VIEW_WORK_INFO,Arrays.asList(0xa0));
        strategyTables.put(ConstantValues.VIEW_SELF_CHECK,Arrays.asList(0xa0));
        strategyTables.put(ConstantValues.VIEW_FEEDER,Arrays.asList(0x08,0x03));






    }


    public static boolean isNeedSendMessage(BaseUi baseUi, int type){

        List<Integer> permissions=strategyTables.get(baseUi.getID());

        if(permissions!=null){
            return permissions.contains(type);
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
