package th.service.repeat;

import java.util.List;

import th.service.helper.ThPackage;

/**
 * Created by Administrator on 2017/7/12.
 * 重发回调接口
 */

public interface RepeatListenser {
    public void onResult(boolean needRepeatYN,List<ThPackage> needResendPacket);
}
