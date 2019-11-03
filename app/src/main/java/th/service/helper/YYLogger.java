package th.service.helper;

import android.content.Context;

import com.yy.sorter.manager.FileManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import th.service.core.AbstractDataServiceFactory;

/**
 * Created by YUYANG on 2018/11/6.
 */

public class YYLogger {
    private static boolean isDebug= YYCommand.DEBUG;//YYCommand.DEBUG
    private static final String UDP_LOG_FILENAME="udpLog";
    private static final String TCP_LOG_FILENAME="tcpLog";
    private static final String ERROR_LOG_FILENAME="errorLog";
    private static final String PACKET_LOG_FILENAME="packetLog";
    public static void debug(String tag,String message){
        if(isDebug){
            StringBuilder sb=new StringBuilder();
            sb.append("[");
            sb.append(tag);
            sb.append("]  ");
            sb.append(message);
            System.err.println(sb);
        }
    }
    public static Context mctx;

    public static void setContext(Context ctx){
        mctx=ctx;
    }
    private static StringBuilder udpLoggerBuffer=new StringBuilder();
    private static StringBuilder tcpLoggerBuffer=new StringBuilder();
    private static StringBuilder errorLoggerBuffer=new StringBuilder();
    private static StringBuilder packetLoggerBuffer=new StringBuilder();

    public static String getIpAddress(){
        if(mctx!=null){
           return IPUtils.localIpAddress(mctx);
        }
        return "";
    }

    public static void addErrorLog(String message){
        String timeStr = currentTime();
        errorLoggerBuffer.append("-------[时间： " + timeStr + " ip:" + getIpAddress() + " ]----------\n");
        errorLoggerBuffer.append(message + "\n");
    }
    public static  void outputErrorLog(){
        FileManager.getInstance().writeErrorLogToLocal(errorLoggerBuffer.toString()
                + "\n---本次记录结束---\n", String.format("%s%s.txt", ERROR_LOG_FILENAME, currentTime()));
        errorLoggerBuffer=new StringBuilder();
    }

    public static void addPacketLog(String message){
        String timeStr = currentTime();
        packetLoggerBuffer.append("-------[时间： " + timeStr + " ip:" + getIpAddress() + " ]----------\n");
        packetLoggerBuffer.append(message + "\n");
    }
    public static  void outputPacketLog(){
        FileManager.getInstance().writeErrorLogToLocal(packetLoggerBuffer.toString()
                + "\n---本次记录结束---\n", String.format("%s.txt", PACKET_LOG_FILENAME));
        packetLoggerBuffer=new StringBuilder();
    }


    public static void addLog(String message){
        System.out.println("THLOG::"+message);
        if(isDebug){
            if(AbstractDataServiceFactory.isTcp()){
                addTCPLog(message);
            }else{
                addUDPLog(message);
            }
        }
    }
    public static void outputLog(){
        if(isDebug){
            if(AbstractDataServiceFactory.isTcp()){
                outputTcpLog();
            }else{
                outputUdpLog();
            }
        }
    }

    private static void addUDPLog(String message){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr=dateFormat.format(new Date());
        udpLoggerBuffer.append("-------[时间： "+timeStr+" ip:"+getIpAddress()+" ]----------\n");
        udpLoggerBuffer.append(message+"\n");

    }

    private static void addTCPLog(String message){
        String timeStr = currentTime();
        tcpLoggerBuffer.append("-------[时间： " + timeStr + " ip:" + getIpAddress() + " ]----------\n");
        tcpLoggerBuffer.append(message + "\n");
    }



    public static String currentTime(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        String timeStr=dateFormat.format(new Date());
        return timeStr;
    }

    private static void outputUdpLog(){
        FileManager.getInstance().writeErrorLogToLocal(udpLoggerBuffer.toString() + "\n---本次记录结束---\n", String.format("%s%s.txt", UDP_LOG_FILENAME, currentTime()));
        udpLoggerBuffer=new StringBuilder();
    }
    private static void outputTcpLog(){
        FileManager.getInstance().writeErrorLogToLocal(tcpLoggerBuffer.toString() + "\n---本次记录结束---\n", String.format("%s%s.txt", TCP_LOG_FILENAME, currentTime()));
        tcpLoggerBuffer=new StringBuilder();
    }

}
