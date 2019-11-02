package th.service.data;

import java.util.Arrays;

import th.service.core.TrafficManager;

/**
 * Created by YUYANG on 2018/11/6.
 */
public class YYFeeder {
    private static final int SIZE = 30;
    private byte chuteNumber;            // 给料量通道数
    private byte groupNumber;         // 给料量分组数
    private byte[] groupData;   // 每组给料量 4
    private byte[] groupOpen;   // 每组开关状态 4
    private byte[] vibdata;     // 每个通道的给料量  10
    private byte[] vibOpen;     // 每个通道的给料量开关 10


    public YYFeeder(byte[] contents)
    {
        groupData = new byte[4];
        groupOpen = new byte[4];
        vibdata = new byte[10];
        vibOpen = new byte[10];

        if(check(contents))
        {
            chuteNumber = contents[0];
            groupNumber = contents[1];

            System.arraycopy(contents,2,groupData,0,groupData.length);
            System.arraycopy(contents,2+4,groupOpen,0,groupOpen.length);
            System.arraycopy(contents,2+4+4,vibdata,0,vibdata.length);
            System.arraycopy(contents,2+4+4+10,vibOpen,0,vibOpen.length);

        }
    }

    public boolean check(byte[] contents){
        if(contents==null||contents.length!=SIZE){
            TrafficManager.getInstance().showErrorMessage();
            return false;
        }
        return true;
    }

    public byte getChuteNumber() {
        return chuteNumber;
    }

    public void setChuteNumber(byte chuteNumber) {
        this.chuteNumber = chuteNumber;
    }

    public byte getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(byte groupNumber) {
        this.groupNumber = groupNumber;
    }

    public byte[] getGroupData() {
        return groupData;
    }

    public void setGroupData(byte[] groupData) {
        this.groupData = groupData;
    }

    public byte[] getGroupOpen() {
        return groupOpen;
    }

    public void setGroupOpen(byte[] groupOpen) {
        this.groupOpen = groupOpen;
    }

    public byte[] getVibdata() {
        return vibdata;
    }

    public void setVibdata(byte[] vibdata) {
        this.vibdata = vibdata;
    }

    public byte[] getVibOpen() {
        return vibOpen;
    }

    public void setVibOpen(byte[] vibOpen) {
        this.vibOpen = vibOpen;
    }

    @Override
    public String toString() {
        return "YYFeeder{" +
                "chuteNumber=" + chuteNumber +
                ", groupNumber=" + groupNumber +
                ", groupData=" + Arrays.toString(groupData) +
                ", groupOpen=" + Arrays.toString(groupOpen) +
                ", vibdata=" + Arrays.toString(vibdata) +
                ", vibOpen=" + Arrays.toString(vibOpen) +
                '}';
    }
}
