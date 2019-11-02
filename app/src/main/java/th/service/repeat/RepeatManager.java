package th.service.repeat;

import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.TextCacheUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import th.service.helper.PacketExt;
import th.service.helper.ThLogger;
import th.service.helper.ThPackage;

/**
 * Created by YUYANG on 2018/11/6.
 * 重发管理机制
 *
 * 描述：
 *       1.将每个数据包在发送之前加入待重发队列，并记录时间
 *       2.定时检测数据包是否超时1s
 *       3.如果超时则重发并记录重发次数
 *       4.如果重发次数超过3次则丢弃或重连
 *       5.如果发送数据包有返回，则根据相同包号找到并在重发队列中移除
 *
 */

public class RepeatManager {
    private static RepeatManager instance=new RepeatManager();
    private static int packetAutoID=0;//0-255
    private AtomicLong sendPackCount;
    private AtomicLong acceptCount;
    public static RepeatManager getInstance(){
        return instance;
    }
    private RepeatManager(){
        resetCount();
        init();
    }
    private void resetCount(){
        sendPackCount=new AtomicLong(0);
        acceptCount=new AtomicLong(0);
    }
    private ConcurrentMap<Integer,ThPackage> packetMap=new ConcurrentHashMap<>();
    public  void init(){
        packetMap.clear();
    }

    public void outputLog(){
        if(sendPackCount!=null&&sendPackCount.get()>0){

            long sendCache=TextCacheUtils.getValueLong("Send",0);
            long acceptCache=TextCacheUtils.getValueLong("Accept",0);

            sendCache+=sendPackCount.get();
            acceptCache+=acceptCount.get();


            ThLogger.addPacketLog("数据包：已发送："+sendCache
                    +" , 已接收："+acceptCache
                    +" 丢失率："+100*(1-acceptCache*1.0f/sendCache)+"%");
            ThLogger.outputPacketLog();

            TextCacheUtils.loadLong("Send",sendCache);
            TextCacheUtils.loadLong("Accept",acceptCache);

        }
        resetCount();
    }



    public void add(ThPackage thPackage){
        /**
         * 只有加入到容器里面的包才会使用循环包号
         * 1-255
         */
        if(packetAutoID<=1){
            packetAutoID=255;
        }else if(packetAutoID>255){
            packetAutoID=1;
        }
        thPackage.setPacketNumber((byte) packetAutoID);
        packetAutoID++;

        packetMap.put(ConvertUtils.unsignByteToInt(thPackage.getPacketNumber()),thPackage);

        int pid= ConvertUtils.unsignByteToInt(thPackage.getPacketNumber());

        ThLogger.addLog("加入包号 "+pid +" [packet] type:"+ Integer.toHexString(ConvertUtils.unsignByteToInt(thPackage.getType()))
                +" extendType:"+Integer.toHexString(thPackage.getExtendType()));
        if(sendPackCount!=null){
            sendPackCount.incrementAndGet();
        }

    }

    private void removeInner(ThPackage thPackage,boolean innerRemoveYN){
        if(thPackage==null){
            return;
        }
        int pid=ConvertUtils.unsignByteToInt(thPackage.getPacketNumber());

        ThPackage deletePacket = packetMap.get(pid);

        if(deletePacket==null||
                deletePacket.getType()!=thPackage.getType()||
                deletePacket.getExtendType()!=thPackage.getExtendType()){
            return;
        }

        deletePacket=packetMap.remove(pid);

        if(deletePacket!=null&&!innerRemoveYN){
            if(acceptCount!=null){
                acceptCount.incrementAndGet();
            }
        }

        if(innerRemoveYN){
            ThLogger.addLog("已经完成三次重发，移除该包:"+pid);
        }else{
            ThLogger.addLog("移除包号 "+pid +" [packet] type:"+Integer.toHexString(ConvertUtils.unsignByteToInt(thPackage.getType()))
                    +" extendType:"+Integer.toHexString(thPackage.getExtendType())
                    +" 是否属于队列中："+(deletePacket!=null));
        }
    }

    public  void remove(ThPackage thPackage){
        removeInner(thPackage,false);
    }

    public  void  check(RepeatListenser listenser){
        if(listenser==null){
            return;
        }
        List<ThPackage> reSendPackets=new ArrayList<>();
        long currentTime=System.currentTimeMillis();
        for(ThPackage thPackage:packetMap.values()){
            PacketExt packetExt=thPackage.getPacketExt();
            if(packetExt!=null){
                if(packetExt.getRepeatCount()>=3){
                    /**
                     * 只会重发三次 从容器移除
                     */
                    removeInner(thPackage,true);
                    break;
                }else{
                    /**
                     * 超过2s没有数据返回则重发
                     */
                    if(currentTime-packetExt.getSendTime()>2000){
                        packetExt.increment();
                        /**
                         *  重新更新发送时间
                         */
                        packetExt.setSendTime(System.currentTimeMillis());
                        reSendPackets.add(thPackage);
                    }
                }
            }
        }
        if(reSendPackets.size()>0){
            listenser.onResult(true,reSendPackets);
        }else{
            listenser.onResult(false,null);
        }
    }



}
