package th.service.core;

/**
 * Created by Administrator on 2017/5/22.
 */

public abstract class IReceiveListenser {
    public void onReadData(byte[] contents,int len){

    }

    public void onReadData(byte[] contents, String senderIp,int length){

    }

}
