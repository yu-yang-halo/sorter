package th.service.data;

public class ThMode {
    private byte bigIndex;
    private byte smallIndex;
    private String modeName;
    private String modeTime;
    private byte   flag;

    public ThMode(byte bigIndex, byte smallIndex, String modeName, String modeTime, byte flag) {
        this.bigIndex = bigIndex;
        this.smallIndex = smallIndex;
        this.modeName = modeName;
        this.modeTime = modeTime;
        this.flag = flag;
    }

    public byte getBigIndex() {
        return bigIndex;
    }

    public void setBigIndex(byte bigIndex) {
        this.bigIndex = bigIndex;
    }

    public byte getSmallIndex() {
        return smallIndex;
    }

    public void setSmallIndex(byte smallIndex) {
        this.smallIndex = smallIndex;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public String getModeTime() {
        return modeTime;
    }

    public void setModeTime(String modeTime) {
        this.modeTime = modeTime;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }
}
