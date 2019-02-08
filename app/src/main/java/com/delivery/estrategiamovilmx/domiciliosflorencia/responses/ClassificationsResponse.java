package com.delivery.estrategiamovilmx.domiciliosflorencia.responses;

import com.delivery.estrategiamovilmx.domiciliosflorencia.items.ClassificationItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ClassificationsResponse implements Serializable{
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private List<ClassificationItem> result = null;
    @SerializedName("message")
    @Expose
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ClassificationItem> getResult() {
        return result;
    }

    public void setResult(List<ClassificationItem> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ClassificationsResponse{" +
                "status='" + status + '\'' +
                ", result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}