package th.service.repeat;

import java.util.List;

import th.service.helper.YYPackage;

/**
 * Created by YUYANG on 2018/11/6.
 * 重发回调接口
 */

public interface RepeatListenser {
    public void onResult(boolean needRepeatYN,List<YYPackage> needResendPacket);
}
