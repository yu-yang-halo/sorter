package th.service.data;

import th.service.core.TrafficManager;

public class ThSvmInfo {
    public static final int SIZE = 8;
    private byte view;
    private byte used;
    private byte blowSample;
    private byte[] spotDiff;//杂质比
    private byte[] spotDiffMax;//杂质比上限
    private byte spotSensor; //灵敏度


    public ThSvmInfo(byte[] contents) {
        spotDiff = new byte[2];
        spotDiffMax = new byte[2];
        if(check(contents))
        {
            view = contents[0];
            used = contents[1];
            blowSample = contents[2];
            System.arraycopy(contents,3,spotDiff,0,spotDiff.length);
            System.arraycopy(contents,3+2,spotDiffMax,0,spotDiffMax.length);

            spotSensor = contents[7];
        }

    }

    public boolean check(byte[] contents){
        if(contents==null||contents.length!=SIZE){
            TrafficManager.getInstance().showErrorMessage();
            return false;
        }
        return true;
    }

    public byte getUsed() {
        return used;
    }

    public void setUsed(byte used) {
        this.used = used;
    }

    public byte getBlowSample() {
        return blowSample;
    }

    public void setBlowSample(byte blowSample) {
        this.blowSample = blowSample;
    }

    public byte[] getSpotDiff() {
        return spotDiff;
    }

    public void setSpotDiff(byte[] spotDiff) {
        this.spotDiff = spotDiff;
    }

    public byte[] getSpotDiffMax() {
        return spotDiffMax;
    }

    public void setSpotDiffMax(byte[] spotDiffMax) {
        this.spotDiffMax = spotDiffMax;
    }

    public byte getSpotSensor() {
        return spotSensor;
    }

    public void setSpotSensor(byte spotSensor) {
        this.spotSensor = spotSensor;
    }

    public byte getView() {
        return view;
    }

    public void setView(byte view) {
        this.view = view;
    }
}
