package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;

import com.delivery.estrategiamovilmx.domiciliosflorencia.items.HelpText;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HelpTextsResponse implements Serializable{
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private List<HelpText> result = null;
    @SerializedName("message")
    @Expose
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<HelpText> getResult() {
        return result;
    }

    public void setResult(List<HelpText> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}