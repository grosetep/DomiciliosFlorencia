package com.delivery.estrategiamovilmx.domiciliosflorencia.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 11/08/2017.
 */
public class UpdateShoppingCartRequest implements Serializable{
    @SerializedName("idUser")
    private String idUser;
    @SerializedName("products")
    private String products;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }
}
