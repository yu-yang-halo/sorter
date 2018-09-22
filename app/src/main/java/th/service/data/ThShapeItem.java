package th.service.data;

import com.yy.sorter.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import th.service.core.TrafficManager;

public class ThShapeItem {
    public static final int MIN_SIZE = 55;
    private byte  shapeType;//形选类型  基础形选/花生/小麦.....
    private byte  shapeId;//形选项  选长 选短
    private String name;
    private byte   used;
    private byte   mutex;//互斥的shapeId   默认255  则没有
    private byte   count;//MiniItem 的数量
    private List<MiniItem> miniItemList;


    public ThShapeItem(byte[] contents)
    {
        if(contents.length >= 55)
        {
            shapeType = contents[0];
            shapeId = contents[1];
            byte[] strArr = new byte[50];
            System.arraycopy(contents,2,strArr,0,strArr.length);
            used = contents[52];
            mutex = contents[53];
            count = contents[54];

            if(count*MiniItem.SIZE+MIN_SIZE == contents.length)
            {
                miniItemList = new ArrayList<>();

                byte[] buffer = new byte[MiniItem.SIZE];
                for(int i=0;i<count;i++)
                {
                    System.arraycopy(contents,MIN_SIZE+i*MiniItem.SIZE,buffer,0,buffer.length);

                    MiniItem miniItem = new MiniItem(buffer);
                    miniItemList.add(miniItem);

                }
            }


        }else
        {
            TrafficManager.getInstance().showErrorMessage();
        }
    }


    public byte getShapeType() {
        return shapeType;
    }

    public void setShapeType(byte shapeType) {
        this.shapeType = shapeType;
    }

    public byte getShapeId() {
        return shapeId;
    }

    public void setShapeId(byte shapeId) {
        this.shapeId = shapeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getUsed() {
        return used;
    }

    public void setUsed(byte used) {
        this.used = used;
    }

    public byte getMutex() {
        return mutex;
    }

    public void setMutex(byte mutex) {
        this.mutex = mutex;
    }

    public byte getCount() {
        return count;
    }

    public void setCount(byte count) {
        this.count = count;
    }

    public List<MiniItem> getMiniItemList() {
        return miniItemList;
    }

    public void setMiniItemList(List<MiniItem> miniItemList) {
        this.miniItemList = miniItemList;
    }

    public static class MiniItem
    {
        public static final int SIZE = 57;
        private byte index; //形选具体算法  灵敏度 、最小长度
        private String  name;//50 bytes
        private byte[] value;
        private byte[] max;
        private byte[] min;

        public MiniItem(byte[] contents)
        {
            value = new byte[2];
            max = new byte[2];
            min = new byte[2];
            if(check(contents))
            {
                index = contents[0];
                byte[] strArr = new byte[50];
                System.arraycopy(contents,1,strArr,0,strArr.length);
                name = StringUtils.convertByteArrayToString(strArr);
                System.arraycopy(contents,51,value,0,value.length);
                System.arraycopy(contents,53,max,0,max.length);
                System.arraycopy(contents,55,min,0,min.length);

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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



}
