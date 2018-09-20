package th.service.data;

import com.yy.sorter.utils.ConvertUtils;

import java.util.Arrays;
import java.util.List;

import th.service.core.TrafficManager;
import th.service.helper.ThLogger;

/**
 * Created by Administrator on 2017/4/11.
 */

public class ThSVersion {
    private static ThSVersion instance;
    private ThSVersion(){

    }
    public static ThSVersion getInstance(){
        synchronized (ThSVersion.class){

            if(instance==null){
                instance=new ThSVersion();
            }
        }
        return instance;
    }

    public static void emptyInstance(){
        synchronized (ThSVersion.class){

            if(instance!=null){
                instance=null;
            }
        }
    }
    private List<CameraVersion> cameraVersions;
    private List<ColorVersion>  colorVersions;
    private BaseVersion   baseVersion;

    public List<CameraVersion> getCameraVersions() {
        return cameraVersions;
    }

    public void setCameraVersions(List<CameraVersion> cameraVersions) {
        this.cameraVersions = cameraVersions;
    }

    public List<ColorVersion> getColorVersions() {
        return colorVersions;
    }

    public void setColorVersions(List<ColorVersion> colorVersions) {
        this.colorVersions = colorVersions;
    }

    public BaseVersion getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(BaseVersion baseVersion) {
        this.baseVersion = baseVersion;
    }

    public static class CameraVersion{
        private byte cameraType;
        private byte ch;
        private byte[] front_software;   //前视软件版本 2bytes
        private byte[] front_hardware; //前视硬件版本 2bytes
        private byte[] rear_software;  //后视软件版本 2bytes
        private byte[] rear_hardware; ////后视硬件版本 2bytes

        public byte getCameraType() {
            return cameraType;
        }

        public void setCameraType(byte cameraType) {
            this.cameraType = cameraType;
        }

        public String getFrontHardwareStr(){
            return String.format("%x%x",front_hardware[0]&0xFF,front_hardware[1]&0xFF);
        }
        public String getRearHardwareStr(){
            return String.format("%x%x",rear_hardware[0]&0xFF,rear_hardware[1]&0xFF);
        }


        public CameraVersion(byte[] others,byte cameraType) {
              this.cameraType=cameraType;
              this.front_software=new byte[2];
              this.front_hardware=new byte[2];
              this.rear_software=new byte[2];
              this.rear_hardware=new byte[2];
              if(check(others)){
                  ch=others[0];
                  System.arraycopy(others,1,front_software,0,front_software.length);
                  System.arraycopy(others,1+2,front_hardware,0,front_hardware.length);
                  System.arraycopy(others,1+2*2,rear_software,0,rear_software.length);
                  System.arraycopy(others,1+2*3,rear_hardware,0,rear_hardware.length);
              }
        }



        public boolean check(byte[] contents){
            if(contents==null||contents.length!=9){
                TrafficManager.getInstance().showErrorMessage();
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "CameraVersion{" +
                    "ch=" + ch +
                    ", front_software=" + Arrays.toString(front_software) +
                    ", front_hardware=" + Arrays.toString(front_hardware) +
                    ", rear_software=" + Arrays.toString(rear_software) +
                    ", rear_hardware=" + Arrays.toString(rear_hardware) +
                    '}';
        }

        public byte getCh() {
            return ch;
        }

        public void setCh(byte ch) {
            this.ch = ch;
        }

        public byte[] getFront_software() {
            return front_software;
        }

        public void setFront_software(byte[] front_software) {
            this.front_software = front_software;
        }

        public byte[] getFront_hardware() {
            return front_hardware;
        }

        public void setFront_hardware(byte[] front_hardware) {
            this.front_hardware = front_hardware;
        }

        public byte[] getRear_software() {
            return rear_software;
        }

        public void setRear_software(byte[] rear_software) {
            this.rear_software = rear_software;
        }

        public byte[] getRear_hardware() {
            return rear_hardware;
        }

        public void setRear_hardware(byte[] rear_hardware) {
            this.rear_hardware = rear_hardware;
        }
    }

    public static class ColorVersion{
        private byte ch;
        private byte[] arm;             //arm版本 2bytes
        private byte[] fpga;             //fpga版本 2bytes
        private byte[] hardware;        // 硬件版本 2bytes


        public String getHardwareStr(){
            return String.format("%x%x",hardware[0]&0xFF,hardware[1]&0xFF);
        }


        public ColorVersion(byte[] others) {
            this.arm=new byte[2];
            this.fpga=new byte[2];
            this.hardware=new byte[2];
            if(check(others)){
                ch=others[0];
                System.arraycopy(others,1,arm,0,arm.length);
                System.arraycopy(others,1+2,fpga,0,fpga.length);
                System.arraycopy(others,1+2*2,hardware,0,hardware.length);
            }
        }

        public boolean check(byte[] contents){
            if(contents==null||contents.length!=7){
                TrafficManager.getInstance().showErrorMessage();
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "ColorVersion{" +
                    "ch=" + ch +
                    ", arm=" + Arrays.toString(arm) +
                    ", fpga=" + Arrays.toString(fpga) +
                    ", hardware=" + Arrays.toString(hardware) +
                    '}';
        }



        public byte getCh() {
            return ch;
        }

        public void setCh(byte ch) {
            this.ch = ch;
        }

        public byte[] getArm() {
            return arm;
        }

        public void setArm(byte[] arm) {
            this.arm = arm;
        }

        public byte[] getFpga() {
            return fpga;
        }

        public void setFpga(byte[] fpga) {
            this.fpga = fpga;
        }

        public byte[] getHardware() {
            return hardware;
        }

        public void setHardware(byte[] hardware) {
            this.hardware = hardware;
        }
    }


    public static class BaseVersion{
        private String showversion;      //显示板
        private byte[] control; 		  //控制板 2bytes
        private byte[]  led;              //led    2bytes
        private byte[]  sensor;           //传感器 2bytes
        private byte[]  wheel;            // 履带
        private byte[]  timeLed;            // 分时led 2bytes

        public BaseVersion(String showversion,byte[] others) {
            this.showversion = showversion;
            this.control=new byte[2];
            this.led=new byte[2];
            this.sensor=new byte[2];
            this.wheel=new byte[2];
            this.timeLed=new byte[2];
           if(check(others)){
               System.arraycopy(others,0,control,0,control.length);
               System.arraycopy(others,2*1,led,0,led.length);
               System.arraycopy(others,2*2,sensor,0,sensor.length);
               System.arraycopy(others,2*3,wheel,0,wheel.length);
               System.arraycopy(others,2*4,timeLed,0,timeLed.length);
               log();
           }

        }
        private void log(){
            ThLogger.debug("LOG", ConvertUtils.bytes2ToFloat(control)+":"+
                    ConvertUtils.bytes2ToFloat(led)+":"+
                    ConvertUtils.bytes2ToFloat(sensor)+":"+
                    ConvertUtils.bytes2ToFloat(wheel)+":"+
                    ConvertUtils.bytes2ToFloat(timeLed));
        }

        public boolean check(byte[] contents){
            if(contents==null||contents.length!=10){
                TrafficManager.getInstance().showErrorMessage();
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "BaseVersion{" +
                    "showversion='" + showversion + '\'' +
                    ", control=" + Arrays.toString(control) +
                    ", led=" + Arrays.toString(led) +
                    ", sensor=" + Arrays.toString(sensor) +
                    ", wheel=" + Arrays.toString(wheel) +
                    ", timeLed=" + Arrays.toString(timeLed) +
                    '}';
        }

        public String getShowversion() {
            return showversion;
        }

        public void setShowversion(String showversion) {
            this.showversion = showversion;
        }

        public byte[] getControl() {
            return control;
        }

        public void setControl(byte[] control) {
            this.control = control;
        }


        public byte[] getLed() {
            return led;
        }


        public void setLed(byte[] led) {
            this.led = led;
        }

        public byte[] getSensor() {
            return sensor;
        }

        public void setSensor(byte[] sensor) {
            this.sensor = sensor;
        }

        public byte[] getWheel() {
            return wheel;
        }

        public void setWheel(byte[] wheel) {
            this.wheel = wheel;
        }

        public byte[] getTimeLed() {
            return timeLed;
        }

        public void setTimeLed(byte[] timeLed) {
            this.timeLed = timeLed;
        }
    }

}
