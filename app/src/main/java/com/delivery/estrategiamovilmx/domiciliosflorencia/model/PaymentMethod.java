package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 04/08/2017.
 */
public class PaymentMethod implements Serializable {
    @SerializedName("idPaymentMethod")
    private String idPaymentMethod;
    @SerializedName("method")
    private String method;
    @SerializedName("description")
    private String description;
    @SerializedName("status")
    private String status;
    @SerializedName("image")
    private String image;
    @SerializedName("isSelected")
    private boolean isSelected;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getIdPaymentMethod() {
        return idPaymentMethod;
    }

    public void setIdPaymentMethod(String idPaymentMethod) {
        this.idPaymentMethod = idPaymentMethod;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
