package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 04/09/2017.
 */
public class OrderDetail implements Serializable {
    @SerializedName("idOrder")
    @Expose
    private String idOrder;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("vehicle")
    @Expose
    private String vehicle;
    @SerializedName("weigth")
    @Expose
    private String weigth;
    @SerializedName("hourCreation")
    @Expose
    private String hourCreation;
    @SerializedName("dateCreation")
    @Expose
    private String dateCreation;
    @SerializedName("destinations")
    @Expose
    private String destinations;
    @SerializedName("idClient")
    @Expose
    private String idClient;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("nameClient")
    @Expose
    private String nameClient;



    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getWeigth() {
        return weigth;
    }

    public void setWeigth(String weigth) {
        this.weigth = weigth;
    }

    public String getHourCreation() {
        return hourCreation;
    }

    public void setHourCreation(String hourCreation) {
        this.hourCreation = hourCreation;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getDestinations() {
        return destinations;
    }

    public void setDestinations(String destinations) {
        this.destinations = destinations;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNameClient() {
        return nameClient;
    }

    public void setNameClient(String nameClient) {
        this.nameClient = nameClient;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "idOrder='" + idOrder + '\'' +
                ", content='" + content + '\'' +
                ", vehicle='" + vehicle + '\'' +
                ", weigth='" + weigth + '\'' +
                ", hourCreation='" + hourCreation + '\'' +
                ", dateCreation='" + dateCreation + '\'' +
                ", destinations='" + destinations + '\'' +
                ", idClient='" + idClient + '\'' +
                ", email='" + email + '\'' +
                ", nameClient='" + nameClient + '\'' +
                '}';
    }
}
