package com.delivery.estrategiamovilmx.domiciliosflorencia.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 03/08/2017.
 */
public class Additional implements Serializable {
    @SerializedName("idAdditional")
    @Expose
    private String idAdditional;
    @SerializedName("additional")
    @Expose
    private String additional;
    @SerializedName("price")
    @Expose
    private String price;

    private final static long serialVersionUID = -7294540814952659712L;

    public String getIdAdditional() {
        return idAdditional;
    }

    public void setIdAdditional(String idAdditional) {
        this.idAdditional = idAdditional;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return new String().concat("idAdditional:").concat(idAdditional!=null?idAdditional:"null").concat("additional:").concat(additional!=null?additional:"null").concat("price:").concat(price!=null?price:"null").toString();
    }
}
