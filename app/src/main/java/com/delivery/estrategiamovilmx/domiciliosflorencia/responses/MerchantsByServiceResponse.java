package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;


import com.delivery.estrategiamovilmx.domiciliosflorencia.items.MerchantItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MerchantsByServiceResponse implements Serializable {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private List<MerchantItem> result = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("total")
    @Expose
    private String total;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MerchantItem> getResult() {
        return result;
    }

    public void setResult(List<MerchantItem> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "MerchantsByServiceResponse{" +
                "status='" + status + '\'' +
                ", result=" + result +
                ", message='" + message + '\'' +
                ", total='" + total + '\'' +
                '}';
    }
}