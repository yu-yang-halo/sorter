package com.th.colorsorter.thcolorsorter;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Tracer;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.widget.EditText;

import org.junit.Test;

import java.util.List;
/**
 * Created by Administrator on 2017/11/6.
 * uiautomator 自动化测试
 */
public class ThColorSortTest {
    private final static int REPEAT_COUNT=30;
    @Test
    public void testLogin() throws Exception {
        int count=0;
        UiDevice  uiDevice=UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject myApp = uiDevice.findObject(new UiSelector().text("泰禾色选机"));
        myApp.click();
        Tracer.getInstance().setOutputMode(Tracer.Mode.LOGCAT);
        while (count<REPEAT_COUNT){

            Tracer.trace("执行次数:"+(count+1));

            UiObject searchUiObj=uiDevice.findObject(new UiSelector().text("搜索设备列表"));

            searchUiObj.clickAndWaitForNewWindow();

            UiObject itemUiObj=uiDevice.findObject(new UiSelector().text("thgd"));

            itemUiObj.clickAndWaitForNewWindow();

            uiDevice.findObject(new UiSelector().text("灵敏度")).click();
            uiDevice.findObject(new UiSelector().text("系统信息")).click();
            uiDevice.findObject(new UiSelector().text("系统设置")).click();
            uiDevice.findObject(new UiSelector().text("光学校准")).click();
            uiDevice.pressBack();

            uiDevice.findObject(new UiSelector().text("灵敏度")).click();
            uiDevice.findObject(new UiSelector().text("形选")).click();
            UiObject btnEnable=uiDevice.findObject(new UiSelector().text("禁用"));
            if(btnEnable.exists()){
                btnEnable.click();
            }else{
                btnEnable=uiDevice.findObject(new UiSelector().text("使用"));
                btnEnable.click();
            }

            uiDevice.findObject(new UiSelector().text("智能分选")).click();
            UiObject btnSample=uiDevice.findObject(new UiSelector().text("剔除负样本"));
            if(btnSample.exists()){
                btnSample.click();
            }else{
                btnSample=uiDevice.findObject(new UiSelector().text("剔除正样本"));
                btnSample.click();
            }

            List<UiObject2> list= uiDevice.findObjects(By.clazz(EditText.class));

            for(UiObject2 o2:list){

                o2.click();
                Thread.sleep(200);
                uiDevice.findObject(new UiSelector().text("5")).click();

                uiDevice.findObject(new UiSelector().text("确定")).click();

                Thread.sleep(2000);

            }


            uiDevice.pressBack();
            uiDevice.pressBack();
            uiDevice.pressBack();
            uiDevice.pressBack();
            count++;



        }


    }
}
