package th.service.core;

import android.os.Handler;
import android.os.Looper;
import com.yy.sorter.utils.TextCacheUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import th.service.helper.CircleBuffer;
import th.service.helper.IPUtils;
import th.service.helper.ThCommand;
import th.service.helper.ThLogger;
import th.service.helper.ThPackage;

public class TcpCoreManager extends IReceiveListenser{
	/**
	 * 单例模式 TCP核心管理模块
	 * 功能：
	 * 1.发送心跳保持连接确保不被服务器踢掉（通过tcp内置的读取超时机制来判断与服务器断开情况）
	 * 2.重连机制
	 */
	private static final String TAG="TCPCoreManger";
	private final static String TCP_SERVER=ThCommand.TCP_CORE_SERVER_IP;
	private final static int TCP_PORT = ThCommand.TCP_CORE_SERVER_PORT;
	private final static long KICK_MAX_TIME=600000; //长时间不操作被踢 10分钟
	private final static int CONNECT_TIME_OUT=6000;//ms
	private final static int RECEIVE_TIME_OUT=11*1000;//ms
	private final static int RECONNECT_TIME_DURATION=4*1000;//ms
	private final static int RECONNECT_MAX_COUNT=7;//最多重连7次 每次延迟累加1000ms
	private final static int INCREMENT_STEP_INTERVAL_TIME=2000;

	private static TcpCoreManager instance = new TcpCoreManager();
	private Handler mainUIhandler=new Handler(Looper.getMainLooper());
	private ExecutorService executorService= Executors.newCachedThreadPool();
	private Socket sendSocket;
	private OutputStream clientOS;
	private InputStream clientIS;

	private volatile AtomicBoolean isListenserData=new AtomicBoolean(false);
	private volatile AtomicBoolean created=new AtomicBoolean(false);
	private volatile AtomicInteger readTimeoutCount=new AtomicInteger(0);
	private volatile AtomicBoolean reconnectFlag=new AtomicBoolean(false);
	private volatile AtomicBoolean loginState=new AtomicBoolean(false);

	private Object lockObj=new Object();

	private CircleBuffer cicleBuffer=new CircleBuffer();

	private String ipAddress = null;

	private TcpCoreManager() {
		cicleBuffer.setiReceiveListenser(this);
	}
	public static TcpCoreManager getInstance() {
		return instance;
	}
	public void startReceiveLooper(){
		if(!isListenserData.get()){
			ThLogger.addLog("开始启动接收数据监听器");
			isListenserData.getAndSet(true);
			reciveLoop();
		}
	}

	public void reSetReconnectFlag() {
		synchronized (lockObj){
			loginState.getAndSet(false);
			reconnectFlag.getAndSet(false);
			readTimeoutCount.getAndSet(0);
		}
	}
	public void closeConnect(){
		if(created.get()){
			ThLogger.addLog("关闭连接");
			created.getAndSet(false);
			isListenserData.getAndSet(false);
			loginState.getAndSet(false);

			executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						if(sendSocket!=null){
							sendSocket.shutdownInput();
							sendSocket.shutdownOutput();
							sendSocket.close();
						}
						if(clientOS!=null){
							clientOS.close();
						}
						if(clientIS!=null){
							clientIS.close();
						}

					} catch (IOException e) {
						ThLogger.debug(TAG,"closeConnect ...."+e);
					}finally {
						ThLogger.outputLog();
					}
				}
			});


		}


		TrafficManager.getInstance().forceWrite();

	}

	private void sendError(final int errorCode){
		mainUIhandler.post(new Runnable() {
			@Override
			public void run() {
				ThUIManger.getInstance().setChanged();
				ThUIManger.getInstance().notifyObservers(errorCode);
			}
		});
	}
	private void sendErrorCloseConnect(int notifyType){

		closeConnect();
		sendError(notifyType);
	}


	public void sendData(final byte[] buffer) {
		/**
		 * 发送流量统计
		 */
		TrafficManager.getInstance().addSendSize(buffer.length);

		executorService.execute(new Runnable() {
			@Override
			public void run() {
				synchronized (lockObj){
					if (!created.get()){
						if(!IPUtils.netAvaliable()){
							if(!reconnectFlag.get()){
								sendErrorCloseConnect(ThCommand.TCP_CONNECT_OFFLINE);
							}
							return;
						}
						ipAddress = IPUtils.getServerIp();

						ThLogger.addLog("创建Socket连接");
						startReceiveLooper();
						cicleBuffer.reset();
						sendSocket = new Socket();

						SocketAddress socketAddress = new InetSocketAddress(ipAddress,TCP_PORT);

						try {
							sendSocket.connect(socketAddress, CONNECT_TIME_OUT);
							sendSocket.setKeepAlive(true);
							sendSocket.setOOBInline(true);
							sendSocket.setSoLinger(true,100);
							sendSocket.setSoTimeout(RECEIVE_TIME_OUT);
							clientOS=sendSocket.getOutputStream();
							clientIS=sendSocket.getInputStream();
						} catch (IOException e) {
							if(!reconnectFlag.get()){
								ThLogger.addLog("关闭Socket连接 连接超时");
								sendErrorCloseConnect(ThCommand.TCP_CONNECT_TIMEOUT);
							}
						}

					}
				}


				/**
				 * 发送socket 为空 则创建新的socket 启动接受数据
				 */
				try {
					clientOS.write(buffer);
					clientOS.flush();
				} catch (Exception e) {
				}
			}
		});

	}
	public  void reciveLoop(){
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					while (true){

						while(isListenserData.get()){

							if(sendSocket==null||sendSocket.isClosed()||clientIS==null){
								continue;
							}

						    execute2(clientIS);

							synchronized (lockObj){
								if(isListenserData.get()==false){
									break;
								}
							}
						}

						synchronized (lockObj){
							if(!isListenserData.get()){
								ThLogger.addLog("退出接收数据监听器循环。。");
								break;
							}
						}

					}
				}
			});
	}

	long lastTime=0;
	long currentTime=0;

	/**
	 *
	 * @param contents
	 * @param len         总长度
     */
	@Override
	public void onReadData(byte[] contents,int len) {
		created.getAndSet(true);

		/**
		 * 接收流量统计
		 */
		TrafficManager.getInstance().addAcceptSize(len);

		ThPackage thPackage=new ThPackage(contents,len);

	    if(thPackage.getType()==0x52&&thPackage.getExtendType()==0x00){
			ThLogger.addLog("收到心跳");
			/**
			 * 回心跳
			 */
		    sendData(thPackage.myByteArrays());
			currentTime=System.currentTimeMillis();
			long diff_time=currentTime-lastTime;
			if(diff_time>KICK_MAX_TIME){
				ThLogger.addLog("长时间不操作退出应用");
				sendErrorCloseConnect(ThCommand.TCP_CONNECT_CLOSE);
			}
			return;
		}else if(thPackage.getType()==0x51&&thPackage.getExtendType()==0x01){
			ThLogger.addLog("tcp连接成功");
			sendError(ThCommand.NETWORK_LOGGIN_SUCCESS);
			lastTime=System.currentTimeMillis();
		}else if(thPackage.getType()==0x01&&thPackage.getExtendType()==0x01){
			ThLogger.addLog("tcp获取机器数据成功");
			reSetReconnectFlag();
			loginState.getAndSet(true);
			lastTime=System.currentTimeMillis();
		}else{
			lastTime=System.currentTimeMillis();
		}
		ThLogger.debug(TAG,"len:: "+len+"\n"+thPackage);

		/**
		 * UIManager 发送消息给【BaseUi】界面
		 */

		ThUIManger.getInstance().setChanged();
		ThUIManger.getInstance().notifyObservers(thPackage);

	}

	/**
	 * 解决粘包问题
	 */
	/**
	 * 读取tcp流方式二
	 */
	byte[] tcpBuffers=new byte[CircleBuffer.RECEIVE_BUFFER_MIN];

	public void execute2(InputStream is) {
		int len=0;
		try {
			while ((len=is.read(tcpBuffers))!=-1){
				cicleBuffer.pushData(tcpBuffers,len);
            }
            if(len==-1){
				synchronized (lockObj){
					if(reconnectFlag.get()){
						return;
					}

					if(loginState.get()){
						executorService.execute(new Runnable() {
							@Override
							public void run() {
								reconnect();
							}
						});
					}
				}

			}
		} catch (IOException e) {
			if(e instanceof SocketTimeoutException){
				synchronized (lockObj){
					if(reconnectFlag.get()){
						return;
					}
					if(loginState.get()){
						executorService.execute(new Runnable() {
							@Override
							public void run() {
								reconnect();
							}
						});
					}
				}
			}
		} catch (Exception e) {
			if(!reconnectFlag.get()){
				ThLogger.addLog("关闭Socket连接 e："+e.getMessage());
				sendErrorCloseConnect(ThCommand.TCP_CONNECT_CLOSE);
			}

		}

	}

	private void close(){
		synchronized (lockObj){
			created.getAndSet(false);
		}
		try {
			if(sendSocket!=null){
				sendSocket.shutdownInput();
				sendSocket.shutdownOutput();
				sendSocket.close();
			}
			if(clientOS!=null){
				clientOS.close();
			}
			if(clientIS!=null){
				clientIS.close();
			}
		} catch (IOException e) {
			ThLogger.debug(TAG,"closeConnect ...."+e);
		}

	}

	private void reconnect(){
		synchronized (lockObj){
			if(reconnectFlag.get()){
				return;
			}
			reconnectFlag.getAndSet(true);
		}
		readTimeoutCount.getAndSet(0);
		ThLogger.addLog("readTimeoutCount::"+readTimeoutCount.get());
		while (readTimeoutCount.getAndIncrement()<RECONNECT_MAX_COUNT){
			if(!reconnectFlag.get()){
				break;
			}
			if(!loginState.get()){
				break;
			}

			close();
			ThLogger.addLog("读取超时，开始重连 ******"+readTimeoutCount.get());
			mainUIhandler.post(new Runnable() {
				@Override
				public void run() {
					String deviceNumber=TextCacheUtils.getValueString(TextCacheUtils.KEY_DEVICE_NUMBER,"");
					int vcode= TextCacheUtils.getValueInt(TextCacheUtils.KEY_VCODE,-1);
					ThLogger.addLog("重连 deviceNumber："+deviceNumber+" vcode:"+vcode);
					sendError(ThCommand.NETWORK_TIMEOUT_RECONNECT);
					AbstractDataServiceFactory.getInstance().requestDeviceList(deviceNumber,null,null,vcode);

				}
			});
			try {
				new Thread().sleep(RECONNECT_TIME_DURATION+((readTimeoutCount.get()-1)*INCREMENT_STEP_INTERVAL_TIME));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		synchronized (lockObj){
			if(reconnectFlag.getAndSet(false)){
				sendErrorCloseConnect(ThCommand.TCP_CONNECT_CLOSE);

				if(loginState.get()){
					ThLogger.addLog("重连失败");
				}else{
					ThLogger.addLog("退出登录，重连失败");
				}

			}else{
				ThLogger.addLog("重连结束");
			}
		}

	}

}

