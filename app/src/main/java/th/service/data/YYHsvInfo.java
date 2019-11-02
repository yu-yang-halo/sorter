package th.service.data;

import th.service.core.TrafficManager;

/**
 * Created by YUYANG on 2018/11/6.
 */
public class YYHsvInfo {
    public static final int SIZE = 9;
    private byte[] vStartEnd; //v[起点,终点] ,2个字节 明度
    private byte[] sStartEnd; //s[起点,终点] 饱和度
    private byte[] hStart;
    private byte[] hEnd;      //h[起点(2bytes),终点(2bytes)]
    private byte index;       //在数组中的索引


    public YYHsvInfo(int idx)
    {
        vStartEnd = new byte[]{(byte)80,(byte)100};
        sStartEnd = new byte[]{80,100};
        hStart = new byte[]{0,50};
        hEnd = new byte[]{0,100};
        index = (byte) idx;
    }
    public YYHsvInfo(byte[] contents)
    {
        vStartEnd = new byte[2];
        sStartEnd = new byte[2];
        hStart = new byte[2];
        hEnd = new byte[2];
        if(check(contents))
        {
            System.arraycopy(contents,0,vStartEnd,0,vStartEnd.length);
            System.arraycopy(contents,2,sStartEnd,0,sStartEnd.length);
            System.arraycopy(contents,4,hStart,0,hStart.length);
            System.arraycopy(contents,6,hEnd,0,hEnd.length);
            index = contents[8];
        }
    }

    private boolean check(byte[] contents) {
        if(contents==null||contents.length!=SIZE){
            TrafficManager.getInstance().showErrorMessage();
            return false;
        }
        return true;
    }

    public byte[] getvStartEnd() {
        return vStartEnd;
    }

    public void setvStartEnd(byte[] vStartEnd) {
        this.vStartEnd = vStartEnd;
    }

    public byte[] getsStartEnd() {
        return sStartEnd;
    }

    public void setsStartEnd(byte[] sStartEnd) {
        this.sStartEnd = sStartEnd;
    }

    public byte[] gethStart() {
        return hStart;
    }

    public void sethStart(byte[] hStart) {
        this.hStart = hStart;
    }

    public byte[] gethEnd() {
        return hEnd;
    }

    public void sethEnd(byte[] hEnd) {
        this.hEnd = hEnd;
    }

    public byte getIndex() {
        return index;
    }

    public void setIndex(byte index) {
        this.index = index;
    }
}
