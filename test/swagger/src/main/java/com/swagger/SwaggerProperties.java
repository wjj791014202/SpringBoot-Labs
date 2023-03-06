package com.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import springfox.documentation.service.Contact;

@RefreshScope
@ConfigurationProperties(
        prefix = "kucoin.swagger"
)
public class SwaggerProperties {
    private boolean enable = true;
    private String title;
    private String description;
    private Contact contact;
    private String version;
    private String scan;

    public SwaggerProperties() {
        this.title = (String)SwaggerConstants.CONFIG_MAP.get("title");
        this.description = (String)SwaggerConstants.CONFIG_MAP.get("description");
        this.contact = new Contact((String)SwaggerConstants.CONFIG_MAP.get("contact.name"), (String)SwaggerConstants.CONFIG_MAP.get("contact.url"), (String)SwaggerConstants.CONFIG_MAP.get("contact.email"));
        this.version = (String)SwaggerConstants.CONFIG_MAP.get("version");
        this.scan = (String)SwaggerConstants.CONFIG_MAP.get("scan");
    }

    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Contact getContact() {
        return this.contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getScan() {
        return this.scan;
    }

    public void setScan(String scan) {
        this.scan = scan;
    }
}
