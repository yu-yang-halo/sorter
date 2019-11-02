package th.service.data;

import java.util.Arrays;

import th.service.helper.ThCommand;
import th.service.helper.ThPackage;

/**
 * Created by YUYANG on 2018/11/6.
 * 波形返回数据
 */

public class YYWaveData {

    private byte waveType;   //波形类型

    public byte[] getrBytes() {
        return rBytes;
    }

    public void setrBytes(byte[] rBytes) {
        this.rBytes = rBytes;
    }

    public byte[] getgBytes() {
        return gBytes;
    }

    public void setgBytes(byte[] gBytes) {
        this.gBytes = gBytes;
    }

    public byte[] getbBytes() {
        return bBytes;
    }

    public void setbBytes(byte[] bBytes) {
        this.bBytes = bBytes;
    }

    public byte[] getIr1Bytes() {
        return ir1Bytes;
    }

    public void setIr1Bytes(byte[] ir1Bytes) {
        this.ir1Bytes = ir1Bytes;
    }

    public byte[] getIr2Bytes() {
        return ir2Bytes;
    }

    public void setIr2Bytes(byte[] ir2Bytes) {
        this.ir2Bytes = ir2Bytes;
    }

    private byte algorithm;  //算法类型


    public byte getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(byte algorithm) {
        this.algorithm = algorithm;
    }

    public byte getWaveType() {
        return waveType;
    }

    /**
     * data1 [0-5]

     waveType :0
     data1[0]:  color
     data1[1]：一次终止
     data1[2]：一次上限
     data1[3]：一次下限
     data1[4]：二次上限
     data1[5]：二次下限

     waveType :1
     data1[0]:  color1
     data1[1]： color2
     data1[2]： 上限
     data1[3]： 下限

     waveType :2
     data1[0]:  color1
     data1[1]：
     data1[2]： 上限
     data1[3]： 下限

     背景灯光

     waveType :3
     data1[0]:  0:可见 ，1：单红外 2：双红外
     BYTE waveData[256]*3+[256]+[256];   //波形的数据

     相机增益
     waveType :3
     data1[0]:  0:可见 ，1：单红外 2：双红外
     BYTE waveData[256]*3+[256]+[256];

     */


    private byte data1_0;   //color
    private byte data1_1;    //一次终止

    private byte data1_2;  //一次上限
    private byte data1_3;  //一次下限
    private byte data1_4; //二次上限
    private byte data1_5; //二次下限

    private byte[] waveData; //波形的数据 256*5[R,G,B,R1,R2]   |   maybe 512*5


    /**
     * 波形值  RGB,亮度  color1Bytes
     * 色差波形值  色差
     */
    private byte[] color1Bytes;
    private byte[] color2Bytes;

    private byte[] rBytes;
    private byte[] gBytes;
    private byte[] bBytes;
    private byte[] ir1Bytes;
    private byte[] ir2Bytes;


    public byte[] getColor1Bytes() {
        return color1Bytes;
    }

    public byte[] getColor2Bytes() {
        return color2Bytes;
    }




    private int length;//波形数据长度

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }


    public YYWaveData(ThPackage retData) {
        this.waveType=retData.getExtendType();
        this.data1_0 = retData.getData1()[0];
        this.data1_1 = retData.getData1()[1];
        this.data1_2 = retData.getData1()[2];
        this.data1_3 = retData.getData1()[3];
        this.data1_4 = retData.getData1()[4];
        this.data1_5 = retData.getData1()[5];
        this.waveData= retData.getContents();
        this.length=retData.getLen();

        switch (waveType){

            case ThCommand.WAVE_TYPE_CAMERA_ORIGIN:
            case ThCommand.WAVE_TYPE_CAMERA_TEST:
            case ThCommand.WAVE_TYPE_CAMERA_ADJUST:
                rBytes=new byte[512];

                if(waveData.length/512>=3){
                    gBytes=new byte[512];
                    bBytes=new byte[512];
                    System.arraycopy(waveData,0,rBytes,0,512);
                    System.arraycopy(waveData,512,gBytes,0,512);
                    System.arraycopy(waveData,512*2,bBytes,0,512);
                    if(waveData.length/512==4){
                        ir1Bytes=new byte[512];
                        System.arraycopy(waveData,512*3,ir1Bytes,0,512);
                    }else if(waveData.length/512==5){
                        ir1Bytes=new byte[512];
                        ir2Bytes=new byte[512];

                        System.arraycopy(waveData,512*3,ir1Bytes,0,512);
                        System.arraycopy(waveData,512*4,ir2Bytes,0,512);

                    }
                }else if(waveData.length/512==1){
                    System.arraycopy(waveData,0,rBytes,0,512);
                }
                break;
            case ThCommand.WAVE_TYPE_BACKGROUN_LIGHT:
            case ThCommand.WAVE_TYPE_CAMERA_GAIN:
                rBytes=new byte[256];
                gBytes=new byte[256];
                bBytes=new byte[256];

                if(waveData.length>=256*3){
                    System.arraycopy(waveData,0,rBytes,0,256);
                    System.arraycopy(waveData,256,gBytes,0,256);
                    System.arraycopy(waveData,256*2,bBytes,0,256);
                }

                if(data1_0==1){
                    ir1Bytes=new byte[256];
                    if(waveData.length>=256*4){
                        System.arraycopy(waveData,256*3,ir1Bytes,0,256);
                    }

                }else if (data1_0==2){
                    ir1Bytes=new byte[256];
                    ir2Bytes=new byte[256];
                    if(waveData.length>=256*5){
                        System.arraycopy(waveData,256*3,ir1Bytes,0,256);
                        System.arraycopy(waveData,256*4,ir2Bytes,0,256);
                    }
                }
                break;
        }


    }

    @Override
    public String toString() {
        return "ThWaveRet{" +
                "waveType=" + waveType +
                ", algorithm=" + algorithm +
                ", data1_0=" + data1_0 +
                ", data1_1=" + data1_1 +
                ", data1_2=" + data1_2 +
                ", data1_3=" + data1_3 +
                ", data1_4=" + data1_4 +
                ", data1_5=" + data1_5 +
                ", waveData=" + Arrays.toString(waveData) +
                ", color1Bytes=" + Arrays.toString(color1Bytes) +
                ", color2Bytes=" + Arrays.toString(color2Bytes) +
                ", rBytes=" + Arrays.toString(rBytes) +
                ", gBytes=" + Arrays.toString(gBytes) +
                ", bBytes=" + Arrays.toString(bBytes) +
                ", ir1Bytes=" + Arrays.toString(ir1Bytes) +
                ", ir2Bytes=" + Arrays.toString(ir2Bytes) +
                ", length=" + length +
                '}';
    }

    public void setWaveType(byte waveType) {
        this.waveType = waveType;
    }

    public byte getData1_0() {
        return data1_0;
    }

    public void setData1_0(byte data1_0) {
        this.data1_0 = data1_0;
    }

    public byte getData1_1() {
        return data1_1;
    }

    public void setData1_1(byte data1_1) {
        this.data1_1 = data1_1;
    }

    public byte getData1_2() {
        return data1_2;
    }

    public void setData1_2(byte data1_2) {
        this.data1_2 = data1_2;
    }

    public byte getData1_3() {
        return data1_3;
    }

    public void setData1_3(byte data1_3) {
        this.data1_3 = data1_3;
    }

    public byte getData1_4() {
        return data1_4;
    }

    public void setData1_4(byte data1_4) {
        this.data1_4 = data1_4;
    }

    public byte getData1_5() {
        return data1_5;
    }

    public void setData1_5(byte data1_5) {
        this.data1_5 = data1_5;
    }


    public void setColor1Bytes(byte[] color1Bytes) {
        this.color1Bytes = color1Bytes;
    }

    public void setColor2Bytes(byte[] color2Bytes) {
        this.color2Bytes = color2Bytes;
    }

    public byte[] getWaveData() {
        return waveData;
    }

    public void setWaveData(byte[] waveData) {
        this.waveData = waveData;
    }
}
