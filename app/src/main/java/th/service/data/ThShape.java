package th.service.data;

import com.yy.sorter.utils.ConvertUtils;
import com.yy.sorter.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import th.service.core.TrafficManager;


public class ThShape {
    public static final int MIN_SIZE = 52;
    private int shapeType;
    private int shapeItemCount;
    private String shapeName;

    private List<ThShapeItem> shapeItemList;
    public ThShape()
    {

    }

    public ThShape(byte[] contents)
    {
        shapeItemList = new ArrayList<>();
        if(contents.length >= 52)
        {
            shapeType = contents[0];
            shapeItemCount = ConvertUtils.unsignByteToInt(contents[1]);
            byte[] strArr = new byte[50];
            System.arraycopy(contents,2,strArr,0,strArr.length);
            shapeName = StringUtils.convertByteArrayToString(strArr);

            int pos = MIN_SIZE;

            while (pos != contents.length)
            {

                if(pos+ThShapeItem.MIN_SIZE - 1>= contents.length)
                {
                    TrafficManager.getInstance().showErrorMessage();
                    break;
                }

                int miniItemCount =  ConvertUtils.unsignByteToInt(contents[pos+ThShapeItem.MIN_SIZE - 1]);
                int shapeItemSize =  ThShapeItem.MIN_SIZE
                        + miniItemCount * ThShapeItem.MiniItem.SIZE;
                byte[] buffer = new byte[shapeItemSize];

                if(pos + shapeItemSize > contents.length)
                {
                    TrafficManager.getInstance().showErrorMessage();
                    break;
                }

                System.arraycopy(contents,pos ,buffer,0,shapeItemSize);

                ThShapeItem thShapeItem = new ThShapeItem(buffer);
                shapeItemList.add(thShapeItem);

                pos+=shapeItemSize;
            }

        }else
        {
            TrafficManager.getInstance().showErrorMessage();
        }
    }

    public int getShapeType() {
        return shapeType;
    }

    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
    }

    public int getShapeItemCount() {
        return shapeItemCount;
    }

    public void setShapeItemCount(int shapeItemCount) {
        this.shapeItemCount = shapeItemCount;
    }

    public String getShapeName() {
        return shapeName;
    }

    public void setShapeName(String shapeName) {
        this.shapeName = shapeName;
    }

    public List<ThShapeItem> getShapeItemList() {
        return shapeItemList;
    }

    public void setShapeItemList(List<ThShapeItem> shapeItemList) {
        this.shapeItemList = shapeItemList;
    }
}
