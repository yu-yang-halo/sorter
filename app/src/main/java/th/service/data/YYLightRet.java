package th.service.data;

import java.util.Arrays;

import th.service.core.TrafficManager;

/**
 * Created by YUYANG on 2018/11/6.
 * BYTE r[2]; 		     	//r
 * BYTE g[2];         	    //g
 * BYTE b[2];            	//b
 * BYTE MainLight[2];       //前后主灯
 * BYTE ir[2];  		    //前后红外开关
 *
 */

public class YYLightRet {
    private byte[] fstsndReds;
    private byte[] fstsndGreens;
    private byte[] fstsndBlues;
    private byte[] fstsndMainLights;
    private byte[] fstsndIr;

    public YYLightRet(byte[] contents) {
        fstsndReds=new byte[2];
        fstsndGreens=new byte[2];
        fstsndBlues=new byte[2];
        fstsndMainLights=new byte[2];
        fstsndIr=new byte[2];

        if(check(contents)){
            System.arraycopy(contents,0,fstsndReds,0,fstsndReds.length);
            System.arraycopy(contents,2*1,fstsndGreens,0,fstsndGreens.length);
            System.arraycopy(contents,2*2,fstsndBlues,0,fstsndBlues.length);
            System.arraycopy(contents,2*3,fstsndMainLights,0,fstsndMainLights.length);
            System.arraycopy(contents,2*4,fstsndIr,0,fstsndIr.length);
        }
    }

    private boolean check(byte[] contents){
        if(contents==null||contents.length!=10){
            TrafficManager.getInstance().showErrorMessage();
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "YYLightRet{" +
                "fstsndReds=" + Arrays.toString(fstsndReds) +
                ", fstsndGreens=" + Arrays.toString(fstsndGreens) +
                ", fstsndBlues=" + Arrays.toString(fstsndBlues) +
                ", fstsndMainLights=" + Arrays.toString(fstsndMainLights) +
                ", fstsndIr=" + Arrays.toString(fstsndIr) +
                '}';
    }

    public byte[] getFstsndReds() {
        return fstsndReds;
    }

    public void setFstsndReds(byte[] fstsndReds) {
        this.fstsndReds = fstsndReds;
    }

    public byte[] getFstsndGreens() {
        return fstsndGreens;
    }

    public void setFstsndGreens(byte[] fstsndGreens) {
        this.fstsndGreens = fstsndGreens;
    }

    public byte[] getFstsndBlues() {
        return fstsndBlues;
    }

    public void setFstsndBlues(byte[] fstsndBlues) {
        this.fstsndBlues = fstsndBlues;
    }

    public byte[] getFstsndMainLights() {
        return fstsndMainLights;
    }

    public void setFstsndMainLights(byte[] fstsndMainLights) {
        this.fstsndMainLights = fstsndMainLights;
    }

    public byte[] getFstsndIr() {
        return fstsndIr;
    }

    public void setFstsndIr(byte[] fstsndIr) {
        this.fstsndIr = fstsndIr;
    }
}
