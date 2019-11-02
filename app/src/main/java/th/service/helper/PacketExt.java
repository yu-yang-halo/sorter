package th.service.helper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by YUYANG on 2018/11/6.
 * 包扩展：用于处理重发机制
 */

public class PacketExt {
    private long sendTime;
    private final AtomicInteger repeatCount=new AtomicInteger(0);

    public PacketExt(long sendTime){
        this.sendTime = sendTime;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public void increment(){
       repeatCount.incrementAndGet();
    }
    public int getRepeatCount(){
        return repeatCount.get();
    }


}
