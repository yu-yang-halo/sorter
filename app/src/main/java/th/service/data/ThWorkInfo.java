package th.service.data;

import com.yy.sorter.manager.FileManager;
import com.yy.sorter.utils.ConvertUtils;

import th.service.core.TrafficManager;

public class ThWorkInfo {
    private static final int SIZE = 16;
    private byte[] totalTime;   //总工作时间    （分钟）
    private byte[] todayTime;   //本次工作时间 （分钟）
    private byte[] resever;     //预留

    public ThWorkInfo(byte[] contents)
    {
        totalTime = new byte[4];
        todayTime = new byte[4];
        resever = new byte[8];

        if(check(contents))
        {
            System.arraycopy(contents,0,totalTime,0,totalTime.length);
            System.arraycopy(contents,4,todayTime,0,todayTime.length);
            System.arraycopy(contents,8,resever,0,resever.length);
        }
    }

    public boolean check(byte[] contents){
        if(contents==null||contents.length!=SIZE){
            TrafficManager.getInstance().showErrorMessage();
            return false;
        }
        return true;
    }

    public String getTotalTime()
    {
        int timeMin = ConvertUtils.bytes4ToInt(totalTime);

        return formatTime(timeMin);
    }
    public String getTodayTime()
    {
        int timeMin = ConvertUtils.bytes4ToInt(todayTime);

        return formatTime(timeMin);
    }

    private String formatTime(int min)
    {
        //136#小时
        //137#分钟
        String strMin = FileManager.getInstance().getString(137,"分钟");
        String strHour = FileManager.getInstance().getString(136,"小时");
        if(min<60)
        {
            return min + strMin;
        }else{
            int timeH = min/60;
            int timeM = min%60;

            return timeH+strHour+" "+timeM+strMin;
        }
    }


}
