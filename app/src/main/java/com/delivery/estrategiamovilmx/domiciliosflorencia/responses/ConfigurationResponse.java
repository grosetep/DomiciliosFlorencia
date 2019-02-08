package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;



import com.delivery.estrategiamovilmx.domiciliosflorencia.items.ConfigItem;

import java.io.Serializable;

/**
 * Created by administrator on 19/08/2017.
 */
public class ConfigurationResponse implements Serializable {
    private String status;
    private ConfigItem result = null;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ConfigItem getResult() {
        return result;
    }

    public void setResult(ConfigItem result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
