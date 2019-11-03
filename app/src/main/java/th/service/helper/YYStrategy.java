package th.service.helper;

import com.yy.sorter.ui.base.BaseUi;
import com.yy.sorter.ui.base.ConstantValues;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YUYANG on 2018/11/6.
 * 消息发送策略
 *
 * 定义给每一个BaseUI 需要接收的消息类型
 * 悬浮框显示策略
 */

public class YYStrategy {
    private static  Map<Integer,List<Byte>> strategyTables=new HashMap<>();

    /**
     * 所有页面都会接收到此类消息
     */
    private static List<Byte> mustBeReceiveMessages=Arrays.asList((byte)0x55,(byte)0xb0);

    static {
        /**
         *****************   配置页面消息接收权限 ***************
         */
        strategyTables.put(ConstantValues.VIEW_LOGIN,Arrays.asList(YYCommand.LOGIN_CMD,
                YYCommand.BROADCAST_DEV_CMD,(byte)0x51,(byte)0x53,(byte)0xc1,(byte)0xc2));
        strategyTables.put(ConstantValues.VIEW_LOGIN_REMOTE,Arrays.asList(YYCommand.LOGIN_CMD,(byte)0x51));
        strategyTables.put(ConstantValues.VIEW_DEVICE_LIST,Arrays.asList(YYCommand.LOGIN_CMD, YYCommand.BROADCAST_DEV_CMD));
        strategyTables.put(ConstantValues.VIEW_HOME,Arrays.asList(YYCommand.LOGIN_CMD,
                YYCommand.CONTROL_CMD,
                YYCommand.MODE_CMD,
                YYCommand.WORKINFO_CMD));
        strategyTables.put(ConstantValues.VIEW_SENSE,Arrays.asList(YYCommand.SENSE_CMD,
                YYCommand.SVM_CMD, YYCommand.HSV_CMD, YYCommand.SHAPE_CMD, YYCommand.WAVE_CMD));
        strategyTables.put(ConstantValues.VIEW_MODE_LIST,Arrays.asList(YYCommand.MODE_CMD));
        strategyTables.put(ConstantValues.VIEW_FEEDER,Arrays.asList(YYCommand.FEEDER_CMD));
        strategyTables.put(ConstantValues.VIEW_LAN,Arrays.asList((byte)0xc1,(byte)0xc2));
        strategyTables.put(ConstantValues.VIEW_REGISTER,Arrays.asList((byte)0x56));
        strategyTables.put(ConstantValues.VIEW_CAMERAADJUST,Arrays.asList(YYCommand.WAVE_CMD));
        strategyTables.put(ConstantValues.VIEW_VERSION,Arrays.asList(YYCommand.VERSION_CMD));
        strategyTables.put(ConstantValues.VIEW_BACKGROUND,Arrays.asList(YYCommand.LIGHT_CMD, YYCommand.WAVE_CMD));
        strategyTables.put(ConstantValues.VIEW_VALVE_RATE,Arrays.asList(YYCommand.VALVE_RATE_CMD));
        strategyTables.put(ConstantValues.VIEW_ADD_TEXT,Arrays.asList(YYCommand.ADD_TEXT_CMD));





    }


    public static boolean isNeedSendMessage(BaseUi baseUi, byte type){

        List<Byte> permissions=strategyTables.get(baseUi.getID());

        if(permissions!=null){
            boolean contain = permissions.contains(type);
            return  contain;
        }

        return false;
    }

    public static boolean isMustBeReceiveMessage(byte type){
        if(mustBeReceiveMessages!=null){
            return mustBeReceiveMessages.contains(type);
        }
        return false;
    }



}
