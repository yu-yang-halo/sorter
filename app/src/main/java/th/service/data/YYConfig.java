package th.service.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YUYANG on 2018/11/6.
 * 配置文件对象
 */

public class YYConfig {

    private int version;
    private Map<String,Map<String,String>> android;
    private Map<String,Map<String,String>> ios;
    private List<LanguageVersion> language;

    public LanguageVersion findSameObject(LanguageVersion languageVersion){

        for(LanguageVersion lan:this.getLanguage()){

            if(lan.getCountryId()==languageVersion.getCountryId()){

                return  lan;
            }

        }
        return null;
    }

    public YYConfig transferConfig(YYConfig otherConfig){
        List<LanguageVersion> languageVersions=otherConfig.getLanguage();
        for(LanguageVersion languageVersion:languageVersions){
           LanguageVersion version=findSameObject(languageVersion);
           if(version==null){
                continue;
           }
           languageVersion.setLastVersion(languageVersion.getVersion());
           languageVersion.setVersion(version.getVersion());

        }

        return otherConfig;
    }

    public List<LanguageVersion> getLanguage() {
        return language;
    }

    public void setLanguage(List<LanguageVersion> language) {
        this.language = language;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "YYConfig{" +
                "version=" + version +
                ", android=" + android +
                ", ios=" + ios +
                ", language=" + language +
                '}';
    }

    public static class  LanguageVersion{
        private String name;
        private int version;
        private String url;
        private int lastVersion=0;//最新版本
        private boolean hasDownloaded;//是否已经下载
        private int countryId;

        public int getCountryId() {
            return countryId;
        }

        public void setCountryId(int countryId) {
            this.countryId = countryId;
        }

        public int getLastVersion() {
            return lastVersion;
        }

        public void setLastVersion(int lastVersion) {
            this.lastVersion = lastVersion;
        }

        public boolean isHasDownloaded() {
            return hasDownloaded;
        }

        public void setHasDownloaded(boolean hasDownloaded) {
            this.hasDownloaded = hasDownloaded;
        }

        public LanguageVersion(){

        }
        public LanguageVersion(String name, int version, String url) {
            this.name = name;
            this.version = version;
            this.url = url;
        }

        @Override
        public String toString() {
            return "LanguageVersion{" +
                    "name='" + name + '\'' +
                    ", version=" + version +
                    ", url='" + url + '\'' +
                    '}';
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    public Map<String, Map<String, String>> getAndroid() {
        if(android==null){
            return new HashMap<>();
        }
        return android;
    }

    public void setAndroid(Map<String, Map<String, String>> android) {
        this.android = android;
    }

    public Map<String, Map<String, String>> getIos() {
        return ios;
    }

    public void setIos(Map<String, Map<String, String>> ios) {
        this.ios = ios;
    }
}
