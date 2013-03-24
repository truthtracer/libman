package com.library.service;

import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Properties;
@Component
public class Propert {
    private String appkey;
//    private String secKey;
//    private String secId;
//    private String url;
//
//    public String getSecKey() {
//        return secKey;
//    }
//
//    public void setSecKey(String secKey) {
//        this.secKey = secKey;
//    }
//
//    public String getSecId() {
//        return secId;
//    }
//
//    public void setSecId(String secId) {
//        this.secId = secId;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }


    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    @PostConstruct
    public void init(){
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("db.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
//            this.secKey=properties.getProperty("secretKey");
//            this.secId=properties.getProperty("secretId");
//            this.url=properties.getProperty("url");
            this.appkey = properties.getProperty("appkey");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
