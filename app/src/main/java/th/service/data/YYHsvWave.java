package th.service.data;

import th.service.core.TrafficManager;

/**
 * Created by YUYANG on 2018/11/6.
 *
 * HSV 波形数据结构
 */

public class YYHsvWave {
    private static final int HSV_WAVE_SIZE_UNIT=2304;
    private byte[] waveData1;//1024
    private byte[] waveData2;//1024
    private byte[] waveData3;//256

    public YYHsvWave(byte[] contents){
        waveData1=new byte[1024];
        waveData2=new byte[1024];
        waveData3=new byte[256];

        if(check(contents)){
            System.arraycopy(contents,0,waveData1,0,waveData1.length);
            System.arraycopy(contents,1024,waveData2,0,waveData2.length);
            System.arraycopy(contents,2048,waveData3,0,waveData3.length);
        }
    }
    public boolean check(byte[] contents){
        if(contents==null||contents.length!=HSV_WAVE_SIZE_UNIT){
            TrafficManager.getInstance().showErrorMessage();
            return false;
        }
        return true;
    }

    public byte[] getWaveData1() {
        return waveData1;
    }

    public byte[] getWaveData2() {
        return waveData2;
    }

    public byte[] getWaveData3() {
        return waveData3;
    }
}
