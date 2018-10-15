package th.service.data;

import th.service.core.TrafficManager;

public class ThRelate {
    public static final int SIZE = 7;
    private byte index; //形选具体算法  灵敏度 、最小长度
    private byte[] value;
    private byte[] max;
    private byte[] min;
    public ThRelate(){};
    public ThRelate(byte[] contents)
    {
        value = new byte[2];
        max = new byte[2];
        min = new byte[2];
        if(check(contents))
        {
            index = contents[0];
            System.arraycopy(contents,1,value,0,value.length);
            System.arraycopy(contents,3,max,0,max.length);
            System.arraycopy(contents,5,min,0,min.length);
        }
    }
    private boolean check(byte[] contents){
        if(contents==null||contents.length!=(SIZE)){
            TrafficManager.getInstance().showErrorMessage();
            return false;
        }
        return true;
    }

    public byte getIndex() {
        return index;
    }

    public void setIndex(byte index) {
        this.index = index;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public byte[] getMax() {
        return max;
    }

    public void setMax(byte[] max) {
        this.max = max;
    }

    public byte[] getMin() {
        return min;
    }

    public void setMin(byte[] min) {
        this.min = min;
    }
}
