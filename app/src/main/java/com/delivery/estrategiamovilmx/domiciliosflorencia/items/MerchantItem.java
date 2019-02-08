package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MerchantItem implements Serializable{
    @SerializedName("idMerchant")
    @Expose
    private int idMerchant;
    @SerializedName("serviceKey")
    @Expose
    private String serviceKey;
    @SerializedName("imagePath")
    @Expose
    private String imagePath;
    @SerializedName("imageName")
    @Expose
    private String imageName;
    @SerializedName("tpoDelivery")
    @Expose
    private String tpoDelivery;
    @SerializedName("deliveryCost")
    @Expose
    private String deliveryCost;
    @SerializedName("nameBussiness")
    @Expose
    private String nameBussiness;
    @SerializedName("important")
    @Expose
    private String important;

    public int getIdMerchant() {
        return idMerchant;
    }

    public void setIdMerchant(int idMerchant) {
        this.idMerchant = idMerchant;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getTpoDelivery() {
        return tpoDelivery;
    }

    public void setTpoDelivery(String tpoDelivery) {
        this.tpoDelivery = tpoDelivery;
    }

    public String getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(String deliveryCost) {
        this.deliveryCost = deliveryCost;
    }



    public String getImportant() {
        return important;
    }

    public void setImportant(String important) {
        this.important = important;
    }

    public String getNameBussiness() {
        return nameBussiness;
    }

    public void setNameBussiness(String nameBussiness) {
        this.nameBussiness = nameBussiness;
    }

    @Override
    public String toString() {
        return "MerchantItem{" +
                "idMerchant=" + idMerchant +
                ", serviceKey='" + serviceKey + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", imageName='" + imageName + '\'' +
                ", tpoDelivery=" + tpoDelivery +
                ", deliveryCost='" + deliveryCost + '\'' +
                ", nameBussiness='" + nameBussiness + '\'' +
                ", important='" + important + '\'' +
                '}';
    }
}