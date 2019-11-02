package th.service.core;

/**
 * Created by YUYANG on 2018/11/6.
 * 工厂方法 生成通信实例
 */

public class AbstractDataServiceFactory {

    public static final int SERVICE_TYPE_TCP_FILE=0x1002;
    public static final int SERVICE_TYPE_TCP=0x1000;
    public static final int SERVICE_TYPE_UDP=0x1001;

    private static AbstractIDataService service=null;
    private static AbstractIDataService fileDownloadService=null;

    private static boolean tcp;
    private static boolean tcpFile;

    public static boolean isTcpFile() {
        return tcpFile;
    }

    public static void setTcpFile(boolean tcpFile) {
        AbstractDataServiceFactory.tcpFile = tcpFile;
    }

    public static boolean isTcp() {
        return tcp;
    }

    private static void setTcp(boolean tcp) {
        AbstractDataServiceFactory.tcp = tcp;
    }

    public static AbstractIDataService getFileDownloadService(){
        setTcpFile(true);
        if(fileDownloadService==null){
            fileDownloadService =new IDataServiceTcpImpl();
        }
       return fileDownloadService;
    }

    public static AbstractIDataService getInstance(){
        if(service==null){
            initService(SERVICE_TYPE_TCP);
        }
        return service;
    }

    public static void release(){

        service=null;
        fileDownloadService=null;

    }

    public  static void initService(int serviceType){
        if(service!=null){
            service.closeConnect();
        }
        switch (serviceType){
            case SERVICE_TYPE_UDP:
                setTcp(false);
                service=null;
                service=new IDataServiceUdpImpl();
                break;
            case SERVICE_TYPE_TCP:
                setTcp(true);
                service=null;
                service=new IDataServiceTcpImpl();
                break;
        }

    }

}
