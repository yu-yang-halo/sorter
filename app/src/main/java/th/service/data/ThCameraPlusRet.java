package th.service.data;

import java.util.Arrays;

import th.service.core.TrafficManager;

/**
 * Created by Administrator on 2017/4/13.
 *
 * 相机增益返回数据结构
 *
 *
 */

public class ThCameraPlusRet {
    private byte fstRed[];  //2byte 表示数值
    private byte sndRed[];
    private byte fstGreen[];
    private byte sndGreen[];
    private byte fstBlue[];
    private byte sndBlue[];
    private byte fstSndIr1s[];//1byte 表示数值
    private byte fstSndIr2s[];//1byte 表示数值

    public ThCameraPlusRet(byte[] contents) {

        fstRed=new byte[2];
        sndRed=new byte[2];
        fstGreen=new byte[2];
        sndGreen=new byte[2];
        fstBlue=new byte[2];
        sndBlue=new byte[2];
        fstSndIr1s=new byte[2];
        fstSndIr2s=new byte[2];


        if(check(contents)){
            System.arraycopy(contents,0,fstRed,0,fstRed.length);
            System.arraycopy(contents,2*1,sndRed,0,sndRed.length);
            System.arraycopy(contents,2*2,fstGreen,0,fstGreen.length);
            System.arraycopy(contents,2*3,sndGreen,0,sndGreen.length);
            System.arraycopy(contents,2*4,fstBlue,0,fstBlue.length);
            System.arraycopy(contents,2*5,sndBlue,0,sndBlue.length);
            System.arraycopy(contents,2*6,fstSndIr1s,0,fstSndIr1s.length);
            System.arraycopy(contents,2*7,fstSndIr2s,0,fstSndIr2s.length);
        }


    }
    private boolean check(byte[] contents){

        if(contents==null||contents.length!=16){
            TrafficManager.getInstance().showErrorMessage();
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ThCameraPlusRet{" +
                "fstRed=" + Arrays.toString(fstRed) +
                ", sndRed=" + Arrays.toString(sndRed) +
                ", fstGreen=" + Arrays.toString(fstGreen) +
                ", sndGreen=" + Arrays.toString(sndGreen) +
                ", fstSndIr1s=" + Arrays.toString(fstSndIr1s) +
                ", fstSndIr2s=" + Arrays.toString(fstSndIr2s) +
                '}';
    }

    public byte[] getFstBlue() {
        return fstBlue;
    }

    public void setFstBlue(byte[] fstBlue) {
        this.fstBlue = fstBlue;
    }

    public byte[] getSndBlue() {
        return sndBlue;
    }

    public void setSndBlue(byte[] sndBlue) {
        this.sndBlue = sndBlue;
    }

    public byte[] getFstRed() {
        return fstRed;
    }

    public void setFstRed(byte[] fstRed) {
        this.fstRed = fstRed;
    }

    public byte[] getSndRed() {
        return sndRed;
    }

    public void setSndRed(byte[] sndRed) {
        this.sndRed = sndRed;
    }

    public byte[] getFstGreen() {
        return fstGreen;
    }

    public void setFstGreen(byte[] fstGreen) {
        this.fstGreen = fstGreen;
    }

    public byte[] getSndGreen() {
        return sndGreen;
    }

    public void setSndGreen(byte[] sndGreen) {
        this.sndGreen = sndGreen;
    }

    public byte[] getFstSndIr1s() {
        return fstSndIr1s;
    }

    public void setFstSndIr1s(byte[] fstSndIr1s) {
        this.fstSndIr1s = fstSndIr1s;
    }

    public byte[] getFstSndIr2s() {
        return fstSndIr2s;
    }

    public void setFstSndIr2s(byte[] fstSndIr2s) {
        this.fstSndIr2s = fstSndIr2s;
    }
}
