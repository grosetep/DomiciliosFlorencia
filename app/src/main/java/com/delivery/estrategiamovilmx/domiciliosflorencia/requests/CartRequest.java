package com.delivery.estrategiamovilmx.domiciliosflorencia.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 08/08/2017.
 */
public class CartRequest implements Serializable{
    @SerializedName("id_user")
    private String id_user;
    @SerializedName("id_product")
    private String id_product;
    @SerializedName("units")
    private String units;
    @SerializedName("operation")
    private String operation;

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_product() {
        return id_product;
    }

    public void setId_product(String id_product) {
        this.id_product = id_product;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
