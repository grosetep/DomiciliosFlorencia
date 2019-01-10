package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;

import com.delivery.estrategiamovilmx.domiciliosflorencia.items.FactorItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RatesResponse implements Serializable {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private FactorItem result = null;
    @SerializedName("message")
    @Expose
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FactorItem getResult() {
        return result;
    }

    public void setResult(FactorItem result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}