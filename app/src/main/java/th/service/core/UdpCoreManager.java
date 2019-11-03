package th.service.core;

import android.os.Handler;
import android.os.Looper;

import com.yy.sorter.utils.ConvertUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import th.service.helper.YYCommand;
import th.service.helper.YYLogger;
import th.service.helper.YYPackage;
import th.service.repeat.RepeatListenser;
import th.service.repeat.RepeatManager;

/**
 * Created by YUYANG on 2018/11/6.
 */
public class UdpCoreManager extends IReceiveListenser {
    /**
     * 单例模式 Udp核心管理模块
     * 采用心跳的方式保证通信连接状态
     */
    private static final String TAG = "UdpCoreManger";
    private final static int UDP_PORT = YYCommand.UDP_CORE_SERVER_PORT;
    private static final int BUFFER_SIZE = 5 * 1024;
    private static final int RECONNECT_MAX_COUNT = 3;
    private int reconnectCount = 0;//重连次数
    /**
     * 心跳发送频率
     */
    private static final int HEART_SEND_RATE = 2000;
    private static final int RECV_TIME_OUT = 11 * 1000;
    private static final float RECV_TIPS_TIME_OUT = 8 * 1000;

    private static UdpCoreManager instance = new UdpCoreManager();
    private DatagramSocket sendSocket;
    private volatile boolean isListenserData = false;
    private volatile boolean created = false;
    private volatile boolean deviceInControl = false;
    private Object lockObject = new Object();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private byte[] getBuf = new byte[BUFFER_SIZE];
    private Handler mainUIhandler = new Handler(Looper.getMainLooper());
    private Timer repeatTimer;
    private Timer heartTimer;

    private UdpCoreManager() {

    }

    /**
     * 接收数据循环
     */
    public void startReceiveLooper() {
        if (!isListenserData) {
            isListenserData = true;
            reciveUDPLoop();
            repeatLoop();
        }
    }

    public static UdpCoreManager getInstance() {
        return instance;
    }

    public void closeConnect() {

        if (created) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    YYLogger.addLog("关闭Socket连接 ");

                    RepeatManager.getInstance().init();
                    synchronized (lockObject) {
                        isListenserData = false;
                        created = false;
                    }
                    deviceInControl = false;
                    if (heartTimer != null) {
                        heartTimer.cancel();
                        heartTimer.purge();
                    }
                    if (repeatTimer != null) {
                        repeatTimer.cancel();
                        repeatTimer.purge();
                    }
                    if (sendSocket != null) {
                        sendSocket.close();
                    }
                    YYLogger.outputLog();
                    RepeatManager.getInstance().outputLog();
                }
            });


        }


    }

    public void sendData(final byte[] buffer, final String ip) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                /**
                 * 发送socket 为空 则创建新的socket 启动接受数据
                 */
                try {
                    synchronized (lockObject) {
                        if (!created) {
                            YYLogger.addLog("创建Socket连接");
                            sendSocket = new DatagramSocket();
                            startReceiveLooper();
                            created = true;
                        }
                    }

                    if (buffer[0] == 0x01 && buffer[1] == 0x02) {
                        if (heartTimer != null) {
                            heartTimer.cancel();
                            heartTimer.purge();
                        }
                        deviceInControl = false;
                    }
                    sendSocket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), UDP_PORT));

                } catch (IOException e) {
                    if (deviceInControl) {
                        //通知心跳重连 (不管不问)
                        YYLogger.addLog("设备处于控制中，不管，由心跳自动重连");
                    } else {
                        YYLogger.addLog("数据发送出错，关闭连接");
                        sendErrorCloseConnect(YYCommand.UDP_NETWORK_UNREACHABLE);
                    }

                }
            }
        });

    }

    private void sendErrorCloseConnect(final int errorCode) {
        closeConnect();
        sendError(errorCode);
    }

    private void sendError(final int errorCode) {
        mainUIhandler.post(new Runnable() {
            @Override
            public void run() {
                ThUIManger.getInstance().setChanged();
                ThUIManger.getInstance().notifyObservers(errorCode);
            }
        });
    }

    private void repeatLoop() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (repeatTimer != null) {
                    repeatTimer.cancel();
                    repeatTimer.purge();
                }
                repeatTimer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        RepeatManager.getInstance().check(new RepeatListenser() {
                            @Override
                            public void onResult(boolean needRepeatYN,
                                                 List<YYPackage> needResendPackets) {
                                if (needRepeatYN && needResendPackets != null) {

                                    for (YYPackage thPackage : needResendPackets) {

                                        YYLogger.addLog("Repeat Packet：type:" + Integer.toHexString(ConvertUtils.unsignByteToInt(thPackage.getType()))
                                                + " extendType:" + Integer.toHexString(thPackage.getExtendType())
                                                + " 包号：" + ConvertUtils.unsignByteToInt(thPackage.getPacketNumber())
                                                + " send value:" + ConvertUtils.unsignByteToInt(thPackage.getData1()[2])
                                                + "\n-------" + thPackage);
                                        sendData(thPackage.myByteArrays(), thPackage.getSenderIP());
                                    }
                                }
                            }
                        });
                    }
                };
                try{
                    repeatTimer.schedule(task, 0, 200);
                }catch (Exception e){

                }

            }
        });
    }


    public void reciveUDPLoop() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    while (isListenserData) {
                        if (sendSocket == null) {
                            break;
                        }
                        // 创建接受类型的数据报
                        DatagramPacket getPacket = new DatagramPacket(getBuf, getBuf.length);
                        try {
                            //Log.e(TAG,"等待数据。。。。。");
                            sendSocket.receive(getPacket);
                            //Log.e(TAG,"接受到了数据了 length:"+getPacket.getLength());
                            lastTime = System.currentTimeMillis();
                            reconnectCount = 0;
                            onReadData(getBuf, getPacket.getAddress().getHostAddress(), getPacket.getLength());
                        } catch (IOException e) {
                            //YYLogger.addLog("接收数据报异常 --"+e.getMessage()+"---");
                        }
                    }
                    if (!isListenserData) {
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onReadData(byte[] contents, final String senderIp, int length) {
        YYPackage thPackage = new YYPackage(contents, length);
        thPackage.setSenderIP(senderIp);
        RepeatManager.getInstance().remove(thPackage);

        if (thPackage.getType() == 0x01 && thPackage.getExtendType() == 0x01) {
            YYLogger.addLog("获取机器数据成功。。。");
            RepeatManager.getInstance().init();
            deviceInControl = true;
            sendHeart(thPackage);
        } else if (thPackage.getType() == 0x55 && thPackage.getExtendType() == 0x01) {
            if (heartTimer != null) {
                heartTimer.cancel();
                heartTimer.purge();
            }
            deviceInControl = false;
        }
        if(thPackage.getType() == 0x05)
        {
            YYLogger.addLog("获取机器数据成功。。。");
        }

        /**
         * UIManager 发送消息给【BaseUi】界面
         */
        ThUIManger.getInstance().setChanged();
        ThUIManger.getInstance().notifyObservers(thPackage);

    }

    private long lastTime = -1;
    private long currentTime = -1;

    private void sendHeart(final YYPackage thPackage) {
        if (thPackage.getType() == 0x01 && thPackage.getExtendType() == 0x01) {
            YYLogger.addLog("开始创建心跳定时器");
            deviceInControl = true;
            currentTime = -1;
            if (heartTimer != null) {
                heartTimer.cancel();
                heartTimer.purge();
            }
            heartTimer = new Timer();
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    boolean isSend = true;
                    currentTime = System.currentTimeMillis();
                    long diff_time = currentTime - lastTime;//时间差
                    if (lastTime != -1) {

                        if (diff_time > RECV_TIME_OUT) {
                            if (reconnectCount < RECONNECT_MAX_COUNT) {
                                YYLogger.addLog("*****重连尝试 ：" + (++reconnectCount));
                                AbstractDataServiceFactory.getInstance().login(null, (byte) 0);
                                sendError(YYCommand.NETWORK_TIMEOUT_RECONNECT);
                            } else {
                                YYLogger.addLog("*****网络信号不稳定,退出界面 ");
                                sendErrorCloseConnect(YYCommand.UDP_HEART_CMD_TIMEOUT_TIPS_RETURN);
                                reconnectCount = 0;
                            }

                        } else if (diff_time > RECV_TIPS_TIME_OUT) {
                            YYLogger.addLog("*****网络信号不稳定,开始提示 ");
                        } else if (diff_time <= HEART_SEND_RATE) {
                            isSend = false;
                        }

                    }

                    if (isSend) {
                        byte[] data1 = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
                        YYPackage packet = new YYPackage(YYCommand.UDP_HEART_CMD, (byte) 0, data1, (byte) 0, (byte) 0, (byte) 0, null);
                        sendData(packet.myByteArrays(), thPackage.getSenderIP());
                    }


                }
            };
            try{
                heartTimer.schedule(task, 0, HEART_SEND_RATE);
            }catch (Exception e){

            }

        }

    }


}

