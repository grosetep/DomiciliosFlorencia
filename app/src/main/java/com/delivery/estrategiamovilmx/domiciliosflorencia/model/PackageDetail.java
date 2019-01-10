package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

import java.io.Serializable;

/**
 * Created by administrator on 20/07/2017.
 */
public class PackageDetail implements Serializable {
    private String content;
    private String vehicle;
    private String weigth;

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
}
