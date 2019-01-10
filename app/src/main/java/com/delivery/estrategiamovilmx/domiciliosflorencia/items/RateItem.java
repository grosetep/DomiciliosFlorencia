package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RateItem implements Serializable{
    @SerializedName("idVehicle")
    @Expose
    private String idVehicle;
    @SerializedName("vehicle")
    @Expose
    private String vehicle;
    @SerializedName("factors")
    @Expose
    private String factors;

    public String getIdVehicle() {
        return idVehicle;
    }

    public void setIdVehicle(String idVehicle) {
        this.idVehicle = idVehicle;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getFactors() {
        return factors;
    }

    public void setFactors(String factors) {
        this.factors = factors;
    }

    @Override
    public String toString() {
        return "RateItem{" +
                "idVehicle='" + idVehicle + '\'' +
                ", vehicle='" + vehicle + '\'' +
                ", factors='" + factors + '\'' +
                '}';
    }
}