package th.service.data;

import com.yy.sorter.manager.FileManager;
import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.StringUtils;

import java.util.Arrays;

import th.service.core.TrafficManager;

public class ThSense {
    public static int TEXT_MAX_BYTES = 100;
    private byte type;    //   0-色选,    1-红外,
    private byte subType;
    private byte extType;
    private byte view;   //前视/后视  0/1
    private byte[] sense;  // 灵敏度值 【高位，低位】
    private byte[] senseMin;
    private byte[] senseMax;
    private byte used;   //算法使能状态
    private byte res;
    private String name="";       //算法名称 char[100]
    private int uuid = 0;
    private int viewType;
    public int getHashId() {
        int h = uuid;
        if (h == 0 ) {
            h = 31 * h + view;
            h = 31 * h + type;
            h = 31 * h + subType;
            h = 31 * h + extType;
            uuid = h;
        }
        return h;
    }



    public static ThSense createThSense(byte value)
    {
        ThSense thSense = new ThSense();

        thSense.setType((byte) 0);
        thSense.setSubType((byte) 6);
        thSense.setView((byte) 2);
        thSense.setSense(ConvertUtils.intTo2Bytes(value));
        thSense.setSenseMax(ConvertUtils.intTo2Bytes(19));
        thSense.setSenseMin(ConvertUtils.intTo2Bytes(0));
        thSense.setName(FileManager.getInstance().getString(99));//99#反选

        return thSense;
    }

    public ThSense()
    {

    }

    public ThSense(byte[] contents) {
        byte[] buffer=new byte[TEXT_MAX_BYTES];
        this.sense= new byte[2];
        this.senseMin= new byte[2];
        this.senseMax= new byte[2];
        if(check(contents)){
            this.type = contents[0];
            this.subType = contents[1];
            this.extType = contents[2];
            this.view = contents[3];


            System.arraycopy(contents,4,sense,0,2);
            System.arraycopy(contents,6,senseMin,0,2);
            System.arraycopy(contents,8,senseMax,0,2);

            this.used = contents[10];
            this.res = contents[11];
            System.arraycopy(contents,12,buffer,0,TEXT_MAX_BYTES);
            this.name= StringUtils.convertByteArrayToString(buffer);

        }


    }

    private boolean check(byte[] contents){
        if(contents==null||contents.length!=(TEXT_MAX_BYTES+12)){
            TrafficManager.getInstance().showErrorMessage();
            return false;
        }
        return true;
    }


    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getSubType() {
        return subType;
    }

    public void setSubType(byte subType) {
        this.subType = subType;
    }

    public byte getExtType() {
        return extType;
    }

    public void setExtType(byte extType) {
        this.extType = extType;
    }

    public byte getView() {
        return view;
    }

    public void setView(byte view) {
        this.view = view;
    }

    public byte[] getSense() {
        return sense;
    }

    public void setSense(byte[] sense) {
        this.sense = sense;
    }

    public byte[] getSenseMin() {
        return senseMin;
    }

    public void setSenseMin(byte[] senseMin) {
        this.senseMin = senseMin;
    }

    public byte[] getSenseMax() {
        return senseMax;
    }

    public void setSenseMax(byte[] senseMax) {
        this.senseMax = senseMax;
    }

    public byte getUsed() {
        return used;
    }

    public void setUsed(byte used) {
        this.used = used;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public String toString() {
        return "ThAlgorithmNew{" +
                "type=" + type +
                ", subType=" + subType +
                ", extType=" + extType +
                ", view=" + view +
                ", sense=" + Arrays.toString(sense) +
                ", senseMin=" + Arrays.toString(senseMin) +
                ", senseMax=" + Arrays.toString(senseMax) +
                ", used=" + used +
                ", name='" + name + '\'' +
                '}';
    }

}
