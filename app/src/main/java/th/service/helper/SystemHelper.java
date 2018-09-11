package th.service.helper;

import th.service.core.AbstractDataServiceFactory;

public class SystemHelper {

    public static final int IR_NONE = 0,IR_SINGLE_FRONT = 1,IR_SINGLE_REAR = 2,
            IR_SINGLE_BOTH=3,IR_DOUBLT_FRONT=4,IR_DOUBLE_REAR=5,IR_DOUBLE_BOTH=6,
            IR_TIME_FRONT=7,IR_TIME_REAR=8,IR_TIME_BOTH=9,IR_NEAR_FRONT = 10,IR_NEAR_BOTH = 11;

    public static int getCurrenUseIr()
    {
        int layerNumber = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getLayerNumber();
        int useIr = 0;
        if(layerNumber == 1)
        {
            useIr = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getUseIR();
        }else
        {

            int currentLayer = AbstractDataServiceFactory.getInstance().getCurrentDevice().getCurrentLayer();

            if(currentLayer >=1 && currentLayer <=6)
            {
                useIr = AbstractDataServiceFactory.getInstance().getCurrentDevice().getMachineData().getHasUseIR()[currentLayer - 1];
            }

        }

        return useIr;

    }

    public static boolean isHasIr()
    {
        int useIr = getCurrenUseIr();
        if(useIr == IR_NONE)
        {
            return  false;
        }else
        {
            return  true;
        }

    }

    public static boolean irIsFrontRear()
    {
        int useIr = getCurrenUseIr();
        if(useIr == IR_SINGLE_BOTH || useIr == IR_DOUBLE_BOTH || useIr == IR_TIME_BOTH || useIr == IR_NEAR_BOTH)
        {
            return  true;
        }else
        {
            return  false;
        }

    }

    public static boolean irIsOnlyFront()
    {
        int useIr = getCurrenUseIr();
        if(useIr == IR_SINGLE_FRONT || useIr == IR_DOUBLT_FRONT || useIr == IR_TIME_FRONT || useIr == IR_NEAR_FRONT)
        {
            return  true;
        }else
        {
            return  false;
        }

    }

    public static boolean irIsOnlyRear()
    {
        int useIr = getCurrenUseIr();
        if(useIr == IR_SINGLE_REAR || useIr == IR_DOUBLE_REAR || useIr == IR_TIME_REAR)
        {
            return  true;
        }else
        {
            return  false;
        }
    }



    public static boolean isNearIr()
    {
        int useIr = getCurrenUseIr();

        if(useIr == IR_NEAR_FRONT || useIr == IR_NEAR_BOTH)
        {
            return true;
        }

        return false;
    }

    public static boolean isDoubleIr()
    {
        int useIr = getCurrenUseIr();

        if(useIr >= IR_DOUBLT_FRONT && useIr <= IR_TIME_BOTH)
        {
            return true;
        }

        return false;
    }

    public static boolean isShareTimeIr()
    {
        int useIr = getCurrenUseIr();

        if(useIr >= IR_TIME_FRONT && useIr <= IR_TIME_BOTH)
        {
            return true;
        }

        return false;
    }
}
