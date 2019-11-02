package th.service.data;

import java.util.Arrays;

import th.service.core.TrafficManager;

/**
 * Created by YUYANG on 2018/11/6.
 * 喷阀频率数据结构
 */

public class YYValveRateRet {
    private int valveNum;
    private boolean onlyFrontView;
    private byte[]  frontDatas;
    private byte[]  backDatas;



    public YYValveRateRet(int valveNum, boolean onlyFrontView, byte[] contents) {

        if(check(valveNum,onlyFrontView,contents)){
            this.valveNum = valveNum;
            this.onlyFrontView = onlyFrontView;
            if(onlyFrontView){
                this.frontDatas=contents;
            }else{
                this.frontDatas=new byte[valveNum*2];
                this.backDatas=new byte[valveNum*2];

                System.arraycopy(contents,0,frontDatas,0,frontDatas.length);
                System.arraycopy(contents,frontDatas.length,backDatas,0,backDatas.length);

            }
        }
    }

    public boolean check(int valveNum, boolean onlyFrontView, byte[] contents){
        if(onlyFrontView){
            if(valveNum*2!=contents.length){
                TrafficManager.getInstance().showErrorMessage();
                return false;
            }
        }else{
            if(valveNum*4!=contents.length){
                TrafficManager.getInstance().showErrorMessage();
                return false;
            }
        }

        return true;
    }

    public int getValveNum() {
        return valveNum;
    }

    public void setValveNum(int valveNum) {
        this.valveNum = valveNum;
    }

    public boolean isOnlyFrontView() {
        return onlyFrontView;
    }

    public void setOnlyFrontView(boolean onlyFrontView) {
        this.onlyFrontView = onlyFrontView;
    }

    @Override
    public String toString() {
        return "YYValveRateRet{" +
                "valveNum=" + valveNum +
                ", onlyFrontView=" + onlyFrontView +
                ", frontDatas=" + Arrays.toString(frontDatas) +
                ", backDatas=" + Arrays.toString(backDatas) +
                '}';
    }

    public byte[] getFrontDatas() {
        return frontDatas;
    }

    public void setFrontDatas(byte[] frontDatas) {
        this.frontDatas = frontDatas;
    }

    public byte[] getBackDatas() {
        return backDatas;
    }

    public void setBackDatas(byte[] backDatas) {
        this.backDatas = backDatas;
    }
}
