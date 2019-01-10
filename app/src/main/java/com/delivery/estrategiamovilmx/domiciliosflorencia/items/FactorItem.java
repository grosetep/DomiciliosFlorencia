package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FactorItem {
    @SerializedName("idTypeVehicle")
    @Expose
    private String idTypeVehicle;
    @SerializedName("weigth")
    @Expose
    private String weigth;
    @SerializedName("costByKm")
    @Expose
    private String costByKm;
    @SerializedName("baseDistanceMts")
    @Expose
    private String baseDistanceMts;
    @SerializedName("baseRate")
    @Expose
    private String baseRate;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("idCountry")
    @Expose
    private String idCountry;

    public String getIdTypeVehicle() {
        return idTypeVehicle;
    }

    public void setIdTypeVehicle(String idTypeVehicle) {
        this.idTypeVehicle = idTypeVehicle;
    }

    public String getWeigth() {
        return weigth;
    }

    public void setWeigth(String weigth) {
        this.weigth = weigth;
    }

    public String getCostByKm() {
        return costByKm;
    }

    public void setCostByKm(String costByKm) {
        this.costByKm = costByKm;
    }

    public String getBaseDistanceMts() {
        return baseDistanceMts;
    }

    public void setBaseDistanceMts(String baseDistanceMts) {
        this.baseDistanceMts = baseDistanceMts;
    }

    public String getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(String baseRate) {
        this.baseRate = baseRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(String idCountry) {
        this.idCountry = idCountry;
    }

    @Override
    public String toString() {
        return "FactorItem{" +
                "idTypeVehicle='" + idTypeVehicle + '\'' +
                ", weigth='" + weigth + '\'' +
                ", costByKm='" + costByKm + '\'' +
                ", baseDistanceMts='" + baseDistanceMts + '\'' +
                ", baseRate='" + baseRate + '\'' +
                ", description='" + description + '\'' +
                ", idCountry='" + idCountry + '\'' +
                '}';
    }
}