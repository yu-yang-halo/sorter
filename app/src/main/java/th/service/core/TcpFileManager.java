package th.service.core;

import android.os.Handler;
import android.os.Looper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import th.service.helper.CircleBuffer;
import th.service.helper.ThCommand;
import th.service.helper.ThLogger;
import th.service.helper.ThPackage;

/**
 * Created by YUYANG on 2018/11/6.
 */
public class TcpFileManager extends IReceiveListenser{
	/**
	 * TCP文件下载管理模块
	 * 环形缓冲区关闭时要reset，或者开启时reset
	 */
	private static final String TAG="TcpFileManager";
    private final static String TCP_SERVER=ThCommand.TCP_FILE_SERVER_IP;
	private final static int TCP_PORT = ThCommand.TCP_FILE_SERVER_PORT;
	private final static int CONNECT_TIME_OUT=6000;//ms
	private final static int HEART_RATE=5000;//ms
	private final static int RECEIVE_TIME_OUT=15*1000;//ms

	private static TcpFileManager instance = new TcpFileManager();
	private Handler mainUIhandler=new Handler(Looper.getMainLooper());
	private ExecutorService executorService= Executors.newCachedThreadPool();
	private Socket sendSocket;
	/**
	 * 仅仅连接一次
	 */
	private volatile boolean created=false;
	private volatile boolean isListenserData=false;

	private CircleBuffer cicleBuffer=new CircleBuffer();

	private TcpFileManager() {
		cicleBuffer.setiReceiveListenser(this);
	}
	public static TcpFileManager getInstance() {

		return instance;
	}

	public void startReceiveLooper(){
		if(!isListenserData){
			isListenserData=true;
			reciveLoop();
		}
	}

	public void closeConnect(){
		isListenserData=false;
		created=false;
		cicleBuffer.reset();
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if(sendSocket!=null) {
						sendSocket.close();
					}
				} catch (IOException e) {
					ThLogger.debug(TAG,"closeConnect ...."+e);
				}
			}
		});

		TrafficManager.getInstance().forceWrite();

	}
	private void connect(){
		if(sendSocket==null||sendSocket.isClosed()||!created){
			sendSocket = new Socket();
			created=true;
			cicleBuffer.reset();
			SocketAddress socketAddress = new InetSocketAddress(TCP_SERVER,TCP_PORT);
			try {
				sendSocket.connect(socketAddress, CONNECT_TIME_OUT);
				sendSocket.setKeepAlive(true);
				sendSocket.setOOBInline(true);
				sendSocket.setSoLinger(true,100);
				sendSocket.setSoTimeout(RECEIVE_TIME_OUT);

			} catch (IOException e) {
				created=false;
				closeConnect();
			}
		}


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
				connect();
				startReceiveLooper();
				/**
				 * 发送socket 为空 则创建新的socket 启动接受数据
				 */
				 if(sendSocket!=null){
					 try {
						 OutputStream os=sendSocket.getOutputStream();
						 os.write(buffer);
						 os.flush();
					 } catch (Exception e) {
						 closeConnect();
					 }
				 }
			}
		});

	}
	public  void reciveLoop(){
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					while (true){
						ThLogger.debug(TAG,"reciveLoop ...."+isListenserData);
						while(isListenserData){

							if(sendSocket==null){
								continue;
							}
							try {
								InputStream is=sendSocket.getInputStream();
								execute2(is);
							} catch (IOException e) {
								closeConnect();
							}

						}
						if(!isListenserData){
							break;
						}

					}
				}
			});
	}


	/**
	 *
	 * @param contents
	 * @param len         总长度
     */
	@Override
	public void onReadData(byte[] contents,int len) {
		/**
		 * 接收流量统计
		 */
		TrafficManager.getInstance().addAcceptSize(len);

		ThPackage thPackage=new ThPackage(contents,len);

		ThLogger.debug(TAG,"len:: "+len+"\n"+thPackage);

		/**
		 * UIManager 发送消息给【BaseUi】界面
		 */

		ThUIManger.getInstance().setChanged();
		ThUIManger.getInstance().notifyObservers(thPackage);

	}

	/**
	 * 解决粘包问题
	 * 环形缓冲区读取
	 */
	byte[] tcpBuffers=new byte[CircleBuffer.RECEIVE_BUFFER_MAX];
	public void execute2(InputStream is) {
		int len=0;
		try {
			while ((len=is.read(tcpBuffers))!=-1){
				ThLogger.debug(TAG,"read execute2:: "+len);
				cicleBuffer.pushData(tcpBuffers,len);
            }
			if (len < 0) {
				if(len==-1){
					closeConnect();
				}
			}

		} catch (IOException e) {
			if(e instanceof SocketTimeoutException){
				closeConnect();
			}
		} catch (Exception e) {
			closeConnect();
		}

	}

}

