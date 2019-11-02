package th.service.core;
import com.yy.sorter.utils.ConvertUtils;

import th.service.helper.ThCommand;
import th.service.helper.ThPackage;

/**
 * Created by YUYANG on 2018/11/6.
 * TcpManager 代理类
 */

public class IDataServiceTcpImpl extends AbstractIDataService {

    private static TcpCoreManager tcpCoreManager=TcpCoreManager.getInstance();
    private static TcpFileManager tcpFileManager=TcpFileManager.getInstance();


    /**
     * 文件下载相关的采用 tcpFileManager
     */
    @Override
    public void requestDownloadWhatFile(byte softVersion, byte whatTypeFile, String fileName) {
        byte[] data1=new byte[]{ThCommand.ANDROID_DEVICE,softVersion,whatTypeFile,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
        byte[] extDatas=null;
        if(fileName!=null){
            extDatas= fileName.getBytes();
        }
        ThPackage packet= new ThPackage(ThCommand.TCP_REQ_DOWNLOAD_WHAT_FILE,(byte)1, data1,(byte)0, (byte)0, (byte)0,extDatas);

        sendFilePacketData(packet,null);
    }
    @Override
    public void requestDownloadFile() {
        byte[] data1=new byte[]{(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
        ThPackage packet= new ThPackage(ThCommand.TCP_REQ_DOWNLOAD_FILE,(byte)1, data1,(byte)0, (short) 0, (short)0,null);

        sendFilePacketData(packet,null);
    }

    @Override
    public void closeFileSocket() {
       tcpFileManager.closeConnect();
    }


    @Override
    public void register(String username, String password) {
        String usernamePass=username+"#"+password;
        byte[] bytes=usernamePass.getBytes();
        byte[] data1=new byte[]{(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0};
        ThPackage packet= new ThPackage(ThCommand.TCP_USER_REGISTER_CMD,(byte)0, data1,(byte)0, (byte)0, (byte)0,bytes);

        sendPacketData(packet,null);
    }


    /**
     * 请求登录
     * @param devSN
     * @param username
     * @param password
     */
    @Override
    public void requestDeviceList(String devSN, String username, String password,int code){
        byte[] codeBytes= ConvertUtils.intTo2Bytes(code);


        byte[] data1=new byte[]{(byte) 1,ThCommand.ANDROID_DEVICE,codeBytes[0],codeBytes[1]};
        byte[] extDatas=devSN.getBytes();
        ThPackage packet= new ThPackage(ThCommand.TCP_LOGIN_CMD,(byte)1, data1,(byte)0, (byte)0, (byte)0,extDatas);
        sendPacketData(packet,null);
    }

    @Override
    public void logout() {

    }


    @Override
    public void closeConnect() {
        tcpCoreManager.closeConnect();

    }

    @Override
    public void reSetReconnectFlag() {
        tcpCoreManager.reSetReconnectFlag();
    }

    @Override
    public void sendPacketData(ThPackage thPackage, String ip) {
        tcpCoreManager.sendData(thPackage.myByteArrays());
    }

    @Override
    public void sendPacketData(ThPackage thPackage) {
        sendPacketData(thPackage,"");
    }

    public void sendFilePacketData(ThPackage thPackage, String ip) {
        tcpFileManager.sendData(thPackage.myByteArrays());
    }

}
