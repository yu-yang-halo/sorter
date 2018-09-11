package th.service.helper;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.yy.sorter.activity.BuildConfig;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
/**
 * 
 * @author Administrator
 * 
 * 获取当前设备ip地址
 * 通过当前IP地址计算当前网段的广播地址
 *
 */

public class IPUtils {
	private static String TAG="IPUtils";
	
	public static String getBroadCastAddress(Context ctx){
		WifiManager wifiManager= (WifiManager) ctx.getSystemService(Service.WIFI_SERVICE);
		DhcpInfo dhcpInfo=wifiManager.getDhcpInfo();
		int a=dhcpInfo.ipAddress;
		int b=dhcpInfo.netmask;
		if(b==0&&a!=0){
			/**
			 * 如果子网掩码为0且ip地址不为0
			 * （无法正常获取到子网掩码的情况下则默认使用255.255.255.0）
			 */
			b=16777215;
		}
		int c=~b;
		String broadcastAddress=intToIp(a&b|c);

		if(BuildConfig.DEBUG){
			broadcastAddress="192.168.137.255";
		}
		ThLogger.debug(TAG,"广播地址为：：："+broadcastAddress);
		return broadcastAddress;
	}
	public static String getNetworkType(Context ctx){
		String networkType="";
		ConnectivityManager connectivityManager=(ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
		if(networkInfo!=null&&networkInfo.isConnected()){
			if(networkInfo.getType()==ConnectivityManager.TYPE_WIFI){
				networkType="WIFI";
				ThLogger.debug("TYPE_WIFI",networkInfo.getSubtypeName());
			}else if(networkInfo.getType()==ConnectivityManager.TYPE_MOBILE){
				ThLogger.debug("TYPE_MOBILE",networkInfo.getSubtypeName());
				networkType="4G";
			}
		}
		return networkType;
	}

	public static String localIpAddress(Context ctx){
		WifiManager wifiManager= (WifiManager) ctx.getSystemService(Service.WIFI_SERVICE);
		WifiInfo wifiInfo=wifiManager.getConnectionInfo();


		DhcpInfo dhcpInfo=wifiManager.getDhcpInfo();

		StringBuilder sb = new StringBuilder();
		sb.append("网络信息：");
		sb.append("\nipAddress：" + intToIp(dhcpInfo.ipAddress));
		sb.append("\nnetmask：" + intToIp(dhcpInfo.netmask));
		sb.append("\ngateway：" + intToIp(dhcpInfo.gateway));
		sb.append("\nserverAddress：" + intToIp(dhcpInfo.serverAddress));
		sb.append("\ndns1：" + intToIp(dhcpInfo.dns1));
		sb.append("\ndns2：" + intToIp(dhcpInfo.dns2));
		sb.append("\n");
		sb.append("Wifi信息：");
		sb.append("\nIpAddress：" + intToIp(wifiInfo.getIpAddress()));
		sb.append("\nMacAddress：" + wifiInfo.getMacAddress());
		sb.append("\ngetNetworkId：" + wifiInfo.getNetworkId());
		sb.append("\nwifiInfo：" + wifiInfo);
		ThLogger.debug(TAG,sb.toString());


		return intToIp(dhcpInfo.ipAddress);

	}

	private static String intToIp(int paramInt) {
		return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
				+ (0xFF & paramInt >> 24);
	}


	public static boolean isConnected(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Network[] networks = connectivityManager.getAllNetworks();
			NetworkInfo networkInfo;
			for (Network mNetwork : networks) {
				networkInfo = connectivityManager.getNetworkInfo(mNetwork);
				if(networkInfo==null){
					continue;
				}
				if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
					return true;
				}
			}
		}else {
			if (connectivityManager != null) {
				NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
				if (info != null) {
					for (NetworkInfo anInfo : info) {
						if(anInfo==null){
							continue;
						}
						if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}


	/**
	 * 通过解析域名来判断网络是否连接
	 * @return
	 */
	public static boolean netAvaliable() {
		String hostname="www.baidu.com";
		boolean avaliable=false;
		try {
			DNSResolver dnsRes = new DNSResolver(hostname);
			Thread t = new Thread(dnsRes);
			t.start();
			t.join(1000);
			InetAddress inetAddr = dnsRes.get();
			avaliable=(inetAddr != null);
		} catch (Exception e) {
			avaliable= false;
		}

		if(avaliable){
			System.out.println("可以上网...");
		}else{
			System.out.println("不能上网...");
		}
		return avaliable;
	}

	private static class DNSResolver implements Runnable {
		private String domain;
		private InetAddress inetAddr;

		public DNSResolver(String domain) {
			this.domain = domain;
		}

		public void run() {
			try {
				InetAddress addr = InetAddress.getByName(domain);
				set(addr);
			} catch (UnknownHostException e) {

			}
		}

		public synchronized void set(InetAddress inetAddr) {
			this.inetAddr = inetAddr;
		}

		public synchronized InetAddress get() {
			return inetAddr;
		}
	}



	@Deprecated
	private static boolean netAvaliable2(){
		URL url;
		try {
			url = new URL("https://www.baidu.com/");
			HttpURLConnection connection=(HttpURLConnection) url.openConnection();

			if(200 == connection.getResponseCode()){
				System.err.println("可以上网...");
				return true;
			}
		} catch (Exception e) {

		}
		System.err.println("不能上网...");
		return false;
	}

	@Deprecated
	private static final boolean ping() {

		String result = null;
		try {
			String ip = "www.taobao.com";// ping 的地址，可以换成任何一种可靠的外网
			Process p = Runtime.getRuntime().exec("/system/bin/ping -c 3 -t 3 " + ip);// ping网址3次

			int status = p.waitFor();
			if (status == 0) {
				result = "success";
				return true;
			} else {
				result = "failed";
			}
		} catch (IOException e) {
			    result=e.getMessage();
		} catch (InterruptedException e) {
			    result=e.getMessage();
		} finally {
			System.err.println("result = " + result);
		}
		return false;

	}
	
	

}
