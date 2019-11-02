package th.service.core;

/**
 * Created by YUYANG on 2018/11/6.
 */

public abstract class IReceiveListenser {
    public void onReadData(byte[] contents,int len){

    }

    public void onReadData(byte[] contents, String senderIp,int length){

    }

}
