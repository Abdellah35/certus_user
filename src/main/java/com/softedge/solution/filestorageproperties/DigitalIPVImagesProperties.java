package com.softedge.solution.filestorageproperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "digitalipv")
public class DigitalIPVImagesProperties {
    private String digitalIPVImageDir;

    public String getDigitalIPVImageDir() {
        return digitalIPVImageDir;
    }

    public void setDigitalIPVImageDir(String digitalIPVImageDir) {
        this.digitalIPVImageDir = digitalIPVImageDir;
    }
}
